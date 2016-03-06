package com.tpps.application.network.card;

import java.io.IOException;

import com.tpps.application.network.card.packets.PacketAddCard;
import com.tpps.application.network.card.packets.PacketCheckIfCardExistsAnswer;
import com.tpps.application.network.card.packets.PacketCheckIfCardExistsRequest;
import com.tpps.application.network.card.packets.PacketGetCardAnswer;
import com.tpps.application.network.card.packets.PacketGetCardRequest;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.storage.CardStorageController;

/**
 * Packet-Handler for the Card-Server
 * 
 * @author Steffen Jacobs
 */
public class CardPacketHandlerServer extends PacketHandler {

	CardStorageController cardStorage;

	/**
	 * constructor taking a card-storage
	 * 
	 * @param cardStorage
	 *            the card-storage to use
	 */
	public CardPacketHandlerServer(CardStorageController cardStorage) {
		this.cardStorage = cardStorage;
	}

	/**
	 * handles all the packets for the card-server
	 * 
	 * @param port
	 *            port of the client who sent the packet
	 * @param packet
	 *            packet a client sent
	 */
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		ServerConnectionThread connThread = parent.getClientThread(port);
		switch (packet.getType()) {
		case CARD_CHECK_IF_CARD_EXISTS_REQUEST:
			// TODO: infer uuid-test (via port-map?)
			PacketCheckIfCardExistsRequest request = (PacketCheckIfCardExistsRequest) packet;
			PacketCheckIfCardExistsAnswer answer = new PacketCheckIfCardExistsAnswer(
					this.cardStorage.hasCard(request.getCardName()), request);
			try {
				connThread.sendPacket(answer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case CARD_ADD_CARD:
			PacketAddCard cardAdd = (PacketAddCard) packet;
			// TODO: infer uuid-test
			this.cardStorage.addCard(cardAdd.getSerializedCard());
			break;
		case CARD_GET_CARD_REQUEST:
			PacketGetCardRequest request2 = (PacketGetCardRequest) packet;
			// TODO: infer uuid-test
			try {
				connThread.sendPacket(
						new PacketGetCardAnswer(this.cardStorage.getCard(request2.getRequestedCardName()), request2));
			} catch (IOException e) {
				e.printStackTrace();
			}
		default:
			break;
		}
	}
}