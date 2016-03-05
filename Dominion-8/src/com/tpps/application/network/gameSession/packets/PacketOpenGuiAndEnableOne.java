package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client every time there are already
 * too much player on the server
 * 
 * @author ladler - Lukas Adler
 */
public class PacketOpenGuiAndEnableOne extends Packet {

	private static final long serialVersionUID = 545503397600298278L;
	private final int clientId;

	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketOpenGuiAndEnableOne(int clientId) {
		super(PacketType.OPEN_GUI_AND_ENABLE_ONE);
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
		return this.getClass().getSimpleName() + "clientId: " + this.clientId;
	}
}