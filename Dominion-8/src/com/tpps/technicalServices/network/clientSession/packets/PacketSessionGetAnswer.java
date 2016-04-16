package com.tpps.technicalServices.network.clientSession.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is sent from the Session-Server back to the Login-Server after
 * the Login-Server requested a valid GameSession for a user
 * 
 * @author Steffen Jacobs
 */
public class PacketSessionGetAnswer extends Packet {
	private static final long serialVersionUID = 5656427406840798077L;
	private final UUID sessionID;
	private final PacketSessionGetRequest req;
	private final int answerCode;

	/**
	 * @return the Get-Request
	 */
	public PacketSessionGetRequest getRequest() {
		return req;
	}

	/**
	 * @return the generated Game-Session
	 */
	public UUID getLoginSessionID() {
		return this.sessionID;
	}

	/**
	 * returns: 0: Success, 1: already logged in
	 * 
	 * @return the answer-code for the session-get request
	 */
	public int getAnswerCode() {
		return this.answerCode;
	}

	/**
	 * initializes the Packet with the request and a newly generated
	 * Game-Session
	 * @param request the request-packet
	 * @param sID the requested ID
	 * @param _answerCode the answer-code
	 */
	public PacketSessionGetAnswer(PacketSessionGetRequest request, UUID sID, int _answerCode) {
		super(PacketType.SESSION_GET_ANSWER);
		this.sessionID = sID;
		this.req = request;
		this.answerCode = _answerCode;
	}

	/**
	 * @return a readable representation of the packet
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getRequest().getUsername() + " - "
				+ this.getLoginSessionID().toString();
	}
}
