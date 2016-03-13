package com.tpps.application.network.chat.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

public class PacketChatHandshake extends Packet{
	
	private static final long serialVersionUID = 1L;
	private String sender;
	
	public PacketChatHandshake(String sender) {
		super(PacketType.CHAT_HANDSHAKE);
		this.sender = sender;
	}	

	@Override
	public String toString() {
		return sender;
	}
	
	public String getSender() {
		return sender;
	}
}
