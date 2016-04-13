package com.tpps.technicalServices.network.matchmaking.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class PacketMatchmakingRequest extends Packet {
	private static final long serialVersionUID = 1569507891993590996L;
	
	private final String playerName;
	private final UUID playerID;
	private final boolean abort;


	public PacketMatchmakingRequest(String name, UUID uid, boolean abort) {
		super(PacketType.MATCHMAKING_REQUEST);
		this.playerID = uid;
		this.playerName = name;
		this.abort = abort;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAbort() {
		return abort;
	}

	public String getPlayerName() {
		return playerName;
	}

	public UUID getPlayerID() {
		return playerID;
	}
}
