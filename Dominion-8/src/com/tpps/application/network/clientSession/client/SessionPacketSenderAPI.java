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
import com.tpps.application.network.core.packet.Packet;

/**
 * represents some kind of API-like interface to send check- and get-requests to
 * the session-server (also keep-alive)
 * 
 * Note: ask Steffen Jacobs when you have any questions regarding network &
 * netcode
 * 
 * @author Steffen Jacobs
 */
public final class SessionPacketSenderAPI {

	/**
	 * sends a general packet to the session-server
	 * 
	 * @author Steffen Jacobs
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
	 * @author Steffen Jacobs
	 */
	public static void sendGetRequest(Client c, String username, SuperCallable<PacketSessionGetAnswer> callable) {
		SessionPacketReceiverAPI.addGetRequest(username, callable);
		sendPacket(c, new PacketSessionGetRequest(username));
	}
	
	private static long cnt = 0;
	
	private static synchronized long getNewTS(){
		return cnt++;
	}

	/**
	 * Asks the session-server to check a session and a username. It will be
	 * deployed at com.tpps.network.clients.session.PacketReceiver.
	 * onPacketSessionCheckAnswer
	 * 
	 * @author Steffen Jacobs
	 */
	public static void sendCheckRequest(Client c, String username, UUID sessionID,
			SuperCallable<PacketSessionCheckAnswer> callable) {
		PacketSessionCheckRequest req = new PacketSessionCheckRequest(username, sessionID, getNewTS());
		SessionPacketReceiverAPI.addCheckRequest(req, callable, req.getTimestamp());
		sendPacket(c, req);
	}

	/**
	 * sends a keep-alive packet to the session-server
	 * 
	 * @author Steffen Jacobs
	 */
	public static void sendKeepAlive(Client c, String username) {
		sendPacket(c, new PacketSessionKeepAlive(username));
	}
}