package com.tpps.technicalServices.network.matchmaking.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class PacketGameEnd extends Packet {
	private static final long serialVersionUID = 1304847662526789376L;
	
	private final String[] players;
	private final String winner;

	protected PacketGameEnd(String[] players, String winner) {
		super(PacketType.GAME_END);
		this.winner = winner;
		this.players = players;
	}

	@Override
	public String toString() {
		return null;
	}

	public String getWinner() {
		return winner;
	}

	public String[] getPlayers() {
		return players;
	}
}