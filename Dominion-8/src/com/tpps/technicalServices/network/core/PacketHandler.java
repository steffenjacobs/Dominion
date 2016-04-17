package com.tpps.technicalServices.network.core;

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

	/**
	 * represents the server-instance who's packets are handled by this
	 * PacketHandler-instance
	 */
	protected Server parent;

	/**
	 * is called when a packet was received
	 * 
	 * @param port
	 *            the port a packet was received at
	 * @param packet
	 *            the received packet
	 */
	public abstract void handleReceivedPacket(int port, Packet packet);

	/**
	 * outputs a String to the console, tunnels to the Game-Log
	 * @param str the message to log
	 * 
	 */
	public void output(String str) {
		GameLog.log(MsgType.NETWORK_INFO, sdf.format(new Date()) + ": " + str);
	}

	/** empty - you can override this */
	public PacketHandler() {

	}

	/**
	 * sets the overlying server, ONLY USE THIS if this is a
	 * server-packet-handler
	 * 
	 * @param _parent
	 *            the instance of the server this is handling the packets of
	 */
	public void setParent(Server _parent) {
		this.parent = _parent;
	}
}
