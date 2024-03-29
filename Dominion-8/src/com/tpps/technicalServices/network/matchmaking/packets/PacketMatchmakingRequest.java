package com.tpps.technicalServices.network.matchmaking.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * is sent from the Matchmaker to the Matchmaking-Server to find a lobby,
 * answered with a PacketMatchmakingAnswer by the server
 * 
 * @author Steffen Jacobs
 */
public class PacketMatchmakingRequest extends Packet {
	private static final long serialVersionUID = 1569507891993590996L;

	private final String playerName;
	private final UUID playerID;
	private final boolean abort, privateMatch;

	/**
	 * constructor with the name & uuid of the player searching for a match or
	 * aborting the search
	 * 
	 * @param name
	 *            name of the player who searches for a match
	 * @param uid
	 *            uuid of the player who searches for a match
	 * @param abort
	 *            true if the search should be aborted, false otherwise
	 * @param privateMatch
	 *            whether the packet contains a private match
	 */
	public PacketMatchmakingRequest(String name, UUID uid, boolean abort, boolean privateMatch) {
		super(PacketType.MATCHMAKING_REQUEST);
		this.playerID = uid;
		this.playerName = name;
		this.abort = abort;
		this.privateMatch = privateMatch;
	}

	/**
	 * constructor for inheriting objects with the name & uuid of the player
	 * searching for a match or aborting the search
	 * 
	 * @param name
	 *            name of the player who searches for a match
	 * @param uid
	 *            uuid of the player who searches for a match
	 * @param abort
	 *            true if the search should be aborted, false otherwise
	 * @param type
	 *            sub-type of the packet
	 * @param privateMatch
	 *            whether the packet contains a private match
	 */
	public PacketMatchmakingRequest(PacketType type, String name, UUID uid, boolean abort, boolean privateMatch) {
		super(type);
		this.playerID = uid;
		this.playerName = name;
		this.abort = abort;
		this.privateMatch = privateMatch;
	}

	/** @return a representation of the packet as a String */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getPlayerName() + " - " + this.getPlayerID()
				+ " - aborting: " + this.isAbort();
	}

	/** @return true, if the search should be aborted */
	public boolean isAbort() {
		return abort;
	}

	/**
	 * @return whether the match is private
	 */
	public boolean isPrivate() {
		return privateMatch;
	}

	/** @return the name of the player searching for a match */
	public String getPlayerName() {
		return playerName;
	}

	/** @return the uuid of the player searching for a match */
	public UUID getPlayerID() {
		return playerID;
	}
}
