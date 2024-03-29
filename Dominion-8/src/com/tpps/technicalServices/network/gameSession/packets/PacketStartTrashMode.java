package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from server to client to add the stop trash buttons
 * 
 * @author Lukas Adler
 */
public class PacketStartTrashMode extends Packet {

	private static final long serialVersionUID = 8911022385473606500L;

	public PacketStartTrashMode() {
		super(PacketType.START_TRASH_MODE);
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