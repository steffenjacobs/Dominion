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

import org.junit.Test;

import com.tpps.application.game.CardName;
import com.tpps.application.game.DominionController;
import com.tpps.application.game.GameConstant;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
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
 * @author Steffen Jacobs
 *
 */
public class CheapCardCreator {
	private static final boolean DEBUG = true;
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

		cards.put(CardName.COPPER.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 0, CardName.COPPER.getName(), getImg(CardName.COPPER.getName())));

		// Create Silver
		actions.remove(CardAction.IS_TREASURE);
		actions.put(CardAction.IS_TREASURE, "2");

		cards.put(CardName.SILVER.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 3, CardName.SILVER.getName(), getImg(CardName.SILVER.getName())));

		// Create Gold
		actions.remove(CardAction.IS_TREASURE);
		actions.put(CardAction.IS_TREASURE, "3");

		cards.put(CardName.GOLD.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 6, CardName.GOLD.getName(), getImg(CardName.GOLD.getName())));

		// Create Estate
		actions.remove(CardAction.IS_TREASURE);
		types.remove(CardType.TREASURE);

		actions.put(CardAction.IS_VICTORY, "1");
		types.add(CardType.VICTORY);

		cards.put(CardName.ESTATE.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 2, CardName.ESTATE.getName(), getImg(CardName.ESTATE.getName())));

		// Duchy
		actions.remove(CardAction.IS_VICTORY);

		actions.put(CardAction.IS_VICTORY, "3");

		cards.put(CardName.DUCHY.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, CardName.DUCHY.getName(), getImg(CardName.DUCHY.getName())));

		// Province
		actions.remove(CardAction.IS_VICTORY);

		actions.put(CardAction.IS_VICTORY, "6");

		cards.put(CardName.PROVINCE.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 8, CardName.PROVINCE.getName(), getImg(CardName.PROVINCE.getName())));

		// Cellar
		actions.remove(CardAction.IS_VICTORY);
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "1");
		actions.put(CardAction.DISCARD_AND_DRAW, "-1");
		types.remove(CardType.VICTORY);
		types.add(CardType.ACTION);
		cards.put(CardName.CELLAR.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 2, CardName.CELLAR.getName(), getImg(CardName.CELLAR.getName())));
		// Chapel
		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.remove(CardAction.DISCARD_AND_DRAW);
		actions.put(CardAction.TRASH_CARD, "4");
		cards.put(CardName.CHAPEL.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 2, CardName.CHAPEL.getName(), getImg(CardName.CHAPEL.getName())));

		// Chancellor
		actions.remove(CardAction.TRASH_CARD);
		actions.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, "2");
		actions.put(CardAction.DISCARD_CARD, "Deck");
		cards.put(CardName.CHANCELLOR.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 3, CardName.CHANCELLOR.getName(), getImg(CardName.CHANCELLOR.getName())));

		// Militia
		actions.remove(CardAction.DISCARD_CARD);
		actions.put(CardAction.DISCARD_OTHER_DOWNTO, "3");
		types.add(CardType.ATTACK);
		cards.put(CardName.MILITIA.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, CardName.MILITIA.getName(), getImg(CardName.MILITIA.getName())));
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
		cards.put(CardName.MOAT.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 2, CardName.MOAT.getName(), getImg(CardName.MOAT.getName())));
		// actions DrawCard Seperator Defend
		// types action reaction

		// Village
		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.SEPERATOR);
		actions.remove(CardAction.DEFEND);
		types.remove(CardType.REACTION);
		actions.put(CardAction.DRAW_CARD, "1");
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "2");
		cards.put(CardName.VILLAGE.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 3, CardName.VILLAGE.getName(), getImg(CardName.VILLAGE.getName())));

		// woodcutter
		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.put(CardAction.ADD_PURCHASE, "1");
		actions.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, "2");
		cards.put(CardName.WOODCUTTER.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 3, CardName.WOODCUTTER.getName(), getImg(CardName.WOODCUTTER.getName())));

		// workshop
		actions.remove(CardAction.ADD_PURCHASE);
		actions.remove(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN);
		actions.put(CardAction.GAIN_CARD, "4");
		cards.put(CardName.WORKSHOP.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 3, CardName.WORKSHOP.getName(), getImg(CardName.WORKSHOP.getName())));

		// feast
		actions.remove(CardAction.GAIN_CARD);
		actions.put(CardAction.TRASH_CARD, "this");
		actions.put(CardAction.GAIN_CARD, "5");
		cards.put(CardName.FEAST.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, CardName.FEAST.getName(), getImg(CardName.FEAST.getName())));

		// moneylender
		actions.remove(CardAction.TRASH_CARD);
		actions.remove(CardAction.GAIN_CARD);
		actions.put(CardAction.TRASH_AND_ADD_TEMPORARY_MONEY, CardName.COPPER.getName());
		cards.put(CardName.MONEYLENDER.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, CardName.MONEYLENDER.getName(), getImg(CardName.MONEYLENDER.getName())));

		// remodel

		actions.remove(CardAction.TRASH_AND_ADD_TEMPORARY_MONEY);
		actions.put(CardAction.TRASH_AND_GAIN_MORE_THAN, "1_2");
		cards.put(CardName.REMODEL.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, CardName.REMODEL.getName(), getImg(CardName.REMODEL.getName())));

		// smithy

		actions.remove(CardAction.TRASH_AND_GAIN_MORE_THAN);
		actions.put(CardAction.DRAW_CARD, "3");
		cards.put(CardName.SMITHY.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, CardName.SMITHY.getName(), getImg(CardName.SMITHY.getName())));

		// spy

		actions.remove(CardAction.DRAW_CARD);
		actions.put(CardAction.DRAW_CARD, "1");
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "1");
		actions.put(CardAction.REVEAL_CARD, "NIL");
		cards.put(CardName.SPY.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, CardName.SPY.getName(), getImg(CardName.SPY.getName())));

		// actions Draw_card add_action_to_player reveal_card

		// throneRoom

		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.remove(CardAction.REVEAL_CARD);
		actions.put(CardAction.CHOOSE_CARD_PLAY_TWICE, "NIL");
		cards.put(CardName.THRONEROOM.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, CardName.THRONEROOM.getName(), getImg(CardName.THRONEROOM.getName())));

		// councilRoom

		actions.remove(CardAction.CHOOSE_CARD_PLAY_TWICE);
		actions.put(CardAction.DRAW_CARD, "4");
		actions.put(CardAction.ADD_PURCHASE, "1");
		actions.put(CardAction.DRAW_CARD_OTHERS, "1");
		cards.put(CardName.COUNCILROOM.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, CardName.COUNCILROOM.getName(), getImg(CardName.COUNCILROOM.getName())));

		// thief
		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.ADD_PURCHASE);
		actions.remove(CardAction.DRAW_CARD_OTHERS);
		actions.remove(CardAction.ALL_REVEAL_CARDS_TRASH_COINS_I_CAN_TAKE_DISCARD_OTHERS);
		types.add(CardType.ATTACK);
		cards.put(CardName.THIEF.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, CardName.THIEF.getName(), getImg(CardName.THIEF.getName())));

		// Festival
		actions.remove(CardAction.ALL_REVEAL_CARDS_TRASH_COINS_I_CAN_TAKE_DISCARD_OTHERS);
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "2");
		actions.put(CardAction.ADD_PURCHASE, "1");
		actions.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, "2");
		types.remove(CardType.ATTACK);

		cards.put(CardName.FESTIVAL.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, CardName.FESTIVAL.getName(), getImg(CardName.FESTIVAL.getName())));

		// Laboratory

		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.remove(CardAction.ADD_PURCHASE);
		actions.remove(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN);
		actions.put(CardAction.DRAW_CARD, "2");
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "1");

		cards.put(CardName.LABORATORY.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, CardName.LABORATORY.getName(), getImg(CardName.LABORATORY.getName())));

		// Library

		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.put(CardAction.DRAW_CARD_UNTIL, "7_action");

		cards.put(CardName.LIBRARY.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, CardName.LIBRARY.getName(), getImg(CardName.LIBRARY.getName())));

		// Market

		actions.remove(CardAction.DRAW_CARD_UNTIL);
		actions.put(CardAction.DRAW_CARD, "1");
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "1");
		actions.put(CardAction.ADD_PURCHASE, "1");
		actions.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, "1");
		cards.put(CardName.MARKET.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, CardName.MARKET.getName(), getImg(CardName.MARKET.getName())));

		// Mine
		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.ADD_ACTION_TO_PLAYER);
		actions.remove(CardAction.ADD_PURCHASE);
		actions.remove(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN);
		actions.put(CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND, "1_3");
		cards.put(CardName.MINE.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, CardName.MINE.getName(), getImg(CardName.MINE.getName())));

		// Witch
		actions.remove(CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND);
		actions.put(CardAction.DRAW_CARD, "2");
		actions.put(CardAction.GAIN_CARD_OTHERS, "curse");
		types.add(CardType.ATTACK);
		cards.put(CardName.WITCH.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 5, CardName.WITCH.getName(), getImg(CardName.WITCH.getName())));

		// Curse

		actions.remove(CardAction.DRAW_CARD);
		actions.remove(CardAction.GAIN_CARD_OTHERS);
		actions.put(CardAction.IS_VICTORY, Integer.toString(GameConstant.CURSE_VALUE.getValue()));
		types.remove(CardType.ACTION);
		types.remove(CardType.ATTACK);
		types.add(CardType.CURSE);
		cards.put(CardName.CURSE.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 0, CardName.CURSE.getName(), getImg(CardName.CURSE.getName())));

		//

		// Adventurer
		actions.remove(CardAction.IS_VICTORY);
		actions.put(CardAction.REVEAL_UNTIL_TREASURES, "2");
		types.remove(CardType.CURSE);
		types.add(CardType.ACTION);
		cards.put(CardName.ADVENTURER.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 0, CardName.ADVENTURER.getName(), getImg(CardName.ADVENTURER.getName())));

		// Bureaucrat

		actions.remove(CardAction.REVEAL_UNTIL_TREASURES);
		actions.put(CardAction.GAIN_CARD_DRAW_PILE, "silver");
		actions.put(CardAction.REVEAL_CARD_OTHERS_PUT_IT_ON_TOP_OF_DECK, "victory");
		types.add(CardType.ATTACK);
		cards.put(CardName.BUREAUCRAT.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 0, CardName.BUREAUCRAT.getName(), getImg(CardName.BUREAUCRAT.getName())));
		

		
		actions.remove(CardAction.GAIN_CARD_DRAW_PILE);
		actions.remove(CardAction.REVEAL_CARD_OTHERS_PUT_IT_ON_TOP_OF_DECK);
		types.remove(CardType.ATTACK);
		types.remove(CardType.ACTION);
		types.add(CardType.VICTORY);
		actions.put(CardAction.IS_VICTORY, Integer.toString(GameConstant.GARDEN_VALUE.getValue()));
		cards.put(CardName.GARDENS.getName(), new SerializedCard((LinkedHashMap<CardAction, String>) actions.clone(),
				(LinkedList<CardType>) types.clone(), 4, CardName.GARDENS.getName(), getImg(CardName.GARDENS.getName())));
		
		
		
		
		

		// setup Dummy-DominionController
		DominionController dom = DominionController.getInstance();
		dom.setUsername("testname");

		// get valid session
		if (DEBUG)
			GameLog.log(MsgType.INFO ,"getting session...");
		SessionClient sess;

		if (REMOTE)
			sess = new SessionClient(
					new InetSocketAddress(Addresses.getRemoteAddress(), SessionServer.getStandardPort()));
		else
			sess = new SessionClient(new InetSocketAddress(Addresses.getLocalHost(), SessionServer.getStandardPort()));

		if (DEBUG)
			GameLog.log(MsgType.INFO ,"connected to session-servers");
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
			GameLog.log(MsgType.INFO ,"got session!");

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
					GameLog.log(MsgType.INFO ,"Working on " + sc.getName());
				client.askIfCardnameExists(sc.getName(), new SuperCallable<Boolean>() {

					@Override
					public Boolean callMeMaybe(Boolean object) {
						if (!object.booleanValue()) {
							if (DEBUG)
								GameLog.log(MsgType.INFO ,"Card was new! Adding...");
							client.addCardToRemoteStorage(sc);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							if (DEBUG)
								GameLog.log(MsgType.INFO ,"He already had that.");
						}
						sem.release();
						if (DEBUG)
							GameLog.log(MsgType.INFO ,"Done with " + sc.getName());
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
			GameLog.log(MsgType.INFO ,"finished.");

		// Save Storage
		dom.getCardRegistry().saveCards();

	}

	private BufferedImage getImg(String name) throws IOException {
		return ImageLoader.getImage("resources/img/gameObjects/baseCards/" + name + ".png");
	}

}
