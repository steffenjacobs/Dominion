package com.tpps.application.network.card.packets;

import java.util.UUID;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * a request sent from the client to the server to get a specific card. This
 * packet is answered by the server with a PacketGetCardAnswer
 * 
 * @author Steffen Jacobs
 */
public class PacketGetCardRequest extends Packet {
	private static final long serialVersionUID = -5933726239629126669L;

	private final String requestedCardName;
	private final UUID requesterID;

	/**
	 * constructor for the get-request
	 * 
	 * @param requestedCardName
	 *            the name of the requested card
	 * @param requesterID
	 *            requester's sessionID to authenticate
	 */
	public PacketGetCardRequest(String requestedCardName, UUID requesterID) {
		super(PacketType.CARD_GET_CARD_REQUEST);
		this.requestedCardName = requestedCardName;
		this.requesterID = requesterID;
	}

	/**
	 * getter for the requested card name
	 * 
	 * @return the requested card name
	 */
	public String getRequestedCardName() {
		return requestedCardName;
	}

	/**
	 * getter for the requester's sessionID
	 * 
	 * @return the requester's sessionID
	 */
	public UUID getRequesterID() {
		return requesterID;
	}

	/** @return a readable representation of the object */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + this.requestedCardName + " requested by " + this.requesterID;
	}
}