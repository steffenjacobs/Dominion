package com.tpps.test.application.network;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

import org.junit.Test;

import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.Server;
import com.tpps.application.network.core.events.NetworkListener;
import com.tpps.application.network.core.packet.Packet;

/***
 * checks whether the event-system with the client and server work correctly
 * 
 * create listeners for client and server check register listeners for client
 * and server check if listeners are invoked correctly for client and server
 * check unregister listeners for client and server
 * 
 * @author Steffen Jacobs
 *
 */
public class JUnitEventTest {

	private static HashMap<String, Boolean> checkMap = new HashMap<>();

	@Test
	public void test() throws IOException, InterruptedException {

		// setup generic server
		Server server = new Server(new InetSocketAddress("127.0.0.1", 13), new PacketHandler() {

			@Override
			public void handleReceivedPacket(int port, Packet packet) {
				checkMap.put("receivedSrv", true);
			}
		});
		server.getHandler().setParent(server);

		// setup server-listener for server
		NetworkListener serverListener = new NetworkListener() {

			@Override
			public void onClientDisconnect(int port) {
				checkMap.put("disconnected", true);
			}

			@Override
			public void onClientConnect(int port) {
				checkMap.put("connected", true);
			}
		};

		// Add server-listener to server
		server.getListenerManager().registerListener(serverListener);

		// setup client-listener for client
		NetworkListener clientListener = new NetworkListener() {

			@Override
			public void onClientDisconnect(int port) {
				checkMap.put("disconnectedC", false);
			}

			@Override
			public void onClientConnect(int port) {
				checkMap.put("connectedC", true);
			}
		};

		// setup client
		Client client = new Client(new InetSocketAddress("127.0.0.1", 13), new PacketHandler() {

			@Override
			public void handleReceivedPacket(int port, Packet packet) {
				checkMap.put("receivedCli", true);

			}
		});

		// setup second client-listener for client
		NetworkListener clientListener2 = new NetworkListener() {

			@Override
			public void onClientDisconnect(int port) {
				checkMap.put("disconnectedC", true);
			}

			@Override
			public void onClientConnect(int port) {
				// do nothing
			}
		};

		// add client-listener to client
		client.getListenerManager().registerListener(clientListener);

		// wait until connection is established
		Thread.sleep(100);

		// add second client-listener to client
		client.getListenerManager().registerListener(clientListener2);

		// send packets in both directions
		client.sendMessage(new TestPacket(null));
		server.sendMessage(client.getConnectionThread().getLocalPort(), new TestPacket(null));

		// Wait until packets are at their destinations
		Thread.sleep(100);

		// remove first client-listener
		client.getListenerManager().unregisterListener(clientListener);

		// disconnect
		client.disconnect();

		// wait until disconnected
		Thread.sleep(100);

		// check the check-list
		assertTrue(checkMap.get("connected"));
		assertTrue(checkMap.get("disconnected"));
		assertTrue(checkMap.get("connectedC"));
		assertTrue(checkMap.get("disconnectedC"));
		assertTrue(checkMap.get("receivedSrv"));
		assertTrue(checkMap.get("receivedCli"));
	}
}
