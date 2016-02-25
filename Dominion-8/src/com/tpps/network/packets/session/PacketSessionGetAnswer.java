package com.tpps.network.packets.session;

import java.util.UUID;

import com.tpps.network.packets.Packet;
import com.tpps.network.packets.PacketType;

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
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketSessionGetRequest getRequest() {
		return req;
	}

	/**
	 * @return the generated Game-Session
	 * @author sjacobs - Steffen Jacobs
	 */
	public UUID getSessionID() {
		return this.sessionID;
	}

	/**
	 * initializes the Packet with the request and a newly generated
	 * Game-Session
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketSessionGetAnswer(PacketSessionGetRequest request, UUID sID) {
		super(PacketType.SESSION_GET_ANSWER);
		this.sessionID = sID;
		req = request;
	}

	/**
	 * @return a readable representation of the packet
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getRequest().getUsername() + " - "
				+ this.getSessionID().toString();
	}
}
