package com.tpps.application.network.card.packets;

import java.util.UUID;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

public class PacketGetCardRequest extends Packet {
	private static final long serialVersionUID = -5933726239629126669L;

	private final String requestedCardName;
	private final UUID requesterID;

	public PacketGetCardRequest(String requestedCardName, UUID requesterID) {
		super(PacketType.CARD_GET_CARD_REQUEST);
		this.requestedCardName = requestedCardName;
		this.requesterID = requesterID;
	}

	public String getRequestedCardName() {
		return requestedCardName;
	}

	public UUID getRequesterID() {
		return requesterID;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + this.requestedCardName + " requested by " + this.requesterID;
	}
}