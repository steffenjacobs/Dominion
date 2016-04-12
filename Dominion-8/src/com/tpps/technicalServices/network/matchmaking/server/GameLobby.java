package com.tpps.technicalServices.network.matchmaking.server;

import java.util.concurrent.CopyOnWriteArrayList;

public class GameLobby {

	private static final int MAX_LOBBY_SIZE = 4;

	private int lobbyScore = 0;

	private CopyOnWriteArrayList<MPlayer> players = new CopyOnWriteArrayList<>();

	public void joinPlayer(MPlayer player) {
		this.updateLobbyScore();
		MatchmakingServer.getInstance().sendJoinPacket( players, player.getPlayerName());
		if (this.players.size() != 0) {
			for (MPlayer mplayer : players) {
				MatchmakingServer.getInstance().sendJoinPacket(mplayer, mplayer.getPlayerName());
			}
		}

		this.players.add(player);
	}

	private void updateLobbyScore() {
		this.lobbyScore = 0;
		for (MPlayer player : this.players) {
			this.lobbyScore = getLobbyScore() + player.getScore();
		}
		if (this.players.size() > 0) {
			this.lobbyScore = this.getLobbyScore() / this.players.size();
		}
		if (this.players.size() >= MAX_LOBBY_SIZE) {
			MatchmakingController.startGame(this);
		}
	}

	public void quitPlayer(MPlayer player) {
		this.players.remove(player);
		this.updateLobbyScore();
		
		MatchmakingServer.getInstance().sendQuitPacket(players, player.getPlayerName());
	}

	public boolean isEmpty() {
		return this.players.size() == 0;
	}

	public int getLobbyScore() {
		return lobbyScore;
	}

	public CopyOnWriteArrayList<MPlayer> getPlayers() {
		return this.players;
	}
}