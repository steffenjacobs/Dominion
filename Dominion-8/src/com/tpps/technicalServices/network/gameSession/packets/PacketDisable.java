package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client every time there are already
 * too much player on the server
 * 
 * @author ladler - Lukas Adler
 */
public class PacketDisable extends Packet {

	private static final long serialVersionUID = 3025276399757464287L;
	private final String caption;

	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketDisable(String caption) {
		super(PacketType.DISABLE);
		this.caption = caption;
	}
	
	/**
	 * 
	 * @return the userName of the active player
	 */
	public String getCaption() {
		return this.caption;
	}

	/**
	 * @return a readable String
	 * @author ladler - Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "username: " + this.caption;
	}
}