/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import redis.clients.jedis.Jedis;

/**
 *
 * @author papa
 */
public class Publisher {
	private static final Logger LOG = Logger.getLogger(Publisher.class.getName());
 
    private final Jedis publisherJedis;
 
    private final String channel;
 
    public Publisher(Jedis publisherJedis, String channel) {
        this.publisherJedis = publisherJedis;
        this.channel = channel;
    }
 
    public void start() {
        LOG.log(Level.INFO, "Type your message (quit for terminate)");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
 
            while (true) {
                String line = reader.readLine();
 
                if (!"quit".equals(line)) {
                    publisherJedis.publish(channel, line);
                } else {
                    break;
                }
            }
 
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "IO failure while reading input, e");
        }
    }
}