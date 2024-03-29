package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the reaction mode buttons should be not 
 * there
 * 
 * @author ladler - Lukas Adler
 */
public class PacketDontShowEndReactions extends Packet {

	private static final long serialVersionUID = 5031309487830629571L;

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketDontShowEndReactions() {
		super(PacketType.DONT_SHOW_END_REACTION_MODE);		
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