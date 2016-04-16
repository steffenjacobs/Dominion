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

import javax.imageio.ImageIO;

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

/**
 * test the CardServer: client can send a check-card-name-request to the server,
 * server answers with a correct get-request - client can send an
 * add-card-packet to the server, server adds new card to it's storage - client
 * can send a get-card-request to the server, server returns the requested card
 * 
 * @author Steffen Jacobs
 */
public class JUnitCardServer {

	private final boolean DEBUG = true;

	/**
	 * main-entry-point
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void test() throws IOException, InterruptedException {
		// create test-card
		LinkedHashMap<CardAction, String> actions = new LinkedHashMap<>();
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "2");
		actions.put(CardAction.DRAW_CARD, "5");
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
		//dom.setCredentials("testname", "test@test.test");
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
		SessionClient sess = new SessionClient(
				new InetSocketAddress(Addresses.getRemoteAddress(), SessionServer.getStandardPort()));
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

		// start local card-server
		new CardServer(new InetSocketAddress(Addresses.getLocalHost(), CardServer.getStandardPort()),
				new CardPacketHandlerServer(serverStorage, sess), serverStorage);

		// start local card-client
		CardPacketHandlerClient cHandler = new CardPacketHandlerClient();
		CardClient client = new CardClient(
				new InetSocketAddress(Addresses.getLocalHost(), CardServer.getStandardPort()), cHandler, false, dom);
		cHandler.setCardClient(client);

		// check-card-request, then add card to remote-storage

		halt.acquire();
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
		client.askIfCardnameExists(sc.getName(), new SuperCallable<Boolean>() {

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

		// get card from server and store it automatically
		client.requestCardFromServer(sc.getName());

		// wait until card is back
		Thread.sleep(100);

		// check if card was stored properly
		SerializedCard sc2 = dom.getCardRegistry().getCard(sc.getName());
		assertNotNull(sc2);

		// check if card was sended over the network and back successfully
		assertEquals(sc, sc2);
		assertTrue(sc.equalsEntirely(sc2));

		// Save cards
		dom.getCardRegistry().saveCards();
	}
}