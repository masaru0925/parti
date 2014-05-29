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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
		// TODO: partiIDも付加してハンドリング
		// TODO: partiIDも使って抽出するならPartiIDを非正規化して持たないとダメか
		value = "/messageEndpoint/{access_user_account_id}", encoders = {MessageJSONCoder.class}, decoders = {MessageJSONCoder.class}
)
@Stateless // <ーこれがないとCDIが働かない！！
// See
//	http://stackoverflow.com/questions/20872300/java-ee-7-how-to-inject-an-ejb-into-a-websocket-serverendpoint 
public class MessageEndpoint {

	@EJB
	MessageFacade messageFacade;
	@EJB
	MessageAccessFacade messageAccessFacade;

	private static final String PATH_PARAM_KEY = "access_user_account_id";

	private static Map<Session, Integer> peers = Collections.synchronizedMap(new HashMap<Session, Integer>());
	private static final Logger logger = Logger.getLogger("MessageEndpoint");

	@OnMessage
	public String onMessage(@PathParam(PATH_PARAM_KEY) Integer postUserAccountId, Message message) throws ExecutionException, InterruptedException {
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
			logger.log(Level.INFO, new StringBuilder()
					.append("   --")
					.append(peer.getId())
					.toString());
			//peer.getAsyncRemote().sendObject(message);
			Future future = peer.getAsyncRemote().sendObject(message);
			try {
				if (null == future.get()) {
					recordAccess(message, peers.get(peer));
				}
			} catch (ExecutionException ee) {
				//peers.remove(peer);
				ee.printStackTrace();
				throw ee;
			}
		}
		return null;
	}

	@OnOpen
	public void onOpen(@PathParam(PATH_PARAM_KEY) Integer accessUserAccountId, Session peer) throws EncodeException, IOException, ExecutionException, InterruptedException {
		logger.log(Level.INFO, new StringBuilder()
				.append("open : userId=")
				.append(accessUserAccountId)
				.append(" | ")
				.append(peer.getId())
				.toString());
		peers.put(peer, accessUserAccountId);
		peer.getAsyncRemote().sendObject("{greeting: 'hello'}");

		List<Message> unaccessedMessages
				= messageFacade.findNotAccessedMessages(accessUserAccountId);
		if (!unaccessedMessages.isEmpty()) {
			logger.log(Level.INFO, new StringBuilder()
					.append("[追加配信] ")
					.append(unaccessedMessages.size())
					.append(" 件 ")
					.append(" to ")
					.append(accessUserAccountId)
					.append(" .... ")
					.toString());
		}
		for (Message msg : unaccessedMessages) {
			logger.log(Level.INFO, new StringBuilder()
					.append("     ->")
					.append(msg.getId())
					.toString());
			Future future = peer.getAsyncRemote().sendObject(msg);
			try {
				if (null == future.get()) {
					recordAccess(msg, peers.get(peer));
				}
			} catch (ExecutionException ee) {
				//peers.remove(peer);
				ee.printStackTrace();
				throw ee;
			}
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

	/**
	 * 同じ処理を2回別々に実施するのでとりあえずメソッド化 INSERTするだけの簡単な処理なのでわざわざFacadeに移すようなものでもない。
	 *
	 * @param message
	 * @param userId
	 */
	public void recordAccess(Message message, Integer userId) {
		MessageAccess messageAccess = new MessageAccess();

		messageAccess.setAccessUserAccountId(new UserAccount(userId));
		messageAccess.setMessageId(message);

		messageAccessFacade.create(messageAccess);
		messageAccessFacade.flush();
	}
}
