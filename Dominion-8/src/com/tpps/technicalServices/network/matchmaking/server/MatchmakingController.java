package com.tpps.technicalServices.network.matchmaking.server;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.tpps.technicalServices.network.login.SQLHandling.SQLStatisticsHandler;

public final class MatchmakingController {

	static {
		playersByPort = new ConcurrentHashMap<Integer, MPlayer>();
		lobbies = new CopyOnWriteArrayList<>();
		lobbiesByPlayer = new ConcurrentHashMap<>();
	}

	private static ConcurrentHashMap<Integer, MPlayer> playersByPort;
	private static ConcurrentHashMap<MPlayer, Integer> connectedPortsByPlayer;
	private static ConcurrentHashMap<String, MPlayer> playersByName;
	private static ConcurrentHashMap<MPlayer, GameLobby> lobbiesByPlayer;

	private static CopyOnWriteArrayList<GameLobby> lobbies;

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

	private static void removeLobby(GameLobby lobby) {
		lobbies.remove(lobby);
		// INFO: lobby could not be in lobbiesByPlayer, since no player is in
		// the lobby
	}

	public static int getPortFromPlayer(MPlayer player) {
		return connectedPortsByPlayer.get(player);
	}

	private static void joinLobby(MPlayer player, GameLobby lobby) {
		lobby.joinPlayer(player);
		lobbiesByPlayer.put(player, lobby);
	}

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

	public static void addPlayer(MPlayer player) {
		playersByPort.put(player.getConnectionPort(), player);
		connectedPortsByPlayer.put(player, player.getConnectionPort());
		playersByName.put(player.getPlayerName(), player);
		findLobbyForPlayer(player);
	}

	private static void removePlayer(MPlayer player) {
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

	public static void onPlayerDisconnect(int port) {
		removePlayer(playersByPort.get(port));
	}

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