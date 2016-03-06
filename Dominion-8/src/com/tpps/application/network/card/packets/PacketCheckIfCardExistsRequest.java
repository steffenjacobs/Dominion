package com.tpps.application.network.card.packets;

import java.util.UUID;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

public class PacketCheckIfCardExistsRequest extends Packet {
	private static final long serialVersionUID = 1553141286892991229L;

	private final String cardName;
	private final UUID requesterID;

	public PacketCheckIfCardExistsRequest(String nameToCheck, UUID requesterID) {
		super(PacketType.CARD_CHECK_IF_CARD_EXISTS_REQUEST);
		this.cardName = nameToCheck;
		this.requesterID = requesterID;
	}

	public String getCardName() {
		return cardName;
	}

	public UUID getRequesterID() {
		return requesterID;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + this.getCardName() + " asked by " + this.getRequesterID();
	}
}