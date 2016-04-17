package com.tpps.technicalServices.network.matchmaking.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * is sent from the Matchmaker to the Matchmaking-Server to find a specific
 * lobby, identified by it's unique lobby-id; answered with a
 * PacketMatchmakingAnswer by the server
 * 
 * @author Steffen Jacobs
 */
public class PacketJoinLobby extends PacketMatchmakingRequest {
	private static final long serialVersionUID = 4705919912303592072L;
	private final UUID lobbyID;

	/** constructor for lobby-packet 
	 * @param name the name of the player trying to join
	 * @param playerUID the uuid of the player trying to join
	 * @param lobbyID the ID of the lobby the player wants to join
	 * @param abort whether the player wants to join or quit*/
	public PacketJoinLobby(String name, UUID playerUID, UUID lobbyID, boolean abort) {
		super(PacketType.MATCHMAKING_REQUEST, name, playerUID, abort);
		this.lobbyID = lobbyID;
	}

	/** @return a representation of the packet as a String */
	@Override
	public String toString() {
		return super.toString() + " LobbyID: " + lobbyID;
	}

	/** @return the unique ID of the requested lobby */
	public UUID getLobbyID() {
		return lobbyID;
	}
}