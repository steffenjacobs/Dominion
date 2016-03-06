package com.tpps.test.application.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.tpps.application.game.DominionController;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.network.card.CardClient;
import com.tpps.application.network.card.CardPacketHandlerClient;
import com.tpps.application.network.card.CardPacketHandlerServer;
import com.tpps.application.network.card.CardServer;
import com.tpps.application.network.core.SuperCallable;
import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;

public class JUnitCardServer {

	private final boolean DEBUG = false;

	@Test
	public void test() throws IOException, InterruptedException {
		// create test-card
		LinkedHashMap<CardAction, Integer> actions = new LinkedHashMap<>();
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, 2);
		actions.put(CardAction.DRAW_CARD, 5);
		LinkedList<CardType> types = new LinkedList<>();
		types.add(CardType.CURSE);
		types.add(CardType.VICTORY);
		types.add(CardType.TREASURE);
		BufferedImage img = ImageIO
				.read(getClass().getClassLoader().getResourceAsStream("resources/img/gameObjects/testButton.png"));

		SerializedCard sc = new SerializedCard(actions, types, 5, "☢TestCardäöü☢", img);

		// clear cache
		CardStorageController serverStorage = new CardStorageController("serverCards.bin");
		serverStorage.clearCards();
		if (DEBUG)
			serverStorage.listCards();

		serverStorage.saveCards();

		// setup Dummy-DominionController
		DominionController dom = new DominionController(true);
		dom.setSessionID(UUID.randomUUID());

		// start server
		new CardServer(new InetSocketAddress("127.0.0.1", 1336), new CardPacketHandlerServer(serverStorage),
				serverStorage);

		// start client
		CardPacketHandlerClient cHandler = new CardPacketHandlerClient();
		CardClient client = new CardClient(new InetSocketAddress("127.0.0.1", 1336), cHandler, false, dom);
		cHandler.setCardClient(client);

		// add card to remote-server
		client.askIfCardnameExists(sc.getName(), new SuperCallable<Boolean>() {

			@Override
			public Boolean callMeMaybe(Boolean object) {
				assertFalse(object.booleanValue());
				if (!object.booleanValue()) {
					client.addCardToRemoteStorage(sc);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		});

		// check if card is still there
		Thread.sleep(100);
		if (DEBUG)
			serverStorage.listCards();
		client.askIfCardnameExists(sc.getName(), new SuperCallable<Boolean>() {

			@Override
			public Boolean callMeMaybe(Boolean object) {
				if (DEBUG)
					System.out.println(object);
				assertNotNull(object);
				assertTrue(object.booleanValue());
				return null;
			}
		});

		// get card from server and store it automatically
		client.requestCardFromServer(sc.getName());
		Thread.sleep(100);

		// check if card was stored properly
		SerializedCard sc2 = dom.getCardRegistry().getCard(sc.getName());
		assertNotNull(sc2);

		// check if card was sended over the network and back successfully
		assertEquals(sc, sc2);
		assertTrue(sc.equalsEntirely(sc2));
	}
}