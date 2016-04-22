package com.tpps.technicalServices.network.chat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

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
	private static ChatController instance;
	private int id;	
	
	/**
	 * initializes the chatcontroller class
	 * 
	 * @author jhuhn
	 */
	public void init(){
		if(ChatController.chatclient == null){
			try {
				ChatController.chatclient = new Client(new InetSocketAddress(Addresses.getLocalHost(), 1340), this, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * if the chatcontroller instance is null, it gets initialized
	 * 
	 * @author jhuhn
	 * @return the instance of the chatcontroller
	 */
	public static ChatController getInstance(){
		if(instance == null){
			instance = new ChatController();
			instance.init();
		}
		return instance;
	}
	
	/**
	 * This method sends a packet to create a chatroom with given members
	 * 
	 * @author jhuhn
	 * @param members
	 *            an ArrayList of all members who participate in the chatroom
	 */
	public static void createChatRoom(ArrayList<String> members){
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
		ChatController.createChatRoom(members);
	}
	
	/**
	 * This method deletes a chatroom by one given member
	 * 
	 * @author jhuhn
	 * @param onemember
	 *            String representation of a user who participate in the
	 *            chatroom
	 */
	public void deleteChatroom(String onemember){
		PacketChatController packet = new PacketChatController("deleteChatroom", onemember);
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
		PacketChatController packet = new PacketChatController("addUser", nickname, userport, this.id);
		try {
			ChatController.chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
		}
		System.out.println("sent packet to add a user");
	}

	@Override
	public void handleReceivedPacket(int port, Packet packet) { 
		//TODO: get chatroom id
	}
	
	/**
	 * @author jhuhn
	 * @return chatroom id as an Integer
	 */
	public int getId() {
		return id;
	}
}