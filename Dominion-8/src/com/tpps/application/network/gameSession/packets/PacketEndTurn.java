package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from a client to the server every time the player wants
 * to end his turn. The Server then distributes the packet to the other players.
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class PacketEndTurn extends Packet {
	private static final long serialVersionUID = -8763060018038893429L;
	private final String username;

	/**
	 * @return the name of the player who played wants to end the turn
	 * @author sjacobs - Steffen Jacobs
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * initializes the Packet with the name of the player who wants to end the
	 * turn
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketEndTurn(String playerName) {
		super(PacketType.END_TURN);
		this.username = playerName;
	}

	/**
	 * @return a readable String
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getUsername();
	}
}