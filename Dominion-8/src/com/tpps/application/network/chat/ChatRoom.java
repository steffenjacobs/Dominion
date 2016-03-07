package com.tpps.application.network.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {

	private ChatServer server;
	private ConcurrentHashMap<String, Integer> clientsByUsername = new ConcurrentHashMap<String, Integer>();
	private int id;
	private static int idcounter = 1;
	
	private final static String servercommand1 = "help";
	private final static String servercommand2 = "show all clients";
	private final static String servercommand3 = "show all ports";
	private final static String servercommand4 = "show all clients by ports";
	
	public ChatRoom(ConcurrentHashMap<String, Integer> clientsByUser, ChatServer server){
		this.clientsByUsername = clientsByUser;
		this.server = server;
		this.id = idcounter++;
	}
	
	public void sendChatToAll(PacketSendChatAll packet){
		String message = packet.getChatmessage();
		String sender = packet.getUsername();
		for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {
		    String nickname = entry.getKey();
		    if(nickname.equals(sender)){
		    	continue;
		    }
		    int port = entry.getValue();
		    PacketSendAnswer answer = new PacketSendAnswer(message);
		    try {
				server.sendMessage(port, answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	
	public void sendChatToClient(PacketSendChatToClient packet){
		String sender = packet.getSender();
		String receiver = packet.getReceiver();
		String message = packet.getMessage();
		
		int port = this.clientsByUsername.get(receiver);
		PacketSendAnswer answer = new PacketSendAnswer("Message from " + sender + ": " + message);
		try {
			server.sendMessage(port, answer);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	
	
	public void evaluateCommand(PacketSendChatCommand packet){
		switch(packet.getChatmessage()){
		case servercommand1: 
			String msg = "Commands: \n/" + servercommand1 + "\n/" + servercommand2 + "\n/" + servercommand3 + "\n/" + servercommand4;
			PacketSendAnswer answer = new PacketSendAnswer(msg);
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			break;
		case servercommand2:
			Enumeration<String> clients = this.clientsByUsername.keys();
			String msg2 = "All connected Clients in this chatroom: \n";
			while(clients.hasMoreElements()){
				msg2 += clients.nextElement() + "\n";
			}
			PacketSendAnswer answer2 = new PacketSendAnswer(msg2);
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer2);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			break;
		case servercommand3:
			Enumeration<Integer> ports = this.clientsByUsername.elements();
			String msg3 = "All connected ports in this chatroom: \n";
			while(ports.hasMoreElements()){
				msg3 += ports.nextElement() + "\n";
			}
			PacketSendAnswer answer3 = new PacketSendAnswer(msg3);
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer3);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			break;
		case servercommand4: 
			Enumeration<Integer> ports2 = this.clientsByUsername.elements();
			Enumeration<String> clients2 = this.clientsByUsername.keys();
			String msg4 = "All connected clients with ports in this chatroom: \n";
			while(ports2.hasMoreElements()){
				msg4 += clients2.nextElement() + "  :  " + ports2.nextElement() + "\n";
			}
			PacketSendAnswer answer4 = new PacketSendAnswer(msg4);
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer4);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			break;
		default:
			PacketSendAnswer answer5 = new PacketSendAnswer("Wrong command: " + packet.getChatmessage());
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer5);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			break;
		}		
	}
	
	
	public ArrayList<String> getClients() {
		ArrayList<String> clientsInChatRoom = new ArrayList<String>();
		Enumeration<String> clients = this.clientsByUsername.keys();
		while(clients.hasMoreElements()){
			clientsInChatRoom.add(clients.nextElement());
		}
		return clientsInChatRoom;
	}
	
	public int getId() {
		return id;
	}
}
