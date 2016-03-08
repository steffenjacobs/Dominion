package com.tpps.application.network.clientSession.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import com.tpps.application.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.SuperCallable;
import com.tpps.technicalServices.util.ConcurrentMultiMap;

public class SessionClient extends Client {

	private static final boolean DEBUG = false;
	private static Timer scheduler = null;
	private static int DELTA_SEND_KEEP_ALIVE_MILLISECONDS = 5000;
	private static ConcurrentMultiMap<String, Boolean> sessionRequests = new ConcurrentMultiMap<>();

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
		
		Semaphore blocker = new Semaphore(1);
		try {
			blocker.acquire();
			SessionPacketSenderAPI.sendCheckRequest(this, username, uuid,
					new SuperCallable<PacketSessionCheckAnswer>() {
						@Override
						public PacketSessionCheckAnswer callMeMaybe(PacketSessionCheckAnswer object) {
							sessionRequests.put(username, object.getState());
							blocker.release();
							return null;
						}
					});
			blocker.acquire();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return sessionRequests.remove(username);
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
