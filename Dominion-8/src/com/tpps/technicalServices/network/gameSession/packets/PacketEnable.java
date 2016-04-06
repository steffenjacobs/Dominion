package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client every time there are already
 * too much player on the server
 * 
 * @author ladler - Lukas Adler
 */
public class PacketEnable extends Packet {

	private static final long serialVersionUID = 2897016123058493933L;


	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketEnable() {		
		super(PacketType.ENABLE);
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