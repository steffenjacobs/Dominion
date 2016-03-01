package com.tpps.application.network.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ServerSocketFactory;

import com.tpps.application.network.packet.Packet;
import com.tpps.application.network.packet.PacketType;

public class Server {
	private ServerSocket serverSocket;
	private PacketHandler handler;
	private Thread acceptor;

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
	 * @author sjacobs - Steffen Jacobs
	 */
	public Server(SocketAddress address, PacketHandler _handler) throws IOException {
		this.handler = _handler;
		this.handler.setParent(this);

		serverSocket = ServerSocketFactory.getDefault().createServerSocket();
		serverSocket.bind(address);
		startListening();
	}

	/**
	 * @return handler for all packets
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketHandler getHandler() {
		return this.handler;
	}

	/**
	 * is called when the server starts.
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	protected void startListening() {
		// async acceptor-thread
		acceptor = new Thread(this::asyncAcceptor);
		acceptor.start();

		// add shutdown-hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> onApplicationExit()));
	}

	/**
	 * is called when the server stops
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private void onApplicationExit() {
		System.out.println("Stopping server...");
		stopListening();
		System.out.println("Server was stopped!");
	}

	/**
	 * @return the client thread from port
	 * @param port
	 *            client-thread client-port
	 * @author sjacobs - Steffen Jacobs
	 */
	public ServerConnectionThread getClientThread(int port) {
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
		this.getHandler().output("Starting Server on " + serverSocket.getInetAddress());
		while (!Thread.interrupted()) {
			try {
				Socket client = serverSocket.accept();
				this.getHandler().output("<-" + client.getInetAddress() + ":" + client.getPort() + " connected.");
				ServerConnectionThread clientThread = new ServerConnectionThread(client,
						(socket, data) -> handler.handleReceivedPacket(socket.getPort(), PacketType.getPacket(data)), this);
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
	 * Sends a message to a connected client - replacement for sendMessage(int port, byte[] data)
	 *
	 * @param port
	 *            the port of the client
	 * @param data
	 *            the data to send
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if the client is unknown
	 * @author sjacobs - Steffen Jacobs
	 * */
	public void sendMessage(int port, Packet packet) throws IOException {
		if (!clients.containsKey(port)) {
			throw new IllegalArgumentException("No such client connected: " + port);
		}
		clients.get(port).sendMessage(PacketType.getBytes(packet));
	}

	/**
	 * Closes the connection
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	protected void stopListening() {
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
