package com.tpps.technicalServices.network.matchmaking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.matchmaking.packets.PacketGameEnd;
import com.tpps.technicalServices.network.matchmaking.packets.PacketJoinLobby;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingAnswer;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingRequest;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingStart;

/**
 * PacketHandler for the MatchmakingServer
 * 
 * @author Steffen Jacobs
 */
public class MatchmakingPacketHandler extends PacketHandler {

	private SessionClient sess;

	private ExecutorService threadPool = Executors.newCachedThreadPool();

	/**
	 * constructor to initialize a connection to the session-system
	 * 
	 * @throws IOException
	 *             if the connection to the session-system could not be
	 *             established
	 */
	public MatchmakingPacketHandler() throws IOException {
		this.sess = new SessionClient(new InetSocketAddress(Addresses.getRemoteAddress(), 1337));
	}

	/**
	 * is called to handle a packet received by the Matchmaking-Server
	 * 
	 * @param port
	 *            port the packet was received from
	 * @param packet
	 *            the packet that was received
	 */
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		switch (packet.getType()) {
		case MATCHMAKING_REQUEST:
			PacketMatchmakingRequest pck = (PacketMatchmakingRequest) packet;

			if (pck.isAbort()) {
				MatchmakingController.onPlayerDisconnect(port);
			} else
				handleClientConnect(port, pck, null);
			break;
		case GAME_END:
			PacketGameEnd endPacket = (PacketGameEnd) packet;
			MatchmakingController.onGameEnd(endPacket);
			// called when the game ends
			break;
		case MATCHMAKING_JOIN_LOBBY:
			PacketJoinLobby pjl = (PacketJoinLobby) packet;
			if (!pjl.isAbort()) {

				try {
					if (pjl.getLobbyID() == null) {
						// UI-Bug: ignore...
						super.parent.sendMessage(port, new PacketMatchmakingAnswer(pjl, 2, null));
						;
						return;
					}

					GameLobby lobb = MatchmakingController.getLobbyByID(pjl.getLobbyID());
					if (lobb == null) {
						// no lobby
						super.parent.sendMessage(port, new PacketMatchmakingAnswer(pjl, 2, null));
						GameLog.log(MsgType.NETWORK_ERROR, "No such lobby as " + pjl.getLobbyID());
						return;
					}
					if (lobb.isFull()) {
						// lobby is full
						super.parent.sendMessage(port, new PacketMatchmakingAnswer(pjl, 3, null));
						GameLog.log(MsgType.NETWORK_ERROR, "Lobby " + pjl.getLobbyID() + " is full.");
						return;
					}
					if (lobb.hasStarted()) {
						// lobby has started
						super.parent.sendMessage(port, new PacketMatchmakingAnswer(pjl, 4, null));
						GameLog.log(MsgType.NETWORK_ERROR, "Lobby " + pjl.getLobbyID() + " has already started");
						return;
					}

					// lobby exists & has room for another player
					GameLog.log(MsgType.NETWORK_INFO, "Adding " + pjl.getPlayerName() + " to " + pjl.getLobbyID());

					if (pjl.getPlayerID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
						// added player is an ai
						addAI(pjl.getLobbyID(), pjl);
						GameLog.log(MsgType.NETWORK_INFO,
								"Added AI " + pjl.getPlayerName() + " to " + pjl.getLobbyID());
						return;
					}

					// Added player is a human
					handleClientConnect(port, pjl, pjl.getLobbyID());
					GameLog.log(MsgType.NETWORK_INFO,
							"Added Player " + pjl.getPlayerName() + " to " + pjl.getLobbyID());
					return;
				} catch (IOException ex) {

				}
			} else {
				if (pjl.getPlayerID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
					// quitted player is an ai
					removeAI(pjl.getLobbyID(), pjl);
					GameLog.log(MsgType.NETWORK_INFO, "Removed AI " + pjl.getPlayerName() + " to " + pjl.getLobbyID());
					return;
				}
			}
			break;

		case MATCHMAKING_START_GAME:
			PacketMatchmakingStart pms = (PacketMatchmakingStart) packet;

			// ignore invalid UUID
			if (pms.getSenderID() == null
					|| pms.getSenderID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000")))
				break;

			// check lobby
			GameLobby toStart = MatchmakingController.getLobbyByID(pms.getLobbyID());
			if (toStart == null) {
				break;
			}
			if (toStart.hasStarted()) {
				break;
			}

			// check player
			MPlayer player = MatchmakingController.getPlayerFromName(pms.getSenderName());
			if (player == null || !player.getPlayerUID().equals(pms.getSenderID()) || !toStart.isAdmin(player))
				break;

			MatchmakingController.startGame(toStart, pms.getSelectedActionCards());

			break;

		default:
			GameLog.log(MsgType.NETWORK_ERROR, "Bad packet received: " + packet);
		}
	}

	/**
	 * adds an AI-Player to a lobby
	 * 
	 * @param lobbyID
	 *            the ID of the lobby to add the AI to
	 * @param pmr
	 *            the AI-Add-Request-Packet
	 */
	public void addAI(UUID lobbyID, PacketMatchmakingRequest pmr) {
		MPlayer player = MPlayer.initialize(pmr, -1);
		if (lobbyID != null) {
			MatchmakingController.addAIPlayer(player);
			MatchmakingController.getLobbyByID(lobbyID).joinPlayer(player);
		}
		GameLog.log(MsgType.NETWORK_INFO, "<- AI " + player.getPlayerName());
	}

	/**
	 * removes an AI
	 * 
	 * @param lobbyID
	 *            the ID of the lobby to remove the AI from
	 * @param pmr
	 *            the AI-remove-request
	 */
	public static void removeAI(UUID lobbyID, PacketMatchmakingRequest pmr) {
		MPlayer player = MatchmakingController.removeAiPlayer(pmr.getPlayerName());
		if (lobbyID != null && player != null) {
			MatchmakingController.getLobbyByID(lobbyID).quitPlayer(player);
			GameLog.log(MsgType.NETWORK_INFO, "-> AI " + player.getPlayerName());
		}
	}

	/**
	 * this should be called when a client wants to find a match
	 * 
	 * @param port
	 *            the port of the client searching for a match
	 * @param pmr
	 *            the Request-Packet containing player-information
	 * @param lobbyID
	 *            the lobby the client wants to connect
	 */
	public void handleClientConnect(int port, PacketMatchmakingRequest pmr, UUID lobbyID) {

		threadPool.submit(() -> {
			if (sess.checkSessionSync(pmr.getPlayerName(), pmr.getPlayerID())) {
				MPlayer player = MPlayer.initialize(pmr, port);
				MatchmakingController.addPlayer(player, lobbyID == null);

				if (lobbyID != null) {
					MatchmakingController.getLobbyByID(lobbyID).joinPlayer(player);
				}

				try {
					super.parent.sendMessage(port, new PacketMatchmakingAnswer(pmr, 1,
							MatchmakingController.getLobbyFromPlayer(player).getLobbyID()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				GameLog.log(MsgType.NETWORK_INFO, "-> " + player.getScore());
			} else {
				try {
					super.parent.sendMessage(port, new PacketMatchmakingAnswer(pmr, 0, null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}