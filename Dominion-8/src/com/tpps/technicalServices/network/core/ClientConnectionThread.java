package com.tpps.technicalServices.network.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;
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
	private final CopyOnWriteArrayList<PacketHandler> receivers;
	private int countSent = 0, countReceived = 0;
	private Thread sendThread = null;

	private ExecutorService threadPool = Executors.newCachedThreadPool();

	/** disconnects from the server */
	void disconnect() throws IOException {
		clientSocket.close();
	}

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
	ClientConnectionThread(Socket clientSocket, ArrayList<PacketHandler> receiver, Client _parent) {
		this.receivers = new CopyOnWriteArrayList<>();
		this.receivers.addAll(receiver);
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
			parent.getListenerManager().fireConnectEvent(this.getRemotePort());

			// start async sender
			sendThread = new Thread(() -> this.workQueue());
			sendThread.start();

			// start sync receiver
			while (!Thread.interrupted()) {
				try {
					int length = inStream.readInt();
					if (Server.DEBUG)
						GameLog.log(MsgType.NETWORK_INFO, "TCP-Packet received. Length: " + length);
					byte[] data = new byte[length];
					inStream.readFully(data);
					countReceived++;

					// start async receiver
					threadPool.submit(() -> {
						for (PacketHandler handler : receivers)
							handler.handleReceivedPacket(clientSocket.getLocalPort(), PacketType.getPacket(data));
					});

				} catch (IOException e) {
					GameLog.log(MsgType.NETWORK_ERROR, "Connection Lost.");
					parent.getListenerManager().fireDisconnectEvent(this.getRemotePort());
					parent.tryReconnect();
					this.interrupt();
					this.sendThread.interrupt();
				}
			}
		} catch (IOException e) {
			GameLog.log(MsgType.NETWORK_ERROR, "Connection Lost.");
			parent.getListenerManager().fireDisconnectEvent(this.getRemotePort());
			parent.tryReconnect();
			this.interrupt();
			this.sendThread.interrupt();
		}

		if (!clientSocket.isClosed()) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				GameLog.log(MsgType.NETWORK_ERROR, "Could not close client-socket: " + e.getMessage());
			}
		}
		parent.setDisconnected();
		return;
	}

	private PacketQueue<Packet> packetQueue = new PacketQueue<>();

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
				GameLog.log(MsgType.NETWORK_ERROR, "Connection to Server lost! Reconnecting...");
				parent.getListenerManager().fireDisconnectEvent(this.getRemotePort());
				parent.connectAndLoop(true);
			}
		} else {
			GameLog.log(MsgType.NETWORK_ERROR, "Could not send packet: Server not connected.");
			parent.getListenerManager().fireDisconnectEvent(this.getRemotePort());
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

	/**
	 * adds a packet-handler
	 * 
	 * @param handler
	 */
	public void addPacketHandler(PacketHandler handler) {
		this.receivers.add(handler);
	}
}