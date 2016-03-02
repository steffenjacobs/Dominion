package com.tpps.test.application.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.junit.Test;

import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.Server;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.packet.Packet;

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
 * @author sjacobs - Steffen Jacobs
 */
public class JUnitNetworkTest {
	@Test
	public void test() throws IOException, InterruptedException {
		// initialize variables
		TestPacketHandler serverPacketHandler = new TestPacketHandler();
		TestPacketHandler clientPacketHandler = new TestPacketHandler();

		int port = 2222;
		String testString = "test-string";
		Integer testInt = 2;

		// test server startup
		Server server = new Server(new InetSocketAddress("127.0.0.1", port), serverPacketHandler);
		serverPacketHandler.setParent(server);
		assertNotNull(server);

		// test client startup
		Client client = new Client(new InetSocketAddress("127.0.0.1", port), clientPacketHandler);
		assertNotNull(client);

		// wait to connect.
		Thread.sleep(10);

		int localPort = client.getConnectionThread().getLocalPort();

		// check if connection established
		assertTrue(client.isConnected());

		// check if client is connected to the correct port
		assertEquals(port, client.getConnectionThread().getRemotePort());
		ServerConnectionThread connectedClient = server.getClientThread(localPort);
		assertNotNull(connectedClient);

		// create test-packet
		ArrayList<Serializable> toSend = new ArrayList<>();
		toSend.add(testInt);
		toSend.add(testString);
		Packet sentTestPacket = new TestPacket(toSend);

		/* check client-to-server */
		// send test-packet
		client.sendMessage(sentTestPacket);

		// wait to send & receive
		Thread.sleep(10);

		// check if test-packet was received
		Packet receivedPacket = serverPacketHandler.getLastReceived(localPort);
		assertNotNull(receivedPacket);

		// check if test-packet is not broken
		assertNotNull(receivedPacket);

		// check if test-packet lost no data
		TestPacket receivedTestPacket = (TestPacket) receivedPacket;
		assertEquals(receivedTestPacket.getData(), toSend);

		/* check server-to-client */

		// send test-packet
		server.sendMessage(localPort, sentTestPacket);

		// wait to send & receive
		Thread.sleep(10);

		// check if test-packet was received
		// int localPort = client.getConnectionThread().getLocalPort();
		Packet receivedPacket2 = clientPacketHandler.getLastReceived(localPort);
		assertNotNull(receivedPacket2);

		// check if test-packet is not broken
		assertNotNull(receivedPacket2);

		// check if test-packet lost no data
		TestPacket receivedTestPacket2 = (TestPacket) receivedPacket2;
		assertEquals(receivedTestPacket2.getData(), toSend);

	}
}
