package com.tpps.technicalServices.network.matchmaking.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class MatchmakingAnswer extends Packet {
	private static final long serialVersionUID = 4633962878878433551L;
	
	private final MatchmakingRequest request;
	private final int answerCode;
	
	

	protected MatchmakingAnswer(MatchmakingRequest req, int answerCode) {
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

	public MatchmakingRequest getRequest() {
		return request;
	}

}
