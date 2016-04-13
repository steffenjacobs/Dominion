package com.tpps.technicalServices.network.matchmaking.server;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * represents a game-lobby with players waiting in it to start the game
 * 
 * @author Steffen Jacobs
 */
public class GameLobby {

	private static final int MAX_LOBBY_SIZE = 4;

	private int lobbyScore = 0;

	private CopyOnWriteArrayList<MPlayer> players = new CopyOnWriteArrayList<>();
	private long startTime = 0;

	/**
	 * sets the time the game started, should be called just before the game
	 * starts
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * adds a player to the lobby
	 * 
	 * @param player
	 *            player to add to the lobby
	 */
	public void joinPlayer(MPlayer player) {
		System.out.println("[" + System.identityHashCode(this) + "] <-" + player.getPlayerName());
		this.players.add(player);
		this.updateLobbyScore();

		MatchmakingServer.getInstance().sendJoinPacket(players, player.getPlayerName());
		if (this.players.size() != 0) {
			for (MPlayer mplayer : players) {
				MatchmakingServer.getInstance().sendJoinPacket(mplayer, mplayer.getPlayerName());
			}
		}

	}

	/** updates the average matchmaking-score of the players in the lobby */
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
			this.startTime = System.currentTimeMillis();
		}
	}

	/**
	 * removes a player from the lobby
	 * 
	 * @param player
	 *            the player who quits the lobby
	 */
	public void quitPlayer(MPlayer player) {
		this.players.remove(player);
		this.updateLobbyScore();

		MatchmakingServer.getInstance().sendQuitPacket(players, player.getPlayerName());
	}

	/** @return if the lobby is empty */
	public boolean isEmpty() {
		return this.players.size() == 0;
	}

	/** @return the average matchmaking-score of the players in the lobby */
	public int getLobbyScore() {
		return lobbyScore;
	}

	/** @return all players in the lobby */
	public CopyOnWriteArrayList<MPlayer> getPlayers() {
		return this.players;
	}
}