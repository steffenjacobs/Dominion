package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a client to the server to take the spy cards
 * 
 * @author Lukas Adler
 */
public class PacketTakeCards extends Packet {

	private static final long serialVersionUID = 3242681048729640185L;
	private int clientID;

	public PacketTakeCards(int clientID) {
		super(PacketType.TAKE_CARDS);
		this.clientID = clientID;
	}
	
	public int getClientID() {
		return this.clientID;
	}

	/**
	 * @return a readable String
	 * @author Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}