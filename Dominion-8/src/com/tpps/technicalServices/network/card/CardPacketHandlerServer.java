package com.tpps.technicalServices.network.card;

import com.tpps.application.storage.CardStorageController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.card.packets.PacketAddCard;
import com.tpps.technicalServices.network.card.packets.PacketCheckIfCardExistsAnswer;
import com.tpps.technicalServices.network.card.packets.PacketCheckIfCardExistsRequest;
import com.tpps.technicalServices.network.card.packets.PacketGetCardAnswer;
import com.tpps.technicalServices.network.card.packets.PacketGetCardRequest;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.Server;
import com.tpps.technicalServices.network.core.ServerConnectionThread;
import com.tpps.technicalServices.network.core.packet.Packet;

/**
 * Packet-Handler for the Card-Server
 * 
 * @author Steffen Jacobs
 */
public class CardPacketHandlerServer extends PacketHandler {

	private CardStorageController cardStorage;

	private SessionClient sessionTester;

	/**
	 * constructor taking a card-storage
	 * 
	 * @param cardStorage
	 *            the card-storage to use
	 * @param sessionClient
	 *            the instance of the SessionClient to check the requesters
	 *            validity
	 */
	public CardPacketHandlerServer(CardStorageController cardStorage, SessionClient sessionClient) {
		this.cardStorage = cardStorage;
		this.sessionTester = sessionClient;
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

			PacketCheckIfCardExistsRequest request = (PacketCheckIfCardExistsRequest) packet;

			// check session-validity
			if (!sessionTester.checkSessionSync(request.getRequesterName(), request.getRequesterID())) {
				System.err.println(request.getRequesterName() + " FAILED CHECK!" + System.identityHashCode(packet));
				break;
			}

			// debug-message
			if (Server.DEBUG) {
				GameLog.log(MsgType.DEBUG, request.getRequesterName() + " requested " + request.getCardName());
			}

			PacketCheckIfCardExistsAnswer answer = new PacketCheckIfCardExistsAnswer(
					this.cardStorage.hasCard(request.getCardName()), request);

			new Thread(() -> {
				try {
					connThread.addPacketToQueue(answer);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
			break;
		case CARD_ADD_CARD:
			PacketAddCard cardAdd = (PacketAddCard) packet;

			// check session-validity
			if (!sessionTester.checkSessionSync(cardAdd.getRequesterName(), cardAdd.getRequesterID()))
				break;

			// debug-message
			if (Server.DEBUG) {
				GameLog.log(MsgType.DEBUG,
						cardAdd.getRequesterName() + " added " + cardAdd.getSerializedCard().getName());
			}

			this.cardStorage.addCard(cardAdd.getSerializedCard());
			break;
		case CARD_GET_CARD_REQUEST:
			PacketGetCardRequest request2 = (PacketGetCardRequest) packet;

			// check session-validity
			if (!sessionTester.checkSessionSync(request2.getRequesterName(), request2.getRequesterID()))
				break;

			// debug-message
			if (Server.DEBUG) {
				GameLog.log(MsgType.DEBUG, request2.getRequesterName() + " wants " + request2.getRequestedCardName());
			}

			connThread.addPacketToQueue(
					new PacketGetCardAnswer(this.cardStorage.getCard(request2.getRequestedCardName()), request2));
			break;
		default:
			break;
		}
	}
}