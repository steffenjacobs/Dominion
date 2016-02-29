package com.tpps.application.network.core;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.SocketFactory;

/**
 * represents a client connected to a server on a higher layer then
 * ClientConnectionThread
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class Client {

	private boolean connecting = false, connected = false;
	private Thread tryToConnectThread = null;
	private PacketHandler handler;
	private ClientConnectionThread thread;
	private SocketAddress address;

	/**
	 * Tries to connect to the loaded server asynchronously until a connection
	 * is established.
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void connectAndLoop() {

		long CONNECTION_TIMEOUT = 5000;

		if (!connecting) {
			connecting = true;

			if (tryToConnectThread != null)
				tryToConnectThread.interrupt();

			tryToConnectThread = new Thread(() -> {

				while (!Thread.interrupted()) {
					this.connected = false;
					try {
						try {
							final Socket clientSocket = SocketFactory.getDefault().createSocket();
							clientSocket.connect(address, 5000);
							System.out.println("Connected to Server.");
							this.connected = true;
							thread = new ClientConnectionThread(clientSocket, handler, this);
							thread.start();
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
			});
			tryToConnectThread.start();
		}
	}

	/**
	 * tries to reconnect to the server
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void tryReconnect() {
		connected = false;
		connectAndLoop();
	}

	//
	/**
	 * @return wheter the client is connected to the server
	 * @author sjacobs - Steffen Jacobs
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * sets the connected-boolean to false
	 *
	 * @author sjacobs - Steffen Jacobs
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
	 * @throws IOException
	 * @author sjacobs - Steffen Jacobs
	 */
	public Client(SocketAddress _address, PacketHandler _handler) throws IOException {
		this.address = _address;
		this.handler = _handler;
		connectAndLoop();
	}

	/**
	 * Closes the connection
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void disconnect() {
		this.connected = false;
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
		if (this.connected) {
			thread.sendPacket(data);
		} else {
			System.out.println("Could not send packet: No Connection.");
			this.connectAndLoop();
		}
	}
}
