package com.tpps.technicalServices.network.matchmaking.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class MatchmakingRequest extends Packet {
	private static final long serialVersionUID = 1569507891993590996L;
	
	private final String playerName;
	private final UUID playerID;

	public String getPlayerName() {
		return playerName;
	}

	public UUID getPlayerID() {
		return playerID;
	}

	public MatchmakingRequest(String name, UUID uid) {
		super(PacketType.MATCHMAKING_REQUEST);
		this.playerID = uid;
		this.playerName = name;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
