package com.tpps.application.game;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.game.SynchronisationException;
import com.tpps.technicalServices.network.game.WrongSyntaxException;
import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * @author Lukas Adler
 * @author Nicolas Wipfler
 */
public class GameBoard {

	private LinkedHashMap<String, LinkedList<Card>> tableForVictoryCards;
	private LinkedHashMap<String, LinkedList<Card>> tableForTreasureCards;
	private LinkedHashMap<String, LinkedList<Card>> tableForActionCards;
	private LinkedHashMap<String, LinkedList<Card>> tableForAllActionCards;
	private LinkedList<Card> trashPile;

	/**
	 * Constructor creates the tables for victory, treasure and action cards.
	 * initialises the linkedList for the trashPile calls the init method which
	 * calls all the init methods for the tables
	 */
	public GameBoard(String[] selectedActionCards) {
		this.tableForVictoryCards = new LinkedHashMap<String, LinkedList<Card>>();
		this.tableForTreasureCards = new LinkedHashMap<String, LinkedList<Card>>();
		this.tableForActionCards = new LinkedHashMap<String, LinkedList<Card>>();
		this.tableForAllActionCards = new LinkedHashMap<String, LinkedList<Card>>();
		this.trashPile = new LinkedList<Card>();
		init();
		// setAttackSet();
		setSet(selectedActionCards);
	}

	/**
	 * @return the tableForVictoryCards
	 */
	public synchronized LinkedHashMap<String, LinkedList<Card>> getTableForVictoryCards() {
		return tableForVictoryCards;
	}

	/**
	 * @param tableForVictoryCards
	 *            the tableForVictoryCards to set
	 */
	public synchronized void setTableForVictoryCards(LinkedHashMap<String, LinkedList<Card>> tableForVictoryCards) {
		this.tableForVictoryCards = tableForVictoryCards;
	}

	/**
	 * @return the tableForTreasureCards
	 */
	public synchronized LinkedHashMap<String, LinkedList<Card>> getTableForTreasureCards() {
		return tableForTreasureCards;
	}

	/**
	 * @param tableForTreasureCards
	 *            the tableForTreasureCards to set
	 */
	public synchronized void setTableForTreasureCards(LinkedHashMap<String, LinkedList<Card>> tableForTreasureCards) {
		this.tableForTreasureCards = tableForTreasureCards;
	}

	/**
	 * @return the tableForActionCards
	 */
	public synchronized LinkedHashMap<String, LinkedList<Card>> getTableForActionCards() {
		return tableForActionCards;
	}

	/**
	 * sets the referen for the tableActionCards on the given parameter
	 * 
	 * @param tableForActionCards
	 */
	public synchronized void setTableForActionCards(LinkedHashMap<String, LinkedList<Card>> tableForActionCards) {
		this.tableForActionCards = tableForActionCards;
	}

	/**
	 * @return the trashPile
	 */
	public synchronized LinkedList<Card> getTrashPile() {
		return trashPile;
	}

	/**
	 * sets the trashPile with the given parameter
	 * 
	 * @param trashPile
	 *            the trashPile to set
	 */
	public synchronized void setTrashPile(LinkedList<Card> trashPile) {
		this.trashPile = trashPile;
	}

	/**
	 * return the ids of the treasure Cards lying at the top of the
	 * treasureCards table
	 * @return a list of card all card IDs from tableForTreasureCards
	 */
	public synchronized LinkedList<String> getTreasureCardIDs() {
		return getCardIDs(this.tableForTreasureCards);
	}

	/**
	 * returns the ids of the victory Cards lying at the top of the victoryCards
	 * table
	 * @return a list of card all card IDs from tableForVictoryCards
	 */
	public synchronized LinkedList<String> getVictoryCardIDs() {
		return getCardIDs(this.tableForVictoryCards);
	}

	/**
	 * returns the ids of the action Cards lying at the top of the actionCards
	 * table
	 * @return a list of card all card IDs from tableForActionCards
	 */
	public synchronized LinkedList<String> getActionCardIDs() {
		return getCardIDs(this.tableForActionCards);
	}

