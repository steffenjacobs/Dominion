package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a client to the server every time the player wants
 * to discard his deck
 * 
 * @author Lukas Adler
 */
public class PacketDiscardDeck extends Packet {

	private static final long serialVersionUID = 75283080058700657L;

	public PacketDiscardDeck() {
		super(PacketType.DISCARD_DECK);
	}

	/**
	 * @return a readable String
	 * @author Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}