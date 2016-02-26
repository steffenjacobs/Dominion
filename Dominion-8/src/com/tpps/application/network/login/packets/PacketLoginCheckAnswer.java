package com.tpps.application.network.login.packets;

import java.util.UUID;

import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;

/**
 * This packet is send back as an answer to a the request from a game-client to
 * the Login-Server to check some users credentials. It contains a new
 * sessionID.
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class PacketLoginCheckAnswer extends Packet {
	private static final long serialVersionUID = 4238263297967404678L;
	private final PacketLoginCheckRequest request;
	private final UUID sessionID;
	private final boolean state;

	/**
	 * @return the received request
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketLoginCheckRequest getRequest() {
		return this.request;
	}

	/**
	 * @return the validation result
	 * @author sjacobs - Steffen Jacobs
	 */
	public boolean getState() {
		return this.state;
	}

	/**
	 * @return the validation result
	 * @author sjacobs - Steffen Jacobs
	 */
	public UUID getSessionID() {
		return this.sessionID;
	}

	/**
	 * initializes the Packet with the request and the validation result
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketLoginCheckAnswer(PacketLoginCheckRequest req, boolean _state, UUID _sessionID) {
		super(PacketType.LOGIN_CHECK_ANSWER);
		this.request = req;
		this.state = _state;
		this.sessionID = _sessionID;
	}

	/**
	 * @return a readable String
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getRequest().getUsername() + " - "
				+ this.getRequest().getHashedPW().toString() + " - Result: " + this.getState() + ": "
				+ this.getSessionID();
	}
}
