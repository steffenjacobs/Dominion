package com.tpps.application.network.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.login.SQLHandling.SQLHandler;
import com.tpps.application.network.login.SQLHandling.SQLOperations;

public class ChatPacketHandler extends PacketHandler{

	private ChatServer server;
	private final static String servercommand1 = "help";
	private final static String servercommand2 = "show all clients";
	private final static String servercommand3 = "show all ports";
	private final static String servercommand4 = "show all clients by ports";
	
	private ConcurrentHashMap<String, Integer> clientsByUsername = new ConcurrentHashMap<String, Integer>();
	private ArrayList<ChatRoom> chatrooms = new ArrayList<ChatRoom>();
	
	@Override
	public void handleReceivedPacket(int port, final Packet packet) {
	//	System.out.println("Server received packet: " + packet);
		switch(packet.getType()){
		case SEND_CHAT_ALL:
			PacketSendChatAll castedpacket = (PacketSendChatAll) packet;
			System.out.println("Chat to ALL: " + castedpacket);
			ChatRoom room = this.getSpecificChatRoom(castedpacket.getUsername());
			if(room != null){
				room.sendChatToAll(castedpacket);
			}else{
				PacketSendAnswer answer = new PacketSendAnswer(castedpacket.getChatmessage());
				for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {
				    String nickname = entry.getKey();
				    if(nickname.equals(castedpacket.getUsername())){
				    	continue;
				    }				    
				    if(!this.isUserInChatRoom(nickname)){
				    	try {
							this.server.sendMessage(entry.getValue(), answer);
						} catch (IOException e) {						
							e.printStackTrace();
							continue;
						}
				    }
				}
			}
			break;
			
		case SEND_CHAT_TO_CLIENT:
			PacketSendChatToClient castedpacket3 = (PacketSendChatToClient) packet;
			ChatRoom room3 = this.getSpecificChatRoom(castedpacket3.getSender());
			System.out.println("Chat to Client " + castedpacket3);
			if(room3 != null){
				room3.sendChatToClient(castedpacket3);
			}else{							
				String hostname = "localhost";
				String portsql = "3306";
				String database = "accountmanager";
				String user = "jojo";
				String password = "password";
				SQLHandler.init(hostname, portsql, user, password, database);
				SQLHandler.connect();
				String[] nicknames = SQLOperations.showAllNicknames().split("\n");
				for (int i = 0; i < nicknames.length; i++) {					
					if(castedpacket3.getReceiver().equals(nicknames[i].trim())){
						this.sendMessageToClient(castedpacket3.getSender(), nicknames[i], castedpacket3.getMessage(), clientsByUsername.get(nicknames[i]));
					}
				}	
			}
			break;
		case SEND_CHAT_COMMAND:
			PacketSendChatCommand castedpacket2 = (PacketSendChatCommand) packet;		
			String msg = castedpacket2.getChatmessage();
			System.out.println("Chat Command: " + castedpacket2);
			
			ChatRoom room2 =  this.getSpecificChatRoom(castedpacket2.getSender());
			if(room2 != null){
				room2.evaluateCommand(castedpacket2);
			}else{
				if(!this.evaluateCommands(castedpacket2.getChatmessage(), castedpacket2.getSender(), port)){
					PacketSendAnswer answer2 = new PacketSendAnswer("unknown command: " + msg);
					try {
						server.sendMessage(port, answer2);
					} catch (IOException e) {				
						e.printStackTrace();
					}
				}
			}
			break;
		case CHAT_HANDSHAKE:
			System.out.println("handshake");
			PacketChatHandshake shaked = (PacketChatHandshake) packet;
			String username = shaked.getSender();
			clientsByUsername.put(username, port);
			break;
		case SEND_CHAT_VOTE:
			PacketChatVote castedpacket5 = (PacketChatVote) packet;
			ChatRoom room5 = this.getSpecificChatRoom(castedpacket5.getSender());
			if(room5 != null){
				room5.handleVote(castedpacket5);
			}
			break;
		default:
			System.out.println("sth went wrong with received packet");
			break;
		
		}
	}
	
