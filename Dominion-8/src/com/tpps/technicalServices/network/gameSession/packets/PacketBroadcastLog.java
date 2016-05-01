package com.tpps.technicalServices.network.gameSession.packets;

import java.awt.Color;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client to broadcast a Log message
 * 
 * @author ladler - Lukas Adler, nwipfler - Nicolas Wipfler
 */
public class PacketBroadcastLog extends Packet {

	private static final long serialVersionUID = 6438319829526897629L;
	private final String left;
	private final String username;
	private final String right;
	private final MsgType msgType;
	private final Color color;

	/***
	 * this will be used in most cases
	 * 
	 * @param line
	 *            the line to update
	 * @param color
	 *            the color of the updated line
	 */
	public PacketBroadcastLog(String left, String username, String right, Color color) {
		super(PacketType.BROADCAST_LOG);
		this.left = left;
		this.username = username;
		this.right = right;
		this.msgType = MsgType.GAME;
		this.color = color;
	}

	/**
	 * this will also be used in most cases
	 * 
	 * @param line
	 *            the line to update
	 */
	public PacketBroadcastLog(String right) {
		super(PacketType.BROADCAST_LOG);
		this.left = "";
		this.username = "";
		this.right = right;
		this.msgType = MsgType.GAME;
		this.color = GameLog.getMsgColor();
	}

	// /**
	// *
	// * sets the packettype
	// *
	// * @author ladler - Lukas Adler, nwipfler - Nicolas Wipfler
	// */
	// public PacketBroadcastLog(MsgType msgType, String right, Color color) {
	// super(PacketType.BROADCAST_LOG);
	// this.left = "";
	// this.username = "";
	// this.right = right;
	// this.msgType = msgType;
	// this.color = color;
	// this.logNr = GameLog.getCount();
	// }

	/**
	 *
	 * @param msgType
	 * @param msg
	 */
	public PacketBroadcastLog(MsgType msgType, String right) {
		super(PacketType.BROADCAST_LOG);
		this.left = "";
		this.username = "";
		this.right = right;
		this.msgType = msgType;
		this.color = GameLog.getMsgColor();
	}

	public MsgType getMsgType() {
		return this.msgType;
	}

	public String getLeft() {
		return this.left;
	}

	public String getUsername() {
		return this.username;
	}

	public String getRight() {
		return this.right;
	}

	public Color getColor() {
		return this.color;
	}

	/**
	 * @return a readable String
	 * @author ladler - Lukas Adler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}