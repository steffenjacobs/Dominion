package com.tpps.technicalServices.network.matchmaking.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class PacketMatchmakingSuccessful extends Packet {
	private static final long serialVersionUID = 8824882326543059050L;
	
	private String[] joinedPlayers;

	protected PacketMatchmakingSuccessful(String[] joinedPlayers) {
		super(PacketType.MATCHMAKING_SUCCESSFUL);
		this.setJoinedPlayers(joinedPlayers);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getJoinedPlayers() {
		return joinedPlayers;
	}

	public void setJoinedPlayers(String[] joinedPlayers) {
		this.joinedPlayers = joinedPlayers;
	}

}
