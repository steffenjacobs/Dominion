package com.tpps.technicalServices.network.matchmaking.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.game.GameServer;

/**
 * represents a game-lobby with players waiting in it to start the game
 * 
 * @author Steffen Jacobs
 */
public class GameLobby {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
	private static final int MAX_LOBBY_SIZE = 4;

	private final UUID lobbyID;

	private CopyOnWriteArrayList<MPlayer> players = new CopyOnWriteArrayList<>();
	private int lobbyScore = 0;

	private long startTime = 0;
	private GameServer runningServer;

	private MPlayer admin;

	private final boolean isPrivate;
	
	
	/**is called when the game starts*/
	public void onStart(){
		this.startTime = System.currentTimeMillis();
	}

	/**
	 * constructor, generating its unique lobbyID
	 * 
	 * @param isPrivate
	 *            whether the packet contains a private match
	 */
	public GameLobby(boolean isPrivate) {
		this.lobbyID = MatchmakingController.generateLobbyID();
		this.isPrivate = isPrivate;
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
		return this.players.size() >= 4;
	}

	/** @return whether the game has started yet */
	public boolean hasStarted() {
		return this.runningServer != null && this.startTime != 0;
	}

	/**
	 * sets the instance of the game-server that is running this game
	 * 
	 * @param gs
	 *            the instanceo of the game-server this lobby is running on
	 */
	public void setServer(GameServer gs) {
		this.runningServer = gs;
	}

	/** @return the instance of the game-server that is running this game */
	public GameServer getServer() {
		return this.runningServer;
	}

	/**
	 * @param player
	 *            the player to check admin for
	 * @return if a player is lobby-admin
	 */
	public boolean isAdmin(MPlayer player) {
		if (this.admin == null || player == null || player.isAI())
			return false;
		return this.admin == player;
	}

	/**
	 * updates the lobby-admin
	 * 
	 * @param player
	 *            the new lobby-admin
	 */
	private void setAdmin(MPlayer player) {
		this.admin = player;
	}

	/**
	 * adds a player to the lobby
	 * 
	 * @param player
	 *            player to add to the lobby
	 */
	public void joinPlayer(MPlayer player) {
		if (!player.isAI() && admin == null) {
			setAdmin(player);
		}
		GameLog.log(MsgType.INFO, "[" + this.getLobbyID() + "] <-" + player.getPlayerName());
		for (MPlayer mplayer : players) {

			// send the new player the old player
			if (!player.isAI()) {
				MatchmakingServer.getInstance().sendJoinPacket(player, mplayer.getPlayerName(), isAdmin(mplayer));
			}

			// send the old player the new player
			if (!mplayer.isAI()) {
				MatchmakingServer.getInstance().sendJoinPacket(mplayer, player.getPlayerName(), isAdmin(player));
			}
		}

		// send the new player himself
		if (!player.isAI()) {
			MatchmakingServer.getInstance().sendJoinPacket(player, player.getPlayerName(), isAdmin(player));
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
			// Do nothing -> Lobby is full and waits to start
			// this.startTime = System.currentTimeMillis();
			// MatchmakingController.startGame(this);
		}

		int aiCounter = 0;
		for (MPlayer player : this.players) {
			if (player.isAI()) {
				aiCounter++;
			}
		}

		if (aiCounter > 0) {
			try {
				this.lobbyScore = this.lobbyScore / (this.getPlayers().size() - aiCounter) * this.getPlayers().size();
			} catch (ArithmeticException divByZero) {
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
			// say everyone goodbye
			if (!mplayer.isAI()) {
				MatchmakingServer.getInstance().sendQuitPacket(mplayer, player.getPlayerName(), isAdmin(player));
			}
		}
		if (isAdmin(player)) {
			this.admin = null;
			ArrayList<MPlayer> removeAI = new ArrayList<>();
			for (MPlayer pl : this.players) {

				// remove added AIs
				if (pl.isAI()) {
					removeAI.add(pl);
				}

				else {
					// find new lobby-admin
					if (this.admin == null) {
						setAdmin(pl);
					}
				}
			}

			// clear marked AIs
			for (MPlayer p : removeAI) {
				this.players.remove(p);
				MatchmakingController.removeAiPlayer(p.getPlayerName());
			}

			for (MPlayer mpl : this.players) {

				for (MPlayer ai : removeAI) {
					// send AI-Quit-Packet to everyone
					MatchmakingServer.getInstance().sendQuitPacket(mpl, ai.getPlayerName(), false);
				}

				// tell everyone who is new admin
				MatchmakingServer.getInstance().sendQuitPacket(mpl, this.admin.getPlayerName(), false);
				MatchmakingServer.getInstance().sendJoinPacket(mpl, this.admin.getPlayerName(), true);
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

	/** @return whether this lobby could support more players */
	public boolean isAvailable() {
		return !this.hasStarted() && !this.isFull() && !this.isPrivate;
	}

	/** @return all players in the lobby */
	public CopyOnWriteArrayList<MPlayer> getPlayers() {
		return this.players;
	}

	/** @return a readable representation of the object */
	@Override
	public String toString() {
		String res = "[" + this.getLobbyID() + "] ";

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