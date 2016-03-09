package com.tpps.application.network.clientSession.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.tpps.application.network.core.Client;

public class SessionClient extends Client {

	private static final boolean DEBUG = false;
	private static Timer scheduler = null;
	private static int DELTA_SEND_KEEP_ALIVE_MILLISECONDS = 5000;
	// private static ConcurrentMultiMap2<String, Boolean> sessionRequests = new
	// ConcurrentMultiMap2<>();

	public SessionClient(SocketAddress address) throws IOException {
		super(address, new SessionPacketReceiver(), false);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> onStop()));
	}

	/**
	 * checks whether a session is valid (sync)
	 * 
	 * @param username
	 *            name of the user's session to check
	 * @param uuid
	 *            sessionID of the user to check
	 * @return whether the session is still valid
	 */

	public boolean checkSessionSync(final String username, final UUID uuid) {
		SessionFuture future = new SessionFuture();
		try {
			future.sendRequest(this, username, uuid);
			return future.getResult();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * sets wheter keep-alive packets should be sent for a given user. You can
	 * only send keep-alive-packets for one user at a time
	 * 
	 * @author Steffen Jacobs
	 */
	public void keepAlive(final String username, boolean state) {
		if (state) {
			if (scheduler != null) {
				scheduler.cancel();
				scheduler.purge();
				System.out.println("Keep-Alive changed to " + username);
			} else {
				System.out.println("Keep-Alive set up for " + username);
			}
			scheduler = new Timer();
			final Client instance = this;

			scheduler.schedule(new TimerTask() {
				@Override
				public void run() {
					SessionPacketSenderAPI.sendKeepAlive(instance, username);
				}
			}, 0, DELTA_SEND_KEEP_ALIVE_MILLISECONDS);
		} else {
			if (scheduler != null) {
				scheduler.cancel();
				scheduler.purge();
				System.out.println("Keep-Alive stoppped for " + username);
			}
		}
	}

	/**
	 * is called when the client stops
	 * 
	 * @author Steffen Jacobs
	 */
	public void onStop() {
		super.disconnect();
		if (scheduler != null) {
			scheduler.cancel();
			scheduler.purge();
		}
	}

	public static boolean debug() {
		return DEBUG;
	}
}
