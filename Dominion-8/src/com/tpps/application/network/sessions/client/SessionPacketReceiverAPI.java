package com.tpps.application.network.sessions.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

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
		Callable<Void> toCall = checkRequests.get(packet.getRequest().getUsername());
		try {
			if (toCall != null)
				checkAnswers.put(packet.getRequest().getUsername(), packet);
				toCall.call();
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
		Callable<Void> toCall = getRequests.get(packet.getRequest().getUsername());
		try {
			if (toCall != null)
				getAnswers.put(packet.getRequest().getUsername(), packet);
				toCall.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//TODO: replace with future tasks
	public static void addGetRequest(String username, Callable<Void> callable) {
		getRequests.putIfAbsent(username, callable);
	}

	public static void addCheckRequest(String username, Callable<Void> callable) {
		checkRequests.putIfAbsent(username, callable);
	}
	
	public static PacketSessionGetAnswer getGetAnswer(String username){
		return getAnswers.remove(username);
	}
	
	public static PacketSessionCheckAnswer getCheckAnswer(String username){
		return checkAnswers.remove(username);
	}

	//TODO: replace with FutureTaskts
	private static ConcurrentHashMap<String, PacketSessionGetAnswer> getAnswers = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, PacketSessionCheckAnswer> checkAnswers = new ConcurrentHashMap<>();
	
	private static ConcurrentHashMap<String, Callable<Void>> getRequests = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, Callable<Void>> checkRequests = new ConcurrentHashMap<>();
}
