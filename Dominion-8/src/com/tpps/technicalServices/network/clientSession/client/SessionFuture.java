package com.tpps.technicalServices.network.clientSession.client;

import java.util.UUID;
import java.util.concurrent.Semaphore;

import com.tpps.technicalServices.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.technicalServices.network.core.SuperCallable;

/**
 * This class represents a Future-Task for the SessionCheck
 * 
 * @author Steffen Jacobs
 */
public class SessionFuture {

	private boolean result;
	// can't use reentrant-lock because the releasing thread is not the
	// acquiring thread
	private Semaphore sem = new Semaphore(1);

	/**
	 * sends a session-request to the SessionServer
	 * 
	 * @param client
	 *            the SessionClient connected with the SessionServer
	 * @param username
	 *            the name of the user who's session should be checked
	 * @param uuid
	 *            the sessionID of the user who should be checked
	 */
	public void sendRequest(SessionClient client, String username, UUID uuid) throws InterruptedException {
		sem.acquire();
		SessionPacketSenderAPI.sendCheckRequest(client, username, uuid, new SuperCallable<PacketSessionCheckAnswer>() {
			@Override
			public PacketSessionCheckAnswer callMeMaybe(PacketSessionCheckAnswer object) {
				result = object.getState();
				sem.release();
				return null;
			}
		});
	}

	/**
	 * Blocks the thread until the result of the session-check arrived
	 * 
	 * @return the result of the session-check
	 */
	public boolean getResult() throws InterruptedException {
		sem.acquire();
		return result;
	}
}
