package com.tpps.technicalServices.network.matchmaking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import com.tpps.technicalServices.logger.GameLog;
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

	private void setupConsoleInput(int port) {

		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("* * * * * Dominion Matchmaking Server - Team ++; * * * * *");
		System.out.println("* * * * * * * * * * * Port " + port + " * * * * * * * * * * * ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println();
		System.out.println("Enter 'help' to see all available commands.");
		System.out.println();

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
					System.out.println("Lobbies (" + MatchmakingController.getLobbies().length + "): ");
					for (String lobb : MatchmakingController.getLobbies()) {
						System.out.println(lobb);
						cnt++;
					}
					if (cnt == 0) {
						System.out.println("(empty)");
					}

				} else if (line.startsWith("players")) {
					System.out.println("Online Players (" + MatchmakingController.getPlayers().length + "): ");

					int cnt = 0;
					for (String player : MatchmakingController.getPlayers()) {
						System.out.println(player);
						cnt++;
					}
					if (cnt == 0) {
						System.out.println("(empty)");
					}

				} else if (line.startsWith("help")) {
					System.out.println("-------- Available Commands --------");
					System.out.println("exit");
					System.out.println("lobbies");
					System.out.println("players");
					System.out.println("help");
					System.out.println("------------------------------------");
				} else {
					System.out.println("Bad command: " + line);
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
	 */
	public void sendJoinPacket(MPlayer receiver, String joinedPlayer) {
		ArrayList<MPlayer> tmp = new ArrayList<>();
		tmp.add(receiver);
		sendJoinPacket(tmp, joinedPlayer);
	}

	/**
	 * sends a packet back to the Matchmakers on the client-side if a player
	 * joins the lobby
	 * 
	 * @param receivers
	 *            the players to receive the packet
	 * @param joinedPlayer
	 *            the player who joined
	 */
	public void sendJoinPacket(Collection<MPlayer> receivers, String joinedPlayer) {

		PacketMatchmakingPlayerInfo pmpj = new PacketMatchmakingPlayerInfo(joinedPlayer, true);
		try {
			for (MPlayer receiver : receivers) {
				super.sendMessage(MatchmakingController.getPortFromPlayer(receiver), pmpj);
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
	 */
	public void sendSuccessPacket(MPlayer receiver, String[] opponents, int port) {

		PacketMatchmakingSuccessful pms = new PacketMatchmakingSuccessful(opponents, port);
		try {
			super.sendMessage(MatchmakingController.getPortFromPlayer(receiver), pms);
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
	 * @param quittedPlayer
	 *            the player who quitted
	 */
	public void sendQuitPacket(MPlayer receiver, String quittedPlayer) {
		PacketMatchmakingPlayerInfo pmpj = new PacketMatchmakingPlayerInfo(quittedPlayer, false);
		try {
			super.sendMessage(MatchmakingController.getPortFromPlayer(receiver), pmpj);
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
