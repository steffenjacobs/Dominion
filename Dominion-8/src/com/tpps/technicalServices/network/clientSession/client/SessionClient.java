package com.tpps.technicalServices.network.clientSession.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.tpps.technicalServices.logger.Log;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.Client;

public class SessionClient extends Client {

	private static final boolean DEBUG = false;
	private static Timer scheduler = null;
	private static int DELTA_SEND_KEEP_ALIVE_MILLISECONDS = 5000;

	/**
	 * constructor for session-client
	 * 
	 * @param address
	 *            SocketAddress to connect to
	 */
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
				Log.log(MsgType.NETWORK_INFO, "Keep-Alive changed to " + username);
			} else {
				Log.log(MsgType.NETWORK_INFO, "Keep-Alive set up for " + username);
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
				Log.log(MsgType.NETWORK_INFO, "Keep-Alive stopped for " + username);
			}
		}
	}

	/**
	 * is called when the client stops
	 * 
	 * @author Steffen Jacobs
	 */
	private void onStop() {
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
