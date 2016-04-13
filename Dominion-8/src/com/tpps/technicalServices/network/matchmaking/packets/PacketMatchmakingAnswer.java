package com.tpps.technicalServices.network.matchmaking.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * sent from the MatchmakingServer to a searching client, answer to the
 * PacketMatchmakingRequest from the client
 * 
 * @author Steffen Jacobs
 */
public class PacketMatchmakingAnswer extends Packet {
	private static final long serialVersionUID = 4633962878878433551L;

	private final PacketMatchmakingRequest request;

	/*
	 * answerCode = 0: Error: Bad Session 1: Success
	 */
	private final int answerCode;

	/**
	 * constructor with the request and the answer
	 * 
	 * @param req
	 *            request packet received from the client
	 * @param answerCode
	 *            0: Error, bad session; 1: Success
	 */
	public PacketMatchmakingAnswer(PacketMatchmakingRequest req, int answerCode) {
		super(PacketType.MATCHMAKING_ANSWER);
		this.answerCode = answerCode;
		this.request = req;
	}

	/** @return a representation of the packet as a String */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": Request:" + this.getRequest() + " - AC: " + this.getAnswerCode();
	}

	/**
	 * @return the answerCode of the request: 0: Error, bad session; 1: Success
	 */
	public int getAnswerCode() {
		return answerCode;
	}

	/** @return the request sent to the server */
	public PacketMatchmakingRequest getRequest() {
		return request;
	}

}
