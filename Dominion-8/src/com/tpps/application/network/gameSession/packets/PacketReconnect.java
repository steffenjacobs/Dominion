package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server everytime the client draws
 * a card. The Server then distributes the packet to the other clients.
 * 
 * @author Steffen Jacobs
 */
public class PacketReconnect extends Packet {
	
	private final int clientId;
	private static final long serialVersionUID = -3390002980740295573L;

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketReconnect(int clientId) {
		super(PacketType.RECONECT);
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
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "clientId: " + this.clientId;
	}
}