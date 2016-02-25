package com.tpps.technicalServices.network.servers.session;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * represents the connectoin-thread on the server (very similar to the client
 * one
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class ServerConnectionThread extends Thread {

	private Receiver receiver;
	private Socket clientSocket;
	private DataInputStream inStream;
	private DataOutputStream outStream;

	/**
	 * constructor
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	ServerConnectionThread(Socket clientSocket, Receiver receiver) {
		this.receiver = receiver;
		this.clientSocket = clientSocket;
	}

	/**
	 * closes all sockets & streams
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void closeSockets() throws IOException {
		clientSocket.close();
		inStream.close();
		outStream.close();
	}

	/**
	 * @return a readable representation of the Connection
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public String toString() {
		return clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " @" + clientSocket.getLocalPort();
	}

	/**
	 * is called when the thread is started. Opens streams and receives bytes
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
					byte[] data = new byte[length];
					inStream.readFully(data);
					receiver.received(clientSocket, data);
				} catch (IOException e) {
					PacketHandler.output("Connection Lost with " + this.clientSocket.getInetAddress() + ":"
							+ this.clientSocket.getPort());
					SessionServer.getSessionServer().removeClientThread(this.clientSocket.getPort());
					interrupt();
				}
			}
		} catch (IOException e) {
			PacketHandler.output(
					"Connection Lost with " + this.clientSocket.getInetAddress() + ":" + this.clientSocket.getPort());
			SessionServer.getSessionServer().removeClientThread(this.clientSocket.getPort());
			interrupt();
		}

		if (!clientSocket.isClosed()) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				PacketHandler.output("Could not close client-socket: " + e.getMessage());
			}
		}
	}

	/**
	 * sends the data over the network to the connected server
	 * 
	 * @param data
	 *            bytes to send
	 * @author sjacobs - Steffen Jacobs
	 */
	public boolean sendMessage(byte[] data) throws IOException {
		try {
			outStream.write(ByteBuffer.allocate(4).putInt(data.length).array());
			outStream.write(data);
			outStream.flush();
			return true;
		} catch (SocketException e) {
			return false;
		}
	}

	/** receiver-interface */
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