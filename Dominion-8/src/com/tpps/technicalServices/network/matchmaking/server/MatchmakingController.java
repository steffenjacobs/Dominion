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

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.technicalServices.network.gameSession.packets.PacketRegistratePlayerByServer;
import com.tpps.technicalServices.network.login.SQLHandling.SQLStatisticsHandler;
import com.tpps.technicalServices.network.matchmaking.packets.PacketGameEnd;

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
		lobbiesByPlayerName = new ConcurrentHashMap<>();
		playersByName = new ConcurrentHashMap<>();
		// connectedPortsByPlayer = new ConcurrentHashMap<>();
		lobbiesByID = new ConcurrentHashMap<>();
		runningLobbiesByPort = new ConcurrentHashMap<>();
	}

	private static ConcurrentHashMap<Integer, MPlayer> playersByPort;
	// private static ConcurrentHashMap<MPlayer, Integer>
	// connectedPortsByPlayer;

	/*** also contains AI-names */
	private static ConcurrentHashMap<String, MPlayer> playersByName;

	private static ConcurrentHashMap<String, GameLobby> lobbiesByPlayerName;
	private static ConcurrentHashMap<Integer, GameLobby> runningLobbiesByPort;

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

	/**
	 * create a private lobby for a player
	 * 
	 * @param player
	 *            the player who creates a private lobby
	 */
	static void createPrivateLobby(MPlayer player) {
		GameLobby lobby = new GameLobby(true);
		lobbies.add(lobby);
		lobbiesByID.put(lobby.getLobbyID(), lobby);
		lobby.joinPlayer(player);
		lobbiesByPlayerName.put(player.getPlayerName(), lobby);
		GameLog.log(MsgType.INFO, player.getPlayerName() + " created a private lobby [" + lobby.getLobbyID() + "]");
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
	 * @param playerName
	 *            the player to get the associated GameLobby from
	 * @return the GameLobby-instance associated with the MPlayer-object
	 */
	static GameLobby getLobbyFromPlayerName(String playerName) {
		return lobbiesByPlayerName.get(playerName);
	}

	/**
	 * adds an ai player to the game
	 * 
	 * @param player
	 *            the ai to add
	 */
	static void addAIPlayer(MPlayer player) {
		playersByName.put(player.getPlayerName(), player);
	}

	/**
	 * @param name
	 *            the name of the player to get
	 * @return a player by its name (including AIs)
	 */
	static MPlayer removeAiPlayer(String name) {
		return playersByName.remove(name);
	}

	/**
	 * @param name
	 *            the name of the player to get the corresponding MPlayer-Object
	 *            from
	 * @return the MPlayer-object mapped to the name
	 */
	static MPlayer getPlayerFromName(String name) {
		return playersByName.get(name);
	}

	/**
	 * starts a game, if the lobby is full
	 * 
	 * @param lobby
	 *            the GameLobby to start
	 * @param selectedActionCards
	 *            the cards to play with
	 */
	static void startGame(GameLobby lobby, String[] selectedActionCards) {
		GameLog.log(MsgType.INFO, "Starting lobby " + lobby.getLobbyID());
		exec.submit(() -> {
			GameLog.log(MsgType.INFO, "Setting up lobby " + lobby.getLobbyID());
			lobby.onStart();
			// removeLobby(lobby);
			String[] playerNames = new String[lobby.getPlayers().size()];

			boolean hasAI = false;

			int i = 0;
			for (MPlayer player : lobby.getPlayers()) {
				playerNames[i] = player.getPlayerName();
				i++;
				if (!hasAI && player.getPlayerUID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
					hasAI = true;
				}
			}

			// reserve port & start game-process
			try {
				blockPort.acquire(1);
				int freePort = getFreePort();

				/* start server */
				exec.submit(() -> {
					GameLog.log(MsgType.INFO, "Started GameServer for lobby " + lobby.getLobbyID());
					try {
						GameServer gs = new GameServer(freePort, selectedActionCards);
						lobby.setServer(gs);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				runningLobbiesByPort.put(freePort, lobby);
				Thread.sleep(500);
				blockPort.release(1);

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
					if (pl.isAI()) {
						try {
							cl.sendMessage(new PacketRegistratePlayerByServer(pl.getPlayerName(), pl.getPlayerUID()));
							System.err.println("Registering AI: " + pl.getPlayerName());
							Thread.sleep(100);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					else {
						/* send success to client */
						MatchmakingServer.getInstance().sendSuccessPacket(pl, playerNames, freePort,
								selectedActionCards);
					}
				}
				cl.disconnect();

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
		if (lobby.hasStarted())
			runningLobbiesByPort.remove(lobby.getServer().getPort());
		// INFO: lobby can not be in lobbiesByPlayer, since no player is in
		// the lobby
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
		lobbiesByPlayerName.put(player.getPlayerName(), lobby);
	}

	/** creates new empty lobbies */
	private static void updateLobbyCount() {
		int cntAvailableLobbies = 0;
		int availableLobbyShould = getPlayers().length / 3;
		availableLobbyShould = availableLobbyShould < 2 ? 2 : availableLobbyShould;

		for (GameLobby gl : lobbies) {
			if (gl.isAvailable()) {
				cntAvailableLobbies++;
			}
		}
		if (cntAvailableLobbies < availableLobbyShould) {
			for (; cntAvailableLobbies < availableLobbyShould; cntAvailableLobbies++) {
				GameLobby lobby = new GameLobby(false);
				lobbies.add(lobby);
				lobbiesByID.put(lobby.getLobbyID(), lobby);
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
			bestFitting = new GameLobby(false);
		}

		joinLobby(player, bestFitting);
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
		// connectedPortsByPlayer.put(player, player.getConnectionPort());
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

		// remove client
		GameLog.log(MsgType.NETWORK_INFO, "[-> " + player.getPlayerName() + " @" + player.getConnectionPort());
		playersByPort.remove(player.getConnectionPort());
		// connectedPortsByPlayer.remove(player);
		playersByName.remove(player.getPlayerName());
		GameLobby lobby = lobbiesByPlayerName.remove(player.getPlayerName());
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
	 * @param endPacket
	 *            the packet with the winner and all participants in the game
	 *            that stayed until it ended
	 * @param port
	 *            the port the ended server was running on
	 */
	public static void onGameEnd(PacketGameEnd endPacket, int port) {
		GameLobby lobby = getLobbyFromPlayerName(endPacket.getWinner());
		if (lobby != null) {
			GameLog.log(MsgType.INFO, "Received end-packet: " + endPacket.toString());
		} else {
			GameLog.log(MsgType.ERROR, "Received bad end-packet: " + endPacket.toString());
			return;
		}
		for (String playerName : endPacket.getPlayers()) {

			MPlayer player = playersByName.get(playerName);

			if (player != null && player.isAI()) {
				continue;
			}

			if (!DominionController.isOffline()) {
				// Every player except AIs
				SQLStatisticsHandler.addOverallPlaytime(playerName, System.currentTimeMillis() - lobby.getStartTime());
				if (!playerName.equals(endPacket.getWinner())) {
					// player lost
					SQLStatisticsHandler.addWinOrLoss(playerName, false);
				}
			}

			// int port = connectedPortsByPlayer.get(player);
			try {
				MatchmakingServer.getInstance().disconnect(player.getConnectionPort());
			} catch (NullPointerException ex) {
				System.err.println("Error while disconnecting: not connected!");
			}
		}
		if (!DominionController.isOffline()) {
			// Add win for player who won
			SQLStatisticsHandler.addWinOrLoss(endPacket.getWinner(), true);
		}

		lobby.getServer().stopSrv();

		lobbiesByID.remove(lobby.getLobbyID());
		lobbies.remove(lobby);
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