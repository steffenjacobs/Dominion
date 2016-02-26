package com.tpps.application.network.sessions.client;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;
import com.tpps.application.network.sessions.packets.PacketSessionCheckAnswer;
import com.tpps.application.network.sessions.packets.PacketSessionGetAnswer;
import com.tpps.technicalServices.util.FileParser;

/**
 * this represents the session-client (only one per application
 * 
 * @author sjacobs - Steffen Jacobs
 */
public final class SessionClient extends PacketHandler {

	public static final String FILE_SERVER_PROPERTIES = "./server.cfg";

	private static final int DELTA_SEND_KEEP_ALIVE_MILLISECONDS = 5000;
	private static SessionClient instance;

	private static Timer scheduler = null;
	private Client client;

	static final boolean DEBUG_PACKETS = true;

	/**
	 * Main Entry-Point for Connection-Tester
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void main(String[] args) {
		new SessionClient("127.0.0.1", 1337);
	}

	/**
	 * @return instance of the SessionClient (There will be only one)
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static SessionClient getInstance() {
		return instance;
	}

	/**
	 * constructor, automatically tries to connect to give server
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public SessionClient(String ipAddress, int port) {
		instance = this;
		setup(loadConnectionProperties(FILE_SERVER_PROPERTIES));
		setupScanner();
	}

	/**
	 * @return the Client-Object
	 * @author sjacobs - Steffen Jacobs
	 */
	public Client getClient() {
		return this.client;
	}

	/**
	 * creates the console-input-scanner
	 * 
	 * @author sjacobs - Steffen Jacobs
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
					SessionPacketSenderAPI.sendGetRequest(getClient(), line.split("\\s")[1]);
				} else if (line.startsWith("check")) {
					SessionPacketSenderAPI.sendCheckRequest(getClient(), line.split("\\s")[1], UUID.fromString(line.split(" ")[2]));
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
	 * @author sjacobs - Steffen Jacobs
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
	 * @author sjacobs - Steffen Jacobs
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
		return new InetSocketAddress("127.0.0.1", 1337);
	}

	/**
	 * sets wheter keep-alive packets should be sent for a given user. You can
	 * only send keep-alive-packets for one user at a time
	 * 
	 * @author sjacobs - Steffen Jacobs
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
	 * @author sjacobs - Steffen Jacobs
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
	 * @author sjacobs - Steffen Jacobs
	 */

	@Override
	public void handleReceivedPacket(int port, byte[] bytes) {
		Packet packet = PacketType.getPacket(bytes);
		if (packet == null) {
			System.out.println("Bad packet.");
		} else {
			if (DEBUG_PACKETS)
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