package com.tpps.technicalServices.network.chat.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This class represents a packet that will be sent via network
 *
 * @author jhuhn
 */
public class PacketSendChatCommand extends Packet{

	private static final long serialVersionUID = 1L;
	private String sender;
	private String chatcommand;
	
	
	/**
	 * @author jhuhn
	 * @param sender
	 *            String representation of the sender
	 * @param chatcommand
	 *            String representation of the chatcommand
	 */
	public PacketSendChatCommand(String sender, String chatcommand) {
		super(PacketType.SEND_CHAT_COMMAND);
		this.sender = sender;
		this.chatcommand = chatcommand;
	}

	@Override
	public String toString() {
		return "Message from '" + getSender() + "' Command: /" + getChatcommand();
	}
	
	/**
	 * @author jhuhn
	 * @return String representation of the sender
	 */
	public String getSender() {
		return sender;
	}
	
	/**
	 * @author jhuhn
	 * @return String representation of the chatcommand
	 */
	public String getChatcommand() {
		return chatcommand;
	}
}
