package com.tpps.technicalServices.network.gameSession.packets;

import java.awt.Color;
import java.util.LinkedList;

import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

import javafx.util.Pair;

public class PacketBroadcastLogMultiColor extends Packet {

	private static final long serialVersionUID = -2014159824106961056L;
	private LinkedList<Pair<String, Color>> pair;
	private MsgType msgType;
	
	/***
	 * this will be used in most cases
	 * 
	 * @param line the line to update
	 * @param color the color of the updated line
	 */
	@SafeVarargs
	public PacketBroadcastLogMultiColor(Pair<String, Color>... pairs) {
		super(PacketType.BROADCAST_LOG_MULTI_COLOR);
		this.pair = new LinkedList<Pair<String, Color>>();
 		for (Pair<String,Color> p : pairs) {
			pair.addLast(p);
		}
		this.msgType = MsgType.GAME;
	}
	
	/**
	 * @return the pair
	 */
	public LinkedList<Pair<String, Color>> getPair() {
		return pair;
	}
	
	/**
	 * @param pair the pair to set
	 */
	public void setPair(LinkedList<Pair<String, Color>> pair) {
		this.pair = pair;
	}

	/**
	 * @return the msgType
	 */
	public MsgType getMsgType() {
		return msgType;
	}

	/**
	 * @param msgType the msgType to set
	 */
	public void setMsgType(MsgType msgType) {
		this.msgType = msgType;
	}
	
	/**
	 * @return a readable String
	 * @author nwipfler - Nicolas Wipfler
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
