package com.tpps.technicalServices.network.matchmaking.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class PacketMatchmakingPlayerInfo extends Packet{
	private static final long serialVersionUID = 2384133814902839060L;
	
	private final String playerNameInfo;
	private final boolean status;

	public PacketMatchmakingPlayerInfo(String playerJoining, boolean joined) {
		super(PacketType.MATCHMAKING_PLAYER_INFO);
		this.playerNameInfo = playerJoining;
		this.status = joined;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPlayerNameJoining() {
		return playerNameInfo;
	}

	public boolean isStatus() {
		return status;
	}
}
