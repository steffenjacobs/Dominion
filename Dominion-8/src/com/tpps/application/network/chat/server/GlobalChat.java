package com.tpps.application.network.chat.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.network.chat.packets.PacketSendAnswer;
import com.tpps.application.network.chat.packets.PacketSendChatAll;
import com.tpps.application.network.chat.packets.PacketSendChatCommand;
import com.tpps.application.network.chat.packets.PacketSendChatToClient;
import com.tpps.application.network.login.SQLHandling.SQLHandler;
import com.tpps.application.network.login.SQLHandling.SQLOperations;

public class GlobalChat {
	
	private final static String servercommand1 = "help";
	private final static String servercommand2 = "show all clients";
	private final static String servercommand3 = "show all ports";
	private final static String servercommand4 = "show all clients by ports";

	private ChatServer server;
	private ConcurrentHashMap<String, Integer> clientsByUsername = new ConcurrentHashMap<String, Integer>();
	private ChatPacketHandler chathandler;
	
	public GlobalChat (ChatServer server, ChatPacketHandler chathandler){
		this.server =  server;
		this.chathandler = chathandler;
	}
	
	public void sendChatToAll(PacketSendChatAll packet){
		PacketSendAnswer answer = new PacketSendAnswer(packet.getChatmessage());
		for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {
		    String nickname = entry.getKey();
		    if(nickname.equals(packet.getUsername())){
		    	continue;
		    }				    
		    if(!chathandler.isUserInChatRoom(nickname)){
		    	try {
					this.server.sendMessage(entry.getValue(), answer);
				} catch (IOException e) {						
					e.printStackTrace();
					continue;
				}
		    }
		}
	}
	
	public void sendChatToClient(PacketSendChatToClient packet){
		String hostname = "localhost";
		String portsql = "3306";
		String database = "accountmanager";
		String user = "jojo";
		String password = "password";
		SQLHandler.init(hostname, portsql, user, password, database);
		SQLHandler.connect();
		String[] nicknames = SQLOperations.showAllNicknames().split("\n");
		for (int i = 0; i < nicknames.length; i++) {					
			if(packet.getReceiver().equals(nicknames[i].trim())){
				this.sendMessageToClient(packet.getSender(), nicknames[i], packet.getMessage(), clientsByUsername.get(nicknames[i]));
			}
		}	
	}
	
	public void sendChatCommand(int port, PacketSendChatCommand packet){
		String msg = packet.getChatmessage();
		System.out.println("Chat Command: " + packet);
		
		if(!this.evaluateCommands(packet.getChatmessage(), packet.getSender(), port)){
			PacketSendAnswer answer2 = new PacketSendAnswer("unknown command: " + msg);
			try {
				server.sendMessage(port, answer2);
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
	}
	
	
	
	private void sendMessageToClient(String sender, String receiver, String msg, int port){
		PacketSendAnswer answer = new PacketSendAnswer("Message from " + sender + ": " + msg);
		try {
			server.sendMessage(port, answer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean evaluateCommands(String command, String sender, int port){
		
		switch(command.trim()){
		case servercommand1: //send answer packet back to user, with all comands servercommand1 == /help
			String allcomands = "Commands: \n/" + servercommand1 + "\n/" + servercommand2 + "\n/"
			+ servercommand3 + "\n/" + servercommand4;
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
				if(!this.chathandler.isUserInChatRoom(user)){
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
				if(!this.chathandler.isUserInChatRoom(clients2.nextElement())){
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
				if(!this.chathandler.isUserInChatRoom(user)){
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
	
	public void putUser(String name, int port){
		this.clientsByUsername.putIfAbsent(name, port);
	}
	
	public ConcurrentHashMap<String, Integer> getClientsByUsername() {
		return clientsByUsername;
	}
}
