package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a client to the server every time the player wants
 * to end discard mode. The Server then distributes the packet to the other players.
 * 
 * @author Lukas Adler
 */
public class PacketEndDiscardMode extends Packet {
	
	private static final long serialVersionUID = -1906675942104230602L;


 
	
	public PacketEndDiscardMode() {
		super(PacketType.END_DISCARD_MODE);
	}

	/**
	 * @return a readable String
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}