	/**
	 * goes over all lists in the hashMap and takes the cardId of the last card
	 * of this list
	 * 
	 * @param table the table to check 
	 * @return return this cardIds
	 */
	public synchronized LinkedList<String> getCardIDs(LinkedHashMap<String, LinkedList<Card>> table) {
		Set<String> keys = table.keySet();
		LinkedList<String> cardIds = new LinkedList<String>();
		LinkedList<Card> cardList;
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			cardList = table.get(string);
			if (cardList.size() > 0) {
				cardIds.add(cardList.getLast().getId());
			} else {
				GameLog.log(MsgType.INFO ,"nil added");
				cardIds.add(string + "#");
			}
		}
		return cardIds;
	}

	/**
	 * initialises the tables for victory, treasure and action cards. for this
	 * purpose the table are filled with card
	 */
	private synchronized void init() {
		initHashMapTreasureCards();
		initHashMapVictoryCards();
		initHashMapAllActionCards();
	}

	/**
	 * initializes the tableForTreasureCards with 3 piles à 10 cards of Copper,
	 * Silver and Gold
	 */
	private synchronized void initHashMapTreasureCards() {
		LinkedList<Card> copperList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, Integer.toString(GameConstant.COPPER_VALUE.getValue())),
				CollectionsUtil.linkedList(CardType.TREASURE), CardName.COPPER.getName(), GameConstant.COPPER_COST.getValue()), 60, copperList);
		this.tableForTreasureCards.put(CardName.COPPER.getName(), copperList);
		Card.resetClassID();

		LinkedList<Card> silverList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, Integer.toString(GameConstant.SILVER_VALUE.getValue())),
				CollectionsUtil.linkedList(CardType.TREASURE), CardName.SILVER.getName(), GameConstant.SILVER_COST.getValue()), 40, silverList);
		this.tableForTreasureCards.put(CardName.SILVER.getName(), silverList);
		Card.resetClassID();

		LinkedList<Card> goldList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, Integer.toString(GameConstant.GOLD_VALUE.getValue())), CollectionsUtil.linkedList(CardType.TREASURE),
				CardName.GOLD.getName(), GameConstant.GOLD_COST.getValue()), 30, goldList);
		this.tableForTreasureCards.put(CardName.GOLD.getName(), goldList);
		Card.resetClassID();

	}

	/**
	 * initializes the tableForVictoryCards with 4 piles à 10 cards of Estate,
	 * Duchy, Province and curse
	 */
	private synchronized void initHashMapVictoryCards() {
		LinkedList<Card> estateList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, Integer.toString(GameConstant.ESTATE_VALUE.getValue())), CollectionsUtil.linkedList(CardType.VICTORY),
				CardName.ESTATE.getName(), GameConstant.ESTATE_COST.getValue()), 40, estateList);
		this.tableForVictoryCards.put(CardName.ESTATE.getName(), estateList);
		Card.resetClassID();

		LinkedList<Card> duchyList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, Integer.toString(GameConstant.DUCHY_VALUE.getValue())), CollectionsUtil.linkedList(CardType.VICTORY),
				CardName.DUCHY.getName(), GameConstant.DUCHY_COST.getValue()), GameConstant.INIT_DUCHY_PILE_SIZE.getValue(), duchyList);
		this.tableForVictoryCards.put(CardName.DUCHY.getName(), duchyList);
		Card.resetClassID();

		LinkedList<Card> provinceList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, Integer.toString(GameConstant.PROVINCE_VALUE.getValue())),
				CollectionsUtil.linkedList(CardType.VICTORY), CardName.PROVINCE.getName(), GameConstant.PROVINCE_COST.getValue()), GameConstant.INIT_PROVINCE_PILE_SIZE.getValue(), provinceList);
		this.tableForVictoryCards.put(CardName.PROVINCE.getName(), provinceList);
		Card.resetClassID();

		LinkedList<Card> curseList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, Integer.toString(GameConstant.CURSE_VALUE.getValue())), CollectionsUtil.linkedList(CardType.CURSE),
				CardName.CURSE.getName(), GameConstant.CURSE_COST.getValue()), GameConstant.INIT_CURSE_PILE_SIZE.getValue(), curseList);
		this.tableForVictoryCards.put(CardName.CURSE.getName(), curseList);
		Card.resetClassID();

	}

	/**
	 * initializes the tableForVictoryCards with 10 piles à 10 cards of action
	 * cards. this will change every game
	 * 
	 * 
	 * ------ USER CHOOSES CARDS TO PLAY WITH -------
	 */
	private synchronized void initHashMapAllActionCards() {
		// 1
		LinkedList<Card> cellarList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.ADD_ACTION_TO_PLAYER, CardAction.DISCARD_AND_DRAW }),
				CollectionsUtil.linkedList(new String[] { "1", "-1" })), CollectionsUtil.linkedList(CardType.ACTION), CardName.CELLAR.getName(), 2), 10, cellarList);
		this.tableForAllActionCards.put(CardName.CELLAR.getName(), cellarList);
		Card.resetClassID();

		LinkedList<Card> chapelList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.TRASH_CARD, "4"), CollectionsUtil.linkedList(CardType.ACTION), CardName.CHAPEL.getName(), 2), 10, chapelList);
		this.tableForAllActionCards.put(CardName.CHAPEL.getName(), chapelList);
		Card.resetClassID();

		// // 2
		LinkedList<Card> chancellorList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, CardAction.DISCARD_CARD }),
								CollectionsUtil.linkedList(new String[] { "2", "Deck" })), CollectionsUtil.linkedList(CardType.ACTION), CardName.CHANCELLOR.getName(), 3),
						GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), chancellorList);
		this.tableForAllActionCards.put(CardName.CHANCELLOR.getName(), chancellorList);
		Card.resetClassID();

		LinkedList<Card> militiaList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, CardAction.DISCARD_OTHER_DOWNTO }),
						CollectionsUtil.linkedList(new String[] { "2", "3" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION, CardType.ATTACK }), CardName.MILITIA.getName(), 0),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), militiaList);
		this.tableForAllActionCards.put(CardName.MILITIA.getName(), militiaList);
		Card.resetClassID();

		// moat
		LinkedList<Card> moatList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD, CardAction.SEPERATOR, CardAction.DEFEND }),
						CollectionsUtil.linkedList(new String[] { "2", "NIL", "NIL" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION, CardType.REACTION }), CardName.MOAT.getName(), 2),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), moatList);
		this.tableForAllActionCards.put(CardName.MOAT.getName(), moatList);
		Card.resetClassID();

		// village
		LinkedList<Card> villageList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER }),
						CollectionsUtil.linkedList(new String[] { "1", "2" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.VILLAGE.getName(), 3),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), villageList);
		this.tableForAllActionCards.put(CardName.VILLAGE.getName(), villageList);
		Card.resetClassID();

		// woodcutter
		LinkedList<Card> woodCutterList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.ADD_PURCHASE, CardAction.ADD_TEMPORARY_MONEY_FOR_TURN }),
						CollectionsUtil.linkedList(new String[] { "1", "2" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.WOODCUTTER.getName(), 3),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), woodCutterList);
		this.tableForAllActionCards.put(CardName.WOODCUTTER.getName(), woodCutterList);
		Card.resetClassID();

		// workshop
		LinkedList<Card> workShopList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.GAIN_CARD, "4"), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.WORKSHOP.getName(), 3),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), workShopList);
		this.tableForAllActionCards.put(CardName.WORKSHOP.getName(), workShopList);
		Card.resetClassID();

		// bureaucrat
		LinkedList<Card> bureaucratList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.GAIN_CARD_DRAW_PILE, CardAction.REVEAL_CARD_OTHERS_PUT_IT_ON_TOP_OF_DECK }),
						CollectionsUtil.linkedList(new String[] { "silver", "victory" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION, CardType.ATTACK }), CardName.BUREAUCRAT.getName(), 0),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), bureaucratList);
		this.tableForAllActionCards.put(CardName.BUREAUCRAT.getName(), bureaucratList);
		Card.resetClassID();

		// feast
		LinkedList<Card> feastList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.TRASH_CARD, CardAction.GAIN_CARD }),
						CollectionsUtil.linkedList(new String[] { "this", "5" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.FEAST.getName(), 0),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), feastList);
		this.tableForAllActionCards.put(CardName.FEAST.getName(), feastList);
		Card.resetClassID();

		// moneyLender
		LinkedList<Card> moneylenderList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.TRASH_AND_ADD_TEMPORARY_MONEY }),
						CollectionsUtil.linkedList(new String[] { "Copper_3" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.MONEYLENDER.getName(), 0),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), moneylenderList);
		this.tableForAllActionCards.put(CardName.MONEYLENDER.getName(), moneylenderList);
		Card.resetClassID();

		// remodel
		LinkedList<Card> remodelList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.TRASH_AND_GAIN_MORE_THAN }),
								CollectionsUtil.linkedList(new String[] { "1_2" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.REMODEL.getName(), 0),
						GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), remodelList);
		this.tableForAllActionCards.put(CardName.REMODEL.getName(), remodelList);
		Card.resetClassID();

		// smithy
		LinkedList<Card> smithyList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD }), CollectionsUtil.linkedList(new String[] { "3" })),
						CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.SMITHY.getName(), 0), GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), smithyList);
		this.tableForAllActionCards.put(CardName.SMITHY.getName(), smithyList);
		Card.resetClassID();

		// spy
		LinkedList<Card> spyList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER, CardAction.REVEAL_CARD_ALL }),
						CollectionsUtil.linkedList(new String[] { "1", "1", "NIL" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.SPY.getName(), 0),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), spyList);
		this.tableForAllActionCards.put(CardName.SPY.getName(), spyList);
		Card.resetClassID();

		LinkedList<Card> thiefList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.ALL_REVEAL_CARDS_TRASH_COINS_I_CAN_TAKE_DISCARD_OTHERS }),
						CollectionsUtil.linkedList(new String[] { "2" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION, CardType.ATTACK }), CardName.THIEF.getName(), 0),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), thiefList);
		this.tableForAllActionCards.put(CardName.THIEF.getName(), thiefList);
		Card.resetClassID();

		// ThroneRoom

		LinkedList<Card> throneRoomList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.CHOOSE_CARD_PLAY_TWICE }),
								CollectionsUtil.linkedList(new String[] { "Nil" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.THRONEROOM.getName(), 0),
						GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), throneRoomList);
		this.tableForAllActionCards.put(CardName.THRONEROOM.getName(), throneRoomList);
		Card.resetClassID();

		// CouncilRoom

		LinkedList<Card> councilRoomList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD, CardAction.ADD_PURCHASE, CardAction.DRAW_CARD_OTHERS }),
						CollectionsUtil.linkedList(new String[] { "4", "1", "1" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.COUNCILROOM.getName(), 0),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), councilRoomList);
		this.tableForAllActionCards.put(CardName.COUNCILROOM.getName(), councilRoomList);
		Card.resetClassID();

		LinkedList<Card> festivalList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(
						CollectionsUtil.linkedList(new CardAction[] { CardAction.ADD_ACTION_TO_PLAYER, CardAction.ADD_PURCHASE, CardAction.ADD_TEMPORARY_MONEY_FOR_TURN }),
						CollectionsUtil.linkedList(new String[] { "2", "1", "2" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.FESTIVAL.getName(), 0),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), festivalList);
		this.tableForAllActionCards.put(CardName.FESTIVAL.getName(), festivalList);
		Card.resetClassID();

		// Laboratory
		LinkedList<Card> laboratoryList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER }),
						CollectionsUtil.linkedList(new String[] { "2", "1" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.LABORATORY.getName(), 0),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), laboratoryList);
		this.tableForAllActionCards.put(CardName.LABORATORY.getName(), laboratoryList);
		Card.resetClassID();

		// Library
		LinkedList<Card> libraryList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD_UNTIL }),
								CollectionsUtil.linkedList(new String[] { "7_action" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.LIBRARY.getName(), 0),
						GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), libraryList);
		this.tableForAllActionCards.put(CardName.LIBRARY.getName(), libraryList);
		Card.resetClassID();

		// Market
		LinkedList<Card> marketList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(CollectionsUtil.linkedHashMapAction(
								CollectionsUtil
										.linkedList(new CardAction[] { CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER, CardAction.ADD_PURCHASE, CardAction.ADD_TEMPORARY_MONEY_FOR_TURN }),
								CollectionsUtil.linkedList(new String[] { "1", "1", "1", "1" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.MARKET.getName(), 0),
						GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), marketList);
		this.tableForAllActionCards.put(CardName.MARKET.getName(), marketList);
		Card.resetClassID();

		// Mine
		LinkedList<Card> mineList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND }),
								CollectionsUtil.linkedList(new String[] { "1_3" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.MINE.getName(), 0),
						GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), mineList);
		this.tableForAllActionCards.put(CardName.MINE.getName(), mineList);
		Card.resetClassID();

		// Witch
		LinkedList<Card> witchList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD, CardAction.GAIN_CARD_OTHERS }),
						CollectionsUtil.linkedList(new String[] { "2", "curse" })), CollectionsUtil.linkedList(new CardType[] { CardType.ACTION }), CardName.WITCH.getName(), 0),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), witchList);
		this.tableForAllActionCards.put(CardName.WITCH.getName(), witchList);
		Card.resetClassID();

		// Adventurer
		LinkedList<Card> adventurerList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.REVEAL_UNTIL_TREASURES, "2"), CollectionsUtil.linkedList(CardType.ACTION), CardName.ADVENTURER.getName(), 0),
				GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue(), adventurerList);
		this.tableForAllActionCards.put(CardName.ADVENTURER.getName(), adventurerList);
		Card.resetClassID();
	}

	public synchronized void setRandomSet() {
		this.tableForActionCards = new LinkedHashMap<String, LinkedList<Card>>();
		LinkedHashMap<String, LinkedList<Card>> test = new LinkedHashMap<String, LinkedList<Card>>(this.tableForAllActionCards);

		LinkedList<LinkedList<Card>> values = new LinkedList<LinkedList<Card>>(test.values());
		LinkedList<String> keys = new LinkedList<String>(test.keySet());

		while (this.tableForActionCards.size() < 10) {
			int index = (int) (Math.random() * values.size());
			this.tableForActionCards.put(keys.remove(index), new LinkedList<Card>(values.remove(index)));
		}
	}

	public synchronized void setStandardSet() {
		this.tableForActionCards = new LinkedHashMap<String, LinkedList<Card>>();
		this.tableForActionCards.put(CardName.MOAT.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.MOAT.getName())));
		this.tableForActionCards.put(CardName.CELLAR.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.CELLAR.getName())));
		this.tableForActionCards.put(CardName.CHAPEL.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.CHAPEL.getName())));
		this.tableForActionCards.put(CardName.MILITIA.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.MILITIA.getName())));
		this.tableForActionCards.put(CardName.FEAST.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.FEAST.getName())));
		this.tableForActionCards.put(CardName.MONEYLENDER.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.MONEYLENDER.getName())));
		this.tableForActionCards.put(CardName.THRONEROOM.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.THRONEROOM.getName())));
		this.tableForActionCards.put(CardName.MINE.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.MINE.getName())));
		this.tableForActionCards.put(CardName.LIBRARY.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.LIBRARY.getName())));
		this.tableForActionCards.put(CardName.REMODEL.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.REMODEL.getName())));
	}

	public synchronized void setAttackSet() {
		this.tableForActionCards = new LinkedHashMap<String, LinkedList<Card>>();
		this.tableForActionCards.put(CardName.MOAT.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.MOAT.getName())));
		this.tableForActionCards.put(CardName.MILITIA.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.MILITIA.getName())));
		this.tableForActionCards.put(CardName.SPY.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.SPY.getName())));
		this.tableForActionCards.put(CardName.THIEF.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.THIEF.getName())));
		this.tableForActionCards.put(CardName.WITCH.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.WITCH.getName())));
		this.tableForActionCards.put(CardName.BUREAUCRAT.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.BUREAUCRAT.getName())));
		this.tableForActionCards.put(CardName.ADVENTURER.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.ADVENTURER.getName())));
		this.tableForActionCards.put(CardName.COUNCILROOM.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.COUNCILROOM.getName())));
		this.tableForActionCards.put(CardName.CHANCELLOR.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.CHANCELLOR.getName())));
		this.tableForActionCards.put(CardName.THRONEROOM.getName(), new LinkedList<Card>(this.tableForAllActionCards.get(CardName.THRONEROOM.getName())));
	}

	private void setSet(String[] selectedActionCards) {
		this.tableForActionCards = new LinkedHashMap<String, LinkedList<Card>>();
		for (int i = 0; i < selectedActionCards.length; i++) {
			String string = selectedActionCards[i];
			this.tableForActionCards.put(string, new LinkedList<Card>(this.tableForAllActionCards.get(string)));
		}
	}

	/**
	 * 
	 * @return a list of cards which are the initial cards for the player at the
	 *         beginning of the game
	 */
	public synchronized LinkedList<Card> getStartSet() {
		LinkedList<Card> startCards = new LinkedList<Card>();
		LinkedList<Card> copperList = this.tableForTreasureCards.get(CardName.COPPER.getName());

		for (int i = 0; i < GameConstant.INIT_COPPER_CARDS_ON_HAND.getValue(); i++) {
			startCards.addFirst(copperList.removeLast());

		}

		LinkedList<Card> estateList = this.tableForVictoryCards.get(CardName.ESTATE.getName());
		for (int i = 0; i < GameConstant.INIT_ESTATE_CARDS_ON_HAND.getValue(); i++) {
			startCards.add(estateList.removeLast());
		}
		return startCards;
	}

	/**
	 * @param cardId
	 * @return the card specified by the param cardId
	 * @throws synchronisationException
	 *             when the card is not on the board
	 */
	protected synchronized Card findAndRemoveCardFromBoard(String cardId) throws SynchronisationException, WrongSyntaxException {
		Matcher matcher = Pattern.compile("\\d+").matcher(cardId);

		if (matcher.find()) {

			String key = cardId.substring(0, matcher.start());
			if (this.tableForTreasureCards.containsKey(key) || this.tableForVictoryCards.containsKey(key) || this.tableForActionCards.containsKey(key)) {

				LinkedList<Card> cardList = findCardListFromBoard(cardId);
				return cardList.removeLast();
			} else {
				throw new SynchronisationException();
			}
		} else {
			throw new WrongSyntaxException();
		}
	}

	/**
	 * 
	 * @param cardId
	 * @return a list of which cards which contains the card with the given
	 *         parameter cardId
	 * @throws SynchronisationException
	 *             if the card was not found on the board
	 */
	public synchronized LinkedList<Card> findCardListFromBoard(String cardId) throws SynchronisationException, NoSuchElementException, WrongSyntaxException {
		Matcher matcher = Pattern.compile("\\d+").matcher(cardId);
		if (matcher.find()) {
			String key = cardId.substring(0, matcher.start());
			LinkedList<Card> cardList;
			if (this.tableForTreasureCards.containsKey(key)) {
				cardList = this.tableForTreasureCards.get(key);
			} else if (this.tableForVictoryCards.containsKey(key)) {
				cardList = this.tableForVictoryCards.get(key);
			} else if (this.tableForActionCards.containsKey(key)) {
				cardList = this.tableForActionCards.get(key);
			} else {
				throw new SynchronisationException();
			}
			if (!cardList.isEmpty() && cardList.getLast().getId().equals(cardId)) {
				return cardList;
			} else {
				throw new SynchronisationException();
			}
		} else {
			throw new WrongSyntaxException();
		}
	}

	/**
	 * 
	 * @return true if three piles(lists) are empty in all hashMaps
	 */
	public boolean checkThreePilesEmpty() {
		return amountOfPilesEmpty() == GameConstant.EMPTY_PILES.getValue();
	}

	/**
	 * 
	 * @return amount of empty Piles
	 */
	public int amountOfPilesEmpty() {
		return amountOfPilesEmptyForTable(this.tableForActionCards) + amountOfPilesEmptyForTable(this.tableForTreasureCards) + amountOfPilesEmptyForTable(this.tableForVictoryCards);
	}

	/**
	 * 
	 * @param table
	 * @return the number of empty piles(lists) in the given hashMap
	 */
	public int amountOfPilesEmptyForTable(LinkedHashMap<String, LinkedList<Card>> table) {
		int counter = 0;
		LinkedList<String> keys = new LinkedList<String>(table.keySet());
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			if (table.get(key).isEmpty()) {
				counter++;
				if (counter == GameConstant.EMPTY_PILES.getValue()) {
					return counter;
				}
			}
		}
		return counter;
	}

	public Card getCardToBuyFromBoardWithName(String cardname) {
		if (this.tableForActionCards.get(cardname) != null)
			return this.tableForActionCards.get(cardname).getLast();
		if (this.tableForVictoryCards.get(cardname) != null)
			return this.tableForVictoryCards.get(cardname).getLast();
		if (this.tableForTreasureCards.get(cardname) != null)
			return this.tableForTreasureCards.get(cardname).getLast();
		return null;
	}
}
