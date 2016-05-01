package com.tpps.technicalServices.network.gameSession.packets;

import java.awt.Color;
import java.util.LinkedList;

import javafx.util.Pair;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is send from a server to the client to broadcast a Log message
 * with more than one color in one line
 * 
 * @author nwipfler - Nicolas Wipfler
 *
 */
public class PacketBroadcastLogMultiColor extends Packet {

	private static final long serialVersionUID = -2014159824106961056L;
	private final LinkedList<Pair<String, Color>> pair;
	private final MsgType msgType;
	private final int logNr;

	/***
	 * this will be used in most cases
	 * 
	 * @param line
	 *            the line to update
	 * @param color
	 *            the color of the updated line
	 */
	@SafeVarargs
	public PacketBroadcastLogMultiColor(Pair<String, Color>... pairs) {
		super(PacketType.BROADCAST_LOG_MULTI_COLOR);
		this.pair = new LinkedList<Pair<String, Color>>();
		for (Pair<String, Color> p : pairs) {
			pair.addLast(p);
		}
		this.msgType = MsgType.GAME;
		this.logNr = GameLog.getCountAndInc();
	}

	/**
	 * @return the pair
	 */
	public LinkedList<Pair<String, Color>> getPair() {
		return pair;
	}

	/**
	 * @return the msgType
	 */
	public MsgType getMsgType() {
		return msgType;
	}

	/**
	 * 
	 * @return the logNr
	 */
	public int getLogNr() {
		return this.logNr;
	}

	/**
	 * 
	 * @return a readable String
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
