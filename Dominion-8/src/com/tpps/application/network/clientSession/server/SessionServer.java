package com.tpps.application.network.clientSession.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.tpps.application.network.core.Server;
import com.tpps.application.network.core.ServerConnectionThread;

/** @author Steffen Jacobs */
public class SessionServer extends Server {

	private final static int standardPort = 1337;

	/**
	 * normal constructor
	 * 
	 * @author Steffen Jacobs
	 */
	public SessionServer() throws IOException {
		super(new InetSocketAddress("0.0.0.0", standardPort), new SessionPacketHandler());
		setConsoleInput();
	}

	/**
	 * constructor used by JUnit-Test
	 * 
	 * @author Steffen Jacobs
	 */
	public SessionServer(SessionPacketHandler handler) throws IOException {
		super(new InetSocketAddress("0.0.0.0", standardPort), handler);
	}

	public static void main(String[] args) {
		try {
			new SessionServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		return standardPort;
	}

}