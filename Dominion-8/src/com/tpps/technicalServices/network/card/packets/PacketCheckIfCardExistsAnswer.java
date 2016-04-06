package com.tpps.technicalServices.network.card.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * represents the answer to a PacketCheckIfCardExistsRequest. This packet is
 * sent from the server to the client.
 * 
 * @author Steffen Jacobs
 */
public class PacketCheckIfCardExistsAnswer extends Packet {
	private static final long serialVersionUID = 2282135547877997336L;

	private final boolean state;
	private final PacketCheckIfCardExistsRequest request;

	/**
	 * constructor with the state (whether the card exists) and the request
	 * 
	 * @param state
	 *            the state whether the requested card-name exists
	 * @param request
	 *            the request sent to the server in the first place
	 */
	public PacketCheckIfCardExistsAnswer(boolean state, PacketCheckIfCardExistsRequest request) {
		super(PacketType.CARD_CHECK_IF_CARD_EXISTS_ANSWER);
		this.state = state;
		this.request = request;
	}

	/**
	 * getter for the request
	 * 
	 * @return the request sent to the server in the first place
	 */
	public PacketCheckIfCardExistsRequest getRequest() {
		return request;
	}

	/**
	 * getter for the exists-state
	 * 
	 * @return whether the requested card existed or not
	 */
	public boolean getState() {
		return this.state;
	}

	/** @return a readable representation of the object */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + this.getRequest().getCardName() + " asked by "
				+ this.getRequest().getRequesterID() + ": " + this.getState();
	}
}
