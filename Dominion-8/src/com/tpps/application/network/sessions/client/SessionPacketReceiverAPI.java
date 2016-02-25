package com.tpps.application.network.sessions.client;

import com.tpps.application.network.sessions.packets.PacketSessionCheckAnswer;
import com.tpps.application.network.sessions.packets.PacketSessionGetAnswer;

/**
 * represents some kind of API-like interface where the answered requests will
 * be deployed
 * 
 * Note: ask Steffen Jacobs when you have any questions regarding network &
 * netcode
 * 
 * @author sjacobs - Steffen Jacobs
 */
public final class SessionPacketReceiverAPI {

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
