package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws or plays
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author Steffen Jacobs
 */
public class PacketPlayCard extends Packet {
	private static final long serialVersionUID = -8763060018038893429L;
	private final String playedCardID;
	private final int clientID;

	/**
	 * @return the playedCard
	 * @author Steffen Jacobs
	 */
	public String getCardID() {
		return this.playedCardID;
	}
	
	/**
	 * @return the clientID
	 */
	public int getClientID() {
		return this.clientID;
	}

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @param cardID id of the card
	 * @param clientID id of the player
	 * 
	 * @author Steffen Jacobs
	 */
	public PacketPlayCard(String cardID, int clientID) {
		super(PacketType.CARD_PLAYED);
		this.playedCardID = cardID;
		this.clientID = clientID;
	}

	/**
	 * @return a readable String
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + this.getCardID() + " - " + this.clientID;
	}
}