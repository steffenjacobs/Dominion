package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client every time there are already
 * too much player on the server
 * 
 * @author ladler - Lukas Adler
 */
public class PacketEnableDisable extends Packet {


	private static final long serialVersionUID = 1397382952533953158L;
	private final int clientId;
	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketEnableDisable(int clientId) {		
		super(PacketType.ENABLE_DISABLE);
		this.clientId = clientId;
	}
	
	/**
	 * 
	 * @return the clientId
	 */
	public int getClientId() {
		return clientId;
	}

	/**
	 * @return a readable String
	 * @author ladler - Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "-" + this.clientId;
	}
}