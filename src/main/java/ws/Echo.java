/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import redis.Listener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 *
 * @author msaiki
 */
@ServerEndpoint("/echo")

public class Echo {

	private static final Logger LOG = Logger.getLogger(Echo.class.getName());
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<String> listens = Collections.synchronizedSet(new HashSet<String>());
	private static final String CHANNEL_NAME = "PARTI_1";
	private final Jedis subscriberJedis = new Jedis("localhost", 6380);
	private static JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379, 0);

	@OnMessage
	public String onMessage(String message) {
		Jedis publisherJedis = jedisPool.getResource();
		publisherJedis.publish(CHANNEL_NAME, message);
//`		LOG.log(Level.INFO, "MESSAGE: " + message + " | count(*) peers: " + peers.size());
//`		for (Session peer : peers) {
//`			peer.getAsyncRemote().sendText(message);
//`			LOG.log(Level.INFO, "SEND : " + peer.getId() + " | " + message);
//`		}
		jedisPool.returnResource(publisherJedis);
		return null;
	}

	/**
	 * TODO: SubscriberのonMessageで処理されるがそれをこのクラスでどう受け取るのか
	 * 		とりあえずpeersを渡してみたらうまく動いてる模様
	 * TODO: 通知用に通知してほしいものを追加するActionで新しいThreadでsubscribeするがちゃんと処理されるか
	 * @param peer 
	 */
	@OnOpen
	public void onOpen(Session peer) {
		peers.add(peer);
		LOG.log(Level.INFO, "OPEN : " + peer.getId());
		//subscriberJedis.connect();
		if(listens.add(CHANNEL_NAME)){
			LOG.log(Level.INFO, "ON_OPEN SUBSCRIBE TO : " + CHANNEL_NAME);
			//final Listener listener = new Listener(this);
			final Listener listener = new Listener(peers);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						LOG.log(Level.INFO, "Subscribing to " + CHANNEL_NAME + ". This thread will be blocked.");
						subscriberJedis.subscribe(listener, CHANNEL_NAME);
						LOG.log(Level.INFO, "Subscription ended.");
					} catch (Exception e) {
						LOG.log(Level.SEVERE, "Subscribing failed.", e);
					}
				}
			}).start();
			listener.unsubscribe(CHANNEL_NAME);
		}
	}

	@OnClose
	public void onClose(Session peer) {
		peers.remove(peer);
		LOG.log(Level.INFO, "CLOSE : " + peer.getId());
		if(null != subscriberJedis){
			subscriberJedis.quit();
			//subscriberJedis.close();
			LOG.log(Level.INFO, "CLOSE : JEDIS(SUB)");
		}
	}

}
