package com.tpps.technicalServices.network.clientSession.client;

import java.io.IOException;
import java.util.UUID;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionCheckRequest;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionGetRequest;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionKeepAlive;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.SuperCallable;
import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * represents some kind of API-like interface to send check- and get-requests to
 * the session-server (also keep-alive)
 * 
 * @author Steffen Jacobs
 */
public final class SessionPacketSenderAPI {

	/**
	 * sends a general packet to the session-server
	 * @param c the client to send the packet over
	 * @param packet the packet to send
	 * 
	 */

	private static void sendPacket(Client c, Packet packet) {
		if (c.isConnected()) {
			try {
				if (SessionClient.debug()) {
					GameLog.log(MsgType.NETWORK_INFO, packet.toString());
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
	 * @param c the client to send the request over
	 * @param username the username
	 * @param callable the callable to call later
	 * 
	 */
	public static void sendGetRequest(Client c, String username, SuperCallable<PacketSessionGetAnswer> callable) {
		SessionPacketReceiverAPI.addGetRequest(username, callable);
		sendPacket(c, new PacketSessionGetRequest(username));
	}

	private static long cnt = 0;

	private static synchronized long getNewTS() {
		return cnt++;
	}

	/**
	 * Asks the session-server to check a session and a username. It will be
	 * deployed at com.tpps.network.clients.session.PacketReceiver.
	 * onPacketSessionCheckAnswer
	 * @param c the client to send the request over
	 * @param username the username to check
	 * @param sessionID the session-id to check
	 * @param callable the callable to invoke later
	 * 
	 */
	public static void sendCheckRequest(Client c, String username, UUID sessionID,
			SuperCallable<PacketSessionCheckAnswer> callable) {
		PacketSessionCheckRequest req = new PacketSessionCheckRequest(username, sessionID, getNewTS());
		SessionPacketReceiverAPI.addCheckRequest(req, callable, req.getTimestamp());
		sendPacket(c, req);
	}

	/**
	 * sends a keep-alive packet to the session-server
	 * @param c the client to keep-alive over
	 * @param username the user to keep alive
	 * 
	 */
	public static void sendKeepAlive(Client c, String username) {
		sendPacket(c, new PacketSessionKeepAlive(username));
	}
}