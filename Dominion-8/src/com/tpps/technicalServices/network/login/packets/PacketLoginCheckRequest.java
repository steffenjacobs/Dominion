package com.tpps.technicalServices.network.login.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send as a request from a game client to the LoginServer to
 * check a users credentials.
 * 
 * @author Steffen Jacobs
 */
public class PacketLoginCheckRequest extends Packet {
	private static final long serialVersionUID = 4466044302525259053L;
	private final String username;
	private final String hashedPW;

	/**
	 * @return the username to check
	 * @author Steffen Jacobs
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @return the hashed password to check
	 * @author Steffen Jacobs
	 */
	public String getHashedPW() {
		return this.hashedPW;
	}

	/**
	 * initializes the Packet with a username and a hashed password to check
	 * 
	 * @author Steffen Jacobs
	 * @param name a String representation of the username to log into the game
	 * @param _hashedPW first hashes password in a String representaion
	 */
	public PacketLoginCheckRequest(String name, String _hashedPW) {
		super(PacketType.LOGIN_CHECK_REQUEST);
		this.username = name;
		this.hashedPW = _hashedPW;
	}

	/**
	 * @return a readable representation of the packet
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getUsername() + " - " + this.getHashedPW();
	}
}
