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

/**
 *
 * @author msaiki
 */
@ServerEndpoint("/echo")

public class Echo {
  private static final Logger LOG = Logger.getLogger(Echo.class.getName());
  private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

  @OnMessage
  public String onMessage(String message) {
	LOG.log(Level.INFO, "MESSAGE: "+message +" | count(*) peers: " +peers.size());
	for(Session peer: peers){
	  peer.getAsyncRemote().sendText(message);
	  LOG.log(Level.INFO, "SEND : "+peer.getId()+" | " +message);
	}
	return null;
  }

  @OnOpen
  public void onOpen(Session peer){
	peers.add(peer);
	LOG.log(Level.INFO, "OPEN : "+peer.getId());
  }

  @OnClose
  public void onClose(Session peer){
	peers.remove(peer);
	LOG.log(Level.INFO, "CLOSE : "+peer.getId());
  }


  
  
}
