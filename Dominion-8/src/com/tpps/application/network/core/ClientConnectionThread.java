package com.tpps.application.network.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * represents the connection-thread on the client (very similar to the server
 * one)
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class ClientConnectionThread extends Thread {

	private DataInputStream inStream;
	private DataOutputStream outStream;

	private final Socket clientSocket;
	private final Client parent;
	private final PacketHandler receiver;

	/**
	 * constructor for ConnectionThread
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	ClientConnectionThread(Socket clientSocket, PacketHandler receiver, Client _parent) {
		this.receiver = receiver;
		this.clientSocket = clientSocket;
		this.parent = _parent;
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
					receiver.handleReceivedPacket(clientSocket.getLocalPort(), data);
				} catch (IOException e) {
					System.out.println("Network Error: Connection Lost.");
					interrupt();
					parent.tryReconnect();
				}
			}
		} catch (IOException e) {
			System.out.println("Network Error: Connection Lost.");
			interrupt();
			parent.tryReconnect();
		}

		if (!clientSocket.isClosed()) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				System.out.println("Could not close client-socket: " + e.getMessage());
			}
		}
		parent.setDisconnected();
	}

	/**
	 * sends the bytes over the network to the connected server.
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void sendPacket(byte[] data) throws IOException {
		if (parent.isConnected()) {
			try {
				outStream.write(ByteBuffer.allocate(4).putInt(data.length).array());
				outStream.write(data);
				outStream.flush();
			} catch (SocketException | NullPointerException e) {
				parent.setDisconnected();
				System.out.println("Connection to Cloud-Server lost! Reconnecting...");
				parent.connectAndLoop();
			}
		} else {
			System.out.println("Could not send packet: Server not connected.");
		}
	}

	/**
	 * needed for testing
	 * 
	 * @return remote port on server
	 * @author sjacobs - Steffen Jacobs
	 */
	public int getRemotePort() {
		return clientSocket.getPort();
	}

	/**
	 * needed for testing
	 * 
	 * @return local port
	 * @author sjacobs - Steffen Jacobs
	 */
	public int getLocalPort() {
		return clientSocket.getLocalPort();
	}
}