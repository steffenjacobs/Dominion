package com.tpps.application.network.core;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.SocketFactory;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * represents a client connected to a server on a higher layer then
 * ClientConnectionThread
 * 
 * @author Steffen Jacobs
 */
public class Client {

	private boolean connecting = false, connected = false;
	private Thread tryToConnectThread = null;
	private PacketHandler handler;
	private ClientConnectionThread connectionThread;
	private SocketAddress address;

	/**
	 * needed for testing
	 * 
	 * @author Steffen Jacobs
	 * @return ClientConnectionThread holding the connection to the server
	 */
	public ClientConnectionThread getConnectionThread() {
		return this.connectionThread;
	}

	/**
	 * Tries to connect to the loaded server synchronously until a connection is
	 * established.
	 * 
	 * (ATTENTION: blocks the calling thread!)
	 * 
	 * @author Steffen Jacobs
	 */
	private void connectAndLoopLogic() {
		long CONNECTION_TIMEOUT = 5000;

		while (!Thread.interrupted()) {
			this.connected = false;
			try {
				try {
					final Socket clientSocket = SocketFactory.getDefault().createSocket();
					clientSocket.connect(address, 5000);
					System.out.println("Connected to Server.");
					this.connected = true;
					connectionThread = new ClientConnectionThread(clientSocket, handler, this);
					connectionThread.start();
					Thread.yield();
				} catch (ConnectException ex) {
					this.connected = false;
					System.out.println("Connection refused. Reconnecting...");
				}
				Thread.sleep(50);
				if (this.connected) {
					break;
				} else {
					Thread.sleep(CONNECTION_TIMEOUT);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// do nothing: this exception is normal when the program
				// exits.
			}
		}
		connecting = false;
	}

	/**
	 * Tries to connect to the loaded server synchronously or asynchronously
	 * until a connection is established.
	 * 
	 * @author Steffen Jacobs
	 */
	public void connectAndLoop(boolean async) {

		if (!connecting) {
			connecting = true;

			if (tryToConnectThread != null)
				tryToConnectThread.interrupt();

			if (async) {
				tryToConnectThread = new Thread(() -> connectAndLoopLogic());
				tryToConnectThread.start();
			} else {
				connectAndLoopLogic();
			}
		}
	}

	/**
	 * tries to reconnect to the server
	 * 
	 * @author Steffen Jacobs
	 */
	public void tryReconnect() {
		connected = false;
		connectAndLoop(true);
	}

	//
	/**
	 * @return wheter the client is connected to the server
	 * @author Steffen Jacobs
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * sets the connected-boolean to false
	 *
	 * @author Steffen Jacobs
	 */
	public void setDisconnected() {
		this.connected = false;
	}

	/**
	 * Tries to connect to the specified server (5sec timeout)
	 *
	 * @param address
	 *            SocketAddress of the server
	 * @param _handler
	 *            an implementation of the interface
	 * @param async:
	 *            make the thread wait until the connection is established
	 * @throws IOException
	 * @author Steffen Jacobs
	 */
	public Client(SocketAddress _address, PacketHandler _handler, boolean connectAsync) throws IOException {
		this.address = _address;
		this.handler = _handler;
		connectAndLoop(connectAsync);
	}

	/**
	 * Tries to connect to the specified server (5sec timeout)
	 *
	 * @param address
	 *            SocketAddress of the server
	 * @param _handler
	 *            an implementation of the interface
	 * @throws IOException
	 * @author Steffen Jacobs
	 */
	public Client(SocketAddress _address, PacketHandler _handler) throws IOException {
		this.address = _address;
		this.handler = _handler;
		connectAndLoop(true);
	}

	/**
	 * Closes the connection
	 * 
	 * @author Steffen Jacobs
	 */
	public void disconnect() {
		this.connected = false;
		this.connectionThread.interrupt();
	}

	/**
	 * Sends a message to the server - replacement for sendPacket(byte[])
	 * 
	 * @param data
	 *            the data to send
	 * @throws IOException
	 * @author Steffen Jacobs
	 */
	public void sendMessage(Packet packet) throws IOException {
		if (this.connected) {
			new Thread(() -> {
				try {
					connectionThread.sendPacket(PacketType.getBytes(packet));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();

		} else {
			System.out.println("NETWORK ERROR: Could not send packet: No Connection.");
			this.connectAndLoop(true);
		}
	}

	/**
	 * 
	 * @return the packetHandler of the client
	 */
	public PacketHandler getHandler() {
		return handler;
	}

}
