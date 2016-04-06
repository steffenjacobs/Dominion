package com.tpps.technicalServices.network.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ServerSocketFactory;

import com.tpps.technicalServices.logger.Log;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.events.NetworkListenerManager;
import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * represents a general server that can send and receive packets from and to
 * multiple clients
 * 
 * @author Steffen Jacobs
 */
public class Server {

	public static final boolean DEBUG = false;

	private ServerSocket serverSocket;
	private PacketHandler handler;
	private Thread acceptor, shutdownHook = new Thread(() -> onApplicationExit());

	private NetworkListenerManager listenerManager = new NetworkListenerManager();

	// Integer represents the port
	protected ConcurrentHashMap<Integer, ServerConnectionThread> clients = new ConcurrentHashMap<>();

	/**
	 * Opens a socket for clients to connect to
	 *
	 * @param address
	 *            SocketAddress of the socket to open
	 * @param _handler
	 *            Message-Handler
	 * @throws IOException
	 */
	public Server(SocketAddress address, PacketHandler _handler) throws IOException {
		this.handler = _handler;
		this.handler.setParent(this);

		bind(address);
		startListening();
	}

	/**
	 * binds the server to the port with the given SocketAddress
	 * 
	 * @param address
	 *            SocketAddress to bind the server-socket to
	 * @throws IOException
	 */
	private void bind(SocketAddress address) throws IOException {
		serverSocket = ServerSocketFactory.getDefault().createServerSocket();
		serverSocket.bind(address);
	}

	/**
	 * is called when the server starts listening.
	 */
	protected void startListening() {
		// async acceptor-thread
		acceptor = new Thread(this::asyncAcceptor);
		acceptor.start();

		// add shutdown-hook
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}

	/**
	 * Closes the connection
	 * 
	 */
	protected void stopListening() {
		acceptor.interrupt();

		try {
			serverSocket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (ServerConnectionThread client : clients.values()) {
			try {
				this.getListenerManager().fireDisconnectEvent(client.getPort());
				client.closeSockets();
				client.interrupt();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * is called when the server starts
	 * 
	 */
	private void asyncAcceptor() {
		this.getHandler().output("Starting Server on " + serverSocket.getInetAddress());
		while (!Thread.interrupted()) {
			try {
				Socket client = serverSocket.accept();
				this.getHandler().output("<-" + client.getInetAddress() + ":" + client.getPort() + " connected.");
				ServerConnectionThread clientThread = new ServerConnectionThread(client, handler, this);

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
				this.getHandler().output("Could not close listening socket: " + e.getMessage());
			}
		}
	}

	/**
	 * Sends a message to a connected client - replacement for sendMessage(int
	 * port, byte[] data)
	 *
	 * @param port
	 *            the port of the client
	 * @param data
	 *            the data to send
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if the client is unknown
	 */
	public void sendMessage(int port, Packet packet) throws IOException {
		if (!clients.containsKey(port)) {
			throw new IllegalArgumentException("No such client connected: " + port);
		}
		clients.get(port).addPacketToQueue(packet);
	}

	/**
	 * broadcasts a packet to all connected clients except the one who sends it.
	 * The one who wants to send it is identified by the port.
	 * 
	 * @param packet
	 *            Packet to broadcast
	 * @param senderPort
	 *            identifies the client who wants to broadcast. He will not get
	 *            the packet.
	 */
	public void broadcastMessage(int senderPort, Packet packet) throws IOException {
		for (Entry<Integer, ServerConnectionThread> entr : clients.entrySet()) {
			if (entr.getKey().intValue() == senderPort)
				continue;
			entr.getValue().addPacketToQueue(packet);
		}
	}

	/**
	 * broadcasts a packet to all connected clients *
	 * 
	 * @param packet
	 *            Packet to broadcast
	 */
	public void broadcastMessage(Packet packet) throws IOException {
		for (ServerConnectionThread entr : clients.values()) {
			entr.addPacketToQueue(packet);
		}
	}

	/** is called to stop the server */
	private void stopServer() {
		Log.log(MsgType.NETWORK_INFO, "Stopping server...");
		stopListening();
		Log.log(MsgType.NETWORK_INFO, "Server was stopped!");
	}

	/**
	 * is called when the server stops
	 * 
	 */
	private void onApplicationExit() {
		stopServer();
	}

	/**
	 * removes the client thread from port
	 * 
	 * @param port
	 *            client-thread client-port
	 */
	ServerConnectionThread removeClientThread(int port) {
		return clients.remove(port);
	}

	/**
	 * @return the client thread from port
	 * @param port
	 *            client-thread client-port
	 */
	public ServerConnectionThread getClientThread(int port) {
		return clients.get(port);
	}

	/**
	 * @return handler for all packets
	 */
	public PacketHandler getHandler() {
		return this.handler;
	}

	/**
	 * getter for the NetworkListenerManager
	 * 
	 * @return the NetworkListenerManager instance for registering listeners to
	 *         this server
	 */
	public NetworkListenerManager getListenerManager() {
		return listenerManager;
	}

	/**
	 * is called to disconnect all clients
	 */
	public void disconnectAll() {

		Log.log(MsgType.NETWORK_INFO, "Disconnecting All Clients...");
		for (ServerConnectionThread client : this.clients.values()) {
			this.listenerManager.fireDisconnectEvent(client.getPort());
			client.interrupt();
		}
		this.clients.clear();

	}
}