package com.tpps.test.technicalServices.network;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.clientSession.client.SessionPacketSenderAPI;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.SuperCallable;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.login.SQLHandling.SQLHandler;
import com.tpps.technicalServices.network.login.SQLHandling.SQLOperations;
import com.tpps.technicalServices.network.login.SQLHandling.SQLStatisticsHandler;
import com.tpps.technicalServices.network.login.SQLHandling.Utilties;
import com.tpps.technicalServices.network.matchmaking.client.Matchmaker;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingAnswer;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingPlayerInfo;
import com.tpps.technicalServices.network.matchmaking.packets.PacketMatchmakingSuccessful;
import com.tpps.technicalServices.network.matchmaking.server.MatchmakingPacketHandler;
import com.tpps.technicalServices.network.matchmaking.server.MatchmakingServer;

public class JUnitMatchmakingTest {

	static HashMap<String, UUID> userSessions = new HashMap<>();
	static AtomicInteger cntCheck1 = new AtomicInteger(0), cntCheck2 = new AtomicInteger(0),
			cntCheck3 = new AtomicInteger(0);

	static String username = "test";

	@Test
	public void test() throws IOException, InterruptedException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		HashMap<String, Matchmaker> matchmakers = new HashMap<>();

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
							new TestMatchmakingHandler(), false));

			Field handler = Matchmaker.class.getDeclaredField("handler");
			handler.setAccessible(true);
			handler.set(mm, new TestMatchmakingHandler());

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
		for (int i = 0; i < 4; i++) {
			assertNotNull(userSessions.get(username + i));
		}

		Iterator<String> it = userSessions.keySet().iterator();
		for (int i = 0; i < 4; i++) {
			String name = it.next();
			matchmakers.get(name).findMatch(name, userSessions.get(name));
			Thread.sleep(100);
		}
		// matchmakers.get(username + "0").findMatch(username + "0",
		// userSessions.get(username + "0"));
		// matchmakers.get(username + "1").findMatch(username + "1",
		// userSessions.get(username + "1"));
		// matchmakers.get(username + "2").findMatch(username + "2",
		// userSessions.get(username + "2"));
		Thread.sleep(2000);

		assertEquals(4, cntCheck1.get());
		assertEquals(16, cntCheck2.get());
		assertEquals(20, cntCheck3.get());

	}

	/**
	 * client packet-handler for the matchmaking
	 * 
	 * @author Steffen Jacobs
	 */
	private static class TestMatchmakingHandler extends PacketHandler {

		@Override
		public void handleReceivedPacket(int port, Packet packet) {

			switch (packet.getType()) {
			case MATCHMAKING_ANSWER:
				// is called when this specific player was added to the
				// matchmaking-system
				PacketMatchmakingAnswer pma = (PacketMatchmakingAnswer) packet;
				// 4 checks
				assertEquals(1, pma.getAnswerCode());
				cntCheck1.incrementAndGet();
				break;
			case MATCHMAKING_PLAYER_INFO:
				// is called when a player joined or quitted the lobby
				PacketMatchmakingPlayerInfo pmpi = (PacketMatchmakingPlayerInfo) packet;
				// 1+1+2+1+1+3+1+1+4 = 16 checks
				assertTrue(pmpi.getPlayerName().startsWith(username));
				cntCheck2.incrementAndGet();

				break;
			case MATCHMAKING_SUCCESSFUL:
				// is called, when the lobby is full and the game starts
				PacketMatchmakingSuccessful pms = (PacketMatchmakingSuccessful) packet;
				// 5*4 = 20 checks
				assertEquals(4, pms.getJoinedPlayers().length);
				cntCheck3.incrementAndGet();
				for (int i = 0; i < 4; i++) {
					assertTrue(pms.getJoinedPlayers()[i].startsWith(username));
					cntCheck3.incrementAndGet();
				}
				System.out.println("start Packet received :) " + packet);
				break;
			default:
				System.err.println("Bad packet received: " + packet);
				break;
			}
		}
	}
}
