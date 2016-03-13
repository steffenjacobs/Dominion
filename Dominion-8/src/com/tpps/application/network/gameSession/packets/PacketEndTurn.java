package com.tpps.application.network.gameSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send from a client to the server every time the player wants
 * to end his turn. The Server then distributes the packet to the other players.
 * 
 * @author Steffen Jacobs
 */
public class PacketEndTurn extends Packet {
	private static final long serialVersionUID = -8763060018038893429L;

	/**
	 * @return the name of the player who played wants to end the turn
	 * @author Steffen Jacobs
	 */
//	public String getUsername() {
//		return this.username;
//	}

	/**
	 * initializes the Packet with the name of the player who wants to end the
	 * turn
	 * 
	 * @author Steffen Jacobs
	 */
	public PacketEndTurn() {
		super(PacketType.END_TURN);
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