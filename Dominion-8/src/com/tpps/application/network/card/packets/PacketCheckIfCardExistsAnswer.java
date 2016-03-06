package com.tpps.application.network.card.packets;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;

public class PacketCheckIfCardExistsAnswer extends Packet {
	private static final long serialVersionUID = 2282135547877997336L;

	private final boolean state;
	private final PacketCheckIfCardExistsRequest request;

	public PacketCheckIfCardExistsAnswer(boolean state, PacketCheckIfCardExistsRequest request) {
		super(PacketType.CARD_CHECK_IF_CARD_EXISTS_ANSWER);
		this.state = state;
		this.request = request;
	}

	public PacketCheckIfCardExistsRequest getRequest() {
		return request;
	}

	public boolean getState() {
		return this.state;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + this.getRequest().getCardName() + " asked by "
				+ this.getRequest().getRequesterID() + ": " + this.getState();
	}
}
