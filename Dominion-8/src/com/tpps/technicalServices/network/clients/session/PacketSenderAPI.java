package com.tpps.technicalServices.network.clients.session;

import java.io.IOException;
import java.util.UUID;

import com.tpps.technicalServices.network.packets.Packet;
import com.tpps.technicalServices.network.packets.PacketType;
import com.tpps.technicalServices.network.packets.session.PacketSessionCheckRequest;
import com.tpps.technicalServices.network.packets.session.PacketSessionGetRequest;
import com.tpps.technicalServices.network.packets.session.PacketSessionKeepAlive;

/**
 * represents some kind of API-like interface to send check- and get-requests to
 * the session-server (also keep-alive)
 * 
 * Note: ask Steffen Jacobs when you have any questions regarding network &
 * netcode
 * 
 * @author sjacobs - Steffen Jacobs
 */
public final class PacketSenderAPI {

	/**
	 * sends a general packet to the session-server
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private static void sendPacket(Packet packet) {
		if (SessionClient.isConnected()) {
			try {
				if (SessionClient.DEBUG_PACKETS) {
					System.out.println(packet.toString());
				}
				SessionClient.getConnection().sendMessage(PacketType.getBytes(packet));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Network Error: No connnection");
		}
	}

	/**
	 * Asks the sessionServer for a Session for the username. It will be
	 * deployed at
	 * com.tpps.network.clients.session.PacketReceiver.onPacketSessionGetAnswer
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void sendGetRequest(String username) {
		sendPacket(new PacketSessionGetRequest(username));
	}

	/**
	 * Asks the session-server to check a session and a username. It will be
	 * deployed at com.tpps.network.clients.session.PacketReceiver.
	 * onPacketSessionCheckAnswer
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void sendCheckRequest(String username, UUID sessionID) {
		sendPacket(new PacketSessionCheckRequest(username, sessionID));
	}

	/**
	 * sends a keep-alive packet to the session-server
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	static void sendKeepAlive(String username) {
		sendPacket(new PacketSessionKeepAlive(username));
	}

	/**
	 * disconnects from the server
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	static void disconnect(boolean shuttingDown) {
		if (SessionClient.isConnected()) {
			SessionClient.getConnection().close();
			SessionClient.setConnected(false);
		} else {
			if (!shuttingDown)
				System.err.println("Network Error: No Connection");
		}
	}
}