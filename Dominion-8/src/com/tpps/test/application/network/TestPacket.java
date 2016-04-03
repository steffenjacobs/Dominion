package com.tpps.test.application.network;

import java.io.Serializable;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * custom packet for testing of the network-framework
 * 
 * @author Steffen Jacobs
 */
public class TestPacket extends Packet {
	private static final long serialVersionUID = 8810954992640131497L;
	private final Serializable data;

	/**
	 * creates the TestPacket with some test-data
	 * 
	 * @author Steffen Jacobs
	 */
	protected TestPacket(Serializable _data) {
		super(PacketType.TEST);
		this.data = _data;
	}

	/**
	 * @author Steffen Jacobs
	 * @return the serialized data to test
	 */
	public Serializable getData() {
		return this.data;
	}

	/**
	 * @return a readable representation of the packet
	 * @author Steffen JAcobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + this.getData().toString();
	}
}
