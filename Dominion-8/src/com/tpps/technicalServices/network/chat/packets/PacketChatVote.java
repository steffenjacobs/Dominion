package com.tpps.technicalServices.network.chat.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class PacketChatVote extends Packet{
	
	private static final long serialVersionUID = 1L;
	private boolean voted;
	private String sender;
	
	protected PacketChatVote(boolean voted, String sender) {
		super(PacketType.SEND_CHAT_VOTE);
		this.voted = voted;
		this.sender = sender;
	}	

	public String getSender() {
		return sender;
	}	

	public boolean getVoted(){
		return this.voted;
	}
	
	@Override
	public String toString() {
		return "Vote by: " + this.sender + " : " + this.voted;
	}

}
