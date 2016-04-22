package com.tpps.technicalServices.network.chat.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This class represents a packet that will be sent via network
 * 
 * @author jhuhn
 */
public class PacketSendAnswer extends Packet{
	
	private static final long serialVersionUID = 1L;
	private String answer;
	
	/**
	 * @author jhuhn
	 * @param answer
	 *            String representation of the server's answer
	 */
	public PacketSendAnswer(String answer) {
		super(PacketType.SEND_CHAT_ANSWER);
		this.answer = answer;
	}	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @author jhuhn
	 * @return String representation of the server's answer
	 */
	public String getAnswer() {
		return answer;
	}
}
