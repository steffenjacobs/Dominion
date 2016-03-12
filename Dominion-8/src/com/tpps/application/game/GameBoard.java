package com.tpps.application.game;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.network.game.SynchronisationException;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/**
 * @author Lukas Adler
 * @author Nicolas Wipfler
 */
public class GameBoard {

	private LinkedHashMap<String, LinkedList<Card>> tableForVictoryCards;
	private LinkedHashMap<String, LinkedList<Card>> tableForTreasureCards;
	private LinkedHashMap<String, LinkedList<Card>> tableForActionCards;
	private LinkedList<Card> trashPile;

	/**
	 * 
	 */
	public GameBoard() {
		this.tableForVictoryCards = new LinkedHashMap<String, LinkedList<Card>>();
		this.tableForTreasureCards = new LinkedHashMap<String, LinkedList<Card>>();
		this.tableForActionCards = new LinkedHashMap<String, LinkedList<Card>>();
		this.trashPile = new LinkedList<Card>();
		init();
	}

	/**
	 * @return the tableForVictoryCards
	 */
	public LinkedHashMap<String, LinkedList<Card>> getTableForVictoryCards() {
		return tableForVictoryCards;
	}

	/**
	 * @param tableForVictoryCards the tableForVictoryCards to set
	 */
	public void setTableForVictoryCards(LinkedHashMap<String, LinkedList<Card>> tableForVictoryCards) {
		this.tableForVictoryCards = tableForVictoryCards;
	}

	/**
	 * @return the tableForTreasureCards
	 */
	public LinkedHashMap<String, LinkedList<Card>> getTableForTreasureCards() {
		return tableForTreasureCards;
	}

	/**
	 * @param tableForTreasureCards the tableForTreasureCards to set
	 */
	public void setTableForTreasureCards(LinkedHashMap<String, LinkedList<Card>> tableForTreasureCards) {
		this.tableForTreasureCards = tableForTreasureCards;
	}

	/**
	 * @return the tableForActionCards
	 */
	public LinkedHashMap<String, LinkedList<Card>> getTableForActionCards() {
		return tableForActionCards;
	}

	/**
	 * @param tableForActionCards the tableForActionCards to set
	 */
	public void setTableForActionCards(LinkedHashMap<String, LinkedList<Card>> tableForActionCards) {
		this.tableForActionCards = tableForActionCards;
	}

	/**
	 * @return the trashPile
	 */
	public LinkedList<Card> getTrashPile() {
		return trashPile;
	}

	/**
	 * @param trashPile the trashPile to set
	 */
	public void setTrashPile(LinkedList<Card> trashPile) {
		this.trashPile = trashPile;
	}

	/**
	 * return the ids of the treasure Cards lying at the top of the treasureCards table
	 */
	public LinkedList<String> getTreasureCardIDs() {
		return getCardIDs(this.tableForTreasureCards);
	}

	/**
	 * returns the ids of the victory Cards lying at the top of the victoryCards table
	 */
	public LinkedList<String> getVictoryCardIDs() {
		return getCardIDs(this.tableForVictoryCards);
	}

	/**
	 * returns the ids of the action Cards lying at the top of the actionCards table
	 */
	public LinkedList<String> getActionCardIDs() {
		return getCardIDs(this.tableForActionCards);
	}
	

