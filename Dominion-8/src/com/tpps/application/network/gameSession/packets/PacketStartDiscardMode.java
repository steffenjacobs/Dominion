package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from a client to the server every time the player wants
 * to end his turn. The Server then distributes the packet to the other players.
 * 
 * @author Lukas Adler
 */
public class PacketStartDiscardMode extends Packet {

	private static final long serialVersionUID = 6105464482068904968L;

	public PacketStartDiscardMode() {
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