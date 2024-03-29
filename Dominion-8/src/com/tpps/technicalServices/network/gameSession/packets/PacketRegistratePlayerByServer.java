package com.tpps.technicalServices.network.gameSession.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from the client to the server to registrate a player
 * 
 * 
 * @author ladler - Lukas Adler
 */
public class PacketRegistratePlayerByServer extends Packet {

	private static final long serialVersionUID = -3390002980740295573L;
	private String username;
	private UUID uuid;
	/**
	 * initializes the Packet with the id of the card which had been played and
	 * the name of the player who played the card
	 * 
	 * @author ladler - Lukas Adler
	 */
	public PacketRegistratePlayerByServer(String username, UUID uuid) {
		super(PacketType.REGISTRATE_PLAYER_BY_SERVER);
		this.username = username;
		this.uuid = uuid;
	}
	
	/**
	 * 
	 * @return the uuid of the player
	 */
	public UUID getSessionID() {
		return this.uuid;
	}
	
	/**
	 * 
	 * @return the username of the player
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @return a readable String
	 * @author ladler - Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}