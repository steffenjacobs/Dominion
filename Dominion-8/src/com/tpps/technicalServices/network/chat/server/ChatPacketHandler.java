package com.tpps.technicalServices.network.chat.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.technicalServices.network.chat.packets.PacketChatController;
import com.tpps.technicalServices.network.chat.packets.PacketChatHandshake;
import com.tpps.technicalServices.network.chat.packets.PacketChatVote;
import com.tpps.technicalServices.network.chat.packets.PacketSendAnswer;
import com.tpps.technicalServices.network.chat.packets.PacketSendChatAll;
import com.tpps.technicalServices.network.chat.packets.PacketSendChatCommand;
import com.tpps.technicalServices.network.chat.packets.PacketSendChatToClient;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.events.NetworkListener;
import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * This class is used to control the received packets from the server
 * Mainly this class is responsible for passing the packets to the global chat or to one specific chatroom
 * @author jhuhn - Johannes Huhn
 */
public class ChatPacketHandler extends PacketHandler{

	private ChatServer server;
	private ArrayList<ChatRoom> chatrooms;
	private GlobalChat global;
	
	/**
	 * initializes the chathandler object, respectively the list of chatrooms
	 * @author jhuhn - Johannes Huhn
	 */
	public ChatPacketHandler() {				
		chatrooms = new ArrayList<ChatRoom>();		
	}
	