	private boolean evaluateCommands(String command, String sender, int port){
	
		switch(command.trim()){
		case servercommand1: //send answer packet back to user, with all comands servercommand1 == /help
			String allcomands = "Commands: \n/" + ChatPacketHandler.servercommand1 + "\n/" + ChatPacketHandler.servercommand2 + "\n/"
			+ ChatPacketHandler.servercommand3 + "\n/" + ChatPacketHandler.servercommand4;
			PacketSendAnswer answer = new PacketSendAnswer(allcomands);
			try {
				server.sendMessage(port, answer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		case servercommand2: //show all clients
			StringBuffer buf = new StringBuffer("All connected clients: \n");
			Enumeration<String> clients = this.clientsByUsername.keys();
			while (clients.hasMoreElements()) {
				String user = clients.nextElement();
				if(!this.isUserInChatRoom(user)){
					buf.append(user + "\n");
				}								
			}
			PacketSendAnswer answer2 = new PacketSendAnswer(buf.toString());
			try {
				server.sendMessage(port, answer2);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			return true;
		case servercommand3: //show all ports
			StringBuffer buf2 = new StringBuffer("All connected ports: \n");
			Enumeration<Integer> ports = this.clientsByUsername.elements();
			Enumeration<String> clients2 = this.clientsByUsername.keys();
			while (ports.hasMoreElements()) {
				int port2 = ports.nextElement();
				if(!this.isUserInChatRoom(clients2.nextElement())){
					buf2.append(port2 + "\n");
				}							
			}
			PacketSendAnswer answer3 = new PacketSendAnswer(buf2.toString());
			try {
				server.sendMessage(port, answer3);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			return true;
		case servercommand4://show all clients by ports
			StringBuffer buf3 = new StringBuffer("<client> : <port> \n");
			Enumeration<String> clients3 = this.clientsByUsername.keys();
			Enumeration<Integer> ports3 = this.clientsByUsername.elements();
			while (clients3.hasMoreElements()) {
				String user = clients3.nextElement();
				int port3 = ports3.nextElement();
				if(!this.isUserInChatRoom(user)){
					buf3.append(user + " : " + port3 + "\n");
				}				
			}
			PacketSendAnswer answer4 = new PacketSendAnswer(buf3.toString());
			try {
				server.sendMessage(port, answer4);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			return true;			
		}
		return false;
	}
	
	private void sendMessageToClient(String sender, String receiver, String msg, int port){
		PacketSendAnswer answer = new PacketSendAnswer("Message from " + sender + ": " + msg);
		try {
			server.sendMessage(port, answer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ChatRoom getSpecificChatRoom(String sender){
		Iterator<ChatRoom> chatiter = this.chatrooms.iterator();
		while(chatiter.hasNext()){
			ChatRoom room = chatiter.next();
			Iterator<String>  clientsiter = room.getClients().iterator();			
			while(clientsiter.hasNext()){
				if(sender.equals(clientsiter.next())){
					return room;
				}
			}
		}
		return null;
	}
	
	public void createChatRoom(String user1, String user2, String user3, String user4){
		ArrayList<String> clients = new ArrayList<String>();
		clients.add(user1);
		clients.add(user2);
		clients.add(user3);
		clients.add(user4);
		this.addChatRoom(clients);
	}
	
	public boolean deleteChatRoom(int id){
		for (Iterator<ChatRoom> iterator = chatrooms.iterator(); iterator.hasNext();) {
			ChatRoom chatroom = iterator.next();
			if(chatroom.getId() == id){
				chatrooms.remove(chatroom);
				chatroom = null;
				return true;
			}
		}
		return false;
	}
	
	public boolean deleteChatRoom(String user){
		ChatRoom room = this.getSpecificChatRoom(user);
		if(room != null){
			this.chatrooms.remove(room);
			room = null;
			return true;
		}
		return false;
	}
	
	public void addChatRoom(ArrayList<String> clients){
		ConcurrentHashMap<String, Integer> clientsByUserRoom = new ConcurrentHashMap<String, Integer>();
		for (int j = 0; j < clients.size(); j++) {
			int port = this.clientsByUsername.get(clients.get(j));
			clientsByUserRoom.put(clients.get(j), port);
		}
		this.chatrooms.add(new ChatRoom(clientsByUserRoom, server));
	}
	
	private boolean isUserInChatRoom(String user){
		Iterator<ChatRoom> chatIter = this.chatrooms.iterator();
		while(chatIter.hasNext()){
			ChatRoom temp = chatIter.next();
			Iterator<String> clientsIter = temp.getClients().iterator();
			while(clientsIter.hasNext()){
				if(user.equals(clientsIter.next())){
					return true;
				}
			}
		}
		return false;
	}

	public ChatServer getServer() {
		return server;
	}

	public void setServer(ChatServer server) {
		this.server = server;
	}
	
	public ArrayList<ChatRoom> getChatrooms() {
		return chatrooms;
	}

}
