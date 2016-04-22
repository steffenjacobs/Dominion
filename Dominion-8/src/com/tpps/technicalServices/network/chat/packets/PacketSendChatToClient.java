package com.tpps.technicalServices.network.chat.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This class represents a packet that will be sent via network
 * 
 * @author jhuhn
 */
public class PacketSendChatToClient extends Packet{

	private static final long serialVersionUID = 1L;
	private String sender;
	private String message;
	private String receiver;
	
	/**
	 * initializes the PM packet
	 * 
	 * @author jhuhn
	 * @param sender
	 *            String representation of the sender
	 * @param message
	 *            String representation of the message
	 * @param receiver
	 *            String representation of the receiver
	 */
	public PacketSendChatToClient(String sender, String message, String receiver) {
		super(PacketType.SEND_CHAT_TO_CLIENT);
		this.sender = sender;
		this.message = message.trim();
		this.receiver = receiver;
	}

	@Override
	public String toString() {
		return "Sender: " + this.sender + " to Receiver: " + this.receiver + "   Message: " + this.message;
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
	 * @return String representation of the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @author jhuhn
	 * @return String representation of the receiver
	 */
	public String getReceiver() {
		return receiver;
	}
}
