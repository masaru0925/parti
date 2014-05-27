/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import ejb.MessageAccessFacade;
import ejb.MessageFacade;
import entity.Message;
import entity.MessageAccess;
import entity.UserAccount;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import ws.util.MessageJSONCoder;

/**
 *
 * @author masaru
 */
//@ServerEndpoint(
//		value = "/messageEndpoint", encoders = {MessageJSONCoder.class}, decoders = {MessageJSONCoder.class}
//)
@ServerEndpoint(
		// @PathParamを使う
		value="/messageEndpoint/{access_user_account_id}"
		,encoders={MessageJSONCoder.class}
		,decoders={MessageJSONCoder.class}
)  	
@Stateless // <ーこれがないとCDIが働かない！！
// See
//	http://stackoverflow.com/questions/20872300/java-ee-7-how-to-inject-an-ejb-into-a-websocket-serverendpoint 
public class MessageEndpoint {

		@EJB
		MessageFacade messageFacade;
		@EJB
		MessageAccessFacade messageAccessFacade;
//		@EJB
//		UserAccountFacade userAccountFacade;
//		@EJB
//		PartiFacade partiFacade;

		private static final String PATH_PARAM_KEY = "access_user_account_id";

		private static Map<Session, Integer> peers = Collections.synchronizedMap(new HashMap<Session, Integer>());
		private static final Logger logger = Logger.getLogger("MessageEndpoint");

		@OnMessage
		public String onMessage(@PathParam(PATH_PARAM_KEY) Integer postUserAccountId, Message message) {
				logger.log(Level.INFO, new StringBuilder()
						.append("[onMessage] from userAccount=")
						.append(postUserAccountId)
						.append("  ... broadcasting [")
						.append(message)
						.append("] to ...")
						.toString());
				messageFacade.create(message);
				messageFacade.flush();
				String json = null;
				logger.log(Level.INFO, new StringBuilder()
						.append("[onMessage] created: ")
						.append(json)
						.toString());
				for (Session peer : peers.keySet()) {
						messageAccessFacade.recordAccess(message, peers.get(peer));
//						MessageAccess messageAccess;
//						messageAccess = new MessageAccess();
//						messageAccess.setAccessUserAccountId(new UserAccount(peers.get(peer)));
//						messageAccess.setMessageId(message);
//						messageAccessFacade.create(messageAccess);
//						messageAccessFacade.flush();
						logger.log(Level.INFO, new StringBuilder()
								.append("   --")
								.append(peer.getId())
								.toString());
						peer.getAsyncRemote().sendObject(message);
				}
				return null;
		}

		private void recordAccess(Message message, Integer userId){
						MessageAccess messageAccess;
						messageAccess = new MessageAccess();
						messageAccess.setAccessUserAccountId(new UserAccount(userId));
						messageAccess.setMessageId(message);
						messageAccessFacade.create(messageAccess);
						messageAccessFacade.flush();
		}

		@OnOpen
		public void onOpen(@PathParam(PATH_PARAM_KEY) Integer accessUserAccountId, Session peer) throws EncodeException, IOException{
				logger.log(Level.INFO, new StringBuilder()
						.append("open : userId=")
						.append(accessUserAccountId)
						.append(" | ")
						.append(peer.getId())
						.toString());
				peers.put(peer, accessUserAccountId);
				peer.getAsyncRemote().sendObject("{greeting: 'hello'}");

				List<Message> unaccessedMessages
						= messageAccessFacade.findUnaccessedMessages(accessUserAccountId);
				logger.log(Level.INFO, new StringBuilder()
						.append("[追加配信] to ")
						.append(accessUserAccountId)
						.append(" .... ")
						.toString());
				for(Message msg: unaccessedMessages){
						logger.log(Level.INFO, new StringBuilder()
								.append("     ->")
								.append(msg.getId())
								.toString());
						//peer.getAsyncRemote().sendObject(new MessageDTO(msg));

						//  How To Solve JSON infinite recursion Stackoverflow
						//  see http://goo.gl/jFzCWS
						peer.getAsyncRemote().sendObject(msg);
						messageAccessFacade.recordAccess(msg, peers.get(peer));
						//peer.getBasicRemote().sendObject(new MessageDTO(msg));
				}
		}

		@OnClose
		public void onClose(@PathParam(PATH_PARAM_KEY) Integer accessuserAccountId, Session peer) {
				logger.log(Level.INFO, new StringBuilder()
						.append(" CLOSED | ")
						.append(peer.getId())
						.toString());
				peers.remove(peer);
		}
}
