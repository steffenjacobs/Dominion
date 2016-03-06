package com.tpps.application.network.card.packets;

import java.io.IOException;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;
import com.tpps.application.storage.SerializedCard;

public class PacketGetCardAnswer extends Packet {
	private static final long serialVersionUID = 1387553775307960715L;

	private final byte[] serializedCard;
	private final PacketGetCardRequest request;

	public PacketGetCardAnswer(SerializedCard card, PacketGetCardRequest request) {
		super(PacketType.CARD_GET_CARD_ANSWER);
		this.serializedCard = card != null ? card.getBytes() : null;
		this.request = request;
	}

	public PacketGetCardRequest getRequest() {
		return request;
	}

	public SerializedCard getSerializedCard() {
		try {
			return new SerializedCard(serializedCard);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + this.serializedCard.toString() + " requested by "
				+ request.getRequesterID();
	}
}