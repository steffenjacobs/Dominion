package com.tpps.application.network.clientSession.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * This packet is sent from the Game-Client to the Session-Server to keep the
 * active Game-Session valid. The Game-Session expires after [120] seconds.
 * 
 * @author Steffen Jacobs
 */
public class PacketSessionKeepAlive extends Packet {
	private static final long serialVersionUID = -4121285191894892888L;
	private final String username;

	/**
	 * @return the username who's session should be revalidated
	 * @author Steffen Jacobs
	 */
	public String getUsername() {
		return this.username;
	}

	/** initialize the packet with the username */
	public PacketSessionKeepAlive(String _username) {
		super(PacketType.SESSION_KEEP_ALIVE);
		this.username = _username;
	}

	/**
	 * @return a readable representation of the packet
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.getUsername();
	}
}
