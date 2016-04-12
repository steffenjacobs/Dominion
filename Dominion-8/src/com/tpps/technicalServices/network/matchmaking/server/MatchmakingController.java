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
	private static ConcurrentHashMap<MPlayer, Integer> connectedPortsByPlayer;
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
		connectedPortsByPlayer.put(player, player.getConnectionPort());
		findLobbyForPlayer(player);
	}

	private static void removePlayer(MPlayer player) {
		connectedPlayersByPort.remove(player.getConnectionPort());
		connectedPortsByPlayer.remove(player);
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