package com.tpps.application.network.card.packets;

import java.io.IOException;
import java.util.UUID;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;
import com.tpps.application.storage.SerializedCard;

public class PacketAddCard extends Packet {
	private static final long serialVersionUID = -3418831446068894403L;

	private final byte[] serializedCard;
	private final UUID requesterID;

	public PacketAddCard(UUID requesterID, SerializedCard cardToAdd) {
		super(PacketType.CARD_ADD_CARD);
		this.serializedCard = cardToAdd != null ? cardToAdd.getBytes() : null;
		this.requesterID = requesterID;
	}

	public SerializedCard getSerializedCard() {
		try {
			return new SerializedCard(serializedCard);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public UUID getRequesterID() {
		return requesterID;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "\n" + this.serializedCard.toString() + "\nadded by "
				+ this.requesterID;
	}

}
