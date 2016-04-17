package com.tpps.technicalServices.network.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Semaphore;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * represents the connectoin-thread on the server (very similar to the client
 * one
 * 
 * @author Steffen Jacobs
 */
public class ServerConnectionThread extends Thread {

	private PacketHandler receiver;
	private Socket clientSocket;
	private DataInputStream inStream;
	private DataOutputStream outStream;
	private final Server parent;

	// this is the holy semaphore. Don't touch it. Don't even look at it.
	private Semaphore holySemaphore = new Semaphore(1);

	private PacketQueue<Packet> packetQueue = new PacketQueue<>();

	/**
	 * constructor
	 * @param clientSocket the socket connected to the client
	 * @param receiver the packet-handler handling the packetrs
	 * @param _parent the parent instance of the Server
	 * 
	 */
	ServerConnectionThread(Socket clientSocket, PacketHandler receiver, Server _parent) {
		this.receiver = receiver;
		this.clientSocket = clientSocket;
		this.parent = _parent;
		parent.getListenerManager().fireConnectEvent(this.clientSocket.getPort());
	}

	/**
	 * closes all sockets & streams
	 * @throws IOException 
	 * 
	 */
	public void closeSockets() throws IOException {
		clientSocket.close();
		inStream.close();
		outStream.close();
	}

	/**
	 * @return a readable representation of the Connection
	 */
	@Override
	public String toString() {
		return clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " @" + clientSocket.getLocalPort();
	}

	/**
	 * is called when the thread is started. Opens streams and receives bytes
	 * 
	 */
	@Override
	public void run() {
		try {
			inStream = new DataInputStream(clientSocket.getInputStream());
			outStream = new DataOutputStream(clientSocket.getOutputStream());

			// start async sender
			new Thread(() -> this.workQueue()).start();

			while (!Thread.interrupted()) {
				try {
					int length = inStream.readInt();
					byte[] data = new byte[length];
					inStream.readFully(data);
					new Thread(() -> receiver.handleReceivedPacket(clientSocket.getPort(), PacketType.getPacket(data)))
							.start();
				} catch (IOException e) {
					parent.getHandler().output("Connection Lost with " + this.clientSocket.getInetAddress() + ":"
							+ this.clientSocket.getPort());
					parent.removeClientThread(this.clientSocket.getPort());
					parent.getListenerManager().fireDisconnectEvent(this.clientSocket.getPort());
					interrupt();
				}
			}
		} catch (IOException e) {
			parent.getHandler().output(
					"Connection Lost with " + this.clientSocket.getInetAddress() + ":" + this.clientSocket.getPort());
			parent.removeClientThread(this.clientSocket.getPort());
			parent.getListenerManager().fireDisconnectEvent(this.clientSocket.getPort());
			interrupt();
		}

		if (!clientSocket.isClosed()) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				parent.getHandler().output("Could not close client-socket: " + e.getMessage());
			}
		}
	}

	/**
	 * sends the data synchronously over the network to the connected server
	 * 
	 * @param data
	 *            bytes to send
	 * @throws IOException 
	 * @throws InterruptedException
	 */
	private void sendMessage(byte[] data) throws IOException, InterruptedException {
		holySemaphore.acquire();
		try{
		outStream.writeInt(data.length);
		outStream.write(data);
		outStream.flush();
		}
		catch(SocketException se){
			GameLog.log(MsgType.NETWORK_INFO, "X " + this.getPort());
		}
		holySemaphore.release();
	}

	/**
	 * adds a packet to the queue and allows the queue-worker to work the queue
	 * 
	 * @param pack
	 *            packet to add to the queue
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
					sendMessage(PacketType.getBytes(pack));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**@return the client's port*/
	public int getPort(){
		return clientSocket.getPort();
	}
}