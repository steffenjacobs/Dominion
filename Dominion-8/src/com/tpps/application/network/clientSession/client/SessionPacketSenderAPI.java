package com.tpps.application.network.clientSession.client;

import java.io.IOException;
import java.util.UUID;

import com.tpps.application.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.application.network.clientSession.packets.PacketSessionCheckRequest;
import com.tpps.application.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.application.network.clientSession.packets.PacketSessionGetRequest;
import com.tpps.application.network.clientSession.packets.PacketSessionKeepAlive;
import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.SuperCallable;
import com.tpps.application.network.packet.Packet;

/**
 * represents some kind of API-like interface to send check- and get-requests to
 * the session-server (also keep-alive)
 * 
 * Note: ask Steffen Jacobs when you have any questions regarding network &
 * netcode
 * 
 * @author sjacobs - Steffen Jacobs
 */
public final class SessionPacketSenderAPI {

	/**
	 * sends a general packet to the session-server
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	
	private static void sendPacket(Client c, Packet packet) {
		if (c.isConnected()) {
			try {
				if (SessionClient.debug()) {
					System.out.println(packet.toString());
				}
				c.sendMessage(packet);
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
	public static void sendGetRequest(Client c, String username, SuperCallable<PacketSessionGetAnswer> callable) {
		SessionPacketReceiverAPI.addGetRequest(username, callable);
		sendPacket(c, new PacketSessionGetRequest( username));
	}

	/**
	 * Asks the session-server to check a session and a username. It will be
	 * deployed at com.tpps.network.clients.session.PacketReceiver.
	 * onPacketSessionCheckAnswer
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void sendCheckRequest(Client c, String username, UUID sessionID, SuperCallable<PacketSessionCheckAnswer> callable) {
		SessionPacketReceiverAPI.addCheckRequest(username, callable);
		sendPacket(c, new PacketSessionCheckRequest(username, sessionID));
	}

	/**
	 * sends a keep-alive packet to the session-server
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void sendKeepAlive(Client c, String username) {
		sendPacket(c, new PacketSessionKeepAlive(username));
	}
}