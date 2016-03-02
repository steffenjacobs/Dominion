package com.tpps.test.application.network;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import org.junit.Test;

import com.tpps.application.network.clientSession.client.SessionClient;
import com.tpps.application.network.clientSession.client.SessionPacketSenderAPI;
import com.tpps.application.network.clientSession.packets.PacketSessionCheckAnswer;
import com.tpps.application.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.application.network.clientSession.server.SessionManager;
import com.tpps.application.network.clientSession.server.SessionPacketHandler;
import com.tpps.application.network.clientSession.server.SessionServer;
import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.core.SuperCallable;

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
 * @author sjacobs - Steffen Jacobs
 */

public class JUnitSessionServerTest {
	private final static int MAX_TIMEOUT = 100;
	private final String TEST_USER = "Test-User";

	// easy way to get data over from the other thread
	private static UUID receivedUUID;
	private static boolean state = false;

	/** tests the Session-Server */
	@Test
	public void test() throws IOException, InterruptedException {
		// initialize variables
		SessionPacketHandler serverPacketHandler = new SessionPacketHandler();
		boolean doLongTest = true;

		int sessionPort = 1337;

		// test server startup
		SessionServer server = new SessionServer(serverPacketHandler);
		serverPacketHandler.setParent(server);
		assertNotNull(server);

		// test client startup
		SessionClient sessionClient = new SessionClient(
				new InetSocketAddress("127.0.0.1", SessionServer.getStandardPort()));
		assertNotNull(sessionClient);

		// wait to connect.
		Thread.sleep(10);

		int localPort = sessionClient.getConnectionThread().getLocalPort();

		// check if connection established
		assertTrue(sessionClient.isConnected());

		// check if client is connected to the correct port
		assertEquals(sessionPort, sessionClient.getConnectionThread().getRemotePort());
		ServerConnectionThread connectedClient = server.getClientThread(localPort);
		assertNotNull(connectedClient);

		// send get-request and create session
		SessionPacketSenderAPI.sendGetRequest(sessionClient, TEST_USER, new SuperCallable<PacketSessionGetAnswer>() {
			@Override
			public PacketSessionGetAnswer callMeMaybe(PacketSessionGetAnswer answer) {
				//
				receivedUUID = answer.getLoginSessionID();
				// check if session is not null
				assertNotNull(answer.getLoginSessionID());

				return null;
			}
		});

		Thread.sleep(MAX_TIMEOUT);

		// check if session was created
		assertNotNull(receivedUUID);

		// check if session is still valid
		assertTrue(checkSession(sessionClient));

		if (doLongTest) {
			// Start keep-alive
			sessionClient.keepAlive(TEST_USER, true);

			// wait until session would be invalidated without keep-alive
			System.out.println("Waiting " + (SessionManager.getExpiration() * 1000 + 5000)/1000 + " seconds");
			Thread.sleep(SessionManager.getExpiration() * 1000 + 5000);

			// check if session is still valid
			assertTrue(checkSession(sessionClient));

			// stop keep-alive
			sessionClient.keepAlive(TEST_USER, false);

			// wait again
			System.out.println("Waiting " + (SessionManager.getExpiration() * 1000 + 5000)/1000 + " seconds (again)");
			Thread.sleep(SessionManager.getExpiration() * 1000 + 5000);

			// check if session is still valid
			assertFalse(checkSession(sessionClient));
		}
	}

	/**
	 * checks if the session is still valid
	 * 
	 * @return if the session is still valid
	 * @author sjacobs - Steffen Jacobs
	 */
	private boolean checkSession(Client sessionClient) throws InterruptedException {
		SessionPacketSenderAPI.sendCheckRequest(sessionClient, TEST_USER, receivedUUID,
				new SuperCallable<PacketSessionCheckAnswer>() {
					@Override
					public PacketSessionCheckAnswer callMeMaybe(PacketSessionCheckAnswer answer) {
						state = answer.getState();
						// check if request is not null
						assertThat(answer.getRequest(), is(notNullValue()));

						return null;
					}
				});

		Thread.sleep(MAX_TIMEOUT);
		return state;
	}
}
