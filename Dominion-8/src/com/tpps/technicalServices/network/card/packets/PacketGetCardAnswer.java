package com.tpps.technicalServices.network.card.packets;

import java.io.IOException;

import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * represents a packet sent from the server to the client in repsonse to a
 * PacketGetCardRequest. This packet contains the requested card.
 * 
 * @author Steffen Jacobs
 */
public class PacketGetCardAnswer extends Packet {
	private static final long serialVersionUID = 1387553775307960715L;

	private final byte[] serializedCard;
	private final PacketGetCardRequest request;

	/**
	 * constructor for the answer-packet
	 * 
	 * @param card
	 *            the requested card-object (including image)
	 * @param request
	 *            the request
	 */
	public PacketGetCardAnswer(SerializedCard card, PacketGetCardRequest request) {
		super(PacketType.CARD_GET_CARD_ANSWER);
		this.serializedCard = card != null ? card.getBytes() : null;
		this.request = request;
	}

	/**
	 * getter for the request
	 * 
	 * @return the request
	 */
	public PacketGetCardRequest getRequest() {
		return request;
	}

	/**
	 * getter for the requested card
	 * 
	 * @return the requested card
	 */
	public SerializedCard getSerializedCard() {
		try {
			return new SerializedCard(serializedCard);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** @return a readable representation of the object */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + this.serializedCard.toString() + " requested by "
				+ request.getRequesterID();
	}
}