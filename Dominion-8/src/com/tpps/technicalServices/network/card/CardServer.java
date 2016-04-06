package com.tpps.technicalServices.network.card;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Properties;
import java.util.Scanner;

import com.tpps.application.storage.CardStorageController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.core.ServerConnectionThread;
import com.tpps.technicalServices.util.AutoCreatingProperties;

/**
 * represents a card-server which handles all the storing of the cards, adding
 * cards and sending cards to clients who request them
 * 
 * @author Steffen Jacobs
 */
public class CardServer extends Server {
	private static AutoCreatingProperties config;
	private static final String KEY_PORT = "CARD_PORT", DEFAULT_PORT = "1336", CARD_FILE = "CARD_FILE";

	private static final String CONFIG_FILE = "cards.cfg";

	private CardStorageController serverStorage;

	// init config
	static {
		config = new AutoCreatingProperties();
		config.load(new File(CONFIG_FILE));
	}

	/**
	 * constructor for the CardServer, taking an address (where the server will
	 * listen), a packet-handler, and a card-storage
	 */
	public CardServer(SocketAddress address, PacketHandler _handler, CardStorageController cardStorage)
			throws IOException {
		super(address, _handler);
		this.serverStorage = cardStorage;
		this.serverStorage.loadCards();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> onApplicationExit()));
		new Thread(() -> setConsoleInput()).start();
	}

	/** main entry-point for the CardServer */
	public static void main(String[] input) throws IOException {
		CardStorageController tmpStorage = new CardStorageController(config.getProperty(CARD_FILE, "serverCards.bin"));
		new CardServer(
				new InetSocketAddress(Addresses.getAllInterfaces(),
						Integer.parseInt(config.getProperty(KEY_PORT, DEFAULT_PORT))),
				new CardPacketHandlerServer(tmpStorage,
						new SessionClient(
								new InetSocketAddress(Addresses.getRemoteAddress(), SessionServer.getStandardPort()))),
				tmpStorage);
	}

	/** is called on exit */
	private void onApplicationExit() {
		this.serverStorage.saveCards();
	}

	/**
	 * sets up the console-input
	 * 
	 * @author Steffen Jacobs
	 */
	private void setConsoleInput() {
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("* * * * * Dominion Card Server - Team ++; * * * * *");
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
				} else if (line.startsWith("show")) {
					this.serverStorage.listCards();
				} else if (line.startsWith("clear")) {
					final int cnt = this.serverStorage.getCardCount();
					this.serverStorage.clearCards();
					GameLog.log(MsgType.INFO, "Cleared " + cnt + " cards!");
				} else if (line.startsWith("list")) {
					int cnt = 0;
					for (ServerConnectionThread client : super.clients.values()) {
						GameLog.log(MsgType.INFO, client.toString());
						cnt++;
					}
					if (cnt == 0)
						GameLog.log(MsgType.INFO, "(empty)");
				} else if (line.startsWith("reload")) {
					super.stopListening();
					super.startListening();
				} else if (line.startsWith("help")) {
					System.out.println("-------- Available Commands --------");
					System.out.println("list");
					System.out.println("show");
					System.out.println("clear");
					System.out.println("reload");
					System.out.println("exit");
					System.out.println("help");
					System.out.println("------------------------------------");
				} else {
					GameLog.log(MsgType.INFO, "Bad command: " + line);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				GameLog.log(MsgType.ERROR, "Bad syntax.");
			}
		}
		scanInput.close();
	}

	/** @return the standard Port for the CardServer */
	public static int getStandardPort() {
		return Integer.parseInt(config.getProperty(KEY_PORT, DEFAULT_PORT));
	}

	/** getter for the Card-Server-Properties */
	public Properties getCardServerProperties() {
		return config;
	}
}