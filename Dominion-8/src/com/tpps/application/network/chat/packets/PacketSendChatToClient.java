package com.tpps.application.network.chat.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

public class PacketSendChatToClient extends Packet{

	private static final long serialVersionUID = 1L;
	private String sender;
	private String message;
	private String receiver;
	
	public PacketSendChatToClient(String sender, String message, String receiver) {
		super(PacketType.SEND_CHAT_TO_CLIENT);
		this.sender = sender;
		this.message = message;
		this.receiver = receiver;
	}

	@Override
	public String toString() {
		return "Sender: " + this.sender + " to Receiver: " + this.receiver + "   Message: " + this.message;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getReceiver() {
		return receiver;
	}
}
