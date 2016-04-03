package com.tpps.technicalServices.network.clientSession.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.core.ServerConnectionThread;
import com.tpps.technicalServices.util.AutoCreatingProperties;

/** @author Steffen Jacobs */
public class SessionServer extends Server {

	private static final AutoCreatingProperties config;
	private static final String KEY_PORT = "SESSION_PORT", DEFAULT_PORT = "1337";
	private static final String KEY_LOG = "SESSION_LOG", DEFAULT_LOG = "session.log";

	private static final String CONFIG_FILE = "sessions.cfg";

	static {
		config = new AutoCreatingProperties();
		config.load(new File(CONFIG_FILE));
	}

	/**
	 * normal constructor
	 * 
	 * @author Steffen Jacobs
	 */
	public SessionServer() throws IOException {
		super(new InetSocketAddress(Addresses.getAllInterfaces(),
				Integer.parseInt(config.getProperty(KEY_PORT, DEFAULT_PORT))), new SessionPacketHandler());
		setConsoleInput();
	}

	/**
	 * constructor used by JUnit-Test
	 * 
	 * @author Steffen Jacobs
	 */
	public SessionServer(SessionPacketHandler handler) throws IOException {
		super(new InetSocketAddress(Addresses.getAllInterfaces(),
				Integer.parseInt(config.getProperty(KEY_PORT, DEFAULT_PORT))), handler);
	}

	public static void main(String[] args) throws IOException {
		try {
			new SessionServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// setup logging
		Files.createFile(Paths.get(config.getProperty(KEY_LOG, DEFAULT_LOG)));
		System.setOut(new PrintStream(new FileOutputStream(config.getProperty(KEY_LOG, DEFAULT_LOG))));
	}

	/**
	 * sets up the console-input
	 * 
	 * @author Steffen Jacobs
	 */
	private void setConsoleInput() {
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("* * * * * Dominion Session Server - Team ++; * * * * *");
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
				} else if (line.startsWith("create")) {
					SessionManager.getValidSession(line.split("\\s")[1]);
				} else if (line.startsWith("count")) {
					System.out.println(data.size());
					data.clear();
				} else if (line.startsWith("show")) {
					SessionManager.outputAll(System.out);
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
					System.out.println("create <username>");
					System.out.println("list");
					System.out.println("show");
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

	/**
	 * getter for standard-port for session-server @return standard-port for
	 * session-server @author Steffen Jacobs
	 */
	public static int getStandardPort() {
		return Integer.parseInt(config.getProperty(KEY_PORT, DEFAULT_PORT));
	}

	// TODO: remove both
	private static CopyOnWriteArrayList<String> data = new CopyOnWriteArrayList<>();

	public static void log(String s) {
		data.add(s);
	}

}