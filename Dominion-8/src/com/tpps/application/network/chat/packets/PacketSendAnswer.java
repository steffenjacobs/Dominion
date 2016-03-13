package com.tpps.application.network.chat.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

public class PacketSendAnswer extends Packet{
	
	private static final long serialVersionUID = 1L;
	private String answer;
	
	public PacketSendAnswer(String answer) {
		super(PacketType.SEND_CHAT_ANSWER);
		this.answer = answer;
	}	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAnswer() {
		return answer;
	}
}
