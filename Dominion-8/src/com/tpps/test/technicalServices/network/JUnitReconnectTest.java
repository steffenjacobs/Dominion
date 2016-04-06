package com.tpps.test.technicalServices.network;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.Test;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.Server;

/**
 * JUnit-Test to test whether a client is able to reconnect to a server, when
 * the connection is closed or lost.
 * 
 * @author Steffen Jacobs
 */
public class JUnitReconnectTest {

	private static final int TEST_PORT = 1234;

	@Test
	public void test() throws IOException, InterruptedException {

		// Creating the test-listener
		TestConnectionListener serverListener = new TestConnectionListener();

		// Creating & Starting Server
		final Server server = new Server(new InetSocketAddress(Addresses.getAllInterfaces(), TEST_PORT),
				new TestPacketHandler());

		server.getListenerManager().registerListener(serverListener);

		// Waiting & clearing all clients (there should be non
		Thread.sleep(250);
		server.disconnectAll();

		// Trying to connect to server
		Client client = new Client(new InetSocketAddress(Addresses.getLocalHost(), TEST_PORT), new TestPacketHandler());

		Thread.sleep(250);

		assertEquals(1, serverListener.getConnectCount());

		serverListener.resetTest();

		final int COUNT_DISCONNECTS = 10;

		for (int i = 0; i < COUNT_DISCONNECTS; i++) {
			server.disconnectAll();
			Thread.sleep(250);
			client.connectAndLoop(false);
		}

		assertEquals(COUNT_DISCONNECTS, serverListener.getConnectCount());
		assertEquals(COUNT_DISCONNECTS, serverListener.getDisconnectCount());
	}
}