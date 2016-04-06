package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * the packet is send to the server if the player wants to take the drawed card
 * @author Lukas Adler
 */
public class PacketTakeDrawedCard extends Packet {

	private static final long serialVersionUID = -2732334431695539456L;

	public PacketTakeDrawedCard() {
		super(PacketType.TAKE_DRAWED_CARD);		
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