package com.tpps.technicalServices.network.clientSession.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send back as an answer to a the request from a (game-)server
 * to the Session-Server to check some users session.
 * 
 * @author Steffen Jacobs
 */
public class PacketSessionCheckAnswer extends Packet {
	private static final long serialVersionUID = -8763060018038893429L;
	private final PacketSessionCheckRequest request;
	private final boolean state;

	/**
	 * @return the received request
	 */
	public PacketSessionCheckRequest getRequest() {
		return this.request;
	}

	/**
	 * @return the validation result
	 */
	public boolean getState() {
		return this.state;
	}

	/**
	 * initializes the Packet with the request and the validation result
	 * @param req the request
	 * @param _state the state
	 * 
	 */
	public PacketSessionCheckAnswer(PacketSessionCheckRequest req, boolean _state) {
		super(PacketType.SESSION_CHECK_ANSWER);
		this.request = req;
		this.state = _state;
	}

	/**
	 * @return a readable String
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getRequest().getUsername() + " - "
				+ this.getRequest().getSessionID().toString() + " - Result: " + this.getState();
	}
}
