package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;

/**
 * This packet is send from a server to the client every time there are already
 * too much player on the server
 * 
 * @author ladler - Lukas Adler
 */
public class PacketClientShouldDisconect extends Packet {

	private static final long serialVersionUID = -7638846248749585745L;

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