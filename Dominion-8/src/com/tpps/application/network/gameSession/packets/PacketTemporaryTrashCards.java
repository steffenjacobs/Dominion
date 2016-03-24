package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from a client to the server every time the player wants
 * to end his turn. The Server then distributes the packet to the other players.
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