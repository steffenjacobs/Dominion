package com.tpps.technicalServices.network.card.packets;

import java.io.IOException;
import java.util.UUID;

import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

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
	private final String requesterName;

	/**
	 * constructor with requesterID and the card to add
	 * 
	 * @param requesterID
	 *            sessionID of the requester, used for authentification-purpose
	 * @param cardToAdd
	 *            the card to add to the remote-storage
	 * @param requesterName
	 *            the requester's name
	 */
	public PacketAddCard(UUID requesterID, String requesterName, SerializedCard cardToAdd) {
		super(PacketType.CARD_ADD_CARD);
		this.serializedCard = cardToAdd != null ? cardToAdd.getBytes() : null;
		this.requesterID = requesterID;
		this.requesterName = requesterName;
	}

	/**
	 * getter for the requester's name
	 * 
	 * @return the requester's name
	 */
	public String getRequesterName() {
		return this.requesterName;
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