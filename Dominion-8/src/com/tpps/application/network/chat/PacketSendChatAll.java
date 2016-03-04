package com.tpps.application.network.chat;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

public class PacketSendChatAll extends Packet{
	
	private static final long serialVersionUID = 1L;
	private String chatmessage;
	
	public PacketSendChatAll(String chatmessage) {
		super(PacketType.SEND_CHAT_ALL);
		this.chatmessage  = chatmessage;
	}
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getChatmessage() {
		return chatmessage;
	}
	public void setChatmessage(String chatmessage) {
		this.chatmessage = chatmessage;
	}

}
