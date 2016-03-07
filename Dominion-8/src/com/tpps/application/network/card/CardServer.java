package com.tpps.application.network.card;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Scanner;

import com.tpps.application.network.clientSession.client.SessionClient;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.Server;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.storage.CardStorageController;

/**
 * represents a card-server which handles all the storing of the cards, adding
 * cards and sending cards to clients who request them
 * 
 * @author Steffen Jacobs
 */
public class CardServer extends Server {
	private static final String SERVER_INTERFACE = "0.0.0.0";
	private static final int PORT = 1336;

	private CardStorageController serverStorage;

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
		CardStorageController tmpStorage = new CardStorageController("serverCards.bin");
		new CardServer(new InetSocketAddress(SERVER_INTERFACE, PORT),
				new CardPacketHandlerServer(tmpStorage, new SessionClient(new InetSocketAddress("127.0.0.1", 1337))),
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
					System.out.println("Cleared " + cnt + " cards!");
				} else if (line.startsWith("list")) {
					int cnt = 0;
					for (ServerConnectionThread client : super.clients.values()) {
						System.out.println(client);
						cnt++;
					}
					if (cnt == 0)
						System.out.println("(empty)");
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
					System.out.println("Bad command: " + line);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Bad syntax.");
			}
		}
		scanInput.close();
	}
}