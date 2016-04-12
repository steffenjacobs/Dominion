package com.tpps.technicalServices.network.matchmaking.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class PacketMatchmakingSuccessful extends Packet {
	private static final long serialVersionUID = 8824882326543059050L;

	private final String[] joinedPlayers;
	private final int gameserverPort;

	protected PacketMatchmakingSuccessful(String[] joinedPlayers, int gameserverPort) {
		super(PacketType.MATCHMAKING_SUCCESSFUL);
		this.joinedPlayers = joinedPlayers;
		this.gameserverPort = gameserverPort;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getJoinedPlayers() {
		return joinedPlayers;
	}

	public int getGameserverPort() {
		return gameserverPort;
	}

}
