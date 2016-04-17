package com.tpps.technicalServices.network.clientSession.client;

import java.util.concurrent.ConcurrentHashMap;

import com.tpps.technicalServices.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionCheckRequest;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.technicalServices.network.core.SuperCallable;

/**
 * represents some kind of API-like interface where the answered requests will
 * be deployed
 * 
 * @author Steffen Jacobs
 */
public final class SessionPacketReceiverAPI {
	/**
	 * the answer to a session validity request will be deployed here.
	 * @param packet the packet that was received
	 */
	public static void onPacketSessionCheckAnswer(PacketSessionCheckAnswer packet) {
		checkRequests.remove(packet.getRequest().getTimestamp()).callMeMaybe(packet);
	}

	/**
	 * the answer to the session-get-request will be deployed here
	 * @param packet the received packet
	 * 
	 */
	public static void onPacketSessionGetAnswer(PacketSessionGetAnswer packet) {
		SuperCallable<PacketSessionGetAnswer> toCall = getRequests.remove(packet.getRequest().getUsername());
		try {
			if (toCall != null)
				toCall.callMeMaybe(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * adds a get-Request
	 * @param username the username of the get-request
	 * @param callable the callable to call later
	 * 
	 */
	static void addGetRequest(String username, SuperCallable<PacketSessionGetAnswer> callable) {
		getRequests.putIfAbsent(username, callable);
	}

	/**
	 * adds a check-Request
	 * @param req the received request-packet
	 * @param callable the callable to call later
	 * @param sendedTimestamp the timestamp when sended
	 * 
	 */

	static void addCheckRequest(PacketSessionCheckRequest req, SuperCallable<PacketSessionCheckAnswer> callable,
			long sendedTimestamp) {
		checkRequests.put(req.getTimestamp(), callable);
	}

	private static ConcurrentHashMap<String, SuperCallable<PacketSessionGetAnswer>> getRequests = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Long, SuperCallable<PacketSessionCheckAnswer>> checkRequests = new ConcurrentHashMap<>();
}
