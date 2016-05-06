package com.tpps.technicalServices.network.matchmaking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.core.events.NetworkListener;
import com.tpps.technicalServices.network.login.SQLHandling.SQLHandler;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingPlayerInfo;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingSuccessful;

/**
 * this represents the Server for the Matchmaking
 * 
 * @author Steffen Jacobs
 */
public class MatchmakingServer extends Server {

	private static MatchmakingServer instance;

	private final static int PORT_MATCHMAKING = 1341;

	/** @return the standard-port 1341 */
	public static int getStandardPort() {
		return PORT_MATCHMAKING;
	}

	/**
	 * main entry-point for the matchmaking-server
	 * 
	 * @param args
	 *            start-parameters
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new MatchmakingServer(new InetSocketAddress(Addresses.getAllInterfaces(), PORT_MATCHMAKING),
				new MatchmakingPacketHandler());
	}

	/**
	 * starts a local matchmaking-server
	 * 
	 * @throws IOException
	 */
	public MatchmakingServer() throws IOException {
		super(new InetSocketAddress(Addresses.getAllInterfaces(), PORT_MATCHMAKING), new MatchmakingPacketHandler());
		super.getListenerManager().registerListener(new MatchmakingListener());
		instance = this;
		setupConsoleInput(PORT_MATCHMAKING);
	}

	/**
	 * constructor for the matchmaking-server; warning: blocks!
	 * 
	 * @param address
	 *            the addres + port the server is listening on
	 * @param _handler
	 *            a packet-handler for the server
	 * @throws IOException
	 */
	public MatchmakingServer(InetSocketAddress address, PacketHandler _handler) throws IOException {
		super(address, _handler);
		super.getListenerManager().registerListener(new MatchmakingListener());
		instance = this;
		SQLHandler.init();
		SQLHandler.connect();
		setupConsoleInput(address.getPort());
		GameLog.init();
	}

