package com.tpps.application.network.login.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send as a request from a game client to the LoginServer to
 * check a users credentials.
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class PacketRegisterRequest extends Packet {
	private static final long serialVersionUID = -2467347347400740313L;
	private final String username, hashedPW, emailAddress;

	/**
	 * @return the username
	 * @author sjacobs - Steffen Jacobs
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @return the hashed password
	 * @author sjacobs - Steffen Jacobs
	 */
	public String getHashedPW() {
		return this.hashedPW;
	}
	
	/**
	 * @return the email address of the user who registers
	 * @author sjacobs - Steffen Jacobs
	 */
	public String getEmail() {
		return this.emailAddress;
	}

	/**
	 * initializes the Packet with a username and a hashed password to check
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketRegisterRequest(String name, String _hashedPW, String _emailAddress) {
		super(PacketType.LOGIN_REGISTER_REQUEST);
		this.username = name;
		this.hashedPW = _hashedPW;
		this.emailAddress = _emailAddress;
	}

	/**
	 * @return a readable representation of the packet
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getUsername() + " / " + this.emailAddress + " - " + this.getHashedPW();
	}
}
