package com.tpps.application.network.card.packets;

import java.util.UUID;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

/**
 * represents a Packet sent from the client to the server to check if a card
 * with a requested name already exists. The packet is answered by the server
 * with a PacketCheckIfCradExistsAnswer.
 * 
 * @author Steffen Jacobs
 */
public class PacketCheckIfCardExistsRequest extends Packet {
	private static final long serialVersionUID = 1553141286892991229L;

	private final String cardName;
	private final UUID requesterID;

	/**
	 * constructor for the check-existance-request-packet
	 * 
	 * @param nameToCheck
	 *            the name of the card
	 * @param requesterID
	 *            sessionID of the requester to authenticate
	 */
	public PacketCheckIfCardExistsRequest(String nameToCheck, UUID requesterID) {
		super(PacketType.CARD_CHECK_IF_CARD_EXISTS_REQUEST);
		this.cardName = nameToCheck;
		this.requesterID = requesterID;
	}

	/**
	 * getter for the requested card-name
	 * 
	 * @return the requested card-name
	 */
	public String getCardName() {
		return cardName;
	}

	/**
	 * getter for the requester's uuid
	 * 
	 * @rturn the requester's sessionID
	 */
	public UUID getRequesterID() {
		return requesterID;
	}

	/** @return a readable representation of the object */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + this.getCardName() + " asked by " + this.getRequesterID();
	}
}