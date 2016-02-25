package com.tpps.application.network.sessions.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.SocketFactory;

public class ConnectedClient {

	private ClientConnectionThread thread;

	/**
	 * Tries to connect to the specified server (5sec timeout)
	 *
	 * @param address
	 *            SocketAddress of the server
	 * @param receiver
	 *            an implementation of the interface
	 * @throws IOException
	 * @author sjacobs - Steffen Jacobs
	 */
	public ConnectedClient(SocketAddress address, Receiver receiver) throws IOException {
		try {
			Socket clientSocket = SocketFactory.getDefault().createSocket();
			clientSocket.connect(address, 5000);
			System.out.println("Connected to Session-Server.");
			SessionClient.setConnected(true);
			thread = new ClientConnectionThread(clientSocket, (socket, data) -> receiver.received(data));
			thread.start();
			Thread.yield();
		} catch (ConnectException ex) {
			SessionClient.setConnected(false);
			System.out.println("Connection refused. Reconnecting...");
		}
	}

	/**
	 * Closes the connection
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void close() {
		this.thread.interrupt();
	}

	/**
	 * Sends a message to the server
	 * 
	 * @param data
	 *            the data to send
	 * @throws IOException
	 * @author sjacobs - Steffen Jacobs
	 */
	public void sendMessage(byte[] data) throws IOException {
		if (SessionClient.isConnected()) {
			thread.sendPacket(data);
		} else {
			System.out.println("Could not send packet: No Connection.");
			SessionClient.connectAndLoop();
		}
	}

	public interface Receiver {
		/**
		 * Is called when a message is received
		 * 
		 * @param data
		 *            the data received
		 * @author sjacobs - Steffen Jacobs
		 */
		public void received(byte[] data);
	}
}