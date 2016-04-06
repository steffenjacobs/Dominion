package com.tpps.technicalServices.network.clientSession.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is sent from the Session-Server back to the Login-Server after
 * the Login-Server requested a valid GameSession for a user
 */
public class PacketSessionGetAnswer extends Packet {
	private static final long serialVersionUID = 5656427406840798077L;
	private final UUID sessionID;
	private final PacketSessionGetRequest req;

	/**
	 * @return the Get-Request
	 * @author Steffen Jacobs
	 */
	public PacketSessionGetRequest getRequest() {
		return req;
	}

	/**
	 * @return the generated Game-Session
	 * @author Steffen Jacobs
	 */
	public UUID getLoginSessionID() {
		return this.sessionID;
	}

	/**
	 * initializes the Packet with the request and a newly generated
	 * Game-Session
	 * 
	 * @author Steffen Jacobs
	 */
	public PacketSessionGetAnswer(PacketSessionGetRequest request, UUID sID) {
		super(PacketType.SESSION_GET_ANSWER);
		this.sessionID = sID;
		req = request;
	}

	/**
	 * @return a readable representation of the packet
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getRequest().getUsername() + " - "
				+ this.getLoginSessionID().toString();
	}
}
