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
import com.tpps.technicalServices.network.login.SQLHandling.SQLHandler;
import com.tpps.technicalServices.network.login.SQLHandling.SQLOperations;
import com.tpps.technicalServices.network.login.SQLHandling.SQLStatisticsHandler;
import com.tpps.technicalServices.network.login.SQLHandling.Utilties;
import com.tpps.technicalServices.network.matchmaking.client.Matchmaker;
import com.tpps.technicalServices.network.matchmaking.server.MatchmakingPacketHandler;
import com.tpps.technicalServices.network.matchmaking.server.MatchmakingServer;

public class JUnitMatchmakingTest {

	static HashMap<String, UUID> userSessions = new HashMap<>();

	static String username = "test";

	@Test
	public void test() throws IOException, InterruptedException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		
		HashMap<String, Matchmaker> matchmakers = new HashMap<>();
		TestMatchmakingHandler handler = new TestMatchmakingHandler();
		handler.username = username;

		// init game-log
		GameLog.init();
		SQLHandler.init("localhost", "3306", "root", "root", "accountmanager");
		SQLHandler.connect();
		if (!SQLOperations.checkTable("accountdetails")) {
			SQLOperations.createAccountdetailsTable();
		}
		if (!SQLOperations.checkTable("statistics")) {
			SQLStatisticsHandler.createStatisticsTable(Utilties.createStatisticsList());
		}

		for (int i = 0; i < 4; i++) {
			if (SQLOperations.createAccount(username + i, username + i + "@test.de", "test", "test") == 1) {
				try {
					SQLStatisticsHandler.insertRowForFirstLogin(username + i);
				} catch (Exception ex) {
					System.err.println(ex.getMessage());
				}
			}
		}

		// setup matchmaking-server
		new Thread(() -> {
			try {
				new MatchmakingServer(
						new InetSocketAddress(Addresses.getAllInterfaces(), MatchmakingServer.PORT_MATCHMAKING),
						new MatchmakingPacketHandler());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		// setup session-server
		new Thread(() -> {
			try {
				new SessionServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		Thread.sleep(100);

		// get valid session
		SessionClient sess = new SessionClient(
				new InetSocketAddress(Addresses.getRemoteAddress(), SessionServer.getStandardPort()));
		Semaphore halt = new Semaphore(1);
		halt.acquire();

		for (int i = 0; i < 4; i++) {

			// Setup match-makers
			Matchmaker mm = new Matchmaker();
			matchmakers.put(username + i, mm);

			Field client = Matchmaker.class.getDeclaredField("client");
			client.setAccessible(true);
			client.set(mm,
					new Client(new InetSocketAddress(Addresses.getLocalHost(), MatchmakingServer.PORT_MATCHMAKING),
							handler, false));

			Field handl = Matchmaker.class.getDeclaredField("handler");
			handl.setAccessible(true);
			handl.set(mm, handler);

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

		Thread.sleep(500);

		// check sessions
		for (int i = 0; i < 4; i++) {
			assertNotNull(userSessions.get(username + i));
		}

		Iterator<String> it = userSessions.keySet().iterator();
		for (int i = 0; i < 4; i++) {
			String name = it.next();
			matchmakers.get(name).findMatch(name, userSessions.get(name));
		}

		Thread.sleep(1800);

		assertEquals(4, handler.checks[0].get());
		assertEquals(16, handler.checks[1].get());
		assertEquals(20, handler.checks[2].get());
	}
}
