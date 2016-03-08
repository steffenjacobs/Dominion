package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author ladler - Lukas Adler
 */
public class PacketEndActionPhase extends Packet {

	private static final long serialVersionUID = 8869904290942834312L;
	

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketEndActionPhase() {
		super(PacketType.END_ACTION_PHASE);
		
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