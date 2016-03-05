package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author Steffen Jacobs
 */
public class PacketPlayCard extends Packet {
	private static final long serialVersionUID = -8763060018038893429L;
	private final String playedCardID;
	


	/**
	 * @return the playedCard
	 * @author Steffen Jacobs
	 */
	public String getCardID() {
		return this.playedCardID;
	}
	

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author Steffen Jacobs
	 */
	public PacketPlayCard(String cardID) {
		super(PacketType.CARD_PLAYED);
		this.playedCardID = cardID;		
	}

	/**
	 * @return a readable String
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + this.getCardID();
	}
}