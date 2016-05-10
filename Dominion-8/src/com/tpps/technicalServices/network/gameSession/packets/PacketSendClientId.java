package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from the server to the cliend to send the client id. 
 * 
 * @author Steffen Jacobs
 */
public class PacketSendClientId extends Packet {
	
	private static final long serialVersionUID = -8238146017971931432L;
	private final int clientID;
	



	/**
	 * @return the playedCard
	 * @author Steffen Jacobs
	 */
	public int getClientId() {
		return this.clientID;
	}
	

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author Steffen Jacobs
	 */
	public PacketSendClientId(int clientId) {
		super(PacketType.SEND_CLIENT_ID);
		this.clientID = clientId;		
	}

	/**
	 * @return a readable String
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.clientID;
	}
}