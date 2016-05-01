package com.tpps.technicalServices.network.card;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import com.tpps.application.game.DominionController;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.card.packets.PacketAddCard;
import com.tpps.technicalServices.network.card.packets.PacketCheckIfCardExistsRequest;
import com.tpps.technicalServices.network.card.packets.PacketGetCardRequest;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.SuperCallable;

/**
 * represents the Card-Client exchanging data with the card-server
 * 
 * @author Steffen Jacobs
 */
public class CardClient extends Client {

	private final DominionController parent;

	private ConcurrentHashMap<String, SuperCallable<SerializedCard>> getRequests = new ConcurrentHashMap<String, SuperCallable<SerializedCard>>();
	private ConcurrentHashMap<String, SuperCallable<Boolean>> addRequests = new ConcurrentHashMap<String, SuperCallable<Boolean>>();

	/**
	 * constructor taking: the address of the server, a packet-handler, whether
	 * to connect async and the parent main-instance of the entire application
	 * 
	 * @param _address
	 *            the address to find the server
	 * @param _handler
	 *            the packet-handler for the card-client
	 * @param connectAsync
	 *            whether to connect asynchronously
	 * @param _parent
	 *            the main instance of the entire application
	 * @throws IOException 
	 */
	public CardClient(SocketAddress _address, PacketHandler _handler, boolean connectAsync, DominionController _parent)
			throws IOException {
		super(_address, _handler, connectAsync);
		this.parent = _parent;
	}

	/**
	 * sends a packet to the card-server if the requested card exists
	 * 
	 * @param name
	 *            name of the card to check
	 * @param callable
	 *            the callable that will be called when the answer-packet
	 *            arrives
	 */
	public void askIfCardnameExists(String name, SuperCallable<Boolean> callable) {
		try {
			super.sendMessage(new PacketCheckIfCardExistsRequest(name, parent.getSessionID(), parent.getUsername()));
			addRequests.put(name, callable);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * getter for the get-request-callable, called by the client-packet-handler
	 * 
	 * @param name
	 *            the requested name
	 * @return the callable for the name
	 */
	SuperCallable<SerializedCard> getGetCallable(String name) {
		return getRequests.get(name);
	}

	/**
	 * getter for the add-request-callable, called by the client-packet-handler
	 * 
	 * @param name
	 *            the requested name
	 * @return the callable for the name
	 */
	SuperCallable<Boolean> getAddCallable(String name) {
		return addRequests.remove(name);
	}

	/**
	 * adds a card to the remote-storage. Note: It could take a few milliseconds
	 * until the card has been added
	 * 
	 * @param card
	 *            card to add to the remote storage
	 */
	public void addCardToRemoteStorage(SerializedCard card) {
		try {
			super.sendMessage(new PacketAddCard(parent.getSessionID(), parent.getUsername(), card));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * requests a card from a server and automatically puts it into the own
	 * storage when it arrived
	 * 
	 * @param cardName
	 *            the name of the requested card
	 */
	public void requestCardFromServer(String cardName) {
		try {
			super.sendMessage(new PacketGetCardRequest(cardName, parent.getSessionID(), parent.getUsername()));
			this.getRequests.put(cardName, new SuperCallable<SerializedCard>() {

				@Override
				public SerializedCard callMeMaybe(SerializedCard object) {
					GameLog.log(MsgType.INFO, "Received card " + object.getName());
					parent.getCardRegistry().addCard(object);
					return null;
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * requests a card from a server and automatically puts it into the own
	 * storage
	 * 
	 * @param cardName
	 *            the name of the requested card
	 * @param async
	 *            tells whether you check your storage later again or you wait
	 *            until the card is there.
	 */
	public void requestCardFromServer(String cardName, boolean async) {
		if (async) {
			this.requestCardFromServer(cardName);
			return;
		}

		try {
			Semaphore sem = new Semaphore(1);
			sem.acquire();
			super.sendMessage(new PacketGetCardRequest(cardName, parent.getSessionID(), parent.getUsername()));
			this.getRequests.put(cardName, new SuperCallable<SerializedCard>() {

				@Override
				public SerializedCard callMeMaybe(SerializedCard object) {
					System.out.println("Received card from server: " + object.getName());
					parent.getCardRegistry().addCard(object);
					sem.release();
					return null;
				}
			});
			sem.acquire();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}