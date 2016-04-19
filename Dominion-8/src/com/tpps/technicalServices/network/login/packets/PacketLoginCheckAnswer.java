package com.tpps.technicalServices.network.login.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send back as an answer to a the request from a game-client to
 * the Login-Server to check some users credentials. It contains a new
 * sessionID.
 * 
 * @author Steffen Jacobs
 */
public class PacketLoginCheckAnswer extends Packet {
	private static final long serialVersionUID = 4238263297967404678L;
	private final PacketLoginCheckRequest request;
	private final UUID sessionID;
	private final int state;

	/**
	 * @return the received request
	 * @author Steffen Jacobs
	 */
	public PacketLoginCheckRequest getRequest() {
		return this.request;
	}

	/**
	 * @return the validation result 0: bad password, 1: success, 2: already logged in
	 * @author Steffen Jacobs
	 */
	public int getState() {
		return this.state;
	}

	/**
	 * @return the validation result
	 * @author Steffen Jacobs
	 */
	public UUID getSessionID() {
		return this.sessionID;
	}

	/**
	 * initializes the Packet with the request and the validation result
	 * 
	 * @author Steffen Jacobs
	 * @param req the request packet
	 * @param _state a boolean representation of the state of login
	 * @param _sessionID UUID to verify the session (generated from the SessionServer)
	 */
	public PacketLoginCheckAnswer(PacketLoginCheckRequest req, int _state, UUID _sessionID) {
		super(PacketType.LOGIN_CHECK_ANSWER);
		this.request = req;
		this.state = _state;
		this.sessionID = _sessionID;
	}

	/**
	 * @return a readable String
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getRequest().getUsername() + " - "
				+ this.getRequest().getHashedPW().toString() + " - Result: " + this.getState() + ": "
				+ this.getSessionID();
	}
}
