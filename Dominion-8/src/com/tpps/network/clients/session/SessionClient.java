package com.tpps.network.clients.session;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.tpps.network.packets.Packet;
import com.tpps.network.packets.PacketType;
import com.tpps.network.packets.session.PacketSessionCheckAnswer;
import com.tpps.network.packets.session.PacketSessionGetAnswer;
import com.tpps.util.FileParser;

/**
 * this represents the session-client (only one per application
 * 
 * @author sjacobs - Steffen Jacobs
 */
public final class SessionClient {

	public static final String FILE_SERVER_PROPERTIES = "./server.cfg";

	private static int DELTA_SEND_KEEP_ALIVE_MILLISECONDS = 5000;

	private static String ipAddress = "127.0.0.1";
	private static int serverPort = 1337;

	private static Timer scheduler = null;
	private static boolean connected;
	private static boolean connecting = false;
	private static ConnectedClient connectedClient;
	private static Thread tryToConnectThread = null;

	static final boolean DEBUG_PACKETS = true;

	/**
	 * @return wheter the client is connected to the server
	 * @author sjacobs - Steffen Jacobs
	 */
	public static boolean isConnected() {
		return connected && connectedClient != null;
	}

	/**
	 * @return the client-side object with is connected to the server
	 * @author sjacobs - Steffen Jacobs
	 */
	public static ConnectedClient getConnection() {
		return connectedClient;
	}

	/**
	 * sets the connected-boolean to state
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void setConnected(boolean state) {
		connected = state;
	}

	/**
	 * Main Entry-Point for Connection-Tester
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void main(String[] args) {
		setup();
		setupScanner();
	}

	/**
	 * creates the console-input-scanner
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void setupScanner() {
		Scanner scanInput = new Scanner(System.in);
		String line = null;
		while (true) {
			line = scanInput.nextLine();
			try {
				if (line.equals("exit"))
					break;
				else if (line.startsWith("get")) {
					PacketSenderAPI.sendGetRequest(line.split("\\s")[1]);
				} else if (line.startsWith("check")) {
					PacketSenderAPI.sendCheckRequest(line.split("\\s")[1], UUID.fromString(line.split(" ")[2]));
				} else if (line.startsWith("keep-alive")) {
					keepAlive(line.split("\\s")[1], Boolean.parseBoolean(line.split("\\s")[2]));
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
		tryToConnectThread.interrupt();
		System.exit(0);
	}

	/**
	 * sets up the client and connects to server ***THIS MUST BE CALLED BEFORE
	 * YOU SENT PACKETS!!***
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void setup() {
		System.out.println("Enter 'help' to see all available commands.");
		loadConnectionProperties(FILE_SERVER_PROPERTIES);
		connectAndLoop();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> onStop()));
	}

	/**
	 * Tries to connect to the loaded server asynchronously until a connection
	 * is established.
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	static void connectAndLoop() {

		final long CONNECTION_TIMEOUT = 5000;

		if (!connecting) {
			connecting = true;

			if (tryToConnectThread != null)
				tryToConnectThread.interrupt();

			tryToConnectThread = new Thread(() -> {
				while (!Thread.interrupted() && !isConnected()) {
					setConnected(false);
					try {
						connectedClient = new ConnectedClient(new InetSocketAddress(ipAddress, serverPort),
								new ConnectedClient.Receiver() {
							@Override
							public void received(byte[] data) {
								new Thread(() -> {
									SessionClient.onPacketReceived(data);
								}).start();
							}
						});

						Thread.sleep(50);
						if (isConnected()) {
							break;
						} else {
							Thread.sleep(CONNECTION_TIMEOUT);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						//do nothing: is normal when programm exits.
					}
				}
				connecting = false;
			});
			tryToConnectThread.start();
		}
	}

	/**
	 * loads server-properties (such as ip and port) and connects after that
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void loadConnectionProperties(String path) {
		ArrayList<String> lines = FileParser.loadLines(path);
		if (lines.size() < 2) {
			System.err.println("Bad config file: " + new File(path).getAbsolutePath());
		} else {
			ipAddress = lines.get(0);
			serverPort = Integer.parseInt(lines.get(1));
		}
	}

	/**
	 * sets wheter keep-alive packets should be sent for a given user. You can
	 * only send keep-alive-packets for one user at a time
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private static void keepAlive(final String username, boolean state) {
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
					PacketSenderAPI.sendKeepAlive(username);
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
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void onStop() {
		PacketSenderAPI.disconnect(true);
		if (scheduler != null) {
			scheduler.cancel();
			scheduler.purge();
		}
		connected = false;
	}

	/**
	 * is called when a packet is received
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void onPacketReceived(byte[] bytes) {
		Packet packet = PacketType.getPacket(bytes);
		if (packet == null) {
			System.out.println("Bad packet.");
		} else {
			if (DEBUG_PACKETS)
				System.out.println(packet.toString());
			switch (packet.getType()) {
			case SESSION_CHECK_ANSWER:
				PacketReceiverAPI.onPacketSessionCheckAnswer((PacketSessionCheckAnswer) packet);
				break;
			case SESSION_GET_ANSWER:
				PacketReceiverAPI.onPacketSessionGetAnswer((PacketSessionGetAnswer) packet);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * tries to reconnect to the server
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void tryReconnect() {
		connected = false;
		connectAndLoop();
	}
}