package com.tpps.application.network.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import com.tpps.application.network.core.packet.Packet;
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
	private int countSent = 0, countReceived = 0;

	/** resets the metrics (how many packets where sent & received) */
	public void resetsMetrics() {
		this.countSent = 0;
		this.countReceived = 0;
	}

	/**
	 * getter for metrics: sent packets number
	 * 
	 * @return the number of packets sent since the last reset
	 */
	public int getCountSent() {
		return countSent;
	}

	/**
	 * getter for metrics: received packets number
	 * 
	 * @return the number of packets received since the last reset
	 */
	public int getCountReceived() {
		return countReceived;
	}

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

			// start async sender
			new Thread(() -> this.workQueue()).start();

			// start sync receiver
			while (!Thread.interrupted()) {
				try {
					int length = inStream.readInt();
					if (Server.DEBUG)
						System.out.println("[NETWORK] TCP-Packet received. Length: " + length);
					byte[] data = new byte[length];
					inStream.readFully(data);
					countReceived++;
					// start async receiver
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

	private ConcurrentLinkedQueue<Packet> packetQueue = new ConcurrentLinkedQueue<>();

	/**
	 * sends the bytes over the network to the connected server.
	 * 
	 * @author Steffen Jacobs
	 * @throws InterruptedException
	 */

	private void sendPacket(byte[] data) throws IOException, InterruptedException {
		holySemaphore.acquire();
		if (parent.isConnected()) {
			try {
				outStream.write(ByteUtil.intToByteArray(data.length));
				outStream.write(data);
				outStream.flush();
				countSent++;
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
	 * adds a packet to the queue and allows the queue-worker to work the queue
	 */
	public void addPacketToQueue(Packet pack) {
		packetQueue.offer(pack);
	}

	/**
	 * works the queue until it is empty and sleeps, until new packets are added
	 */
	private void workQueue() {
		System.out.println("started working" + System.currentTimeMillis());
		Packet pack;
		while (true) {
			while ((pack = packetQueue.poll()) != null) {
				try {
					sendPacket(PacketType.getBytes(pack));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
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