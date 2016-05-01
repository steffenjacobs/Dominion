package com.tpps.technicalServices.network.matchmaking.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class PacketMatchmakingStart extends Packet {

	private static final long serialVersionUID = -9092419278238418617L;

	private final UUID lobbyID, senderID;
	private final String senderName;
	private final String[] selectedActionCards;

	public PacketMatchmakingStart(UUID lobbyID, UUID senderID, String senderName, String[] selectedActionCards) {
		super(PacketType.MATCHMAKING_START_GAME);
		this.lobbyID = lobbyID;
		this.senderID = senderID;
		this.senderName = senderName;
		this.selectedActionCards = selectedActionCards;
	}
	
	public String[] getSelectedActionCards() {
		return selectedActionCards;
	}
	
	public UUID getLobbyID() {
		return lobbyID;
	}
	
	public UUID getSenderID() {
		return senderID;
	}
	
	public String getSenderName() {
		return senderName;
	}

	@Override
	public String toString() {
		return "Start-Packet @" + System.identityHashCode(this) + " - " + this.lobbyID + " - " + this.senderID + " - "
				+ this.senderName;
	}

}
