package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a client to the server every time the player wants
 * to put back the spy cards.
 * 
 * @author Lukas Adler
 */
public class PacketPutBackCards extends Packet {

	private static final long serialVersionUID = 7078279259189608746L;
	private int clientID;

	public PacketPutBackCards(int clientID) {
		super(PacketType.PUT_BACK_CARDS);
		this.clientID = clientID;
	}
	
	public int getClientID() {
		return this.clientID;
	}

	/**
	 * @return a readable String
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}