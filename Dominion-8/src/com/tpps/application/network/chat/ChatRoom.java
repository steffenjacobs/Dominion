package com.tpps.application.network.chat;

import java.util.ArrayList;

public class ChatRoom {

	private ArrayList<String> clients;
	
	public ChatRoom(ArrayList<String> clients){
		this.clients = clients;
	}
	
	public ChatRoom(String user1, String user2, String user3, String user4){
		clients = new ArrayList<String>();
		clients.add(user1);
		clients.add(user2);
		clients.add(user3);
		clients.add(user4);
	}
	
	public void sendChatToClient(){
		
	}
	
	public void sendChatToAll(){
		
	}
	
	public void evaluateCommand(){
		
	}
	
	public ArrayList<String> getClients() {
		return clients;
	}
	
}
