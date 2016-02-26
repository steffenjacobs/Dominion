package com.tpps.application.network.sessions.client;

import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.network.core.SuperCallable;
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
		SuperCallable<PacketSessionCheckAnswer> toCall = checkRequests.get(packet.getRequest().getUsername());
		try {
			if (toCall != null)
				toCall.call(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * the answer to the session-get-request will be deployed here
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public static void onPacketSessionGetAnswer(PacketSessionGetAnswer packet) {
		SuperCallable<PacketSessionGetAnswer> toCall = getRequests.get(packet.getRequest().getUsername());
		try {
			if (toCall != null)
				toCall.call(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void addGetRequest(String username, SuperCallable<PacketSessionGetAnswer> callable) {
		getRequests.putIfAbsent(username, callable);
	}

	static void addCheckRequest(String username, SuperCallable<PacketSessionCheckAnswer> callable) {
		checkRequests.putIfAbsent(username, callable);
	}
	
	private static ConcurrentHashMap<String, SuperCallable<PacketSessionGetAnswer>> getRequests = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, SuperCallable<PacketSessionCheckAnswer>> checkRequests = new ConcurrentHashMap<>();
}
