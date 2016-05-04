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
public class PacketSendHandCards extends Packet {

	private static final long serialVersionUID = -3848200270426455963L;
	LinkedList<String> cardIds;
	private String changeButtons = null;

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketSendHandCards(LinkedList<String> cardIds) {
		super(PacketType.SEND_HAND_CARDS);
		this.cardIds = cardIds;
	}
	
	public void setChangeButtons(String changeButtons) {
		this.changeButtons = changeButtons;
	}
	
	public String getChangeButtons() {
		return this.changeButtons;
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