package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * packet is send to enable the others
 * 
 * @author ladler - Lukas Adler
 */
public class PacketEnableOthers extends Packet {
	
	private static final long serialVersionUID = -457011106248167767L;
	private int clientID;

	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketEnableOthers(int clientID) {
		super(PacketType.ENABLE_OTHERS);	
		this.clientID = clientID;
	}
	
	

	public int getClientID() {
		return this.clientID;
	}



	/**
	 * @return a readable String
	 * @author ladler - Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "clientID: " + this.clientID;
	}
}