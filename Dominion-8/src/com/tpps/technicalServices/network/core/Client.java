package com.tpps.technicalServices.network.core;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.net.SocketFactory;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.events.NetworkListenerManager;
import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * represents a client connected to a server on a higher layer then
 * ClientConnectionThread
 * 
 * @author Steffen Jacobs
 */
public class Client {

	private boolean connecting = false, connected = false;
	private Thread tryToConnectThread = null;
	private ArrayList<PacketHandler> handlers;
	private ClientConnectionThread connectionThread;
	private SocketAddress address;

	private NetworkListenerManager listenerManager = new NetworkListenerManager();

	/**
	 * Tries to connect to the specified server (5sec timeout)
	 *
	 * @param _address
	 *            SocketAddress of the server
	 * @param _handler
	 *            an implementation of the interface
	 * @param connectAsync
	 *            make the thread wait until the connection is established
	 * @throws IOException
	 */
	public Client(SocketAddress _address, PacketHandler _handler, boolean connectAsync) throws IOException {
		this.address = _address;
		this.handlers = new ArrayList<>();
		this.handlers.add(_handler);
		connectAndLoop(connectAsync);
	}

	/**
	 * Tries to connect to the specified server (5sec timeout)
	 *
	 * @param _address
	 *            SocketAddress of the server
	 * @param _handler
	 *            an implementation of the interface
	 * @throws IOException
	 */
	public Client(SocketAddress _address, PacketHandler _handler) throws IOException {
		this(_address, _handler, true);
	}

	/**
	 * Tries to connect to the loaded server synchronously until a connection is
	 * established.
	 * 
	 * (ATTENTION: blocks the calling thread!)
	 * 
	 */
	private void connectAndLoopLogic() {
		int CONNECTION_TIMEOUT = 1500;
		Socket clientSocket = null;
		while (!Thread.interrupted()) {
			GameLog.log(MsgType.NETWORK_INFO, "Trying to connect to " + address.toString() + "...");
			this.connected = false;
			try {
				try {
					clientSocket = SocketFactory.getDefault().createSocket();
					clientSocket.connect(address, CONNECTION_TIMEOUT);
					GameLog.log(MsgType.NETWORK_INFO, "Connected to Server.");
					this.connected = true;
					connectionThread = new ClientConnectionThread(clientSocket, handlers, this);
					connectionThread.start();
				} catch (ConnectException ex) {
					this.connected = false;
					clientSocket.close();
					if (connectionThread != null && !connectionThread.isInterrupted()) {
						connectionThread.interrupt();
					}
					GameLog.log(MsgType.NETWORK_ERROR, "Connection refused. Reconnecting...");
				} catch (SocketTimeoutException ste) {
					GameLog.log(MsgType.NETWORK_ERROR, ste.getMessage());
				}
				Thread.sleep(50);
				if (this.connected) {
					break;
				} else {
					Thread.sleep(CONNECTION_TIMEOUT);
				}
			} catch (IOException e) {
				if (e.getMessage().startsWith("Network is unreachable")){
					try {
						GameLog.log(MsgType.NETWORK_ERROR, e.getMessage() + " to " + address.toString());
						Thread.sleep(CONNECTION_TIMEOUT);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				else
				e.printStackTrace();
			} catch (InterruptedException e) {
				// do nothing: this exception is normal when the program
				// exits.
				GameLog.log(MsgType.EXCEPTION, "Exit Program. (nothing to worry about)");
			}
		}
		connecting = false;
	}

	/**
	 * Tries to connect to the loaded server synchronously or asynchronously
	 * until a connection is established.
	 * 
	 * @param async
	 *            whether to connect async or not
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
	 */
	public void tryReconnect() {
		connected = false;
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// normal when thread is killed
		}
		new Thread(() -> connectAndLoop(false)).start();
	}

	/**
	 * Closes the connection
	 * 
	 */
	public void disconnect() {
		this.connected = false;

		if (this.connectionThread != null) {
			try {
				this.connectionThread.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.connectionThread.interrupt();
		}

		if (this.connecting) {
			this.tryToConnectThread.interrupt();
			GameLog.log(MsgType.NETWORK_INFO, "stopped reconnect-attempt");
		}
	}

	/**
	 * Sends a message to the server - replacement for sendPacket(byte[])
	 * 
	 * @param packet
	 *            the data to send
	 * @throws IOException
	 */

	public void sendMessage(Packet packet) throws IOException {
		if (this.connected) {
			new Thread(() -> {
				try {
					connectionThread.addPacketToQueue(packet);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();

		} else {
			GameLog.log(MsgType.NETWORK_ERROR, "Could not send packet: No Connection.");
			this.connectAndLoop(true);
		}
	}

	/**
	 * @return wheter the client is connected to the server
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * sets the connected-boolean to false
	 *
	 */
	void setDisconnected() {
		this.connected = false;
	}

	/**
	 * needed for testing
	 * 
	 * @return ClientConnectionThread holding the connection to the server
	 */
	public ClientConnectionThread getConnectionThread() {
		return this.connectionThread;
	}

	/**
	 * 
	 * @return the packetHandler of the client
	 */
	public ArrayList<PacketHandler> getHandlers() {
		return handlers;
	}

	/**
	 * getter for the NetworkListenerManager
	 * 
	 * @return the NetworkListenerManager instance for registering listeners to
	 *         this client
	 */
	public NetworkListenerManager getListenerManager() {
		return listenerManager;
	}

	/**
	 * adds a packet handler
	 * 
	 * @param handler
	 *            the packet-handler to add
	 */
	public void addPacketHandler(PacketHandler handler) {
		if (this.connectionThread != null)
			this.connectionThread.addPacketHandler(handler);

		this.handlers.add(handler);
	}

}
