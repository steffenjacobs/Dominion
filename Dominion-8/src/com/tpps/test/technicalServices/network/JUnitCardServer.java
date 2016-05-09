package com.tpps.test.technicalServices.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tpps.application.game.DominionController;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.card.CardClient;
import com.tpps.technicalServices.network.card.CardPacketHandlerClient;
import com.tpps.technicalServices.network.card.CardPacketHandlerServer;
import com.tpps.technicalServices.network.card.CardServer;
import com.tpps.technicalServices.network.clientSession.client.SessionClient;
import com.tpps.technicalServices.network.clientSession.client.SessionPacketSenderAPI;
import com.tpps.technicalServices.network.clientSession.packets.PacketSessionGetAnswer;
import com.tpps.technicalServices.network.clientSession.server.SessionServer;
import com.tpps.technicalServices.network.core.SuperCallable;
import com.tpps.technicalServices.util.ImageLoader;

/**
 * test the CardServer: client can send a check-card-name-request to the server,
 * server answers with a correct get-request - client can send an
 * add-card-packet to the server, server adds new card to it's storage - client
 * can send a get-card-request to the server, server returns the requested card
 * 
 * @author Steffen Jacobs
 */
public class JUnitCardServer {

	private static final boolean DEBUG = false;
	private static SerializedCard testCard;

	private static CardStorageController serverStorage;

	private static SessionClient sess;
	private static DominionController dom;
	private static CardClient client;

	/** sets up the network 
	 * @throws InterruptedException 
	 * @throws IOException */
	@BeforeClass
	public static void setup() throws InterruptedException, IOException {
		createTestCard();
		clearCache();
		getValidSession();
		setupNetwork();
	}

	/** creates a test-card 
	 * @throws IOException */
	public static void createTestCard() throws IOException {
		System.out.println("Creating Test-Card...");
		// create test-card
		LinkedHashMap<CardAction, String> actions = new LinkedHashMap<>();
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "2");
		actions.put(CardAction.DRAW_CARD, "5");
		LinkedList<CardType> types = new LinkedList<>();
		types.add(CardType.CURSE);
		types.add(CardType.VICTORY);
		types.add(CardType.TREASURE);
		BufferedImage img = ImageLoader.getImage("resources/img/gameObjects/testButton.png");

		testCard = new SerializedCard(actions, types, 5, "☢TestCardäöü☢", img);
	}

	/** clears the cache */
	public static void clearCache() {
		System.out.println("Clearing Cache...");
		// clear cache
		serverStorage = new CardStorageController("serverCards.bin");
		serverStorage.clearCards();
		if (DEBUG)
			serverStorage.listCards();

		serverStorage.saveCards();
	}

	/** gets a valid sessionID 
	 * @throws InterruptedException 
	 * @throws IOException */
	public static void getValidSession() throws InterruptedException, IOException {
		System.out.println("Retrieving valid session...");
		// setup Dummy-DominionController
		dom = DominionController.getInstance();
		dom.setUsername("testname");

		// setup session-server
		new Thread(() -> {
			try {
				new SessionServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		Thread.sleep(100);

		// get valid session
		sess = new SessionClient(new InetSocketAddress(Addresses.getRemoteAddress(), SessionServer.getStandardPort()));
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
	}

	/** sets up the cardclient/cardserver connection 
	 * @throws IOException 
	 * @throws InterruptedException */
	public static void setupNetwork() throws IOException, InterruptedException {
		System.out.println("Setting up network...");

		// start local card-server
		new CardServer(new InetSocketAddress(Addresses.getLocalHost(), CardServer.getStandardPort()),
				new CardPacketHandlerServer(serverStorage, sess), serverStorage);

		// start local card-client
		CardPacketHandlerClient cHandler = new CardPacketHandlerClient();
		client = new CardClient(new InetSocketAddress(Addresses.getLocalHost(), CardServer.getStandardPort()), cHandler,
				false, dom);
		cHandler.setCardClient(client);

		assertNotNull(client);
	}

	/** pushes a card to the card-server 
	 * @throws IOException 
	 * @throws InterruptedException */
	@Test
	public void testCardPush() throws IOException, InterruptedException {
		System.out.println("Testing Card-Push...");
		Semaphore halt = new Semaphore(1);

		// check-card-request, then add card to remote-storage

		halt.acquire();

		client.askIfCardnameExists(testCard.getName(), new SuperCallable<Boolean>() {

			@Override
			public Boolean callMeMaybe(Boolean object) {
				assertFalse(object.booleanValue());
				if (!object.booleanValue()) {
					client.addCardToRemoteStorage(testCard);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (DEBUG)
					System.out.println("CHECK #1");
				halt.release();
				return null;
			}
		});

		halt.acquire();
		halt.release();

		// wait until card is saved on server
		Thread.sleep(100);

		// check if card is still there
		if (DEBUG)
			serverStorage.listCards();

		halt.acquire();
		client.askIfCardnameExists(testCard.getName(), new SuperCallable<Boolean>() {

			@Override
			public Boolean callMeMaybe(Boolean object) {
				assertNotNull(object);
				assertTrue(object.booleanValue());
				if (DEBUG)
					System.out.println("CHECK #2");
				halt.release();
				return null;
			}
		});

		halt.acquire();
		halt.release();
	}

	/**
	 * retrieves the pushed card from the server - ATTENTION: must be called
	 * after testCardPush
	 * @throws InterruptedException 
	 */
	@Test
	public void testCardRetrieve() throws InterruptedException {
		System.out.println("Testing Card-Retrieve");
		// get card from server and store it automatically
		client.requestCardFromServer(testCard.getName());

		// wait until card is back
		Thread.sleep(250);

		// check if card was stored properly
		SerializedCard sc2 = dom.getCardRegistry().getCard(testCard.getName());
		assertNotNull(sc2);

		// check if card was sended over the network and back successfully
		assertEquals(testCard, sc2);
		assertTrue(testCard.equalsEntirely(sc2));

		// Save cards
		dom.getCardRegistry().saveCards();
	}
}