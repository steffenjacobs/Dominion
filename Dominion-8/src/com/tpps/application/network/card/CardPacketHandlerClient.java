package com.tpps.application.network.card;

import com.tpps.application.network.card.packets.PacketCheckIfCardExistsAnswer;
import com.tpps.application.network.card.packets.PacketGetCardAnswer;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;

/**
 * Packet-Handler for the Card-Client
 * 
 * @author Steffen Jacobs
 */
public class CardPacketHandlerClient extends PacketHandler {

	CardClient cardClient;

	/**
	 * setter setting the parent card-client to manage the callables
	 * 
	 * @param cardClient
	 *            the card-client instance to be set
	 * @return the current instance of the PacketHandler
	 */
	public CardPacketHandlerClient setCardClient(CardClient cardClient) {
		this.cardClient = cardClient;
		return this;
	}

	/**
	 * handles the incoming traffic from the server
	 * 
	 * @param port
	 *            the server is connected
	 * @param packet
	 *            packet the server sent
	 */
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		switch (packet.getType()) {
		case CARD_CHECK_IF_CARD_EXISTS_ANSWER:
			PacketCheckIfCardExistsAnswer answer = (PacketCheckIfCardExistsAnswer) packet;
			try {
				cardClient.getAddCallable(answer.getRequest().getCardName()).callMeMaybe(answer.getState());
			} catch (NullPointerException npe) {
				System.err.println("Card-Request for " + answer.getRequest().getCardName() + " not found!");
			}
			break;
		case CARD_GET_CARD_ANSWER:
			PacketGetCardAnswer answer2 = (PacketGetCardAnswer) packet;
			try {
				cardClient.getGetCallable(answer2.getRequest().getRequestedCardName())
						.callMeMaybe(answer2.getSerializedCard());
			} catch (NullPointerException npe) {
				System.err.println("Card-Request for " + answer2.getRequest().getRequestedCardName() + " not found!");
			}
			break;
		default:
			break;
		}
	}
}