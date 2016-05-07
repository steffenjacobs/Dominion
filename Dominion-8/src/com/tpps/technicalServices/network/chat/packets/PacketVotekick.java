package com.tpps.technicalServices.network.chat.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * Packet that is sent to kick someone out of the game
 * 
 * @author jhuhn
 */
public class PacketVotekick extends Packet{
	
	private String user;
	private static final long serialVersionUID = -597895745988089452L;
	
	/**
	 * initialized the packet
	 * 
	 * @param user String representation of the user who gets kicked
	 */
	public PacketVotekick(String user) {
		super(PacketType.VOTEKICK);
		this.user = user;
	}

	@Override
	public String toString() {
		return "The user gets kicked: " + this.user;
	}

	/**
	 * @return String representation of the user who gets kicked
	 */
	public String getUser() {
		return user;
	}
}
