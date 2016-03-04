package com.tpps.application.network.clientSession.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import com.tpps.application.network.core.Client;

public class SessionClient extends Client {

	private static final boolean DEBUG = true;
	private static Timer scheduler = null;
	private static int DELTA_SEND_KEEP_ALIVE_MILLISECONDS = 5000;

	public SessionClient(SocketAddress address) throws IOException {
		super(address, new SessionPacketReceiver());
		Runtime.getRuntime().addShutdownHook(new Thread(() -> onStop()));
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
