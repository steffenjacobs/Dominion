package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * packet is send to disable enable guis
 * 
 * @author ladler - Lukas Adler
 */
public class PacketEnableDisable extends Packet {


	private static final long serialVersionUID = 1397382952533953158L;
	private final int clientId;
	private final String userName;
	private final boolean resetGameWindow;
	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketEnableDisable(int clientId, String userName, boolean resetGameWindow) {		
		super(PacketType.ENABLE_DISABLE);
		this.clientId = clientId;
		this.userName = userName;
		this.resetGameWindow = resetGameWindow;
	}
	
	/**
	 * 
	 * @return if the gameWindow should be reseted
	 */
	public boolean resetGameWindow() {
		return resetGameWindow;
	}
	
	/**
	 * 
	 * @return the userName 
	 */
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
		return this.getClass().getSimpleName() + "- clientId: " + 
	this.clientId + "- username: " + this.userName + "- resetGameWindow: " + this.resetGameWindow;
	}
}