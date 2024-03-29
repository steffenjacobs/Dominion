package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from server to client to add the stop discard buttons
 * 
 * @author Lukas Adler
 */
public class PacketStartDiscardMode extends Packet {

	private static final long serialVersionUID = 6105464482068904968L;

	public PacketStartDiscardMode() {
		super(PacketType.START_DISCARD_MODE);
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