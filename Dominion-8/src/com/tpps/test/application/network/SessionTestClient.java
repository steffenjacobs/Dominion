package com.tpps.test.application.network;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.clientSession.client.SessionPacketReceiverAPI;
import com.tpps.technicalServices.network.clientSession.client.SessionPacketSenderAPI;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.SuperCallable;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.util.FileParser;

/**
 * this represents the session-client (only one per application
 * 
 * @author Steffen Jacobs
 */
public final class SessionTestClient extends PacketHandler {

	public static final String FILE_SERVER_PROPERTIES = "./server.cfg";

	private static final int DELTA_SEND_KEEP_ALIVE_MILLISECONDS = 5000;
	private static SessionTestClient instance;

	private static Timer scheduler = null;
	private Client client;

	/**
	 * Main Entry-Point for Connection-Tester
	 * 
	 * @author Steffen Jacobs
	 */
	public static void main(String[] args) {
		new SessionTestClient("127.0.0.1", 1337);
	}

	/**
	 * @return instance of the SessionClient (There will be only one)
	 * 
	 * @author Steffen Jacobs
	 */
	public static SessionTestClient getInstance() {
		return instance;
	}

	/**
	 * constructor, automatically tries to connect to give server
	 * 
	 * @author Steffen Jacobs
	 */
	public SessionTestClient(String ipAddress, int port) {
		instance = this;
		setup(loadConnectionProperties(FILE_SERVER_PROPERTIES));
		setupScanner();
	}

	/**
	 * @return the Client-Object
	 * @author Steffen Jacobs
	 */
	public Client getClient() {
		return this.client;
	}

	/**
	 * creates the console-input-scanner
	 * 
	 * @author Steffen Jacobs
	 */
	public void setupScanner() {
		Scanner scanInput = new Scanner(System.in);
		String line = null;
		while (true) {
			line = scanInput.nextLine();
			try {
				if (line.equals("exit"))
					break;
				else if (line.startsWith("get")) {
					SessionPacketSenderAPI.sendGetRequest(getClient(), line.split("\\s")[1],
							new SuperCallable<PacketSessionGetAnswer>() {

								@Override
								public PacketSessionGetAnswer callMeMaybe(PacketSessionGetAnswer answer) {
									System.out.println("Answer received: " + answer.toString());
									return null;
								}

							});
				} else if (line.startsWith("check")) {
					SessionPacketSenderAPI.sendCheckRequest(getClient(), line.split("\\s")[1],
							UUID.fromString(line.split(" ")[2]), new SuperCallable<PacketSessionCheckAnswer>() {

								@Override
								public PacketSessionCheckAnswer callMeMaybe(PacketSessionCheckAnswer object) {
									System.out.println("Answer received: " + object.toString());
									return null;
								}

							});
				} else if (line.startsWith("keep-alive")) {
					keepAlive(getClient(), line.split("\\s")[1], Boolean.parseBoolean(line.split("\\s")[2]));
				} else if (line.startsWith("help")) {
					System.out.println("-------- Available Commands --------");
					System.out.println("get <username>");
					System.out.println("check <username> <UUID>");
					System.out.println("keep-alive <username> <true|false>");
					System.out.println("exit");
					System.out.println("help");
					System.out.println("------------------------------------");
				} else {
					System.out.println("Bad command: " + line);
					System.out.println("Use 'help' to show available commands");
				}
			} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
				System.err.println("Bad syntax.");
			}
		}
		scanInput.close();
		client.disconnect();
		System.exit(0);
	}

	/**
	 * sets up the client and connects to server ***THIS MUST BE CALLED BEFORE
	 * YOU SENT PACKETS!!***
	 * 
	 * @author Steffen Jacobs
	 */
	public void setup(InetSocketAddress address) {
		System.out.println("Enter 'help' to see all available commands.");
		try {
			this.client = new Client(address, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> onStop()));
	}

	/**
	 * loads server-properties (such as ip and port) and connects after that
	 * 
	 * @author Steffen Jacobs
	 */
	public InetSocketAddress loadConnectionProperties(String path) {
		ArrayList<String> lines = FileParser.loadLines(path);
		if (lines.size() < 2) {
			System.err.println("Bad config file: " + new File(path).getAbsolutePath());
		} else {
			try {
				return new InetSocketAddress(lines.get(0), Integer.parseInt(lines.get(1)));
			} catch (NumberFormatException ex) {

			}
		}
		return new InetSocketAddress("127.0.0.1", SessionServer.getStandardPort());
	}

	/**
	 * sets wheter keep-alive packets should be sent for a given user. You can
	 * only send keep-alive-packets for one user at a time
	 * 
	 * @author Steffen Jacobs
	 */
	public static void keepAlive(Client c, final String username, boolean state) {
		if (state) {
			if (scheduler != null) {
				scheduler.cancel();
				scheduler.purge();
				System.out.println("Keep-Alive changed to " + username);
			} else {
				System.out.println("Keep-Alive set up for " + username);
			}
			scheduler = new Timer();
			scheduler.schedule(new TimerTask() {
				@Override
				public void run() {
					SessionPacketSenderAPI.sendKeepAlive(c, username);
				}
			}, 0, DELTA_SEND_KEEP_ALIVE_MILLISECONDS);
		} else {
			if (scheduler != null) {
				scheduler.cancel();
				scheduler.purge();
				System.out.println("Keep-Alive stoppped for " + username);
			}
		}
	}

	/**
	 * is called when the client stops
	 * 
	 * @author Steffen Jacobs
	 */
	public void onStop() {
		this.client.disconnect();
		if (scheduler != null) {
			scheduler.cancel();
			scheduler.purge();
		}
	}

	/**
	 * is called when a packet is received
	 * 
	 * @author Steffen Jacobs
	 */

	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		if (packet == null) {
			System.out.println("Bad packet.");
		} else {
			if (SessionClient.debug())
				System.out.println(packet.toString());
			switch (packet.getType()) {
			case SESSION_CHECK_ANSWER:
				SessionPacketReceiverAPI.onPacketSessionCheckAnswer((PacketSessionCheckAnswer) packet);
				break;
			case SESSION_GET_ANSWER:
				SessionPacketReceiverAPI.onPacketSessionGetAnswer((PacketSessionGetAnswer) packet);
				break;
			default:
				break;
			}
		}
	}
}