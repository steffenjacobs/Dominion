package com.tpps.application.network.chat;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

public class PacketSendChatToClient extends Packet{

	private static final long serialVersionUID = 1L;
	private String sender;
	private String message;
	private String receiver;
	
	public PacketSendChatToClient(PacketType type) {
		super(type);
	}

	

	@Override
	public String toString() {
		return null;
	}

}
