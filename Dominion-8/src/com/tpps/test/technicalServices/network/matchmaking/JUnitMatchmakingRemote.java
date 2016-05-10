package com.tpps.test.technicalServices.network.matchmaking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.junit.Test;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.clientSession.client.SessionPacketSenderAPI;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.SuperCallable;
import com.tpps.technicalServices.network.matchmaking.client.Matchmaker;
import com.tpps.technicalServices.network.matchmaking.server.MatchmakingServer;

/**
 * 
 * JUnit-Test to test if all aspects of the matchmaking on the remote-server are
 * working
 * 
 * @author Steffen Jacobs
 *
 */
public class JUnitMatchmakingRemote {
	private static HashMap<String, UUID> userSessions = new HashMap<>();

	private static String username = "test";
	private static final int countClients = 3;

	/**
	 * test stuff
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void test() throws IOException, InterruptedException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		
		GameLog.init();

		TestMatchmakingHandler tmh = new TestMatchmakingHandler();
		tmh.username = username;

		// init stuff
		HashMap<String, Matchmaker> matchmakers = new HashMap<>();
		GameLog.init();

		// get valid session
		SessionClient sess = new SessionClient(
				new InetSocketAddress(Addresses.getRemoteAddress(), SessionServer.getStandardPort()));
		Semaphore halt = new Semaphore(1);
		halt.acquire();

		for (int i = 0; i < countClients; i++) {

			// Setup match-makers
			Matchmaker mm = Matchmaker.getInstance();
			matchmakers.put(username + i, mm);

			Field client = Matchmaker.class.getDeclaredField("client");
			client.setAccessible(true);
			client.set(mm,
					new Client(new InetSocketAddress(Addresses.getRemoteAddress(), MatchmakingServer.getStandardPort()),
							tmh, false));

			Field handler = Matchmaker.class.getDeclaredField("handler");
			handler.setAccessible(true);
			handler.set(mm, tmh);

			// Setup sessions
			SessionPacketSenderAPI.sendGetRequest(sess, username + i, new SuperCallable<PacketSessionGetAnswer>() {

				@Override
				public PacketSessionGetAnswer callMeMaybe(PacketSessionGetAnswer object) {
					userSessions.put(object.getRequest().getUsername(), object.getLoginSessionID());
					halt.release();
					return null;
				}
			});
		}
		halt.acquire();
		halt.release();

		Thread.sleep(200);

		// check sessions
		for (int i = 0; i < countClients; i++) {
			assertNotNull(userSessions.get(username + i));
		}

		Iterator<String> it = userSessions.keySet().iterator();
		for (int i = 0; i < countClients; i++) {
			String name = it.next();
			matchmakers.get(name).findMatch(name, userSessions.get(name));
			Thread.sleep(100);
		}

		Thread.sleep(4000);
		


		assertEquals(countClients, tmh.checks[1].get());

	}
}
