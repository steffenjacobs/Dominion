package com.tpps.technicalServices.network.matchmaking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.technicalServices.network.gameSession.packets.PacketRegistratePlayerByServer;
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
		gameServersByLobby = new ConcurrentHashMap<>();
		lobbiesByID = new ConcurrentHashMap<>();
	}

	private static ConcurrentHashMap<Integer, MPlayer> playersByPort;
	private static ConcurrentHashMap<MPlayer, Integer> connectedPortsByPlayer;
	private static ConcurrentHashMap<String, MPlayer> playersByName;
	private static ConcurrentHashMap<MPlayer, GameLobby> lobbiesByPlayer;
	private static ConcurrentHashMap<GameLobby, Thread> gameServersByLobby;

	private static ConcurrentHashMap<UUID, GameLobby> lobbiesByID;

	private static Semaphore blockPort = new Semaphore(1);

	private static CopyOnWriteArrayList<GameLobby> lobbies;

	private static ExecutorService exec = Executors.newCachedThreadPool();

	/**
	 * @return the GameLobby associated with the lobbyID
	 * @param lobbyID
	 *            the ID of the lobby to get
	 */
	static GameLobby getLobbyByID(UUID lobbyID) {
		return lobbiesByID.get(lobbyID);
	}

	/** @return a newly generated, unique ID for the lobby */
	static UUID generateLobbyID() {
		UUID rnd = null;
		do {
			rnd = UUID.randomUUID();
		} while (lobbiesByID.containsKey(rnd));
		return rnd;
	}

	/**
	 * @param player
	 *            the player to get the associated GameLobby from
	 * @return the GameLobby-instance associated with the MPlayer-object
	 */
	static GameLobby getLobbyFromPlayer(MPlayer player) {
		return lobbiesByPlayer.get(player);
	}

	/**
	 * starts a game, if the lobby is full
	 * 
	 * @param lobby
	 *            the GameLobby to start
	 */
	static void startGame(GameLobby lobby) {
		exec.submit(() -> {
			// removeLobby(lobby);
			String[] playerNames = new String[lobby.getPlayers().size()];
			MPlayer player;

			boolean hasAI = false;

			for (int i = 0; i < lobby.getPlayers().size(); i++) {
				player = lobby.getPlayers().get(i);
				playerNames[i] = player.getPlayerName();

				if (!hasAI && player.getPlayerUID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
					hasAI = true;
				}
			}

			// reserve port & start game-process
			try {
				blockPort.acquire(1);
				int freePort = getFreePort();

				/* start server */
				Thread gsThread = new Thread(() -> {
					try {
						new GameServer(freePort);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				gameServersByLobby.put(lobby, gsThread);
				gsThread.start();
				Thread.sleep(500);

				Client cl = null;
				if (hasAI) {
					cl = new Client(new InetSocketAddress(Addresses.getLocalHost(), freePort), new PacketHandler() {
						@Override
						public void handleReceivedPacket(int port, Packet packet) {
							// do nothing - be dummy
						}
					}, false);
				}

				for (MPlayer pl : lobby.getPlayers()) {

					/* send AI-register packets */
					if (pl.getPlayerUID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
						try {
							cl.sendMessage(new PacketRegistratePlayerByServer("AI" + System.identityHashCode(cl),
									pl.getPlayerUID()));
							Thread.sleep(100);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					else {
						/* send success to client */
						MatchmakingServer.getInstance().sendSuccessPacket(pl, playerNames, freePort);
					}
				}
				cl.disconnect();
				blockPort.release(1);

			} catch (InterruptedException | IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	/**
	 * @return a random free port
	 * @throws IOException
	 */
	private static int getFreePort() throws IOException {
		ServerSocket srv = new ServerSocket(0);
		srv.close();
		return srv.getLocalPort();
	}

	/**
	 * removes a lobby, if it is empty
	 * 
	 * @param lobby
	 *            the GameLobby to remove
	 */
	private static void removeLobby(GameLobby lobby) {
		lobbies.remove(lobby);
		lobbiesByID.remove(lobby.getLobbyID());
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

	/** creates new empty lobbies */
	private static void updateLobbyCount() {
		int cntAvailableLobbies = 0;
		for (GameLobby gl : lobbies) {
			if (gl.isAvailable()) {
				cntAvailableLobbies++;
			}
		}
		if (cntAvailableLobbies < 2) {
			for (; cntAvailableLobbies < 2; cntAvailableLobbies++) {
				GameLobby lobby = new GameLobby();
				lobbies.add(lobby);
			}
		}
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
		updateLobbyCount();

		// if (lobbies.isEmpty()) {
		// GameLobby lobby = new GameLobby();
		// lobbies.add(lobby);
		// lobbiesByID.put(lobby.getLobbyID(), lobby);
		// joinLobby(player, lobby);
		// } else
		// if (lobbies.size() == 1 && !lobbies.get(0).isFull() &&
		// !lobbies.get(0).hasStarted()) {
		// GameLobby lobby = lobbies.get(0);
		// joinLobby(player, lobby);
		// } else {
		Iterator<GameLobby> it = lobbies.iterator();
		GameLobby gl, bestFitting = null;
		int minDelta = Integer.MAX_VALUE;
		while (it.hasNext()) {
			gl = it.next();
			if (!gl.isAvailable()) {
				continue;
			}
			int delta = Math.abs(gl.getLobbyScore() - score);
			if (minDelta > delta) {
				minDelta = delta;
				bestFitting = gl;
			}
		}
		if (bestFitting == null) {
			bestFitting = new GameLobby();
		}

		joinLobby(player, bestFitting);
		// }

	}

	/**
	 * starts the match-finding process for a player & adds him to the
	 * matchmaking-system
	 * 
	 * @param player
	 *            the player to add
	 * @param search
	 *            whether to search for a lobby or not
	 */
	public static void addPlayer(MPlayer player, boolean search) {
		playersByPort.put(player.getConnectionPort(), player);
		connectedPortsByPlayer.put(player, player.getConnectionPort());
		playersByName.put(player.getPlayerName(), player);
		if (search)
			findLobbyForPlayer(player);
	}

	/**
	 * removes a player from the matchmaking-system
	 * 
	 * @param player
	 *            the player to remove
	 */
	private static void removePlayer(MPlayer player) {
		
		//remove client
		GameLog.log(MsgType.NETWORK_INFO, "[-> " + player.getPlayerName() + " @" + player.getConnectionPort());
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
		if (playersByPort.containsKey(port)) {
			removePlayer(playersByPort.get(port));
		} else {
			// nothing
		}
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
			MPlayer player = playersByName.get(p);
			int port = connectedPortsByPlayer.get(player);
			MatchmakingServer.getInstance().disconnect(port);

//			removePlayer(player);

		}
		SQLStatisticsHandler.addWinOrLoss(winner, true);

		gameServersByLobby.remove(lobby);
	}

	/** @return a readable representation of all active lobbies */
	public static String[] getLobbies() {
		String[] res = new String[lobbies.size()];
		int i = 0;
		for (GameLobby gl : lobbies) {
			res[i] = gl.toString();
			i++;
		}
		return res;
	}

	/** @return a readable representation of all waiting & playing players */
	public static String[] getPlayers() {
		String[] res = new String[playersByPort.size()];
		int cnt = 0;
		for (Map.Entry<Integer, MPlayer> entr : playersByPort.entrySet()) {
			res[cnt] = entr.getValue().getPlayerName() + " @" + entr.getKey();
			cnt++;
		}
		return res;
	}
}