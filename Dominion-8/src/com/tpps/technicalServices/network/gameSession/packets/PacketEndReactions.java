package com.tpps.technicalServices.network.gameSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a client to the server to show he doesn' t want to play his reaction card;
 * 
 * @author Lukas Adler
 */
public class PacketEndReactions extends Packet {
	
	private static final long serialVersionUID = -7373549515047873227L;
	private int clientID;


	/**
	 * 
	 * @author Lukas Adler
	 */
	public PacketEndReactions(int clientID) {
		super(PacketType.END_REACTIONS);
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