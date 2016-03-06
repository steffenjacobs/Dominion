package com.tpps.application.network.card.packets;

import java.io.IOException;
import java.util.UUID;

import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.core.packet.PacketType;
import com.tpps.application.storage.SerializedCard;

/**
 * represents a packet sended from the client to the server to add a card to the
 * remote-storage.
 * 
 * @author Steffen Jacobs
 */
public class PacketAddCard extends Packet {
	private static final long serialVersionUID = -3418831446068894403L;

	private final byte[] serializedCard;
	private final UUID requesterID;

	/**
	 * constructor with requesterID and the card to add
	 * 
	 * @param requesterID
	 *            sessionID of the requester, used for authentification-purpose
	 * @param cardToAdd
	 *            the card to add to the remote-storage
	 */
	public PacketAddCard(UUID requesterID, SerializedCard cardToAdd) {
		super(PacketType.CARD_ADD_CARD);
		this.serializedCard = cardToAdd != null ? cardToAdd.getBytes() : null;
		this.requesterID = requesterID;
	}

	/**
	 * getter for the card to add
	 * 
	 * @return the card to add
	 */
	public SerializedCard getSerializedCard() {
		try {
			return new SerializedCard(serializedCard);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * getter for the requester's UUID
	 * 
	 * @return the requester's sessionID
	 */
	public UUID getRequesterID() {
		return requesterID;
	}

	/** @return a readable representation of the object */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "\n" + this.serializedCard.toString() + "\nadded by "
				+ this.requesterID;
	}
}