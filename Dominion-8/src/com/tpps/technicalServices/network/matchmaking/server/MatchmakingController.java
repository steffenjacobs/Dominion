package com.tpps.technicalServices.network.matchmaking.server;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.tpps.technicalServices.network.login.SQLHandling.SQLStatisticsHandler;

/**
 * this represents the main controller managing all the lobbies and putting the
 * players into lobbies
 * 
 * @author Steffen Jacobs
 */
public final class MatchmakingController {

	static {
		playersByPort = new ConcurrentHashMap<Integer, MPlayer>();
		lobbies = new CopyOnWriteArrayList<>();
		lobbiesByPlayer = new ConcurrentHashMap<>();
		playersByName = new ConcurrentHashMap<>();
		connectedPortsByPlayer = new ConcurrentHashMap<>();
	}

	private static ConcurrentHashMap<Integer, MPlayer> playersByPort;
	private static ConcurrentHashMap<MPlayer, Integer> connectedPortsByPlayer;
	private static ConcurrentHashMap<String, MPlayer> playersByName;
	private static ConcurrentHashMap<MPlayer, GameLobby> lobbiesByPlayer;

	private static CopyOnWriteArrayList<GameLobby> lobbies;

	/**
	 * starts a game, if the lobby is full
	 * 
	 * @param lobby
	 *            the GameLobby to start
	 */
	static void startGame(GameLobby lobby) {
		removeLobby(lobby);
		String[] playerNames = new String[lobby.getPlayers().size()];
		MPlayer player;
		for (int i = 0; i < lobby.getPlayers().size(); i++) {
			player = lobby.getPlayers().get(i);
			playerNames[i] = player.getPlayerName();
		}
		MatchmakingServer.getInstance().sendSuccessPacket(lobby.getPlayers(), playerNames);

	}

	/**
	 * removes a lobby, if it is empty
	 * 
	 * @param lobby
	 *            the GameLobby to remove
	 */
	private static void removeLobby(GameLobby lobby) {
		lobbies.remove(lobby);
		// INFO: lobby could not be in lobbiesByPlayer, since no player is in
		// the lobby
	}

	/**
	 * @return the port a player is connected with
	 * @param player
	 *            the requested player
	 */
	public static int getPortFromPlayer(MPlayer player) {
		return connectedPortsByPlayer.get(player);
	}

	/**
	 * adds a player to a lobby
	 * 
	 * @param player
	 *            the player to add to the lobby
	 * @param lobby
	 *            the lobby the player is added to
	 */
	private static void joinLobby(MPlayer player, GameLobby lobby) {
		lobby.joinPlayer(player);
		lobbiesByPlayer.put(player, lobby);
	}

	/**
	 * finds a lobby best-fitting to the player's matchmaking-score and puts the
	 * player in
	 * 
	 * @param player
	 *            the player to find a lobby for
	 */
	private static void findLobbyForPlayer(MPlayer player) {
		int score = player.getScore();

		if (lobbies.isEmpty()) {
			GameLobby lobby = new GameLobby();
			lobbies.add(lobby);
			joinLobby(player, lobby);
		} else if (lobbies.size() == 1) {
			GameLobby lobby = lobbies.get(playersByPort.keys().nextElement());
			joinLobby(player, lobby);
		} else {
			Iterator<GameLobby> it = lobbies.iterator();
			GameLobby gl, bestFitting = null;
			int minDelta = Integer.MAX_VALUE;
			while (it.hasNext()) {
				gl = it.next();
				int delta = Math.abs(gl.getLobbyScore() - score);
				if (minDelta > delta) {
					minDelta = delta;
					bestFitting = gl;
				}
			}
			joinLobby(player, bestFitting);
		}

	}

	/**
	 * starts the match-finding process for a player & adds him to the
	 * matchmaking-system
	 * 
	 * @param player
	 *            the player to add
	 */
	public static void addPlayer(MPlayer player) {
		playersByPort.put(player.getConnectionPort(), player);
		connectedPortsByPlayer.put(player, player.getConnectionPort());
		playersByName.put(player.getPlayerName(), player);
		findLobbyForPlayer(player);
	}

	/**
	 * removes a player from the matchmaking-system
	 * 
	 * @param player
	 *            the player to remove
	 */
	private static void removePlayer(MPlayer player) {
		if (player == null) {
			System.err.println("Player not found: " + player);
			return;
		}
		playersByPort.remove(player.getConnectionPort());
		connectedPortsByPlayer.remove(player);
		playersByName.remove(player.getPlayerName());
		GameLobby lobby = lobbiesByPlayer.remove(player);
		if (lobby != null) {
			lobby.quitPlayer(player);
			if (lobby.isEmpty()) {
				removeLobby(lobby);
			}
		}
	}

	/**
	 * called by the NetworkListener when a client disconnected
	 * 
	 * @param port
	 *            the port the client disconnected from
	 */
	public static void onPlayerDisconnect(int port) {
		removePlayer(playersByPort.get(port));
	}

	/**
	 * is called when the game-end-packet was received from the game-server,
	 * adds all statistics to the database
	 * 
	 * @param winner
	 *            the player who won the game
	 * @param players
	 *            all participants in the game that stayed until it ended
	 */
	public static void onGameEnd(String winner, String[] players) {
		GameLobby lobby = lobbiesByPlayer.get(winner);
		for (String p : players) {
			SQLStatisticsHandler.addOverallPlaytime(p, System.currentTimeMillis() - lobby.getStartTime());
			if (!p.equals(winner)) {
				SQLStatisticsHandler.addWinOrLoss(p, false);

			}

			removePlayer(playersByName.get(p));
		}
		SQLStatisticsHandler.addWinOrLoss(winner, true);

	}
}