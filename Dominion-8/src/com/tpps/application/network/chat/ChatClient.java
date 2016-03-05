package com.tpps.application.network.chat;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;

public class ChatClient extends PacketHandler{

	private Client chatclient;
	private String sender;
	
	public ChatClient(String username) {
		this.sender = username;
		try {
			chatclient = new Client(new InetSocketAddress("127.0.0.1", 1340), this, false);
			PacketChatHandshake handshake = new PacketChatHandshake(sender);
			chatclient.sendMessage(handshake);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleReceivedPacket(int port, final Packet packet) {
		switch(packet.getType()){
		case SEND_CHAT_ANSWER:
			PacketSendAnswer answer = (PacketSendAnswer) packet;
			System.out.println(answer.getAnswer());
			break;
		default:System.out.println("sth with answer packet is wrong"); break;
		}
		
	}

	
	public void sendMessage(String chatmessage){
		if(chatmessage.startsWith("/")){
			this.sendCommand(new StringBuffer(chatmessage).deleteCharAt(0).toString());
		}else{
			PacketSendChatAll packet = new PacketSendChatAll(sender, chatmessage);
			try {
				chatclient.sendMessage(packet);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	private void sendCommand(String command){
		PacketSendChatCommand packet = new PacketSendChatCommand(sender, command);
		try {
			chatclient.sendMessage(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String msg1 = "das ist eine nachricht für alle :)";
	//	String msg2 = "/receiver das ist eine nachricht für den receiver";
		String msg2 = "/help";
		ChatClient c1 = new ChatClient("client 1");
		ChatClient c2 = new ChatClient("cleint 2");
		ChatClient c3 = new ChatClient("client 3");
		c1.sendMessage(msg1);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {		
			e.printStackTrace();
		}
		c2.sendMessage(msg2);
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {		
//			e.printStackTrace();
//		}
	//	c3.sendMessage(msg3);
	}
}
