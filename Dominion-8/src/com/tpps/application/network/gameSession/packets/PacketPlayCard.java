package com.tpps.application.network.gameSession.packets;

import com.tpps.application.game.card.CardType;
import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class PacketPlayCard extends Packet {
	private static final long serialVersionUID = -8763060018038893429L;
	private final String playedCardID;
	private final String username;  

	/**
	 * @return the name of the player who played the card
	 * @author sjacobs - Steffen Jacobs
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @return the playedCard
	 * @author sjacobs - Steffen Jacobs
	 */
	public String getCardID() {
		return this.playedCardID;
	}
	

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketPlayCard(String cardID, String playerName) {
		super(PacketType.CARD_PLAYED);
		this.playedCardID = cardID;		
		this.username = playerName;
	}

	/**
	 * @return a readable String
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getUsername() + " - " + this.getCardID();
	}
}