package com.tpps.technicalServices.network.matchmaking.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class PacketMatchmakingPlayerJoining extends Packet{
	private static final long serialVersionUID = 2384133814902839060L;
	
	private final String playerNameJoining;

	protected PacketMatchmakingPlayerJoining(String playerJoining) {
		super(PacketType.MATCHMAKING_PLAYER_JOING);
		this.playerNameJoining = playerJoining;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPlayerNameJoining() {
		return playerNameJoining;
	}

}
