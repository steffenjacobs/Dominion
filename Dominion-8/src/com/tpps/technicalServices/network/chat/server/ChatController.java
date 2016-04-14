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
	
	private Client chatclient;
	
	public ChatController() {
		try {
			chatclient = new Client(new InetSocketAddress(Addresses.getLocalHost(), 1340), this, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createChatRoom(ArrayList<String> members){
		PacketChatController packet = new PacketChatController("createChatroom", members);
		try {
			this.chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	public void createChatRoom(String user1, String user2, String user3, String user4){
		ArrayList<String> members = new ArrayList<String>();
		members.add(user1);
		members.add(user2);
		members.add(user3);
		members.add(user4);
		this.createChatRoom(members);
	}
	
	public void deleteChatroom(String onemember){
		PacketChatController packet = new PacketChatController("deleteChatroom", onemember);
		try {
			this.chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}

	@Override
	public void handleReceivedPacket(int port, Packet packet) { }
	
//	public static void main(String[] args) {
//		ChatController troller = new ChatController();
//		System.out.println("I am: the chatcontroller");
//		Scanner scanInput = new Scanner(System.in);
//		String line = null;
//		while(true){
//			line = scanInput.nextLine();
//			if(line.startsWith("create")){
//				String[] split = line.split("\\s+");
//				ArrayList<String> list = new ArrayList<String>();
//				list.add(split[1]);
//				list.add(split[2]);
//				list.add(split[3]);
//				list.add(split[4]);
//				troller.createChatRoom(list);
//				System.out.println("create chatroom");
//			}else if(line.startsWith("del")){
//				String[] split = line.split("\\s+");
//				troller.deleteChatroom(split[1]);
//				System.out.println("delete chatroom");
//			}else{
//				System.out.println("bad command");
//			}
//		}
//	}
}