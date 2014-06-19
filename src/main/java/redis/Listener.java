/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package redis;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;
import redis.clients.jedis.JedisPubSub;
import ws.Echo;

/**
 *
 * @author papa
 */
public class Listener extends JedisPubSub {
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
	private Echo caller;
	private static final Logger LOG = Logger.getLogger(Listener.class.getName());

	public Listener(Set<Session> ss) {
		peers = ss;
	}
	public Listener(Echo echo) {
		caller = echo;
	}

  @Override
    public void onMessage(String channel, String message) {
        LOG.log(Level.INFO, "Message received. Channel: "+channel +", Msg: "+message);
//		caller.onMessage(message);
		for (Session peer : peers) {
			peer.getAsyncRemote().sendText(message);
			LOG.log(Level.INFO, "SEND : " + peer.getId() + " | " + message);
		}
    }
 
    @Override
    public void onPMessage(String pattern, String channel, String message) {
 
    }
 
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
 
    }
 
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
 
    }
 
    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
 
    }
 
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
 
    }
	
	
}
