package com.tpps.test.technicalServices.network;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * custom PacketHandler for the testing of the network-framework
 * 
 * @author Steffen Jacobs
 */
public class TestPacketHandler extends PacketHandler {
	private HashMap<Integer, Packet> lastReceived = new HashMap<>();
	private CopyOnWriteArrayList<Packet> receivedPackets = new CopyOnWriteArrayList<>();

	/** removes the list of received packets */
	public void clearPackets() {
		this.receivedPackets.clear();
	}

	/** @return the number of packets received */
	public int countPackets() {
		return this.receivedPackets.size();
	}

	/**
	 * trivial
	 */
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		System.out.println("[SUCCESS] Received Packet!");
		this.lastReceived.put(port, packet);
		this.receivedPackets.add(packet);

	}

	/**
	 * @return the last received packet from a specific port
	 * @param port the port to return the last received packet from
	 */
	public Packet getLastReceived(int port) {
		return lastReceived.get(port);
	}

	/** @return all received packets */
	public CopyOnWriteArrayList<Packet> getReceivedPackets() {
		return receivedPackets;
	}

}
