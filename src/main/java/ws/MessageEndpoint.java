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
		value = "/messageEndpoint/{partiId}/{accessUserAccountId}", encoders = {MessageJSONCoder.class}, decoders = {MessageJSONCoder.class}
)
@Stateless // <ーこれがないとCDIが働かない！！
// See
//	http://stackoverflow.com/questions/20872300/java-ee-7-how-to-inject-an-ejb-into-a-websocket-serverendpoint 
public class MessageEndpoint {

	@EJB
	MessageFacade messageFacade;
	@EJB
	MessageAccessFacade messageAccessFacade;

	private static final String PATHPARAM_partiId_KEY = "partiId";
	private static final String PATHPARAM_accessUserAccountId_KEY = "accessUserAccountId";

	private static Map<Integer, Map<Session, Integer>> partiPeers = Collections.synchronizedMap(new HashMap<Integer, Map<Session, Integer>>());
	private static final Logger logger = Logger.getLogger("MessageEndpoint");

	/**
	 * 
	 * 最後にアクセスしてきたクライアントのパスの値が渡ってくるのでメッセージに入っている値を使う
	 * @param partiId
	 * @param accessUserAccountId
	 * @param message
	 * @param senderPeer
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException 
	 */
	@OnMessage
	public String onMessage(
	//		@PathParam(PATHPARAM_partiId_KEY) Integer partiId
	//		,@PathParam(PATHPARAM_accessUserAccountId_KEY) Integer accessUserAccountId
	//		,Message message
			//		@PathParam(PATHPARAM_partiId_KEY) Integer partiId
	//		,@PathParam(PATHPARAM_accessUserAccountId_KEY) Integer accessUserAccountId
	//		,Message message
			Message message
			,Session senderPeer
	) throws ExecutionException, InterruptedException {
		// Parti自体が登録されているか
		if(null == partiPeers.get(message.getPartiId().getId())){
			// Parti自体が登録されていない
			// -> Partiをキーにセッション＝ユーザアカウントを新規登録
			Map<Session, Integer> peerMap = Collections.synchronizedMap(new HashMap<Session, Integer>());
			peerMap.put(senderPeer, message.getPostUserAccountId().getId());
			partiPeers.put(message.getPartiId().getId(), peerMap);
			senderPeer.getAsyncRemote().sendObject("{partiId: '"+message.getPartiId().getId()+"', greeting: 'hello again. you are 1st'}");
			// Partiは登録されている。
			// そこにセッション＝ユーザアカウントは登録されているか
		}else if(null==partiPeers.get(message.getPartiId().getId())){
			// セッション＝ユーザアカウントは登録されていない
			// -> PartiをキーにMapを取得しそこに追加登録
			partiPeers.get(message.getPartiId().getId()).put(senderPeer, message.getPostUserAccountId().getId());
			senderPeer.getAsyncRemote().sendObject("{partiId: '"+message.getPartiId().getId()+"', greeting: 'hello again, there is/are member(s)'}");
		}else{
			// セッション＝ユーザアカウントはすでに登録されている
			// ->何もしない
		}
		// Partiにセッション＝ユーザアカウントが登録されている状態である
		logger.log(Level.INFO, new StringBuilder()
				.append("[onMessage] Message(@)=")
				.append(message.getPartiId().getId())
				.append("  | from Message(userAccount)=")
				.append(message.getPostUserAccountId())
				.append("  ... broadcasting [")
				.append(message)
				.append("] to ...")
				.toString());
		messageFacade.create(message);
		messageFacade.flush();
		for (Session peer : partiPeers.get(message.getPartiId().getId()).keySet()){
			logger.log(Level.INFO, new StringBuilder()
					.append("   --")
					.append(peer.getId())
					.toString());
//			peer.getAsyncRemote().sendObject(message);
			Future future = peer.getAsyncRemote().sendObject(message);
			try {
				if (null == future.get()) {
					recordAccess(message, message.getPostUserAccountId().getId());
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
	public void onOpen(
			@PathParam(PATHPARAM_partiId_KEY) Integer partiId
			,@PathParam(PATHPARAM_accessUserAccountId_KEY) Integer accessUserAccountId
			,Session peer
	) throws EncodeException, IOException, ExecutionException, InterruptedException {
		logger.log(Level.INFO, new StringBuilder()
				.append("open : @=")
				.append(partiId)
				.append(" | userId=")
				.append(accessUserAccountId)
				.append(" | ")
				.append(peer.getId())
				.toString());
		// Parti自体が登録されているか
		if(null == partiPeers.get(partiId)){
			// Parti自体が登録されていない
			// ->Partiをキーにセッション＝ユーザアカウントを新規登録
			Map<Session, Integer> peerMap = Collections.synchronizedMap(new HashMap<Session, Integer>());
			peerMap.put(peer, accessUserAccountId);
			partiPeers.put(partiId, peerMap);
			peer.getAsyncRemote().sendObject("{partiId: '"+partiId+"', greeting: 'you are 1st'}");
		}else{
			// Partiは登録されている
			// onOpenなのでここにセッション＝ユーザアカウントは登録されていないのでそこまでは確認不要
			// ->PartiをキーにMapを取得し、そこにセッション＝ユーザアカウントを追加
			partiPeers.get(partiId).put(peer, accessUserAccountId);
			peer.getAsyncRemote().sendObject("{partiId: '"+partiId+"', greeting: 'there is/are member(s)'}");
		}
		// Partiにセッション＝ユーザアカウントが登録されている状態である

		List<Message> unaccessedMessages
				= messageFacade.findNotAccessedMessages(partiId, accessUserAccountId);
		if (!unaccessedMessages.isEmpty()) {
			logger.log(Level.INFO, new StringBuilder()
					.append("[追加配信] ")
					.append(unaccessedMessages.size())
					.append(" 件 ")
					.append(" @=")
					.append(partiId)
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
					//recordAccess(msg, peers.get(peer));
					recordAccess(msg, accessUserAccountId);
				}
			} catch (ExecutionException ee) {
				//peers.remove(peer);
				ee.printStackTrace();
				throw ee;
			}
		}
	}

	@OnClose
	public void onClose(
			@PathParam(PATHPARAM_partiId_KEY) Integer partiId
			,@PathParam(PATHPARAM_accessUserAccountId_KEY) Integer accessUserAccountId
			,Session peer){
		// TODO: PathParamから取得しているので間違っているのでは？
		logger.log(Level.INFO, new StringBuilder()
				.append(" CLOSED : ")
				.append(" @=")
				.append(partiId)
				.append(" | userId=")
				.append(accessUserAccountId)
				.append(" | ")
				.append(peer.getId())
				.toString());
		partiPeers.get(partiId).remove(peer);
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
