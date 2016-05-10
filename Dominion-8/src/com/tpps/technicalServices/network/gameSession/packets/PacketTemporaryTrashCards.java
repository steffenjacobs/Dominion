package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a client to server to temporary trash card. 
 * but not used yet
 * 
 * @author Lukas Adler
 */
public class PacketTemporaryTrashCards extends Packet {

	private static final long serialVersionUID = 5939484024169429535L;
	private int clientID;

	public PacketTemporaryTrashCards(int clientID) {
		super(PacketType.TEMPORARY_TRASH_CARDS);
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