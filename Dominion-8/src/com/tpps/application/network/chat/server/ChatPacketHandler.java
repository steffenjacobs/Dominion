package com.tpps.application.network.chat.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.network.chat.packets.PacketChatHandshake;
import com.tpps.application.network.chat.packets.PacketChatVote;
import com.tpps.application.network.chat.packets.PacketSendChatAll;
import com.tpps.application.network.chat.packets.PacketSendChatCommand;
import com.tpps.application.network.chat.packets.PacketSendChatToClient;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;

public class ChatPacketHandler extends PacketHandler{

	private ChatServer server;
	
	private ArrayList<ChatRoom> chatrooms;
	private GlobalChat global;
	
	
	
	public ChatPacketHandler() {				
		chatrooms = new ArrayList<ChatRoom>();		
	}
	
	
	@Override
	public void handleReceivedPacket(int port, final Packet packet) {
		switch(packet.getType()){
		case SEND_CHAT_ALL:
			PacketSendChatAll castedpacket = (PacketSendChatAll) packet;
			System.out.println("Chat to ALL: " + castedpacket);
			ChatRoom room = this.getSpecificChatRoom(castedpacket.getUsername());
			if(room != null){
				room.sendChatToAllExceptSender(castedpacket);
			}else{
				global.sendChatToAll(castedpacket);
			}
			break;
			
		case SEND_CHAT_TO_CLIENT:
			PacketSendChatToClient castedpacket3 = (PacketSendChatToClient) packet;
			ChatRoom room3 = this.getSpecificChatRoom(castedpacket3.getSender());
			System.out.println("Chat to Client " + castedpacket3);
			if(room3 != null){
				room3.sendChatToChatRoomClient(castedpacket3);
			}else{							
				global.sendChatToClient(castedpacket3);
			}
			break;
		case SEND_CHAT_COMMAND:
			PacketSendChatCommand castedpacket2 = (PacketSendChatCommand) packet;					
			
			ChatRoom room2 =  this.getSpecificChatRoom(castedpacket2.getSender());
			if(room2 != null){
				room2.evaluateCommand(castedpacket2);
			}else{
				global.sendChatCommand(port, castedpacket2);
			}
			break;
		case CHAT_HANDSHAKE:			
			PacketChatHandshake shaked = (PacketChatHandshake) packet;
			String username = shaked.getSender();
			this.global.putUser(username, port);
			System.out.println("handshake with " + username);
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
	
	public void addChatRoom(ArrayList<String> clients){
		ConcurrentHashMap<String, Integer> clientsByUserRoom = new ConcurrentHashMap<String, Integer>();
		for (int j = 0; j < clients.size(); j++) {
			int port = this.global.getClientsByUsername().get(clients.get(j));
			this.global.getClientsByUsername().remove(clients.get(j));
			clientsByUserRoom.put(clients.get(j), port);
		}
		this.chatrooms.add(new ChatRoom(clientsByUserRoom, server));
	}
		
	public boolean deleteChatRoom(int id){		
		for (Iterator<ChatRoom> iterator = chatrooms.iterator(); iterator.hasNext();) {
			ChatRoom chatroom = iterator.next();
			if(chatroom.getId() == id){				
				this.killChatRoom(chatroom);
				return true;
			}
		}
		return false;
	}
	
	public boolean deleteChatRoom(String user){
		ChatRoom chatroom = this.getSpecificChatRoom(user);
		if(chatroom != null){
			this.killChatRoom(chatroom);
			return true;
		}
		return false;
	}
	
	private void killChatRoom(ChatRoom chatroom){
		ConcurrentHashMap<String, Integer> clientsByUsername = chatroom.getClientsByUsername();				
		for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {					
			this.global.putUser(entry.getKey(), entry.getValue());
		}				
		chatrooms.remove(chatroom);
		chatroom = null;
	}
	
	

	public boolean isUserInChatRoom(String user){
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

	public void init(ChatServer server) {
		this.server = server;
		this.global = new GlobalChat(server, this);
	}
	
	public ArrayList<ChatRoom> getChatrooms() {
		return chatrooms;
	}

}
