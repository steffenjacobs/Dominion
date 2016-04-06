package com.tpps.Zpresentation.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * This PacketHandler can be used by a client or a server for computing every
 * received packet
 * 
 * @author Steffen Jacobs
 */
public abstract class PacketHandler {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	// represents the server-instance who's packets are handled by this
	// PacketHandler-instance
	protected Server parent;

	/** is called when a packet was received */
	public abstract void handleReceivedPacket(int port, Packet packet);

	/**
	 * outputs a String to the console
	 * 
	 * @author Steffen Jacobs
	 */
	// TODO: save log
	public void output(String str) {
		GameLog.log(MsgType.NETWORK_INFO, sdf.format(new Date()) + ": " + str);
	}

	public PacketHandler() {

	}

	public void setParent(Server _parent) {
		this.parent = _parent;
	}
}
