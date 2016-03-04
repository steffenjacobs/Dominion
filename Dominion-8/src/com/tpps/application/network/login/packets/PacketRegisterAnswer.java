package com.tpps.application.network.login.packets;

import java.util.UUID;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is send back as an answer to a the request from a game-client to
 * the Login-Server to check some users credentials. It contains a new
 * sessionID.
 * 
 * @author Steffen Jacobs
 */
public class PacketRegisterAnswer extends Packet {
	private static final long serialVersionUID = -8011318119424211580L;
	private final PacketRegisterRequest request;
	private final UUID sessionID;
	private final int state;

	/**
	 * @return the received request
	 * @author Steffen Jacobs
	 */
	public PacketRegisterRequest getRequest() {
		return this.request;
	}

	/**
	 * @return the validation result
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
	 */
	public PacketRegisterAnswer(PacketRegisterRequest req, int _state, UUID _sessionID) {
		super(PacketType.LOGIN_REGISTER_ANSWER);
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
