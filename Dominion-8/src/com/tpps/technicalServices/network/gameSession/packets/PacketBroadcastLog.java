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
public class PacketBroadcastLog extends Packet {

	private static final long serialVersionUID = 6438319829526897629L;
	private final String msg;
	private final MsgType msgType;
	private final Color color;
	
	/**
	 * 
	 * sets the packettype
	 * 
	 * @author ladler - Lukas Adler, nwipfler - Nicolas Wipfler
	 */
	public PacketBroadcastLog(MsgType msgType, String msg, Color color) {
		super(PacketType.BROADCAST_LOG);
		this.msg = msg;
		this.msgType = msgType;
		this.color = color;
		GameLog.setMsgColor(color);
	}
	
	public PacketBroadcastLog(MsgType msgType, String msg) {
		super(PacketType.BROADCAST_LOG);
		this.msg = msg;
		this.msgType = msgType;
		this.color = Color.WHITE;
		GameLog.setMsgColor(color);
	}
	
	/***
	 * this will be used in most cases
	 * 
	 * @param line the line to update
	 * @param color the color of the updated line
	 */
	public PacketBroadcastLog(String line, Color color) {
		super(PacketType.BROADCAST_LOG);
		this.msg = line;
		this.msgType = MsgType.GAME;
		this.color = color;
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