package com.tpps.technicalServices.network.matchmaking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.core.events.NetworkListener;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingPlayerInfo;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingSuccessful;

/**
 * this represents the Server for the Matchmaking
 * 
 * @author Steffen Jacobs
 */
public class MatchmakingServer extends Server {

	private static MatchmakingServer instance;

	public final static int PORT_MATCHMAKING = 1341;

	/** main entry-point for the matchmaking-server */
	public static void main(String[] args) throws IOException {
		new MatchmakingServer(new InetSocketAddress(Addresses.getAllInterfaces(), PORT_MATCHMAKING),
				new MatchmakingPacketHandler());
	}

	/**
	 * constructor for the matchmaking-server; warning: blocks!
	 * 
	 * @parma address the addres + port the server is listening on
	 * @param _handler
	 *            a packet-handler for the server
	 */
	public MatchmakingServer(InetSocketAddress address, PacketHandler _handler) throws IOException {
		super(address, _handler);
		super.getListenerManager().registerListener(new MatchmakingListener());
		instance = this;
		setupConsoleInput(address.getPort());
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
				} else if (line.startsWith("countplayers")) {
					System.out.println(MatchmakingController.getPlayers().length);
				} else if (line.startsWith("listlobbies")) {
					int cnt = 0;
					for (String player : MatchmakingController.getLobbies()) {
						System.out.println(player);
						cnt++;
					}
					if (cnt == 0)
						System.out.println("(empty)");
				} else if (line.startsWith("listusers")) {
					int cnt = 0;
					for (String player : MatchmakingController.getPlayers()) {
						System.out.println(player);
						cnt++;
					}
					if (cnt == 0)
						System.out.println("(empty)");
				} else if (line.startsWith("help")) {
					System.out.println("-------- Available Commands --------");
					System.out.println("exit");
					System.out.println("listlobbies");
					System.out.println("listusers");
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
	 * @param receivers
	 *            the players to receive the packet
	 * @param opponents
	 *            all the players in the lobby
	 */
	public void sendSuccessPacket(Collection<MPlayer> receivers, String[] opponents) {

		PacketMatchmakingSuccessful pms = new PacketMatchmakingSuccessful(opponents, 0);
		try {
			for (MPlayer receiver : receivers) {
				super.sendMessage(MatchmakingController.getPortFromPlayer(receiver), pms);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sends a packet back to the Matchmaker on the client-side if a player
	 * joins the lobby
	 * 
	 * @param receivers
	 *            the players to receive the packet
	 * @param quittedPlayer
	 *            the player who quitted
	 */
	public void sendQuitPacket(Collection<MPlayer> receivers, String quittedPlayer) {
		PacketMatchmakingPlayerInfo pmpj = new PacketMatchmakingPlayerInfo(quittedPlayer, false);
		try {
			for (MPlayer receiver : receivers) {
				super.sendMessage(MatchmakingController.getPortFromPlayer(receiver), pmpj);
			}
		} catch (IOException e) {
			e.printStackTrace();
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