	/**
	 * returns the ids of the cards selected through the key of the hashMap lying at the end of the list
	 */
	public LinkedList<String> getCardIDs(LinkedHashMap<String, LinkedList<Card>> table) {
		Set<String> keys = table.keySet();
		LinkedList<String> cardIds = new LinkedList<String>();
		LinkedList<Card> cardList;
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			cardList = table.get(string);
			if (cardList.size() > 0){
				cardIds.add(cardList.get(cardList.size() - 1).getId());
			} else {
				cardIds.add("NIL");
			}
		}
		return cardIds;
	}
	
	/**
	 * 
	 */
	private void init() {
		initHashMapTreasureCards();
		initHashMapVictoryCards();
		initHashMapActionCards();
	}

	/**
	 * initializes the tableForTreasureCards with 3 piles à 10 cards of Copper, Silver and Gold
	 */
	private void initHashMapTreasureCards() {
		LinkedList<Card> copperList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, Integer.toString(GameConstant.COPPER_VALUE)), CollectionsUtil.linkedList(CardType.TREASURE), "Copper", GameConstant.COPPER_COST), 60, copperList);
		this.tableForTreasureCards.put("Copper", copperList);
		Card.resetClassID();

		LinkedList<Card> silverList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, Integer.toString(GameConstant.SILVER_VALUE)), CollectionsUtil.linkedList(CardType.TREASURE), "Silver", GameConstant.SILVER_COST), 40, silverList);
		this.tableForTreasureCards.put("Silver", silverList);
		Card.resetClassID();

		LinkedList<Card> goldList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, Integer.toString(GameConstant.GOLD_VALUE)), CollectionsUtil.linkedList(CardType.TREASURE), "Gold", GameConstant.GOLD_COST), 30, goldList);
		this.tableForTreasureCards.put("Gold", goldList);
		Card.resetClassID();
		
	}

	/**
	 * initializes the tableForVictoryCards with 3 piles à 10 cards of Estate, Duchy and Province
	 */
	private void initHashMapVictoryCards() {
		LinkedList<Card> estateList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, Integer.toString(GameConstant.ESTATE_VALUE)), CollectionsUtil.linkedList(CardType.VICTORY), "Estate", GameConstant.ESTATE_COST), 40, estateList);
		this.tableForVictoryCards.put("Estate", estateList);
		Card.resetClassID();

		LinkedList<Card> duchyList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, Integer.toString(GameConstant.DUCHY_VALUE)), CollectionsUtil.linkedList(CardType.VICTORY), "Duchy", GameConstant.DUCHY_COST), GameConstant.INIT_PILE_SIZE, duchyList);
		this.tableForVictoryCards.put("Duchy", duchyList);
		Card.resetClassID();

		LinkedList<Card> provinceList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, Integer.toString(GameConstant.PROVINCE_VALUE)), CollectionsUtil.linkedList(CardType.VICTORY), "Province", GameConstant.PROVINCE_COST), GameConstant.INIT_PILE_SIZE, provinceList);
		this.tableForVictoryCards.put("Province", provinceList);
		Card.resetClassID();
	}

	/**
	 * initializes the tableForVictoryCards with 10 piles à 10 cards of action cards.
	 * this will change every game
	 * 
	 * 
	 * ------ USER CHOOSES CARDS TO PLAY WITH -------
	 */
	private void initHashMapActionCards() {
		LinkedList<Card> copperList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, Integer.toString(GameConstant.COPPER_VALUE)), CollectionsUtil.linkedList(CardType.TREASURE), "Copper", GameConstant.COPPER_COST), GameConstant.INIT_PILE_SIZE, copperList);
		this.tableForActionCards.put("Copper", copperList);
		Card.resetClassID();

		LinkedList<Card> silverList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, Integer.toString(GameConstant.SILVER_VALUE)), CollectionsUtil.linkedList(CardType.TREASURE), "Silver", GameConstant.SILVER_COST), GameConstant.INIT_PILE_SIZE, silverList);
		this.tableForActionCards.put("Silver", silverList);
		Card.resetClassID();

		LinkedList<Card> goldList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, Integer.toString(GameConstant.GOLD_VALUE)), CollectionsUtil.linkedList(CardType.TREASURE), "Gold", GameConstant.GOLD_COST), GameConstant.INIT_PILE_SIZE, goldList);
		this.tableForActionCards.put("Gold", goldList);
		Card.resetClassID();
		
		LinkedList<Card> estateList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, Integer.toString(GameConstant.ESTATE_VALUE)), CollectionsUtil.linkedList(CardType.VICTORY), "Estate", GameConstant.ESTATE_COST), GameConstant.INIT_PILE_SIZE, estateList);
		this.tableForActionCards.put("Estate", estateList);
		Card.resetClassID();

		LinkedList<Card> duchyList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, Integer.toString(GameConstant.DUCHY_VALUE)), CollectionsUtil.linkedList(CardType.VICTORY), "Duchy", GameConstant.DUCHY_COST), GameConstant.INIT_PILE_SIZE, duchyList);
		this.tableForActionCards.put("Duchy", duchyList);
		Card.resetClassID();

		LinkedList<Card> provinceList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, Integer.toString(GameConstant.PROVINCE_VALUE)), CollectionsUtil.linkedList(CardType.VICTORY), "Province", GameConstant.PROVINCE_COST), GameConstant.INIT_PILE_SIZE, provinceList);
		this.tableForActionCards.put("Province", provinceList);
		Card.resetClassID();
		
		// 1
		LinkedList<Card> cellarList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[]{CardAction.ADD_ACTION_TO_PLAYER, CardAction.DISCARD_AND_DRAW}), CollectionsUtil.linkedList(new String[]{"1", "-1"})), 
				CollectionsUtil.linkedList(CardType.ACTION), "Cellar", 2), 10, cellarList);
		this.tableForActionCards.put("Cellar", cellarList);
		Card.resetClassID();
		
		LinkedList<Card> chapelList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.TRASH_CARD, "4"), 
				CollectionsUtil.linkedList(CardType.ACTION), "Chapel", 2), 10, chapelList);
		this.tableForActionCards.put("Chapel", chapelList);
		Card.resetClassID();
//		
//		// 2
		LinkedList<Card> chancellorList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, CardAction.DISCARD_CARD}), 
				CollectionsUtil.linkedList(new String[] {"2", "Deck"})),CollectionsUtil.linkedList(CardType.ACTION), "Chancellor", 3), GameConstant.INIT_PILE_SIZE, chancellorList);
		this.tableForActionCards.put("Chancellor", chancellorList);
		Card.resetClassID();
