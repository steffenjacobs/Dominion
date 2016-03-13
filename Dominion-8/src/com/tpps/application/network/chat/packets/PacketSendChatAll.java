package com.tpps.application.network.chat.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

public class PacketSendChatAll extends Packet{
	
	private static final long serialVersionUID = 1L;
	private final String chatmessage;
	private final String username;
	
	public PacketSendChatAll(String username, String chatmessage) {
		super(PacketType.SEND_CHAT_ALL);
		this.chatmessage  = chatmessage;
		this.username = username;
	}	

	@Override
	public String toString() {
		return "Sender: " + getUsername() + " Message to all: " + getChatmessage();
	}
	
	public String getChatmessage() {
		return chatmessage;
	}
	
	public String getUsername() {
		return username;
	}
}
