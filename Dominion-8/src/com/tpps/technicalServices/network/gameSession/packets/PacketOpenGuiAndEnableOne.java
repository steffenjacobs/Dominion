package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client to open all guis and enable the one of the active
 * player
 * 
 * @author ladler - Lukas Adler
 */
public class PacketOpenGuiAndEnableOne extends Packet {

	private static final long serialVersionUID = 545503397600298278L;
	private final int clientId;
	private final String userName;

	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketOpenGuiAndEnableOne(int clientId, String userName) {
		super(PacketType.OPEN_GUI_AND_ENABLE_ONE);
		this.clientId = clientId;
		this.userName = userName;
	}
	
	public String getUserName() {
		return this.userName;
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
		return this.getClass().getSimpleName() + "clientId: " + this.clientId + "userName: " + this.userName;
	}
}