//		
//		// 3
//		LinkedList<Card> smithyList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.DRAW_CARD, "3"), CollectionsUtil.linkedList(CardType.ACTION), "Smithy", 4), GameConstant.INIT_PILE_SIZE, smithyList);
//		this.tableForActionCards.put("Smithy", smithyList);
//		Card.resetClassID();
//		
//		// 4
//		LinkedList<Card> woodCutterList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER }), CollectionsUtil.linkedList(new String[] {"1", "2"})),CollectionsUtil.linkedList(CardType.ACTION), "Woodcutter", 3), GameConstant.INIT_PILE_SIZE, woodCutterList);
//		this.tableForActionCards.put("Woodcutter", woodCutterList);
//		Card.resetClassID();
//		
//		// 5
//		cellarList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, "1"), CollectionsUtil.linkedList(CardType.ACTION), "Test1", 2), GameConstant.INIT_PILE_SIZE, cellarList);
//		this.tableForActionCards.put("Test1", cellarList);
//		Card.resetClassID();
//		
//		// 6
//		villageList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER }), CollectionsUtil.linkedList(new String[] {"1", "2"})),CollectionsUtil.linkedList(CardType.ACTION), "Test2", 3), GameConstant.INIT_PILE_SIZE, villageList);
//		this.tableForActionCards.put("Test2", villageList);
//		Card.resetClassID();
//		
//		// 7
//		cellarList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, "1"), CollectionsUtil.linkedList(CardType.ACTION), "Test3", 2), GameConstant.INIT_PILE_SIZE, cellarList);
//		this.tableForActionCards.put("Test3", cellarList);
//		Card.resetClassID();
//		
//		// 8
//		villageList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER }), CollectionsUtil.linkedList(new String[] {"1", "2"})),CollectionsUtil.linkedList(CardType.ACTION), "Test4", 3), GameConstant.INIT_PILE_SIZE, villageList);
//		this.tableForActionCards.put("Test4", villageList);
//		Card.resetClassID();
//		
//		// 9
//		cellarList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, "1"), CollectionsUtil.linkedList(CardType.ACTION), "Test5", 2), GameConstant.INIT_PILE_SIZE, cellarList);
//		this.tableForActionCards.put("Test5", cellarList);
//		Card.resetClassID();
//		
//		// 10
//		villageList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER }), CollectionsUtil.linkedList(new String[] {"1", "2"})),CollectionsUtil.linkedList(CardType.ACTION), "Test6", 3), GameConstant.INIT_PILE_SIZE, villageList);
//		this.tableForActionCards.put("Test6", villageList);
//		Card.resetClassID();
	}

	/**
	 * 
	 */
	protected Card findAndRemoveCardFromBoard(String cardId) throws SynchronisationException {
		Matcher matcher = Pattern.compile("\\d+").matcher(cardId);
		matcher.find();	
		
		String key = cardId.substring(0, matcher.start());
		if (this.tableForTreasureCards.containsKey(key)) {
			LinkedList<Card> cardList = this.tableForTreasureCards.get(key);
			return cardList.remove(cardList.size() - 1);
		} else if (this.tableForVictoryCards.containsKey(key)) {
			LinkedList<Card> cardList = this.tableForVictoryCards.get(key);
			return cardList.remove(cardList.size() - 1);
		} else if (this.tableForActionCards.containsKey(key)) {
			LinkedList<Card> cardList = this.tableForActionCards.get(key);
			return cardList.remove(cardList.size() - 1);
		} else {
			throw new SynchronisationException();
		}
	}
	
	public LinkedList<Card> getStartSet(){
		LinkedList<Card> startCards = new LinkedList<Card>();
		LinkedList<Card> copperList = this.tableForTreasureCards.get(GameConstant.COPPER);
		
		for (int i = 0; i < GameConstant.INIT_COPPER_CARDS; i++){
			startCards.addFirst(copperList.removeLast());			
			
		}
		
		LinkedList<Card> estateList = this.tableForVictoryCards.get(GameConstant.ESTATE);
		for (int i = 0; i < GameConstant.INIT_ESTATE_CARDS; i++){
			startCards.add(estateList.removeLast());
		}
		return startCards;
	}
	
	/**
	 * 
	 * @param cardId
	 * @return
	 * @throws SynchronisationException
	 */
	/*------- schoener machen? -----------*/
	protected LinkedList<Card> findCardListFromBoard(String cardId) throws SynchronisationException {
		Matcher matcher = Pattern.compile("\\d+").matcher(cardId);
		matcher.find();	
		
		String key = cardId.substring(0, matcher.start());
		if (this.tableForTreasureCards.containsKey(key)) {
			LinkedList<Card> cardList = this.tableForTreasureCards.get(key);
			return cardList;
		} else if (this.tableForVictoryCards.containsKey(key)) {
			LinkedList<Card> cardList = this.tableForVictoryCards.get(key);
			return cardList;
		} else if (this.tableForActionCards.containsKey(key)) {
			LinkedList<Card> cardList = this.tableForActionCards.get(key);
			return cardList;
		} else {
			throw new SynchronisationException();
		}
	}
}
