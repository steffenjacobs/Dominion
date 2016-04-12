package com.tpps.technicalServices.network.matchmaking.server;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class MatchmakingController {

	static {
		connectedPlayersByPort = new ConcurrentHashMap<Integer, MPlayer>();
		lobbies = new CopyOnWriteArrayList<>();
		lobbiesByPlayer = new ConcurrentHashMap<>();
	}

	private static ConcurrentHashMap<Integer, MPlayer> connectedPlayersByPort;
	private static ConcurrentHashMap<MPlayer, GameLobby> lobbiesByPlayer;

	private static CopyOnWriteArrayList<GameLobby> lobbies;

	private static void removeLobby(GameLobby lobby) {
		lobbies.remove(lobby);
		// INFO: lobby could not be in lobbiesByPlayer, since no player is in
		// the lobby
	}

	private static void joinLobby(MPlayer player, GameLobby lobby) {
		lobby.joinPlayer(player);
		lobbiesByPlayer.put(player, lobby);
	}

	private static void findLobbyForPlayer(MPlayer player) {
		int score = player.calculateMatchmakingScore();

		if (lobbies.isEmpty()) {
			GameLobby lobby = new GameLobby();
			lobbies.add(lobby);
			joinLobby(player, lobby);
		} else if (lobbies.size() == 1) {
			GameLobby lobby = lobbies.get(connectedPlayersByPort.keys().nextElement());
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
		connectedPlayersByPort.put(player.getConnectionPort(), player);
		findLobbyForPlayer(player);
	}

	private static void removePlayer(MPlayer player) {
		connectedPlayersByPort.remove(player.getConnectionPort());
		GameLobby lobby = lobbiesByPlayer.remove(player);
		if (lobby != null) {
			lobby.quitPlayer(player);
			if (lobby.isEmpty()) {
				removeLobby(lobby);
			}
		}
	}

	public static void onPlayerDisconnect(int port) {
		removePlayer(connectedPlayersByPort.get(port));
	}
}