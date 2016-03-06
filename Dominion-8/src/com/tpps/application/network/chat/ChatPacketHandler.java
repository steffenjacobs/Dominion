package com.tpps.application.network.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.login.SQLHandling.SQLHandler;
import com.tpps.application.network.login.SQLHandling.SQLOperations;

public class ChatPacketHandler extends PacketHandler{

	private ChatServer server;
	private final static String servercommand1 = "help";
	private final static String servercommand2 = "cmd";
	private ConcurrentHashMap<String, Integer> clientsByUsername = new ConcurrentHashMap<String, Integer>();
	private ArrayList<ChatRoom> chatrooms = new ArrayList<ChatRoom>();
	
	@Override
	public void handleReceivedPacket(int port, final Packet packet) {
	//	System.out.println("Server received packet: " + packet);
		switch(packet.getType()){
		case SEND_CHAT_ALL:
			PacketSendChatAll castedpacket = (PacketSendChatAll) packet;
			ChatRoom room = this.testIfPacketGoesToChatRoom(port, castedpacket.getUsername());
			if(room != null){
				room.sendChatToAll();
			}else{
				PacketSendAnswer answer = new PacketSendAnswer(castedpacket.getChatmessage());
				try {
					this.parent.broadcastMessage(port, answer);
				} catch (IOException e1) {			
					e1.printStackTrace();
				}
			}
			break;
		case SEND_CHAT_COMMAND:
			PacketSendChatCommand castedpacket2 = (PacketSendChatCommand) packet;		
			String sender = castedpacket2.getSender();
			String msg = castedpacket2.getChatmessage();
			
			ChatRoom room2 =  this.testIfPacketGoesToChatRoom(port, castedpacket2.getSender());
			if(room2 != null){
				
			}
			//TODO: evaluate commands
//			if(!this.evaluateCommands(castedpacket2.getChatmessage(), sender, port)){
//				PacketSendAnswer answer2 = new PacketSendAnswer("unknown command: " + msg);
//				try {
//					server.sendMessage(port, answer2);
//				} catch (IOException e) {				
//					e.printStackTrace();
//				}
//			}
			break;
		case CHAT_HANDSHAKE:
			PacketChatHandshake shaked = (PacketChatHandshake) packet;
			String username = shaked.getSender();
			clientsByUsername.put(username, port);
			break;
		default:
			System.out.println("sth went wrong with received packet");
			break;
		
		}
	}
	
	private boolean evaluateCommands(String message, String sender, int port){
		String[] split = message.split(" ");
		String command = split[0];
		message = "";
		for (int i = 1; i < split.length; i++) {
			message += split[i] + " ";
		}
		
		switch(command.trim()){
		case servercommand1: //send answer packet back to user, with all comands servercommand1 == /help
			String allcomands = "Commands: \n" + ChatPacketHandler.servercommand1 + "\n" + ChatPacketHandler.servercommand2 + "\n";
			PacketSendAnswer answer = new PacketSendAnswer(allcomands);
			try {
				server.sendMessage(port, answer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		case servercommand2: //TODO: do sth. command
			return true;
		}
		String hostname = "localhost";
		String sqlport = "3306";
		String database = "accountmanager";
		String user = "jojo";
		String password = "password";
		SQLHandler.init(hostname, sqlport, user, password, database);
		SQLHandler.connect();
		String[] nicknames = SQLOperations.showAllNicknames().split("\n");
		for (int i = 0; i < nicknames.length; i++) {
			System.out.println(nicknames[i]);
			if(command.equals(nicknames[i].trim())){
				this.sendMessageToClient(sender, nicknames[i], message, clientsByUsername.get(nicknames[i]));
				return true;
			}
		}
		return false;
	}
	
	private void sendMessageToClient(String sender, String receiver, String msg, int port){
		PacketSendAnswer answer = new PacketSendAnswer(msg);
		try {
			server.sendMessage(port, answer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ChatRoom testIfPacketGoesToChatRoom(int port, String sender){
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
	
	public void addChatRoom(ArrayList<String> clients){
		this.chatrooms.add(new ChatRoom(clients));
	}
	
	public void addChatRoom(String user1, String user2, String user3, String user4){
		this.chatrooms.add(new ChatRoom(user1, user2, user3, user4));
	}

	public ChatServer getServer() {
		return server;
	}

	public void setServer(ChatServer server) {
		this.server = server;
	}

}
