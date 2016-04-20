package com.tpps.technicalServices.network.chat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.chat.packets.PacketChatController;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;

public class ChatController extends PacketHandler{
	
	private static Client chatclient;
	private static ChatController instance;
	private int id;
	
	public ChatController() { }
	
	public void init(){
		if(ChatController.chatclient == null){
			try {
				ChatController.chatclient = new Client(new InetSocketAddress(Addresses.getLocalHost(), 1340), this, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static ChatController getInstance(){
		if(instance == null){
			instance = new ChatController();
			instance.init();
		}
		return instance;
	}
	
	public static void createChatRoom(ArrayList<String> members){
		PacketChatController packet = new PacketChatController("createChatroom", members);
		try {
			ChatController.chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	public void createChatRoom(String[] usernames){
		ArrayList<String> members = new ArrayList<String>();
		for (int i = 0; i < usernames.length; i++) {
			members.add(usernames[i]);
		}
		ChatController.createChatRoom(members);
	}
	
	public void deleteChatroom(String onemember){
		PacketChatController packet = new PacketChatController("deleteChatroom", onemember);
		try {
			ChatController.chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
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
	
	public int getId() {
		return id;
	}
}