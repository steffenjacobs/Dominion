package com.tpps.technicalServices.network.clientSession.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send as a request from a (game-)server to the Session-Server
 * to check a users sessionID.
 * 
 * @author Steffen Jacobs
 */
public class PacketSessionCheckRequest extends Packet {
	private static final long serialVersionUID = 3207961189002304124L;
	private final String username;
	private final UUID sessionID;
	private final long timestamp;

	/**
	 * @return the username to check
	 * @author Steffen Jacobs
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @return the sessionID to check
	 * @author Steffen Jacobs
	 */
	public UUID getSessionID() {
		return this.sessionID;
	}

	/**
	 * @return the timestamp the packet was created
	 * @author Steffen Jacobs
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * initializes the Packet with a username and a sessionID to check
	 * 
	 * @author Steffen Jacobs
	 * @param name the requested name
	 * @param sID the requested sessionID
	 * @param timestamp the timestamp
	 */
	public PacketSessionCheckRequest(String name, UUID sID, long timestamp) {
		super(PacketType.SESSION_CHECK_REQUEST);
		this.username = name;
		this.sessionID = sID;
		this.timestamp = timestamp;
	}

	/**
	 * @return a readable representation of the packet
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getUsername() + " - " + this.getSessionID();
	}
}
