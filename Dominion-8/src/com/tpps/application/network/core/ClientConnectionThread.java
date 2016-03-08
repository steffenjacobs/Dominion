package com.tpps.application.network.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Semaphore;

import com.tpps.application.network.core.packet.PacketType;
import com.tpps.technicalServices.util.ByteUtil;

/**
 * represents the connection-thread on the client (very similar to the server
 * one)
 * 
 * @author Steffen Jacobs
 */
public class ClientConnectionThread extends Thread {

	private DataInputStream inStream;
	private DataOutputStream outStream;

	private final Socket clientSocket;
	private final Client parent;
	private final PacketHandler receiver;

	// This one is holy, too, like in ServerConnectionThread
	private Semaphore holySemaphore = new Semaphore(1);

	/**
	 * constructor for ConnectionThread
	 * 
	 * @author Steffen Jacobs
	 */
	ClientConnectionThread(Socket clientSocket, PacketHandler receiver, Client _parent) {
		this.receiver = receiver;
		this.clientSocket = clientSocket;
		this.parent = _parent;
	}

	/**
	 * is called as soon as the thread starts
	 * 
	 * @author Steffen Jacobs
	 */
	@Override
	public void run() {
		try {
			inStream = new DataInputStream(clientSocket.getInputStream());
			outStream = new DataOutputStream(clientSocket.getOutputStream());
			while (!Thread.interrupted()) {
				try {
					int length = inStream.readInt();
					if (Server.DEBUG)
						System.out.println("[NETWORK] TCP-Packet received. Length: " + length);
					byte[] data = new byte[length];
					inStream.readFully(data);
					new Thread(() -> receiver.handleReceivedPacket(clientSocket.getLocalPort(),
							PacketType.getPacket(data))).start();
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
	 * @author Steffen Jacobs
	 * @throws InterruptedException 
	 */
	public void sendPacket(byte[] data) throws IOException, InterruptedException {
			holySemaphore.acquire();
		if (parent.isConnected()) {
			try {
				outStream.write(ByteUtil.intToByteArray(data.length));
				outStream.write(data);
				outStream.flush();
			} catch (SocketException | NullPointerException e) {
				parent.setDisconnected();
				System.out.println("NETWORK-ERROR: Connection to Server lost! Reconnecting...");
				parent.connectAndLoop(true);
			}
		} else {
			System.out.println("NETWORK-ERROR: Could not send packet: Server not connected.");
		}
		holySemaphore.release();
	}

	/**
	 * needed for testing
	 * 
	 * @return remote port on server
	 * @author Steffen Jacobs
	 */
	public int getRemotePort() {
		return clientSocket.getPort();
	}

	/**
	 * needed for testing
	 * 
	 * @return local port
	 * @author Steffen Jacobs
	 */
	public int getLocalPort() {
		return clientSocket.getLocalPort();
	}
}