	/**
	 * This method is called when the chatserver receives a packet from a client.
	 * It casts the packet to the right class and parses information out of the packet.
	 * With these information (sender, message, receiver) the method finds out 
	 * if the packet passes to the gloabl chat or to one specific chatroom
	 * @author jhuhn - Johannes Huhn
	 */
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
				System.out.println("chatcommand in chatroom: " + room2.getId());
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
		case CHAT_CONTROLLER:
			PacketChatController castedpacket6 = (PacketChatController) packet;
			this.evaluateChatController(castedpacket6);
			break;
		default:
			System.out.println("sth went wrong with received packet");
			break;
		
		}
	}
	
	private void evaluateChatController(PacketChatController packet){
		switch(packet.getCommand()){
		case "createChatroom":
			this.addChatRoom(packet.getMembers());
			break;
		case "deleteChatroom":
			this.deleteChatRoom(packet.getMemberOfChatRoom());
			break;
		}
	}
	
	/**
	 * This method finds out the chatroom where the sender belongs to
	 * @author jhuhn - Johannes Huhn
	 * @param sender a String representation of the user, that sent the packet
	 * @return one specific chatroom object where the sender is included, if the sender isn't part of a chatroom the chatroom returns null
	 */
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
	
	/**
	 * This method is able to create a chatroom for 4 user.
	 * @author jhuhn - Johannes Huhn
	 * @param user1 a String representation of one nickname
	 * @param user2 a String representation of one nickname
	 * @param user3 a String representation of one nickname
	 * @param user4 a String representation of one nickname
	 */
	public void createChatRoom(String user1, String user2, String user3, String user4){
		ArrayList<String> clients = new ArrayList<String>();
		clients.add(user1);
		clients.add(user2);
		clients.add(user3);
		clients.add(user4);
		this.addChatRoom(clients);
	}
	
	/**
	 * This method is able to create a chatroom.
	 * @author jhuhn - Johannes Huhn
	 * @param clients an arraylist of Strings which includes nicknames that should be all together in one chatroom
	 */
	public void addChatRoom(ArrayList<String> clients){
		ConcurrentHashMap<String, Integer> clientsByUserRoom = new ConcurrentHashMap<String, Integer>();
		for (int j = 0; j < clients.size(); j++) {
			int port = this.global.getClientsByUsername().get(clients.get(j));
			this.global.getClientsByUsername().remove(clients.get(j));
			clientsByUserRoom.put(clients.get(j), port);
		}
		this.chatrooms.add(new ChatRoom(clientsByUserRoom, server, this));
	}
		
	/**
	 * This method deletes a chatroom by an id
	 * @author jhuhn - Johannes Huhn
	 * @param id an Integer which identifies the chatroom
	 * @return true if the chatroom deleted successfully, false else
	 */
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
	
	/**
	 * This method deletes a chatroom by one member
	 * @author jhuhn - Johannes Huhn
	 * @param user a String representation of a user that is included in one chatroom
	 * @return true if the chatroom deleted successfully, false else 
	 */
	public boolean deleteChatRoom(String user){
		ChatRoom chatroom = this.getSpecificChatRoom(user);
		if(chatroom != null){
			this.killChatRoom(chatroom);
			return true;
		}
		return false;
	}
	
	/**
	 * This method removes one chatroom object from the chatroom list and puts all members in the global chat
	 * @author jhuhn - Johannes Huhn
	 * @param chatroom the chatroom object to delete
	 */
	private void killChatRoom(ChatRoom chatroom){
		ConcurrentHashMap<String, Integer> clientsByUsername = chatroom.getClientsByUsername();				
		for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {					
			this.global.putUser(entry.getKey(), entry.getValue());
		}				
		chatrooms.remove(chatroom);
		chatroom = null;
	}
	
	
	/**
	 * This method is able to find out if a user is in a chatroom or not
	 * @author jhuhn - Johannes Huhn
	 * @param user a String representation of the nickname
	 * @return true if the user is in a chatroom, false else
	 */
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
	
	/**
	 * gets the server object
	 * @author jhuhn - Johannes Huhn
	 * @return the server object
	 */
	public ChatServer getServer() {
		return server;
	}

	/**
	 * initializes the server and the globalchat
	 * @author jhuhn - Johannes Huhn
	 * @param server
	 */
	public void init(ChatServer server) {
		this.server = server;
		this.global = new GlobalChat(server, this);
		System.out.println(ChatPacketHandler.this.global);
		this.server.getListenerManager().registerListener(new Listener());
	}
	
	/**
	 * gets all chatrooms
	 * @author jhuhn - Johannes Huhn
	 * @return an arraylist of all chatrooms
	 */
	public ArrayList<ChatRoom> getChatrooms() {
		return chatrooms;
	}
	
	/**
	 * This method moves the kicked player from the old chatroom to the global chat
	 * @param usertogetkicked
	 */
	public void kickPlayer(String usertogetkicked){
		ChatRoom chatroom = this.getSpecificChatRoom(usertogetkicked);
		int port = chatroom.getClientsByUsername().get(usertogetkicked);
		chatroom.getClientsByUsername().remove(usertogetkicked, port);
		
		this.global.getClientsByUsername().put(usertogetkicked, port);
		PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "[BOT]: You joined the global chat \n");
		try {
			this.server.sendMessage(port, answer);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	private class Listener implements NetworkListener{

		@Override
		public void onClientConnect(int port) { }

		@Override
		public void onClientDisconnect(int port) {
			System.out.println(ChatPacketHandler.this.global);
			if(!this.kickUserFromGlobalChat(port)){
				this.kickUserFromChatRoom(port);
			}
		}
		
		private boolean kickUserFromChatRoom(int port){
			Iterator<ChatRoom> chatIter = ChatPacketHandler.this.chatrooms.iterator();
			while(chatIter.hasNext()){
				ChatRoom temp = chatIter.next();
				ConcurrentHashMap<String, Integer> clientsByUsername = temp.getClientsByUsername();
				for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {
					if(port == entry.getValue()){
						temp.removeUser(entry.getKey());
						System.out.println("kicked " + entry.getKey() + " from chatroom");
						System.out.println("hashmap size: " + temp.getClientsByUsername().size());
						if(temp.getClientsByUsername().size() == 0){
							ChatPacketHandler.this.deleteChatRoom(entry.getKey());
							System.out.println("Deleted chatroom with user: " + entry.getKey());
						}
						return true;
					}
				}
			}
			return false;
		}
		
		private boolean kickUserFromGlobalChat(int port){
		//	ConcurrentHashMap<String, Integer> clientsByUsername = ChatPacketHandler.this.global.getClientsByUsername();
			for (Entry<String, Integer> entry : ChatPacketHandler.this.global.getClientsByUsername().entrySet()) {
				if(entry.getValue() == port){
					ChatPacketHandler.this.global.removeUser(entry.getKey());
					System.out.println("kicked " + entry.getKey() + " from globalchat");
					return true;
				}
			}
			return false;
		}
	}
	
}
