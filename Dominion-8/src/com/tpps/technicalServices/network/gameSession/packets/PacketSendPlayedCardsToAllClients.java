package com.tpps.technicalServices.network.gameSession.packets;

import java.util.LinkedList;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author ladler - Lukas Adler
 */
public class PacketSendPlayedCardsToAllClients extends Packet {

	private static final long serialVersionUID = -4608918287454256366L;
	LinkedList<String> cardIds;

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketSendPlayedCardsToAllClients(LinkedList<String> cardIds) {
		super(PacketType.SEND_PLAYED_CARDS_TO_ALL_CLIENTS);
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