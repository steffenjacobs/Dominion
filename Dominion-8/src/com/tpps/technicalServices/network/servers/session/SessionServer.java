package com.tpps.technicalServices.network.servers.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ServerSocketFactory;

/** @author sjacobs - Steffen Jacobs */
public class SessionServer {
	private static SessionServer sessionServerInstance = null;

	private static final String IP_ADDRESS = "127.0.0.1";
	private static final int DEFAULT_PORT = 1337;
	private ServerSocket serverSocket;
	private PacketHandler receiver;
	private Thread acceptor;

	/**
	 * Integer represents the port
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private ConcurrentHashMap<Integer, ServerConnectionThread> clients = new ConcurrentHashMap<>();

	/**
	 * @return current instance of the session servers main-thread
	 * @author sjacobs - Steffen Jacobs
	 */
	public static SessionServer getSessionServer() {
		return sessionServerInstance;
	}

	/**
	 * main entry point for Session-Server
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		setup();
	}

	/** starts the server and sets up the console-input */
	public static void setup() {
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("* * * * * Dominion Session Server - Team ++; * * * * *");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println();
		System.out.println("Enter 'help' to see all available commands.");
		System.out.println();

		try {
			sessionServerInstance = new SessionServer(new InetSocketAddress(IP_ADDRESS, DEFAULT_PORT), new PacketHandler());
			sessionServerInstance.consoleInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sets up the console-input
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private void consoleInput() {
		String line = null;
		Scanner scanInput = new Scanner(System.in);
		while (true) {
			line = scanInput.nextLine();
			try {
				if (line.equals("exit")) {
					System.exit(0);
					break;
				} else if (line.startsWith("create")) {
					SessionManager.getValidSession(line.split("\\s")[1]);
				} else if (line.startsWith("show")) {
					SessionManager.outputAll(System.out);
				} else if (line.startsWith("list")) {
					int cnt = 0;
					for (ServerConnectionThread client : clients.values()) {
						System.out.println(client);
						cnt++;
					}
					if (cnt == 0)
						System.out.println("(empty)");
				} else if (line.startsWith("reload")) {
					this.onStop();
					setup();
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
	 * Opens a socket for clients to connect to
	 *
	 * @param address
	 *            SocketAddress of the socket to open
	 * @param receiver
	 *            Message-Handler
	 * @throws IOException
	 * @author sjacobs - Steffen Jacobs
	 */
	private SessionServer(SocketAddress address, PacketHandler receiver) throws IOException {
		this.receiver = receiver;
		this.receiver.setParent(this);

		serverSocket = ServerSocketFactory.getDefault().createServerSocket();
		serverSocket.bind(address);

		// async acceptor-thread
		acceptor = new Thread(this::asyncAcceptor);
		acceptor.start();

		onStart();
	}

	/**
	 * is called when the server starts.
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private void onStart() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> onStop()));
	}

	/**
	 * is called when the server stops
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private void onStop() {
		System.out.println("Stopping server...");
		close();
		System.out.println("Server was stopped!");
	}

	/**
	 * @return the client thread from port
	 * @param port
	 *            client-thread client-port
	 * @author sjacobs - Steffen Jacobs
	 */
	ServerConnectionThread getClientThread(int port) {
		return clients.get(port);
	}

	/**
	 * removes the client thread from port
	 * 
	 * @param port
	 *            client-thread client-port
	 * @author sjacobs - Steffen Jacobs
	 */
	ServerConnectionThread removeClientThread(int port) {
		return clients.remove(port);
	}

	/**
	 * is called when the server starts
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private void asyncAcceptor() {
		PacketHandler.output("Session-Server started on " + IP_ADDRESS + ":" + DEFAULT_PORT);
		while (!Thread.interrupted()) {
			try {
				Socket client = serverSocket.accept();
				PacketHandler.output("<-" + client.getInetAddress() + ":" + client.getPort() + " connected.");
				ServerConnectionThread clientThread = new ServerConnectionThread(client,
						(socket, data) -> messageRcv(socket.getPort(), data));
				clients.putIfAbsent(client.getPort(), clientThread);
				clientThread.start();
			} catch (IOException e) {
				Thread.currentThread().interrupt();
			}
		}
		// Close Socket due to error or request
		if (!serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				PacketHandler.output("Could not close listening socket: " + e.getMessage());
			}
		}
	}

	/**
	 * tunnels the received message to MessageReceiver
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private void messageRcv(int port, byte[] data) {
		receiver.onPacketReceived(port, data);
	}

	/**
	 * Sends a message to a connected client
	 *
	 * @param port
	 *            the port of the client
	 * @param data
	 *            the data to send
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if the client is unknown
	 * @author sjacobs - Steffen Jacobs
	 */
	public void sendMessage(int port, byte[] data) throws IOException {
		if (!clients.containsKey(port)) {
			throw new IllegalArgumentException("No such client connected: " + port);
		}
		clients.get(port).sendMessage(data);
	}

	/**
	 * Closes the connection
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (ServerConnectionThread client : clients.values()) {
			try {
				client.closeSockets();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}