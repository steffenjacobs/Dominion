package com.tpps.test.application.storage;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.tpps.application.game.DominionController;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.network.card.CardClient;
import com.tpps.application.network.card.CardPacketHandlerClient;
import com.tpps.application.network.card.CardPacketHandlerServer;
import com.tpps.application.network.card.CardServer;
import com.tpps.application.network.clientSession.client.SessionClient;
import com.tpps.application.network.clientSession.client.SessionPacketSenderAPI;
import com.tpps.application.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.application.network.clientSession.server.SessionServer;
import com.tpps.application.network.core.SuperCallable;
import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;

public class CheapCardCreator {
	private static final boolean DEBUG = false;
	private static final boolean REMOTE = true;
	private static final boolean doCheck = true;
	private static final boolean doSend = true;

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws IOException, InterruptedException {

		// Setup
		HashMap<String, SerializedCard> cards = new HashMap<String, SerializedCard>();
		LinkedHashMap<CardAction, String> actions = new LinkedHashMap<>();
		LinkedList<CardType> types = new LinkedList<>();

		// Create Copper
		actions.put(CardAction.IS_TREASURE, "1");
		types.add(CardType.TREASURE);

		cards.put("Copper", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 0, "Copper", getImg("Copper")));

		// Create Silver
		actions.remove(CardAction.IS_TREASURE);
		actions.put(CardAction.IS_TREASURE, "2");

		cards.put("Silver", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 3, "Silver", getImg("Silver")));

		// Create Gold
		actions.remove(CardAction.IS_TREASURE);
		actions.put(CardAction.IS_TREASURE, "3");

		cards.put("Gold", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 6, "Gold", getImg("Gold")));

		// Create Estate
		actions.remove(CardAction.IS_TREASURE);
		types.remove(CardType.TREASURE);

		actions.put(CardAction.IS_VICTORY, "1");
		types.add(CardType.VICTORY);

		cards.put("Estate", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 2, "Estate", getImg("Estate")));

		// Duchy
		actions.remove(CardAction.IS_VICTORY);

		actions.put(CardAction.IS_VICTORY, "3");

		cards.put("Duchy", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, "Duchy", getImg("Duchy")));

		// Province
		actions.remove(CardAction.IS_VICTORY);

		actions.put(CardAction.IS_VICTORY, "6");

		cards.put("Province", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 8, "Province", getImg("Province")));
		
//		Cellar		
		actions.remove(CardAction.IS_VICTORY);
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "1");
		actions.put(CardAction.DISCARD_AND_DRAW, "-1");
		types.remove(CardType.VICTORY);
		types.add(CardType.ACTION);
		cards.put("Cellar", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(), 
				(LinkedList<CardType>) types.clone(), 2, "Cellar", getImg("Cellar")));
		
		

		// setup Dummy-DominionController
		DominionController dom = new DominionController(true);
		dom.setCredentials("testname", "test@test.test");

		// get valid session
		if (DEBUG)
			System.out.println("getting session...");
		SessionClient sess;

		if (REMOTE)
			sess = new SessionClient(new InetSocketAddress("78.31.66.224", 1337));
		else
			sess = new SessionClient(new InetSocketAddress("127.0.0.1", 1337));

		if (DEBUG)
			System.out.println("connected to session-servers");
		Semaphore halt = new Semaphore(1);
		halt.acquire();
		SessionPacketSenderAPI.sendGetRequest(sess, dom.getUsername(), new SuperCallable<PacketSessionGetAnswer>() {

			@Override
			public PacketSessionGetAnswer callMeMaybe(PacketSessionGetAnswer object) {
				dom.setSessionID(object.getLoginSessionID());
				halt.release();
				return null;
			}
		});
		halt.acquire();
		halt.release();
		if (DEBUG)
			System.out.println("got session!");

		if (!REMOTE) {
			// setup server-cache
			CardStorageController serverStorage = new CardStorageController("serverCards.bin");
			if (DEBUG)
				serverStorage.listCards();

			serverStorage.saveCards();

			// setup session-server
			new Thread(() -> {
				try {
					new SessionServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
			Thread.sleep(100);

			// start server
			new CardServer(new InetSocketAddress("127.0.0.1", 1336), new CardPacketHandlerServer(serverStorage, sess),
					serverStorage);
		}

		// start client
		CardPacketHandlerClient cHandler = new CardPacketHandlerClient();
		CardClient client;

		if (REMOTE)
			client = new CardClient(new InetSocketAddress("78.31.66.224", 1336), cHandler, false, dom);
		else
			client = new CardClient(new InetSocketAddress("127.0.0.1", 1336), cHandler, false, dom);

		cHandler.setCardClient(client);

		Semaphore sem = new Semaphore(1);
		if (doSend)
			for (SerializedCard sc : cards.values()) {
				// check-card-request, then add card to remote-storage

				sem.acquire();
				if (DEBUG)
					System.out.println("Working on " + sc.getName());
				client.askIfCardnameExists(sc.getName(), new SuperCallable<Boolean>() {

					@Override
					public Boolean callMeMaybe(Boolean object) {
						if (!object.booleanValue()) {
							if (DEBUG)
								System.out.println("Card was new! Adding...");
							client.addCardToRemoteStorage(sc);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							if (DEBUG)
								System.out.println("He already had that.");
						}
						sem.release();
						if (DEBUG)
							System.out.println("Done with " + sc.getName());
						return null;
					}
				});
			}
		sem.acquire();
		sem.release();

		// wait until the last card is saved on server
		Thread.sleep(100);

		// check if they are still there
		if (doSend)
			for (SerializedCard sc : cards.values()) {
				sem.acquire();
				client.askIfCardnameExists(sc.getName(), new SuperCallable<Boolean>() {
					@Override
					public Boolean callMeMaybe(Boolean object) {
						assertTrue(object.booleanValue());
						sem.release();
						return null;
					}

				});
			}
		sem.acquire();
		sem.release();
		dom.getCardRegistry().clearCards();

		// check if they are correct
		if (doCheck)
			for (SerializedCard sc : cards.values()) {
				sem.acquire();
				client.requestCardFromServer(sc.getName(), false);
				assertNotNull(dom.getCardRegistry().getCard(sc.getName()));
				assertEquals(sc, dom.getCardRegistry().getCard(sc.getName()));
				assertTrue(sc.equals(dom.getCardRegistry().getCard(sc.getName())));
				sem.release();
			}
		sem.acquire();
		sem.release();

		if (DEBUG)
			System.out.println("finished.");
		
		//Save Storage
		dom.getCardRegistry().saveCards();

	}

	private BufferedImage getImg(String name) throws IOException {
		return ImageIO.read(getClass().getClassLoader()
				.getResourceAsStream("resources/img/gameObjects/baseCards/" + name + ".png"));
	}

}
