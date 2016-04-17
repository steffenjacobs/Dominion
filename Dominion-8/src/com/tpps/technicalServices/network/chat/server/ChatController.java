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
	
	public ChatController() {
		try {
			ChatController.chatclient = new Client(new InetSocketAddress(Addresses.getAllInterfaces(), 1340), this, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createChatRoom(ArrayList<String> members){
		PacketChatController packet = new PacketChatController("createChatroom", members);
		try {
			ChatController.chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	public static void createChatRoom(String[] usernames){
		ArrayList<String> members = new ArrayList<String>();
		for (int i = 0; i < usernames.length; i++) {
			members.add(usernames[i]);
		}
		ChatController.createChatRoom(members);
	}
	
	public static void deleteChatroom(String onemember){
		PacketChatController packet = new PacketChatController("deleteChatroom", onemember);
		try {
			ChatController.chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}

	@Override
	public void handleReceivedPacket(int port, Packet packet) { }
	
}