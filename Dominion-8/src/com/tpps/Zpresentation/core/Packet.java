package com.tpps.Zpresentation.core;

import java.io.Serializable;

import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * represents a network packet ready to send via our network-interface
 * 
 * @author Steffen Jacobs
 */
public abstract class Packet implements Serializable {
	private static final long serialVersionUID = 9013592092025644535L;
	private PacketType packetType;

	/**
	 * initializes a Packet with a type
	 * 
	 * @param type
	 *            PacketType to initalize the packet with
	 * @author Steffen Jacobs
	 */
	protected Packet(PacketType type) {
		this.packetType = type;
	}

	/**
	 * @return the packet type
	 * @author Steffen Jacobs
	 */
	public PacketType getType() {
		return this.packetType;
	}

	/**
	 * @return a readable String which represents the packet
	 * @author Steffen Jacobs
	 */
	public abstract String toString();
}
