package com.tpps.test.technicalServices.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.core.ServerConnectionThread;
import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * tests the network-framework as required
 * 
 * check if server starts
 * 
 * check if client starts
 * 
 * check whether a local-host connection can be established and lies between the
 * correct ports
 * 
 * test wheter packets can be send from client to server
 * 
 * check whether packets can be send from server to client
 * 
 * @author Steffen Jacobs
 */
public class JUnitNetworkTest {

	private static TestPacketHandler serverPacketHandler, clientPacketHandler, clientPacketHandler2;
	private static int port, localPort;
	private static Client client;
	private static Server server;

	private static ArrayList<Serializable> toSend;
	private static Packet sentTestPacket;

	/** sets up the client and the server and connects them */
	@BeforeClass
	public static void setupNetwork() throws IOException, InterruptedException {
		// initialize variables
		serverPacketHandler = new TestPacketHandler();
		clientPacketHandler = new TestPacketHandler();
		clientPacketHandler2 = new TestPacketHandler();

		port = 2222;

		// test server startup
		server = new Server(new InetSocketAddress(Addresses.getLocalHost(), port), serverPacketHandler);
		serverPacketHandler.setParent(server);
		assertNotNull(server);

		// test client startup
		client = new Client(new InetSocketAddress(Addresses.getLocalHost(), port), clientPacketHandler);
		client.addPacketHandler(clientPacketHandler2);
		assertNotNull(client);

		System.out.println("Prepared Network");
		Thread.sleep(100);
	}

	/** creates a test-packet to send around later */
	@BeforeClass
	public static void prepareTestData() {
		String testString = "test-string";
		Integer testInt = 2;

		toSend = new ArrayList<>();
		toSend.add(testInt);
		toSend.add(testString);
		sentTestPacket = new TestPacket(toSend);
		assertNotNull(sentTestPacket);
	}

	/** closes all open connections */
	@AfterClass
	public static void cleanupNetwork() throws InterruptedException {
		client.disconnect();
		server.disconnectAll();
	}

	/** checks, if the client is connected to the server via the correct port */
	@Test
	public void testIfConnected() throws InterruptedException {
		System.out.println("Doing test: TestIfConnected");
		// wait to connect.
		Thread.sleep(200);

		localPort = client.getConnectionThread().getLocalPort();

		// check if connection established
		assertTrue(client.isConnected());

		// check if client is connected to the correct port
		assertEquals(port, client.getConnectionThread().getRemotePort());
		ServerConnectionThread connectedClient = server.getClientThread(localPort);
		assertNotNull(connectedClient);
	}

	/**
	 * checks if data can be sent from the client to the server and are received
	 * correctly
	 */
	@Test
	public void checkClientToServer() throws IOException, InterruptedException {
		System.out.println("Doing test: ClientToServer");

		/* check client-to-server */
		// send test-packet
		client.sendMessage(sentTestPacket);

		// wait to send & receive
		Thread.sleep(50);

		// check if test-packet was received
		Packet receivedPacket = serverPacketHandler.getLastReceived(localPort);
		assertNotNull(receivedPacket);

		// check if test-packet is not broken
		assertNotNull(receivedPacket);

		// check if test-packet lost no data
		TestPacket receivedTestPacket = (TestPacket) receivedPacket;
		assertEquals(receivedTestPacket.getData(), toSend);
	}

	/**
	 * checks if data can be sent from the server to the client and are received
	 * correctly
	 */
	@Test
	public void checkServerToClient() throws IOException, InterruptedException {
		System.out.println("Doing test: ServerToClient");

		/* check server-to-client */

		// send test-packet
		assertNotNull(sentTestPacket);
		server.sendMessage(localPort, sentTestPacket);

		// wait to send & receive
		Thread.sleep(10);

		// check if test-packet was received
		Packet receivedPacket2 = clientPacketHandler.getLastReceived(localPort);
		assertNotNull(receivedPacket2);

		// check if test-packet is not broken
		assertNotNull(receivedPacket2);

		// check if test-packet lost no data
		TestPacket receivedTestPacket2 = (TestPacket) receivedPacket2;
		assertEquals(receivedTestPacket2.getData(), toSend);
	}

	/**
	 * does a bulk-test with sending 10k Packets from the server to the client
	 */
	@Test
	public void doBulkTest() throws IOException, InterruptedException {
		System.out.println("Doing test: Bulk-Test");

		// prepare for bulk-test
		final int PACKET_COUNT = 10000;
		clientPacketHandler.clearPackets();
		clientPacketHandler2.clearPackets();

		// bulk-test
		for (int i = 0; i < PACKET_COUNT; i++) {
			server.sendMessage(localPort, sentTestPacket);
			// Thread.sleep(10);
		}
		Thread.sleep(3000);

		assertEquals(PACKET_COUNT, ((TestPacketHandler) client.getHandlers().get(0)).countPackets());
		assertEquals(PACKET_COUNT, ((TestPacketHandler) client.getHandlers().get(1)).countPackets());

		System.out.println("[SUCCESS] " + ((TestPacketHandler) client.getHandlers().get(1)).countPackets() + "/"
				+ PACKET_COUNT + " packets sent & received!");

	}
}
