package com.tpps.technicalServices.network.chat.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This class represents a packet that will be sent via network
 * 
 * @author jhuhn
 */
public class PacketSendChatAll extends Packet{
	
	private static final long serialVersionUID = 1L;
	private final String chatmessage;
	private final String username;
	
	
	/**
	 * @author jhuhn
	 * @param username
	 *            String representation of the user
	 * @param chatmessage
	 *            String representation of the chatmessage
	 */
	public PacketSendChatAll(String username, String chatmessage) {
		super(PacketType.SEND_CHAT_ALL);
		this.chatmessage  = chatmessage;
		this.username = username;
	}	

	@Override
	public String toString() {
		return "Sender: " + getUsername() + " Message to all: " + getChatmessage();
	}
	
	/**
	 * @author jhuhn
	 * @return String representation of the chatmessage
	 */
	public String getChatmessage() {
		return chatmessage;
	}
	
	/**
	 * @author jhuhn
	 * @return String representation of the user
	 */
	public String getUsername() {
		return username;
	}
}
