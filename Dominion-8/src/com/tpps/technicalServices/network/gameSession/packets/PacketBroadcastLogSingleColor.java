package com.tpps.technicalServices.network.gameSession.packets;

import java.awt.Color;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client every time there are already
 * too much player on the server
 * 
 * @author ladler - Lukas Adler
 */
public class PacketBroadcastLogSingleColor extends Packet {

	private static final long serialVersionUID = 6438319829526897629L;
	private final String msg;
	private final MsgType msgType;
	private final Color color;

	// ersten beiden mit MsgType.GAME
	
	/***
	 * this will be used in most cases
	 * 
	 * @param line the line to update
	 * @param color the color of the updated line
	 */
	public PacketBroadcastLogSingleColor(String line, Color color) {
		super(PacketType.BROADCAST_LOG_SINGLE_COLOR);
		this.msg = line;
		this.msgType = MsgType.GAME;
		this.color = color;
	}
	
	/**
	 * this will also be used in most cases
	 * 
	 * @param line the line to update
	 */
	public PacketBroadcastLogSingleColor(String line) {
		super(PacketType.BROADCAST_LOG_SINGLE_COLOR);
		this.msg = line;
		this.msgType = MsgType.GAME;
		this.color = GameLog.getMsgColor();
	}
		
	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler, nwipfler - Nicolas Wipfler
	 */
	public PacketBroadcastLogSingleColor(MsgType msgType, String msg, Color color) {
		super(PacketType.BROADCAST_LOG_SINGLE_COLOR);
		this.msg = msg;
		this.msgType = msgType;
		this.color = color;
		GameLog.setMsgColor(color);
	}
	
	/**
	 * 
	 * @param msgType
	 * @param msg
	 */
	public PacketBroadcastLogSingleColor(MsgType msgType, String msg) {
		super(PacketType.BROADCAST_LOG_SINGLE_COLOR);
		this.msg = msg;
		this.msgType = msgType;
		this.color = GameLog.getMsgColor();
	}
	
	public MsgType getMsgType(){
		return this.msgType;
	}
	
	public String getMessage(){
		return this.msg;
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