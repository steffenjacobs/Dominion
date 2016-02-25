package com.tpps.network.packets;

import java.io.Serializable;

/**
 * represents a network packet ready to send via our network-interface
 * 
 * @author sjacobs - Steffen Jacobs
 */
public abstract class Packet implements Serializable {
	private static final long serialVersionUID = 9013592092025644535L;
	private PacketType packetType;

	/**
	 * initializes a Packet with a type
	 * 
	 * @param type
	 *            PacketType to initalize the packet with
	 * @author sjacobs - Steffen Jacobs
	 */
	protected Packet(PacketType type) {
		this.packetType = type;
	}

	/**
	 * @return the packet type
	 * @author sjacobs - Steffen Jacobs
	 */
	public PacketType getType() {
		return this.packetType;
	}

	/**
	 * @return a readable String which represents the packet
	 * @author sjacobs - Steffen Jacobs
	 */
	public abstract String toString();
}
