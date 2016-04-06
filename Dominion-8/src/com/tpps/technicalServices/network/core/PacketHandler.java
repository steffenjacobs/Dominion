package com.tpps.technicalServices.network.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.packet.Packet;

public abstract class PacketHandler {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	// represents the server-instance who's packets are handled by this
	// PacketHandler-instance
	protected Server parent;

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
