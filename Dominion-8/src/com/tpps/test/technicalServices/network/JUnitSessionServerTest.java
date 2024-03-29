package com.tpps.test.technicalServices.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.junit.Test;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.clientSession.client.SessionPacketSenderAPI;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.technicalServices.network.clientSession.server.SessionManager;
import com.tpps.technicalServices.network.clientSession.server.SessionPacketHandler;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.core.ServerConnectionThread;
import com.tpps.technicalServices.network.core.SuperCallable;

/**
 * tests the Session-Server
 * 
 * checks if session-server and session-client start & connect
 * 
 * checks if sessions are created as requested
 * 
 * checks if sessions are sended as requested
 * 
 * checks if sessions are valid
 * 
 * checks if sessions are invalidated after the timeout
 * 
 * checks if the sessions are revalidated by the keep-alive thread
 * 
 * @author Steffen Jacobs
 */

public class JUnitSessionServerTest {
	private final static int MAX_TIMEOUT = 1000;
	private final String TEST_USER = "Test-User";
	private static final boolean doLongTest = false;

	private static final boolean REMOTE = true;

	private static UUID receivedUUID;

	/** tests the Session-Server 
	 * @throws IOException 
	 * @throws InterruptedException */
	@Test
	public void test() throws IOException, InterruptedException {
		// initialize variables
		SessionPacketHandler serverPacketHandler = new SessionPacketHandler();

		int sessionPort = 1337;

		// test server startup
		SessionServer server = null;
		if (!REMOTE) {
			server = new SessionServer(serverPacketHandler);
			serverPacketHandler.setParent(server);
			assertNotNull(server);
		}

		// test client startup
		SessionClient sessionClient;
		if (REMOTE)
			sessionClient = new SessionClient(new InetSocketAddress(Addresses.getRemoteAddress(), SessionServer.getStandardPort()));
		else
			sessionClient = new SessionClient(new InetSocketAddress(Addresses.getLocalHost(), SessionServer.getStandardPort()));
		assertNotNull(sessionClient);

		// wait to connect.
		Thread.sleep(10);

		int localPort = sessionClient.getConnectionThread().getLocalPort();

		// check if connection established
		assertTrue(sessionClient.isConnected());

		// check if client is connected to the correct port
		assertEquals(sessionPort, sessionClient.getConnectionThread().getRemotePort());
		if (!REMOTE) {
			assertNotNull(server);
			ServerConnectionThread connectedClient = server.getClientThread(localPort);
			assertNotNull(connectedClient);
		}

		// send get-request and create session
		Semaphore blocker = new Semaphore(1);
		long oldTime = System.currentTimeMillis();
		blocker.acquire();
		SessionPacketSenderAPI.sendGetRequest(sessionClient, TEST_USER, new SuperCallable<PacketSessionGetAnswer>() {
			@Override
			public PacketSessionGetAnswer callMeMaybe(PacketSessionGetAnswer answer) {
				//
				receivedUUID = answer.getLoginSessionID();
				// check if session is not null
				assertNotNull(answer.getLoginSessionID());
				blocker.release();
				return null;
			}
		});
		blocker.acquire();
		blocker.release();

		assertTrue(System.currentTimeMillis() < oldTime + MAX_TIMEOUT);

		// check if session was created
		assertNotNull(receivedUUID);

		// check if session is still valid
		assertTrue(sessionClient.checkSessionSync(TEST_USER, receivedUUID));

		// bulk-test
		final int PACKET_COUNT = 1000;
		Semaphore bulk = new Semaphore(PACKET_COUNT);

		sessionClient.getConnectionThread().resetsMetrics();
		ExecutorService threadPool = Executors.newFixedThreadPool(PACKET_COUNT);

		for (int i = 0; i < PACKET_COUNT; i++) {
			threadPool.submit(() -> {
				try {
					bulk.acquire(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				assertTrue(sessionClient.checkSessionSync(TEST_USER, receivedUUID));
				bulk.release(1);
			});
		}
		/* If test fails, increase this variable */
		final int sleepTime = 12000;
		Thread.sleep(sleepTime);
		
		assertEquals(sessionClient.getConnectionThread().getCountReceived(), PACKET_COUNT);
		assertEquals(sessionClient.getConnectionThread().getCountSent(), PACKET_COUNT);
		assertEquals(PACKET_COUNT, bulk.availablePermits());

		if (doLongTest) {
			// Start keep-alive
			sessionClient.keepAlive(TEST_USER, true);

			// wait until session would be invalidated without keep-alive
			System.out.println("Waiting " + (SessionManager.getExpiration() * 1000 + 5000) / 1000 + " seconds");
			Thread.sleep(SessionManager.getExpiration() * 1000 + 5000);

			// check if session is still valid
			assertTrue(sessionClient.checkSessionSync(TEST_USER, receivedUUID));

			// stop keep-alive
			sessionClient.keepAlive(TEST_USER, false);

			// wait again
			System.out.println("Waiting " + (SessionManager.getExpiration() * 1000 + 5000) / 1000 + " seconds (again)");
			Thread.sleep(SessionManager.getExpiration() * 1000 + 5000);

			// check if session is still valid
			assertFalse(sessionClient.checkSessionSync(TEST_USER, receivedUUID));
		}
	}
}
