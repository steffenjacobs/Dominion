package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from a client to the server to show he doesn' t want to play his reaction card;
 * 
 * @author Lukas Adler
 */
public class PacketEndReactions extends Packet {
	private int clientID;

	/**
	 * 
	 */
	private static final long serialVersionUID = 382132236460850033L;

	/**
	 * 
	 * @author Lukas Adler
	 */
	public PacketEndReactions(int clientID) {
		super(PacketType.END_TURN);
		this.clientID = clientID;
	}

	/**
	 * 
	 * @return the clientID
	 */
	public int getClientID() {
		return clientID;
	}

	/**
	 * @return a readable String
	 * @author Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "clientID: " + clientID;
	}
}