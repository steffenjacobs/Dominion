package com.tpps.technicalServices.network.chat.packets;

import java.awt.Color;

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
	
	private String timeStamp;
	private String sender;
	private String chatmessage;
	private Color color;
	
	/**
	 * @author jhuhn
	 * @param answer
	 *            String representation of the server's answer
	 */
	public PacketSendAnswer(String answer) {
		super(PacketType.SEND_CHAT_ANSWER);
		this.answer = answer;
	}	

	
	public PacketSendAnswer(String timeStamp, String sender, String chatmessage, Color color) {
		super(PacketType.SEND_CHAT_ANSWER);
		this.timeStamp = timeStamp;
		this.sender = sender;
		this.chatmessage = chatmessage;
		this.color = color;
	}
	
	@Override
	public String toString() {		
		return null;
	}

	/**
	 * @author jhuhn
	 * @return String representation of the server's answer
	 */
	public String getAnswer() {
		return answer;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getChatmessage() {
		return chatmessage;
	}
	
	public Color getColor() {
		return color;
	}
}
