package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * packet is send to the server if the player wants to set aside the drawed card
 * @author Lukas Adler
 */
public class PacketSetAsideDrawedCard extends Packet {

	private static final long serialVersionUID = -7232107467349951828L;

	public PacketSetAsideDrawedCard() {
		super(PacketType.SET_ASIDE_DRAWED_CARD);		
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