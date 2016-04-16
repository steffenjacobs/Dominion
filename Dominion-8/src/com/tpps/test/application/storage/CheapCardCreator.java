package com.tpps.test.application.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import com.tpps.technicalServices.util.GameConstant;

/**
 * @author Steffen Jacobs
 *
 */
public class CheapCardCreator {
	private static final boolean DEBUG = false;
	private static final boolean REMOTE = true;
	private static final boolean doCheck = true;
	private static final boolean doSend = true;

	/**
	 * @throws IOException
	 * @throws InterruptedException
	 */
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

		// Cellar
		actions.remove(CardAction.IS_VICTORY);
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "1");
		actions.put(CardAction.DISCARD_AND_DRAW, "-1");
		types.remove(CardType.VICTORY);
		types.add(CardType.ACTION);
		cards.put("Cellar", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 2, "Cellar", getImg("Cellar")));
		// Chapel
		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.remove(CardAction.DISCARD_AND_DRAW);
		actions.put(CardAction.TRASH_CARD, "4");
		cards.put("Chapel", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 2, "Chapel", getImg("Chapel")));

		// Chancellor
		actions.remove(CardAction.TRASH_CARD);
		actions.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, "2");
		actions.put(CardAction.DISCARD_CARD, "Deck");
		cards.put("Chancellor", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 3, "Chancellor", getImg("Chancellor")));

		// Militia
		actions.remove(CardAction.DISCARD_CARD);
		actions.put(CardAction.DISCARD_OTHER_DOWNTO, "3");
		types.add(CardType.ATTACK);
		cards.put("Militia", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, "Militia", getImg("Militia")));
		// types Attack and Action
		// action Add_Temporary_Money discard other downto

		// Moat
		actions.remove(CardAction.DISCARD_OTHER_DOWNTO);
		actions.remove(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN);
		actions.put(CardAction.DRAW_CARD, "2");
		actions.put(CardAction.SEPERATOR, "NIL");
		actions.put(CardAction.DEFEND, "NIL");
		types.remove(CardType.ATTACK);
		types.add(CardType.REACTION);
		cards.put("Moat", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 2, "Moat", getImg("Moat")));
		// actions DrawCard Seperator Defend
		// types action reaction

		// Village
		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.SEPERATOR);
		actions.remove(CardAction.DEFEND);
		types.remove(CardType.REACTION);
		actions.put(CardAction.DRAW_CARD, "1");
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "2");
		cards.put("Village", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 3, "Village", getImg("Village")));

		// woodcutter
		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.put(CardAction.ADD_PURCHASE, "1");
		actions.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, "2");
		cards.put("Woodcutter", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 3, "Woodcutter", getImg("Woodcutter")));

		// workshop
		actions.remove(CardAction.ADD_PURCHASE);
		actions.remove(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN);
		actions.put(CardAction.GAIN_CARD, "4");
		cards.put("Workshop", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 3, "Workshop", getImg("Workshop")));

		// feast
		actions.remove(CardAction.GAIN_CARD);
		actions.put(CardAction.TRASH_CARD, "this");
		actions.put(CardAction.GAIN_CARD, "5");
		cards.put("Feast", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, "Feast", getImg("Feast")));

		// moneylender
		actions.remove(CardAction.TRASH_CARD);
		actions.remove(CardAction.GAIN_CARD);
		actions.put(CardAction.TRASH_AND_ADD_TEMPORARY_MONEY, "Copper");
		cards.put("Moneylender", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, "Moneylender", getImg("Moneylender")));

		// remodel

		actions.remove(CardAction.TRASH_AND_ADD_TEMPORARY_MONEY);
		actions.put(CardAction.TRASH_AND_GAIN_MORE_THAN, "1_2");
		cards.put("Remodel", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, "Remodel", getImg("Remodel")));

		// smithy

		actions.remove(CardAction.TRASH_AND_GAIN_MORE_THAN);
		actions.put(CardAction.DRAW_CARD, "3");
		cards.put("Smithy", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, "Smithy", getImg("Smithy")));

		// spy

		actions.remove(CardAction.DRAW_CARD);
		actions.put(CardAction.DRAW_CARD, "1");
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "1");
		actions.put(CardAction.REVEAL_CARD, "NIL");
		cards.put("Spy", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, "Spy", getImg("Spy")));

		// actions Draw_card add_action_to_player reveal_card

		// throneRoom

		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.remove(CardAction.REVEAL_CARD);
		actions.put(CardAction.CHOOSE_CARD_PLAY_TWICE, "NIL");
		cards.put("ThroneRoom", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, "ThroneRoom", getImg("ThroneRoom")));

		// councilRoom

		actions.remove(CardAction.CHOOSE_CARD_PLAY_TWICE);
		actions.put(CardAction.DRAW_CARD, "4");
		actions.put(CardAction.ADD_PURCHASE, "1");
		actions.put(CardAction.DRAW_CARD_OTHERS, "1");
		cards.put("CouncilRoom", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, "CouncilRoom", getImg("CouncilRoom")));

		// thief
		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.ADD_PURCHASE);
		actions.remove(CardAction.DRAW_CARD_OTHERS);
		actions.remove(CardAction.ALL_REVEAL_CARDS_TRASH_COINS_I_CAN_TAKE_DISCARD_OTHERS);
		types.add(CardType.ATTACK);
		cards.put("Thief", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, "Thief", getImg("Thief")));

		// Festival
		actions.remove(CardAction.ALL_REVEAL_CARDS_TRASH_COINS_I_CAN_TAKE_DISCARD_OTHERS);
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "2");
		actions.put(CardAction.ADD_PURCHASE, "1");
		actions.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, "2");
		types.remove(CardType.ATTACK);

		cards.put("Festival", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, "Festival", getImg("Festival")));

		// Laboratory

		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.remove(CardAction.ADD_PURCHASE);
		actions.remove(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN);
		actions.put(CardAction.DRAW_CARD, "2");
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "1");

		cards.put("Laboratory", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, "Laboratory", getImg("Laboratory")));

		// Library

		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.put(CardAction.DRAW_CARD_UNTIL, "7_action");

		cards.put("Library", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, "Library", getImg("Library")));

		// Market

		actions.remove(CardAction.DRAW_CARD_UNTIL);
		actions.put(CardAction.DRAW_CARD, "1");
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "1");
		actions.put(CardAction.ADD_PURCHASE, "1");
		actions.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, "1");
		cards.put("Market", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, "Market", getImg("Market")));

		// Mine
		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.remove(CardAction.ADD_PURCHASE);
		actions.remove(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN);
		actions.put(CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND, "1_3");
		cards.put("Mine", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, "Mine", getImg("Mine")));

		// Witch
		actions.remove(CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND);
		actions.put(CardAction.DRAW_CARD, "2");
		actions.put(CardAction.GAIN_CARD_OTHERS, "curse");
		types.add(CardType.ATTACK);
		cards.put("Witch", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, "Witch", getImg("Witch")));

		// Curse

		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.GAIN_CARD_OTHERS);
		actions.put(CardAction.IS_VICTORY, Integer.toString(GameConstant.CURSE_VALUE));
		types.remove(CardType.ACTION);
		types.remove(CardType.ATTACK);
		types.add(CardType.CURSE);
		cards.put("Curse", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 0, "Curse", getImg("Curse")));

		//

		// Adventurer
		actions.remove(CardAction.IS_VICTORY);
		actions.put(CardAction.REVEAL_UNTIL_TREASURES, "2");
		types.remove(CardType.CURSE);
		types.add(CardType.ACTION);
		cards.put("Adventurer", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 0, "Adventurer", getImg("Adventurer")));

		// Bureaucrat

		actions.remove(CardAction.REVEAL_UNTIL_TREASURES);
		actions.put(CardAction.GAIN_CARD_DRAW_PILE, "silver");
		actions.put(CardAction.REVEAL_CARD_OTHERS_PUT_IT_ON_TOP_OF_DECK, "victory");
		types.add(CardType.ATTACK);
		cards.put("Bureaucrat", new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 0, "Bureaucrat", getImg("Bureaucrat")));

		// setup Dummy-DominionController
		DominionController dom = new DominionController(true);
		dom.setCredentials("testname", "test@test.test");
	//	dom.setUsername("testname");

		// get valid session
		if (DEBUG)
			System.out.println("getting session...");
		SessionClient sess;

		if (REMOTE)
			sess = new SessionClient(
					new InetSocketAddress(Addresses.getRemoteAddress(), SessionServer.getStandardPort()));
		else
			sess = new SessionClient(new InetSocketAddress(Addresses.getLocalHost(), SessionServer.getStandardPort()));

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
			new CardServer(new InetSocketAddress(Addresses.getLocalHost(), 1336),
					new CardPacketHandlerServer(serverStorage, sess), serverStorage);
		}

		// start client
		CardPacketHandlerClient cHandler = new CardPacketHandlerClient();
		CardClient client;

		if (REMOTE)
			client = new CardClient(new InetSocketAddress(Addresses.getRemoteAddress(), CardServer.getStandardPort()),
					cHandler, false, dom);
		else
			client = new CardClient(new InetSocketAddress(Addresses.getLocalHost(), CardServer.getStandardPort()),
					cHandler, false, dom);

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

		// Save Storage
		dom.getCardRegistry().saveCards();

	}

	private BufferedImage getImg(String name) throws IOException {
		return ImageIO.read(getClass().getClassLoader()
				.getResourceAsStream("resources/img/gameObjects/baseCards/" + name + ".png"));
	}

}
