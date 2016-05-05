package com.tpps.technicalServices.network.matchmaking.client;

import java.awt.Color;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.matchmaking.packets.PacketJoinLobby;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingAnswer;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingPlayerInfo;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingRequest;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingStart;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingSuccessful;
import com.tpps.technicalServices.network.matchmaking.server.MatchmakingServer;

/**
 * main matchmaking system for client
 * 
 * @author Steffen Jacobs
 */
public final class Matchmaker {
	private Client client;
	private PacketHandler handler;
	private static Matchmaker INSTANCE;

	private Matchmaker() {
		// singleton
	}

	/**
	 * @return the only instance of the Matchmaker
	 */
	public static Matchmaker getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Matchmaker();
		}
		return INSTANCE;
	}

	/**
	 * creates & opens a new connection to the matchmaking-server if necessary
	 * 
	 * @throws IOException
	 */
	private void checkAndCreateClient() throws IOException {
		if (client == null || !client.isConnected()) {
			handler = new MatchmakingHandler();
			client = new Client(
					new InetSocketAddress(Addresses.getRemoteAddress(), MatchmakingServer.getStandardPort()), handler,
					false);
		}
	}

	/**
	 * send the start-packet to the matchmaking-server
	 * 
	 * @param senderName
	 *            name of the lobby-admin
	 * @param senderUID
	 *            sessionID of the lobby-admin
	 * @param lobbyID
	 *            UUID of the lobby
	 * @param selectedActionCards
	 *            the names of the cards to play with
	 * @throws IOException
	 */
	public void sendStartPacket(String senderName, UUID senderUID, UUID lobbyID, String[] selectedActionCards)
			throws IOException {
		checkAndCreateClient();
		client.sendMessage(new PacketMatchmakingStart(lobbyID, senderUID, senderName, selectedActionCards));
		GameLog.log(MsgType.INFO, "Sent request to start lobby " + lobbyID.toString());
	}

	/**
	 * sends an AI-Packet to the matchmaking-system
	 * 
	 * @param name
	 *            displayed name of the AI
	 * @param lobbyID
	 *            UUID of the lobby
	 * @param abort
	 *            whether to join or quit an AI
	 * @throws IOException
	 *             if there is no network connection available or the server is
	 *             unreachable
	 */
	public void sendAIPacket(String name, UUID lobbyID, boolean abort) throws IOException {
		checkAndCreateClient();
		client.sendMessage(
				new PacketJoinLobby(name, UUID.fromString("00000000-0000-0000-0000-000000000000"), lobbyID, abort));
		GameLog.log(MsgType.INFO,
				"Sent request to " + (abort ? "quit" : "join") + "AI: " + name + " lobby " + lobbyID.toString());
	}

	/**
	 * tries to join a specific lobby
	 * 
	 * @param username
	 *            name of the player searching for a match
	 * @param uid
	 *            uuid of the player searching for a match
	 * @param lobbyID
	 *            the uuid of the lobby the player wants to join
	 * @throws IOException
	 *             if there is no network connection available or the server is
	 *             unreachable
	 */
	public void tryJoinLobby(String username, UUID uid, UUID lobbyID) throws IOException {
		checkAndCreateClient();
		client.sendMessage(new PacketJoinLobby(username, uid, lobbyID, true));
		GameLog.log(MsgType.INFO, "Sent request to join lobby " + lobbyID.toString());
	}

	/**
	 * finds a match for the player
	 * 
	 * @param username
	 *            name of the player searching for a match
	 * @param uid
	 *            uuid of the player searching for a match
	 * @throws IOException
	 *             if there is no network connection available or the server is
	 *             unreachable
	 */
	public void findMatch(String username, UUID uid) throws IOException {
		checkAndCreateClient();
		client.sendMessage(new PacketMatchmakingRequest(username, uid, false, false));
		GameLog.log(MsgType.INFO, "Start searching a match");
	}

	
	/**
	 * create a private lobby for a player
	 * @param username the user who creates the private lobby
	 * @param uid the unique ID of the creator of the lobby 
	 * @throws IOException
	 */
	public void createPrivateMatch(String username, UUID uid) throws IOException {
		checkAndCreateClient();
		client.sendMessage(new PacketMatchmakingRequest(username, uid, false, true));
		GameLog.log(MsgType.INFO, "Start searching a match");
	}

	/**
	 * aborts the search for a match for a player
	 * 
	 * @param username
	 *            name of the player aborting the search
	 * @param uid
	 *            uuid of the player aborting the search
	 * @throws IOException
	 */
	public void abort(String username, UUID uid) throws IOException {
		checkAndCreateClient();
		client.sendMessage(new PacketMatchmakingRequest(username, uid, true, false));
		GameLog.log(MsgType.INFO, "Aborted to search a match");
	}

	/** @return the actual network-client connected to the matchmaking-system */
	public Client getNetworkClient() {
		return client;
	}

	/**
	 * client packet-handler for the matchmaking
	 * 
	 * @author Steffen Jacobs
	 */
	private static class MatchmakingHandler extends PacketHandler {

		/**
		 * is called when a packet is received
		 * 
		 * @param port
		 *            the port the packet was received on (unnecessary here,
		 *            because all received packets are from the same server at
		 *            the same port
		 * @param packet
		 *            the received packet
		 */
		@Override
		public void handleReceivedPacket(int port, Packet packet) {

			switch (packet.getType()) {
			case MATCHMAKING_ANSWER:
				PacketMatchmakingAnswer pma = (PacketMatchmakingAnswer) packet;
				// is called when the player is put into a matchmaking-lobby
				processAnswerCode(pma);
				break;
			case MATCHMAKING_PLAYER_INFO:
				// is called when a player joins or quits
				PacketMatchmakingPlayerInfo pmpi = (PacketMatchmakingPlayerInfo) packet;
				
				String chatmessageName = "\"";
				if (pmpi.getPlayerName().contains("(")) {
					String[] split = pmpi.getPlayerName().split("\\(");
					chatmessageName += split[0].substring(0, split[0].length() - 1) + "\" (" + split[1];
				} else
					chatmessageName += pmpi.getPlayerName() + "\"";
				
				if (pmpi.isStatus()) {
					DominionController.getInstance()
							.receiveChatMessageFromChatServer("A Player joined the lobby " + chatmessageName + (pmpi.isLobbyAdmin() ? " (Admin)" : ""), "BOT", "", Color.YELLOW);
				} else if (!pmpi.isStatus()) {
					DominionController.getInstance().receiveChatMessageFromChatServer("A Player quitted from the lobby " + chatmessageName, "BOT", "", Color.YELLOW);
				}
				if (pmpi.isStatus()) {
					GameLog.log(MsgType.INFO, "----- Player " + pmpi.getPlayerName() + " joined the lobby.");
					if(pmpi.isLobbyAdmin()){
						DominionController.getInstance().insertPlayerToGUI(pmpi.getPlayerName() + " (Admin)");
					}else{
						DominionController.getInstance().insertPlayerToGUI(pmpi.getPlayerName());
					}
					if (pmpi.getPlayerName().equals(DominionController.getInstance().getUsername())) {
						if (pmpi.isLobbyAdmin()) {
							System.out.println(pmpi.getPlayerName() + "is a host");
							DominionController.getInstance().setHost(true);
						} else {
							DominionController.getInstance().setHost(false);
						}
					}
				} else {
					GameLog.log(MsgType.INFO, "----- Player " + pmpi.getPlayerName() + " left from lobby.");
					DominionController.getInstance().clearPlayerFromGUI(pmpi.getPlayerName());
				}
				break;
			case MATCHMAKING_SUCCESSFUL:
				PacketMatchmakingSuccessful pms = (PacketMatchmakingSuccessful) packet;
				// is called, when the lobby is full and the game starts
				GameLog.log(MsgType.INFO, "Downloading Cards...");
				DominionController.getInstance().getCardRegistry().checkAndDownloadCards(pms.getSelectedActionCards());
				GameLog.log(MsgType.INFO, "starting match!");
				DominionController.getInstance().startMatch(pms.getGameserverPort());
				break;
			default:
				GameLog.log(MsgType.NETWORK_ERROR, "Bad packet received: " + packet);
				break;
			}
		}

		/**
		 * processes the answer-code: shows MessageDialogs or saves lobby-id
		 * 
		 * @param pck
		 *            the packet contains the answer-code to process
		 */
		private static void processAnswerCode(PacketMatchmakingAnswer pck) {
			switch (pck.getAnswerCode()) {
			case 0: // Bad Session
				// system.exit();
				break;
			case 1: // Success

				if(pck.getRequest().isPrivate()){
					DominionController.getInstance()
					.receiveChatMessageFromChatServer("You joined a private lobby successfully", "BOT", "",
							Color.YELLOW);
				}
				/*
				 * :id : " + pck.getLobbyID()
				 */
				DominionController.getInstance().setLobbyID(pck.getLobbyID());
				break;
			case 2: // Lobby does not exist
				DominionController.getInstance().receiveChatMessageFromChatServer("Lobby does not exist", "BOT", "",
						Color.YELLOW);
				break;
			case 3: // Lobby is already full
				DominionController.getInstance().receiveChatMessageFromChatServer("Lobby is already full", "BOT", "",
						Color.YELLOW);
				break;
			case 4: // Lobby already started
				DominionController.getInstance().receiveChatMessageFromChatServer("Lobby already started", "BOT", "",
						Color.YELLOW);
				break;
			default: // unknown error
				DominionController.getInstance().receiveChatMessageFromChatServer("unknown error", "BOT", "",
						Color.YELLOW);
				break;
			}
		}
	}
}