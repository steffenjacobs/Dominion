package com.tpps.technicalServices.network.chat.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This class represents a packet that will be sent via network
 * 
 * @author jhuhn
 */
public class PacketChatHandshake extends Packet{
	
	private static final long serialVersionUID = -3761269201140314822L;
	private String sender;
	
	/**
	 * initializes the object
	 * 
	 * @author jhuhn
	 * @param sender
	 *            String representaion of the sender
	 */
	public PacketChatHandshake(String sender) {
		super(PacketType.CHAT_HANDSHAKE);
		this.sender = sender;
	}	
	/**
	 * @see com.tpps.technicalServices.network.core.packet.Packet#toString()
	 */
	@Override
	public String toString() {
		return sender;
	}
	
	/**
	 * @author jhuhn
	 * @return a String representation of the sender
	 */
	public String getSender() {
		return sender;
	}
}
