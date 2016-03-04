package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class PacketSendClientId extends Packet {
	
	private static final long serialVersionUID = -8238146017971931432L;
	private final int clientID;
	



	/**
	 * @return the playedCard
	 * @author sjacobs - Steffen Jacobs
	 */
	public int getClientId() {
		return this.clientID;
	}
	

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketSendClientId(int clientId) {
		super(PacketType.SEND_CLIENT_ID);
		this.clientID = clientId;		
	}

	/**
	 * @return a readable String
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.clientID;
	}
}