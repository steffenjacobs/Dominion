package com.tpps.network.packets.session;

import com.tpps.network.packets.Packet;
import com.tpps.network.packets.PacketType;

/**
 * This packet is send from the Login-Server to the Session-Server to retrieve a
 * Game-Session for a user
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class PacketSessionGetRequest extends Packet {
	private static final long serialVersionUID = 1544367295338928457L;

	private final String username;

	/**
	 * @return requested user-name
	 * @author sjacobs - Steffen Jacobs
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * initialize Packet with the requested user-name
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketSessionGetRequest(String name) {
		super(PacketType.SESSION_GET_REQUEST);
		this.username = name;
	}

	/**
	 * @return a readable representation of the packet
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getUsername();
	}
}