	/**
	 * sets up the console-output
	 * 
	 * @param port
	 *            the port the server is running on
	 */
	private void setupConsoleInput(int port) {

		GameLog.log(MsgType.INFO ,"            * * * * * * * * * * * * * *      ");
		GameLog.log(MsgType.INFO ,"      * * * * * * * * * * * * * * * * * * * *");
		GameLog.log(MsgType.INFO ,"* * * * * Dominion Matchmaking Server - Team ++; * * * * *");
		GameLog.log(MsgType.INFO ,"* * * * * * * * * * * Port " + port + " * * * * * * * * * * * ");
		GameLog.log(MsgType.INFO ,"      * * * * * * * * * * * * * * * * * * * *");
		GameLog.log(MsgType.INFO ,"            * * * * * * * * * * * * * *      ");
		GameLog.log(MsgType.INFO ,"");
		GameLog.log(MsgType.INFO ,"Enter 'help' to see all available commands.");
		GameLog.log(MsgType.INFO ,"");

		String line = null;
		Scanner scanInput = new Scanner(System.in);
		while (true) {
			line = scanInput.nextLine();
			try {
				if (line.equals("exit") || line.equals("stop")) {
					System.exit(0);
					break;
				} else if (line.startsWith("lobbies")) {
					int cnt = 0;
					GameLog.log(MsgType.MM ,"Lobbies (" + MatchmakingController.getLobbies().length + "): ");
					for (String lobb : MatchmakingController.getLobbies()) {
						GameLog.log(MsgType.MM ,lobb);
						cnt++;
					}
					if (cnt == 0) {
						GameLog.log(MsgType.MM ,"(empty)");
					}

				} else if (line.startsWith("players")) {
					GameLog.log(MsgType.MM ,"Online Players (" + MatchmakingController.getPlayers().length + "): ");

					int cnt = 0;
					for (String player : MatchmakingController.getPlayers()) {
						GameLog.log(MsgType.MM ,player);
						cnt++;
					}
					if (cnt == 0) {
						GameLog.log(MsgType.MM ,"(empty)");
					}

				} else if (line.startsWith("help")) {
					GameLog.log(MsgType.INFO ,"-------- Available Commands --------");
					GameLog.log(MsgType.INFO ,"exit");
					GameLog.log(MsgType.INFO ,"lobbies");
					GameLog.log(MsgType.INFO ,"players");
					GameLog.log(MsgType.INFO ,"help");
					GameLog.log(MsgType.INFO ,"------------------------------------");
				} else {
					GameLog.log(MsgType.INFO ,"Bad command: " + line);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Bad syntax.");
			}
		}
		scanInput.close();
	}

	/**
	 * sends a packet back to the Matchmaker on the client-side if a player
	 * joins the lobby
	 * 
	 * @param receiver
	 *            the player to receive the packet
	 * @param joinedPlayer
	 *            the player who joined
	 * @param adm
	 *            wether joinedPlayer is admin
	 */
	public void sendJoinPacket(MPlayer receiver, String joinedPlayer, boolean adm) {
		ArrayList<MPlayer> tmp = new ArrayList<>();
		tmp.add(receiver);
		sendJoinPacket(tmp, joinedPlayer, adm);
	}

	/**
	 * sends a packet back to the Matchmakers on the client-side if a player
	 * joins the lobby
	 * 
	 * @param receivers
	 *            the players to receive the packet
	 * @param joinedPlayer
	 *            the player who joined
	 * @param adm
	 *            whether joinedPlayer is admin
	 */
	public void sendJoinPacket(Collection<MPlayer> receivers, String joinedPlayer, boolean adm) {

		PacketMatchmakingPlayerInfo pmpj = new PacketMatchmakingPlayerInfo(joinedPlayer, true, adm);
		try {
			for (MPlayer receiver : receivers) {
				super.sendMessage(receiver.getConnectionPort(), pmpj);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sends a packet back to the Matchmaker on the client-side if the lobby is
	 * full and ready to start and the game-server is already set up
	 * 
	 * @param receiver
	 *            the players to receive the packet
	 * @param opponents
	 *            all the players in the lobby
	 * @param port
	 *            the port the new game-server is waiting
	 * @param selectedActionCards
	 *            the names of the cards to play with
	 */
	public void sendSuccessPacket(MPlayer receiver, String[] opponents, int port, String[] selectedActionCards) {

		PacketMatchmakingSuccessful pms = new PacketMatchmakingSuccessful(opponents, port, selectedActionCards);
		try {
			super.sendMessage(receiver.getConnectionPort(), pms);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sends a packet back to the Matchmaker on the client-side if a player
	 * quits the lobby
	 * 
	 * @param receiver
	 *            the player to receive the packet
	 * @param quitPlayer
	 *            the player who quit
	 * @param adm
	 *            whether the player who quit was admin
	 */
	public void sendQuitPacket(MPlayer receiver, String quitPlayer, boolean adm) {
		PacketMatchmakingPlayerInfo pmpj = new PacketMatchmakingPlayerInfo(quitPlayer, false, adm);
		try {
			super.sendMessage(receiver.getConnectionPort(), pmpj);
		} catch (NullPointerException | IOException | IllegalArgumentException e) {
			// if one this player is already disconnected, too
		}
	}

	/**
	 * @return the instance of the MatchmakingServer (there will be only one)
	 */
	public static MatchmakingServer getInstance() {
		return instance;
	}

	/**
	 * this represents a Network-Listener for the matchmaking-server to catch if
	 * a player disconnects
	 */
	private static class MatchmakingListener implements NetworkListener {

		/** trivial */
		@Override
		public void onClientConnect(int port) {
			// nothing
		}

		/**
		 * called if a player disconnected
		 * 
		 * @param port
		 *            the port of the disconnected client
		 */
		@Override
		public void onClientDisconnect(int port) {
			MatchmakingController.onPlayerDisconnect(port);
		}
	}

}
