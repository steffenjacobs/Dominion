package com.tpps.network.clients.session;

import com.tpps.network.packets.session.PacketSessionCheckAnswer;
import com.tpps.network.packets.session.PacketSessionGetAnswer;

/**
 * represents some kind of API-like interface where the answered requests will
 * be deployed
 * 
 * Note: ask Steffen Jacobs when you have any questions regarding network &
 * netcode
 * 
 * @author sjacobs - Steffen Jacobs
 */
public final class PacketReceiverAPI {

	/**
	 * the answer to a session validity request will be deployed here.
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void onPacketSessionCheckAnswer(PacketSessionCheckAnswer packet) {
		// insert code here
	}

	/**
	 * the answer to the session-get-request will be deployed here
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void onPacketSessionGetAnswer(PacketSessionGetAnswer packet) {
		// for login server
	}
}
