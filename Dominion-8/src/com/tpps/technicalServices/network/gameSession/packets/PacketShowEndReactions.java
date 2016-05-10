package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from the server to client to remove the reactions buttons.
 * or from client to server to show that the reaction mode is finished
 * 
 * @author ladler - Lukas Adler
 */
public class PacketShowEndReactions extends Packet {

	private static final long serialVersionUID = -8625911361818225947L;

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketShowEndReactions() {
		super(PacketType.SHOW_END_REACTION_MODE);		
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