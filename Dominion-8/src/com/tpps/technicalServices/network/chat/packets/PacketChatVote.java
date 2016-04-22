package com.tpps.technicalServices.network.chat.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This class represents a packet that will be sent via network
 * 
 * @author jhuhn
 */
public class PacketChatVote extends Packet{
	
	private static final long serialVersionUID = -4328855211555677043L;
	private boolean voted;
	private String sender;
	
	/**
	 * initializes the packet
	 * 
	 * @author jhuhn
	 * @param voted
	 *            boolean representation of the senders vote
	 * @param sender
	 *            String representation of the sender
	 */
	protected PacketChatVote(boolean voted, String sender) {
		super(PacketType.SEND_CHAT_VOTE);
		this.voted = voted;
		this.sender = sender;
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
	 * @return boolean representation of the users vote
	 */
	public boolean getVoted(){
		return this.voted;
	}
	
	@Override
	public String toString() {
		return "Vote by: " + this.sender + " : " + this.voted;
	}

}
