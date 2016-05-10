package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client presses 
 * the play treasures button
 * 
 * @author ladler - Lukas Adler
 */
public class PacketPlayTreasures extends Packet {

	private static final long serialVersionUID = 5746594526553393904L;


	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketPlayTreasures() {
		super(PacketType.PLAY_TREASURES);
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