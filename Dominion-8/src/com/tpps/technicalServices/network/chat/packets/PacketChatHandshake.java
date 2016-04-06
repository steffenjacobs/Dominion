package com.tpps.technicalServices.network.chat.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

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
