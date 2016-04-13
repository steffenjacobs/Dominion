package com.tpps.test.technicalServices.network;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
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

	static UUID sessionID;

	@Test
	public void test() throws IOException, InterruptedException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {

		String username = "test";

		// init game-log
		GameLog.init();
		SQLHandler.init("localhost", "3306", "root", "root", "accountmanager");
		SQLHandler.connect();

		if(!SQLOperations.checkTable("statistics")){
			SQLStatisticsHandler.createStatisticsTable(Utilties.createStatisticsList());
		}
		
		SQLStatisticsHandler.insertRowForFirstLogin("test");
		

		// setup matchmaking-server
		new MatchmakingServer(new InetSocketAddress(Addresses.getAllInterfaces(), MatchmakingServer.PORT_MATCHMAKING),
				new MatchmakingPacketHandler());

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
		SessionPacketSenderAPI.sendGetRequest(sess, username, new SuperCallable<PacketSessionGetAnswer>() {

			@Override
			public PacketSessionGetAnswer callMeMaybe(PacketSessionGetAnswer object) {
				sessionID = object.getLoginSessionID();
				halt.release();
				return null;
			}
		});
		halt.acquire();
		halt.release();

		// setup matchmaking-client

		Field client = Matchmaker.class.getDeclaredField("client");
		client.setAccessible(true);
		client.set(null, new Client(new InetSocketAddress(Addresses.getLocalHost(), MatchmakingServer.PORT_MATCHMAKING),
				new TestMatchmakingHandler(), false));

		Field handler = Matchmaker.class.getDeclaredField("handler");
		handler.setAccessible(true);
		handler.set(null, new TestMatchmakingHandler());

		Matchmaker.findMatch(username, sessionID);
		Thread.sleep(1000);

		Thread.sleep(50000);

	}

	/**
	 * client packet-handler for the matchmaking
	 * 
	 * @author Steffen Jacobs
	 */
	private static class TestMatchmakingHandler extends PacketHandler {

		@Override
		public void handleReceivedPacket(int port, Packet packet) {
			System.out.println(" Packet received :) " + packet);

			switch (packet.getType()) {
			case MATCHMAKING_ANSWER:
				PacketMatchmakingAnswer pma = (PacketMatchmakingAnswer) packet;
				// is called when the player is put into a matchmaking-lobby
				// TODO: show LobbyScreen
				break;
			case MATCHMAKING_PLAYER_INFO:
				PacketMatchmakingPlayerInfo pmpi = (PacketMatchmakingPlayerInfo) packet;
				// is called when a player joined or quitted the lobby
				// TODO: add player and remove one instance of "Waiting for
				// player..." @LobbyScreen

				break;
			case MATCHMAKING_SUCCESSFUL:
				PacketMatchmakingSuccessful pms = (PacketMatchmakingSuccessful) packet;
				// is called, when the lobby is full and the game starts
				// TODO: connect to the gameServer & start the round
				break;
			default:
				System.err.println("Bad packet received: " + packet);
				break;
			}
		}
	}

}
