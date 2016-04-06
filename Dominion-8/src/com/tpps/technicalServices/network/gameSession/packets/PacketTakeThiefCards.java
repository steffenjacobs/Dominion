package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send to the server if the player wants to take the choosen cards from the thief
 * 
 * @author Lukas Adler
 */
public class PacketTakeThiefCards extends Packet {

	private static final long serialVersionUID = -8869493841315700504L;


	public PacketTakeThiefCards() {
		super(PacketType.TAKE_THIEF_CARDS);
		
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