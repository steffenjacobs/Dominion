package com.tpps.test.technicalServices.network;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.Test;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.Server;

public class JUnitReconnectTest {

	private static final int TEST_PORT = 1234;

	@Before
	public void before() {

		GameLog.useAsciiOnConsole = false;
	}

	@Test
	public void test() throws IOException, InterruptedException {

		TestConnectionListener serverListener = new TestConnectionListener();

		// Creating & Starting Server
		final Server server = new Server(new InetSocketAddress(Addresses.getAllInterfaces(), TEST_PORT),
				new TestPacketHandler());
		//
		// // Adding Listener To Server
		server.getListenerManager().registerListener(serverListener);
		//
		Thread.sleep(250);
		server.disconnectAll();
		Thread.sleep(250);

		InetSocketAddress local = new InetSocketAddress(Addresses.getLocalHost(), TEST_PORT);

		// Trying to connect to server
		Client client = new Client(local, new TestPacketHandler());

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