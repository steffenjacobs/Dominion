package com.tpps.technicalServices.network.matchmaking.server;

import java.util.concurrent.CopyOnWriteArrayList;

public class GameLobby {

	private int lobbyScore = 0;

	private CopyOnWriteArrayList<MPlayer> players = new CopyOnWriteArrayList<>();

	public void joinPlayer(MPlayer player) {
		this.players.add(player);
		this.updateLobbyScore();
	}

	private void updateLobbyScore() {
		this.lobbyScore = 0;
		for (MPlayer player : this.players) {
			this.lobbyScore = getLobbyScore() + player.calculateMatchmakingScore();
		}
		if (this.players.size() > 0) {
			this.lobbyScore = this.getLobbyScore() / this.players.size();
		}
	}

	public void quitPlayer(MPlayer player) {
		this.players.remove(player);
		this.updateLobbyScore();

	}

	public boolean isEmpty() {
		return this.players.size() == 0;
	}

	public int getLobbyScore() {
		return lobbyScore;
	}
}