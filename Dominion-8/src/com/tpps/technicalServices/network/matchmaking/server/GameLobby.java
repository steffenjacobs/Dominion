package com.tpps.technicalServices.network.matchmaking.server;

import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * represents a game-lobby with players waiting in it to start the game
 * 
 * @author Steffen Jacobs
 */
public class GameLobby {

	private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

	private static final int MAX_LOBBY_SIZE = 4;

	private int lobbyScore = 0;

	private CopyOnWriteArrayList<MPlayer> players = new CopyOnWriteArrayList<>();
	private long startTime = 0;

	private final UUID lobbyID;

	/** constructor, generating its unique lobbyID */
	public GameLobby() {
		this.lobbyID = MatchmakingController.generateLobbyID();
	}

	/**
	 * sets the time the game started, should be called just before the game
	 * starts
	 * 
	 * @return the time the match started
	 */
	public long getStartTime() {
		return startTime;
	}

	/** @return whether there are 4 players in the lobby */
	public boolean isFull() {
		return this.players.size() == 4;
	}

	/** @return wether the game has started yet */
	public boolean hasStarted() {
		return this.startTime != 0;
	}

	/**
	 * adds a player to the lobby
	 * 
	 * @param player
	 *            player to add to the lobby
	 */
	public void joinPlayer(MPlayer player) {
		System.out.println("[" + System.identityHashCode(this) + "] <-" + player.getPlayerName());
		for (MPlayer mplayer : players) {

			// send the new player the old player
			if (!player.isAI()) {
				MatchmakingServer.getInstance().sendJoinPacket(player, mplayer.getPlayerName());
			}

			// send the old player the new player
			if (!mplayer.isAI()) {
				MatchmakingServer.getInstance().sendJoinPacket(mplayer, player.getPlayerName());
			}
		}

		// send the new player himself
		if (!player.isAI()) {
			MatchmakingServer.getInstance().sendJoinPacket(player, player.getPlayerName());
		}

		this.players.add(player);
		this.updateLobbyScore();

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

		int aiCounter = 0;
		for (MPlayer player : this.players) {
			if (player.isAI()) {
				aiCounter++;
			}
		}

		if (aiCounter > 0) {
			try{
			this.lobbyScore = this.lobbyScore / (this.getPlayers().size() - aiCounter) * this.getPlayers().size();
			}
			catch(ArithmeticException divByZero){
				this.lobbyScore = 0;
			}
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

		for (MPlayer mplayer : this.players) {
			if (!mplayer.isAI()) {
				MatchmakingServer.getInstance().sendQuitPacket(mplayer, player.getPlayerName());
			}
		}

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

	/** @return a readable representation of the object */
	@Override
	public String toString() {
		String res = "[" + System.identityHashCode(this) + "] ";

		if (this.startTime != 0) {
			res += " (running since " + sdf.format(this.startTime) + ") ";
		}

		for (MPlayer p : this.players) {
			res += p.getPlayerName() + " ";
		}

		res += "Score: " + this.getLobbyScore();

		return res;
	}

	/** @return the unique ID of this lobby */
	public UUID getLobbyID() {
		return lobbyID;
	}
}