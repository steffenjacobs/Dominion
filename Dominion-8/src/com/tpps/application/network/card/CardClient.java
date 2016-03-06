package com.tpps.application.network.card;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.game.DominionController;
import com.tpps.application.network.card.packets.PacketAddCard;
import com.tpps.application.network.card.packets.PacketCheckIfCardExistsRequest;
import com.tpps.application.network.card.packets.PacketGetCardRequest;
import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.SuperCallable;
import com.tpps.application.storage.SerializedCard;

public class CardClient extends Client {
	DominionController parent;

	public CardClient(SocketAddress _address, PacketHandler _handler, boolean connectAsync, DominionController _parent)
			throws IOException {
		super(_address, _handler, connectAsync);
		this.parent = _parent;
	}

	public void askIfCardnameExists(String name, SuperCallable<Boolean> callable) {
		try {
			super.sendMessage(new PacketCheckIfCardExistsRequest(name, parent.getSessionID()));
			addRequests.put(name, callable);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SuperCallable<SerializedCard> getGetCallable(String name) {
		return getRequests.get(name);
	}

	public SuperCallable<Boolean> getAddCallable(String name) {
		return addRequests.get(name);
	}

	public void addCardToRemoteStorage(SerializedCard card) {
		try {
			super.sendMessage(new PacketAddCard(parent.getSessionID(), card));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void requestCardFromServer(String cardName) {
		try {
			super.sendMessage(new PacketGetCardRequest(cardName, parent.getSessionID()));
			this.getRequests.put(cardName, new SuperCallable<SerializedCard>() {

				@Override
				public SerializedCard callMeMaybe(SerializedCard object) {
					parent.getStorageController().addCard(object);
					return null;
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ConcurrentHashMap<String, SuperCallable<SerializedCard>> getRequests = new ConcurrentHashMap<String, SuperCallable<SerializedCard>>();
	private ConcurrentHashMap<String, SuperCallable<Boolean>> addRequests = new ConcurrentHashMap<String, SuperCallable<Boolean>>();

}
