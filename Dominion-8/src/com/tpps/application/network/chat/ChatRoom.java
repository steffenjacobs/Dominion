package com.tpps.application.network.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {

	private ChatServer server;
	private ConcurrentHashMap<String, Integer> clientsByUsername = new ConcurrentHashMap<String, Integer>();
	
	private final static String servercommand1 = "help";
	private final static String servercommand2 = "show all clients";
	private final static String servercommand3 = "show all ports";
	private final static String servercommand4 = "show all clients by ports";
	
	public ChatRoom(ArrayList<String> clients, ArrayList<Integer> ports, ChatServer server){
		Iterator<String> clientsIter = clients.iterator();
		Iterator<Integer> portsIter = ports.iterator();
		while (clientsIter.hasNext()) {
			clientsByUsername.put(clientsIter.next(), portsIter.next());			
		}
		this.server = server;
	}
	
	public ChatRoom(ConcurrentHashMap<String, Integer> clientsByUser, ChatServer server){
		this.clientsByUsername = clientsByUser;
		this.server = server;
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
		
	}
	
	
	public ArrayList<String> getClients() {
		ArrayList<String> clientsInChatRoom = new ArrayList<String>();
		Enumeration<String> clients = this.clientsByUsername.keys();
		while(clients.hasMoreElements()){
			clientsInChatRoom.add(clients.nextElement());
		}
		return clientsInChatRoom;
	}
	
}
