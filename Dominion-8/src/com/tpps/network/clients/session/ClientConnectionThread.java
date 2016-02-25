package com.tpps.network.clients.session;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * represents the connectoin-thread on the client (very similar to the server
 * one)
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class ClientConnectionThread extends Thread {

	private Receiver receiver;
	private Socket clientSocket;
	private DataInputStream inStream;
	private DataOutputStream outStream;

	/**
	 * constructor for ConnectionThread
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	ClientConnectionThread(Socket clientSocket, Receiver receiver) {
		this.receiver = receiver;
		this.clientSocket = clientSocket;
	}

	/**
	 * is called as soon as the thread starts
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public void run() {
		try {
			inStream = new DataInputStream(clientSocket.getInputStream());
			outStream = new DataOutputStream(clientSocket.getOutputStream());
			while (!Thread.interrupted()) {
				try {
					int length = inStream.readInt();
					System.out.println(length);
					byte[] data = new byte[length];
					inStream.readFully(data);
					receiver.received(clientSocket, data);
				} catch (IOException e) {
					System.out.println("Network Error: Connection Lost.");
					interrupt();
					SessionClient.tryReconnect();
				}
			}
		} catch (IOException e) {
			System.out.println("Network Error: Connection Lost.");
			interrupt();
			SessionClient.tryReconnect();
		}

		if (!clientSocket.isClosed()) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				System.out.println("Could not close client-socket: " + e.getMessage());
			}
		}
		SessionClient.setConnected(false);
	}

	/**
	 * sends the bytes over the network to the connected server.
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void sendPacket(byte[] data) throws IOException {
		if (SessionClient.isConnected()) {
			try {
				outStream.write(ByteBuffer.allocate(4).putInt(data.length).array());
				outStream.write(data);
				outStream.flush();
			} catch (SocketException | NullPointerException e) {
				SessionClient.setConnected(false);
				System.out.println("Connection to Cloud-Server lost! Reconnecting...");
				SessionClient.connectAndLoop();
			}
		} else {
			System.out.println("Could not send packet: Server not connected.");
		}
	}

	/**
	 * represents a receiver
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public interface Receiver {
		/**
		 * Is called when a message is received
		 *
		 * @param data
		 *            the data received
		 * @author sjacobs - Steffen Jacobs
		 */
		public void received(Socket socket, byte[] data);
	}
}