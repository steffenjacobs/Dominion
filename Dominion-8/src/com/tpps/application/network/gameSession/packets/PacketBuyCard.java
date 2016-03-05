package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author ladler - Lukas Adler
 */
public class PacketBuyCard extends Packet {

	private static final long serialVersionUID = 2232669430503735578L;
	private int cardId;

	/**
	 * initializes the Packet with the id of the card which had been buyed and
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketBuyCard(int cardId) {
		super(PacketType.BUY_CARD);			
		this.cardId = cardId;		
	}
	
	
	/**
	 * 
	 * @return a cardId
	 */
	public int getCardId() {
		return cardId;
	}



	/**
	 * @return a readable String
	 * @author ladler - Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "-" + this.cardId;
	}
}