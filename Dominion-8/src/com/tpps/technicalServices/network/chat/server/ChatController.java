package com.tpps.technicalServices.network.chat.server;

import java.awt.Color;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.chat.packets.PacketChatController;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * This class sends and receives packets that deal with chatroom management
 * 
 * @author jhuhn
 *
 */
public class ChatController extends PacketHandler{
	
	private static Client chatclient;
	private int chatID;	
	private HashMap<String, Color> colorMap;
	
	/**
	 * initializes the chatcontroller class
	 * 
	 * @author jhuhn
	 */
	public ChatController(){	
		try {
			ChatController.chatclient = new Client(new InetSocketAddress(Addresses.getLocalHost(), 1340), this, false);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	

	
	/**
	 * This method sends a packet to create a chatroom with given members
	 * 
	 * @author jhuhn
	 * @param members
	 *            an ArrayList of all members who participate in the chatroom
	 */
	public void createChatRoom(ArrayList<String> members){
		PacketChatController packet = new PacketChatController("createChatroom", members);
		try {
			ChatController.chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * This method sends a packet to create a chatroom with given members
	 * 
	 * @author jhuhn
	 * @param usernames
	 *            String Array of all members who participate in the chatroom
	 */
	public void createChatRoom(String[] usernames){
		ArrayList<String> members = new ArrayList<String>();
		for (int i = 0; i < usernames.length; i++) {
			members.add(usernames[i]);
		}
		this.createChatRoom(members);
	}
	
	/**
	 * This method deletes a chatroom by one given chatID
	 * 
	 * @author jhuhn
	 */
	public void deleteChatroom(){
		PacketChatController packet = new PacketChatController("deleteChatroom", this.chatID);
		try {
			ChatController.chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * This method adds a user to a specific chatroom
	 * 
	 * @author jhuhn
	 * @param nickname
	 *            String representation of the user who should be added to a
	 *            specific chatroom
	 * @param userport
	 *            Integer representation of the users port
	 */
	public void addUserToChatRoom(String nickname, int userport ){
		PacketChatController packet = new PacketChatController("addUser", nickname, userport, this.chatID);
		try {
			ChatController.chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
		}
		System.out.println("sent packet to add a user");
	}

	@Override
	public void handleReceivedPacket(int port, Packet packet) { 
		switch(packet.getType()){
		case CHAT_CONTROLLER:
			PacketChatController packetID = (PacketChatController) packet;
			this.chatID = packetID.getChatroomId();
			this.colorMap = packetID.getColorMap();
			System.out.println("received chatID: " + chatID);
			break;
		default:
			System.out.println("sth went wrong with received packet");
			break;
		}
	}
	
	/**
	 * @author jhuhn
	 * @return chatroom id as an Integer
	 */
	public int getChatID() {
		return chatID;
	}
	
	/**
	 * @author jhuhn
	 * @return a Hashmap with key user and value Color, used for gamelog
	 */
	public HashMap<String, Color> getColorMap() {
		return colorMap;
	}
}