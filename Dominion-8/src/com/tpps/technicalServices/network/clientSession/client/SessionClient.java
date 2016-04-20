package com.tpps.technicalServices.network.clientSession.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.clientSession.server.SessionManager;
import com.tpps.technicalServices.network.core.Client;

/**
 * SessionClient - used for communicating with the SessionServer by users &
 * servers
 * 
 * @author Steffen Jacobs
 */
public class SessionClient extends Client {

	private static final boolean DEBUG = false;
	private static Timer scheduler = null;

	/**
	 * constructor for session-client
	 * 
	 * @param address
	 *            SocketAddress to connect to
	 * @throws IOException
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
	 * @param username
	 *            the user-name to keep alive
	 * @param state
	 *            whether to keep alive or stop keeping-alive the user
	 */
	public void keepAlive(final String username, boolean state) {
		if (state) {
			if (scheduler != null) {
				scheduler.cancel();
				scheduler.purge();
				GameLog.log(MsgType.NETWORK_INFO, "Keep-Alive changed to " + username);
			} else {
				GameLog.log(MsgType.NETWORK_INFO, "Keep-Alive set up for " + username);
			}
			scheduler = new Timer();
			final Client instance = this;

			scheduler.schedule(new TimerTask() {
				@Override
				public void run() {
					SessionPacketSenderAPI.sendKeepAlive(instance, username);
				}
			}, 0, SessionManager.getExpiration() / 3);
		} else {
			if (scheduler != null) {
				scheduler.cancel();
				scheduler.purge();
				GameLog.log(MsgType.NETWORK_INFO, "Keep-Alive stopped for " + username);
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

	/** @return if debugging is switched on */
	public static boolean debug() {
		return DEBUG;
	}
}
