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

	
	/**
	 * this constructor is always used if the chat server wants to send sth to a
	 * client
	 * 
	 * @author jhuhn
	 * @param timeStamp
	 *            String representation of the timestamp
	 * @param sender
	 *            String representation of the sender
	 * @param chatmessage
	 *            String representation of the chatmessage
	 * @param color
	 *            Color object that is used to undeline the username in the
	 *            right color
	 */
	public PacketSendAnswer(String timeStamp, String sender, String chatmessage, Color color) {
		super(PacketType.SEND_CHAT_ANSWER);
		this.timeStamp = timeStamp;
		this.sender = sender;
		this.chatmessage = chatmessage;
		this.color = color;
	}
	/**
	 * @see com.tpps.technicalServices.network.core.packet.Packet#toString()
	 */
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
	
	/**
	 * @return String representation of the timestamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * @return String representation of the sender
	 */
	public String getSender() {
		return sender;
	}
	
	/**
	 * @return String representation of the chatmessage
	 */
	public String getChatmessage() {
		return chatmessage;
	}
	
	/**
	 * @return color instance used to display the username
	 */
	public Color getColor() {
		return color;
	}
}
