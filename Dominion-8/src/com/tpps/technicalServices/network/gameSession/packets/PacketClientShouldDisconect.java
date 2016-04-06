package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client every time there are already
 * too much player on the server
 * 
 * @author ladler - Lukas Adler
 */
public class PacketClientShouldDisconect extends Packet {

	private static final long serialVersionUID = -2463725032477442146L;

	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketClientShouldDisconect() {
		super(PacketType.CLIENT_SHOULD_DISCONECT);
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