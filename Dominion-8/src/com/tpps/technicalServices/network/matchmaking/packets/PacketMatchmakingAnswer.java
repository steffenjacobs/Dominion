package com.tpps.technicalServices.network.matchmaking.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class PacketMatchmakingAnswer extends Packet {
	private static final long serialVersionUID = 4633962878878433551L;

	private final PacketMatchmakingRequest request;

	/*
	 * answerCode = 0: Error: Bad Session 1: Success
	 */
	private final int answerCode;

	public PacketMatchmakingAnswer(PacketMatchmakingRequest req, int answerCode) {
		super(PacketType.MATCHMAKING_ANSWER);
		this.answerCode = answerCode;
		this.request = req;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getAnswerCode() {
		return answerCode;
	}

	public PacketMatchmakingRequest getRequest() {
		return request;
	}

}
