package com.tpps.application.network.chat;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

public class PacketSendChatCommand extends Packet{

	private static final long serialVersionUID = 1L;
	private String sender;
	private String chatcommand;
	
	
	protected PacketSendChatCommand(String sender, String chatmessage) {
		super(PacketType.SEND_CHAT_COMMAND);
		this.sender = sender;
		this.chatcommand = chatmessage;
	}

	@Override
	public String toString() {
		return "Message from '" + getSender() + "' Command: /" + getChatmessage();
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getChatmessage() {
		return chatcommand;
	}
}
