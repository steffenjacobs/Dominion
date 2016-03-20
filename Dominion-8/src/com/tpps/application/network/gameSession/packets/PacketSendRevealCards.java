package com.tpps.application.network.gameSession.packets;

import java.util.LinkedList;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author ladler - Lukas Adler
 */
public class PacketSendRevealCards extends Packet {

	private static final long serialVersionUID = 9024120672748171713L;
	LinkedList<String> cardIds;

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketSendRevealCards(LinkedList<String> cardIds) {
		super(PacketType.SEND_REVEAL_CARDS);
		this.cardIds = cardIds;
	}
	
	
	/**
	 * 
	 * @return the ids of the cards the player has on his hand
	 */
	public LinkedList<String> getCardIds() {
		return cardIds;
	}



	/**
	 * @return a readable String
	 * @author ladler - Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}