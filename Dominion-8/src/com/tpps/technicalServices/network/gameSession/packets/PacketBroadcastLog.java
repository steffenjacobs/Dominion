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

	private final long timestamp;

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
		this.timestamp = System.currentTimeMillis();
		// GameLog.log(MsgType. ," in PBL >>>>>> Timestamp is > " +
		// this.timestamp + " and it's " + (System.currentTimeMillis() -
		// this.timestamp) + " ms old.");
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
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 *
	 * @param msgType
	 *            the messageType of the message
	 * @param msg
	 *            the message to log
	 */
	public PacketBroadcastLog(MsgType msgType, String right) {
		super(PacketType.BROADCAST_LOG);
		this.left = "";
		this.username = "";
		this.right = right;
		this.msgType = msgType;
		this.color = GameLog.getMsgColor();
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * 
	 * @return the message type
	 */
	public MsgType getMsgType() {
		return this.msgType;
	}

	/**
	 * 
	 * @return the right string
	 */
	public String getLeft() {
		return this.left;
	}

	/**
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * 
	 * @return the right string
	 */
	public String getRight() {
		return this.right;
	}

	/**
	 * 
	 * @return the color
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * 
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return this.timestamp;
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