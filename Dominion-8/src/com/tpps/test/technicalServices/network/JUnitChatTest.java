package com.tpps.test.technicalServices.network;

import static org.junit.Assert.*;

import org.junit.Test;

import com.tpps.technicalServices.network.chat.client.ChatClient;

/**
 * tests the chatlogic
 * 
 * @author jhuhn
 *
 */
public class JUnitChatTest {

	private ChatClient client1;
	private ChatClient client2;
	private ChatClient client3;
	private ChatClient client4;
	
	/**
	 * sets up the chatclients
	 * 
	 * @author jhuhn
	 */
	public void setup(){
		this.client1 = new ChatClient("client1");
		this.client2 = new ChatClient("client2");
		this.client3 = new ChatClient("client3");
		this.client4 = new ChatClient("client4");
	}
	
	/**
	 * tests the reveiced chatmessages
	 * 
	 * @author jhuhn
	 */
	@Test
	public void test() {
		this.setup();
		client1.sendMessage("testmessage");
		try {
			//wait for receiving packets
			Thread.sleep(5000);										
		} catch (InterruptedException e) {		
			e.printStackTrace();
		}
		assertEquals("testmessage", client2.getLastmessage());
		assertEquals("testmessage", client3.getLastmessage());
		assertEquals("testmessage", client4.getLastmessage());
		
		client1.sendMessage("@client2 this is a pm");
		client1.sendMessage("@client3 this is a pm");
		client1.sendMessage("@client4 this is a pm");
		
		try {
			//wait for receiving packets
			Thread.sleep(5000);
		} catch (InterruptedException e) {		
			e.printStackTrace();
		}
		
		assertEquals("this is a pm", client2.getLastmessage());
		assertEquals("this is a pm", client3.getLastmessage());
		assertEquals("this is a pm", client4.getLastmessage());
	}

}
