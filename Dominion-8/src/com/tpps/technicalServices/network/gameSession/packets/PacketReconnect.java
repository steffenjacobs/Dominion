package com.tpps.technicalServices.network.gameSession.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server to reconnect to the game
 * 
 * 
 * @author Steffen Jacobs
 */
public class PacketReconnect extends Packet {
	
	private final UUID sessionID;
	private String username;
	private static final long serialVersionUID = -3390002980740295573L;

	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketReconnect(UUID sessionID, String username) {
		super(PacketType.RECONNECT);
		this.sessionID = sessionID;
		this.username = username;
	}
	
	
	/**
	 * @return the sessionID
	 */
	public UUID getSessionID() {
		return this.sessionID;
	}
	
	/**
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}
	
	



	/**
	 * @return a readable String
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "sessionID:" + this.sessionID + 
				"username: " + this.username;
	}
}