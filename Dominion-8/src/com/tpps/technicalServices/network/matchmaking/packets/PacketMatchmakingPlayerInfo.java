package com.tpps.technicalServices.network.matchmaking.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * sent by the server to the clients in a lobby, contains information about the
 * other clients waiting
 * 
 * @author Steffen Jacobs
 */
public class PacketMatchmakingPlayerInfo extends Packet {
	private static final long serialVersionUID = 2384133814902839060L;

	private final String playerNameInfo;
	private final boolean status;
	private final boolean lobbyAdmin;

	/**
	 * constructor to initialize the packet
	 * 
	 * @param playerName
	 *            the name of the player joining or quitting
	 * @param joined
	 *            true if joined, false if quit
	 * @param isAdmin
	 *            true, if the containing player is the lobby-admin
	 */
	public PacketMatchmakingPlayerInfo(String playerName, boolean joined, boolean isAdmin) {
		super(PacketType.MATCHMAKING_PLAYER_INFO);
		this.playerNameInfo = playerName;
		this.status = joined;
		this.lobbyAdmin = isAdmin;
	}

	/** @return whether the containing player has superior permissions */
	public boolean isLobbyAdmin() {
		return lobbyAdmin;
	}

	/** @return a representation of the packet as a String */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getPlayerName() + " - Stat: " + this.isStatus();
	}

	/** @return the name of the player */
	public String getPlayerName() {
		return playerNameInfo;
	}

	/** @return the status of the player: true if joined; false if quit */
	public boolean isStatus() {
		return status;
	}
}
