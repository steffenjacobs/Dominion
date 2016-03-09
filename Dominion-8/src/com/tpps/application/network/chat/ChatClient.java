package com.tpps.application.network.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

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
		if(chatmessage.startsWith("/votekick ")){
			this.sendCommand(new StringBuffer(chatmessage).deleteCharAt(0).toString());
		}
		else if(chatmessage.startsWith("/")){
			this.sendCommand(new StringBuffer(chatmessage).deleteCharAt(0).toString());
		}
		else if(chatmessage.startsWith("@")){
			this.sendMessageToClient(new StringBuffer(chatmessage).deleteCharAt(0).toString());
		}else{
			PacketSendChatAll packet = new PacketSendChatAll(sender, chatmessage);
			try {
				chatclient.sendMessage(packet);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	private void sendMessageToClient(String message){
		String[] split = message.split(" ");
		String receiver = split[0];
		message = "";
		for (int i = 1; i < split.length; i++) {
			message += split[i] + " ";
		}
		
		PacketSendChatToClient packet = new PacketSendChatToClient(this.sender, message, receiver);
		try {
			chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
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
		ChatClient c = new ChatClient("nico");
		System.out.println("I am: " + c.sender);
		Scanner scanInput = new Scanner(System.in);
		String line = null;
		while(true){
			line = scanInput.nextLine();
			c.sendMessage(line);
		}
	}
}
