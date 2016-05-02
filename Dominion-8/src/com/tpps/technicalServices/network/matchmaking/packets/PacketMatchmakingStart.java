package com.tpps.technicalServices.network.matchmaking.packets;

import java.util.UUID;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This packet is sent from the lobby-admin to the matchmaking-server to start a
 * game
 * 
 * @author Steffen Jacobs
 */
public class PacketMatchmakingStart extends Packet {

	private static final long serialVersionUID = -9092419278238418617L;

	private final UUID lobbyID, senderID;
	private final String senderName;
	private final String[] selectedActionCards;

	/**
	 * @param lobbyID
	 *            the ID of the lobby to start
	 * @param senderID
	 *            the UUID of the lobby-admin
	 * @param senderName
	 *            the name of the lobby-admin
	 * @param selectedActionCards
	 *            the cards to play with
	 */
	public PacketMatchmakingStart(UUID lobbyID, UUID senderID, String senderName, String[] selectedActionCards) {
		super(PacketType.MATCHMAKING_START_GAME);
		this.lobbyID = lobbyID;
		this.senderID = senderID;
		this.senderName = senderName;
		this.selectedActionCards = selectedActionCards;
	}

	/**
	 * @return the cards to play with
	 */
	public String[] getSelectedActionCards() {
		return selectedActionCards;
	}

	/**
	 * @return the UUID of the lobby to start
	 */
	public UUID getLobbyID() {
		return lobbyID;
	}

	/**
	 * @return the session ID of the sender
	 */
	public UUID getSenderID() {
		return senderID;
	}

	/**
	 * @return the name of the sender
	 */
	public String getSenderName() {
		return senderName;
	}

	/**
	 * @see com.tpps.technicalServices.network.core.packet.Packet#toString()
	 */
	@Override
	public String toString() {
		return "Start-Packet @" + System.identityHashCode(this) + " - " + this.lobbyID + " - " + this.senderID + " - "
				+ this.senderName;
	}

}
