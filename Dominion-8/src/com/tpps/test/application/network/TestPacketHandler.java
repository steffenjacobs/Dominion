package com.tpps.test.application.network;

import java.util.HashMap;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;

/**
 * custom PacketHandler for the testing of the network-framework
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class TestPacketHandler extends PacketHandler {
	HashMap<Integer, Packet> lastReceived = new HashMap<>();

	/**
	 * trivial
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		System.out.println("[SUCCESS] Received Packet!");
		this.lastReceived.put(port, packet);

	}

	/**
	 * trivial
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public Packet getLastReceived(int port) {
		return lastReceived.get(port);
	}

}
