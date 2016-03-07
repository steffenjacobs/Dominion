package com.tpps.application.network.clientSession.client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.application.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.application.network.core.SuperCallable;

/**
 * represents some kind of API-like interface where the answered requests will
 * be deployed
 * 
 * Note: ask Steffen Jacobs when you have any questions regarding network &
 * netcode
 * 
 * @author Steffen Jacobs
 */
public final class SessionPacketReceiverAPI {

	/**
	 * the answer to a session validity request will be deployed here.
	 * 
	 * @author Steffen Jacobs
	 */
	public static void onPacketSessionCheckAnswer(PacketSessionCheckAnswer packet) {
		SuperCallable<PacketSessionCheckAnswer> toCall = null;

		if (checkRequests.get(packet.getRequest().getUsername()).size() == 1) {
			toCall = checkRequests.remove(packet.getRequest().getUsername()).getFirst().getCallable();
		} else {
			LinkedList<CheckRequest> requests = checkRequests.get(packet.getRequest().getUsername());
			Iterator<CheckRequest> it = requests.iterator();
			while (it.hasNext()) {
				CheckRequest req = it.next();
				if (req.getTimestamp() == packet.getRequest().getTimestamp()) {
					toCall = req.getCallable();
					it.remove();
					break;
				}
			}
			if (requests.size() == 0) {
				checkRequests.remove(packet.getRequest().getUsername());
			} else {
				checkRequests.put(packet.getRequest().getUsername(), requests);
			}
		}

		try {
			if (toCall != null)
				toCall.callMeMaybe(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * the answer to the session-get-request will be deployed here
	 * 
	 * @author Steffen Jacobs
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
	 * 
	 * @author Steffen Jacobs
	 */
	static void addGetRequest(String username, SuperCallable<PacketSessionGetAnswer> callable) {
		getRequests.putIfAbsent(username, callable);
	}

	/**
	 * adds a check-Request
	 * 
	 * @author Steffen Jacobs
	 */

	static void addCheckRequest(String username, SuperCallable<PacketSessionCheckAnswer> callable,
			long sendedTimestamp) {

		LinkedList<CheckRequest> requestList;

		if (checkRequests.containsKey(username)) {
			requestList = checkRequests.get(username);
		} else {
			requestList = new LinkedList<CheckRequest>();
		}

		requestList.add(new CheckRequest(sendedTimestamp, callable));
		checkRequests.put(username, requestList);
	}

	private static ConcurrentHashMap<String, SuperCallable<PacketSessionGetAnswer>> getRequests = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, LinkedList<CheckRequest>> checkRequests = new ConcurrentHashMap<>();

	private static class CheckRequest {
		private final long timestamp;
		private final SuperCallable<PacketSessionCheckAnswer> callable;

		public CheckRequest(long timestamp, SuperCallable<PacketSessionCheckAnswer> pack) {
			super();
			this.timestamp = timestamp;
			this.callable = pack;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public SuperCallable<PacketSessionCheckAnswer> getCallable() {
			return callable;
		}
	}
}
