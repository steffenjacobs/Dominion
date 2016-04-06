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
import com.tpps.technicalServices.network.game.SynchronisationException;
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
	public synchronized LinkedList<String> getTreasureCardIDs() {
		return getCardIDs(this.tableForTreasureCards);
	}

	/**
	 * returns the ids of the victory Cards lying at the top of the victoryCards table
	 */
	public synchronized LinkedList<String> getVictoryCardIDs() {
		return getCardIDs(this.tableForVictoryCards);
	}

	/**
	 * returns the ids of the action Cards lying at the top of the actionCards table
	 */
	public synchronized LinkedList<String> getActionCardIDs() {
		return getCardIDs(this.tableForActionCards);
	}
	

	/**
	 * returns the ids of the cards selected through the key of the hashMap lying at the end of the list
	 */
	public synchronized LinkedList<String> getCardIDs(LinkedHashMap<String, LinkedList<Card>> table) {
		Set<String> keys = table.keySet();
		LinkedList<String> cardIds = new LinkedList<String>();
		LinkedList<Card> cardList;
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			cardList = table.get(string);
			if (cardList.size() > 0){
				cardIds.add(cardList.get(cardList.size() - 1).getId());
			} else {
				System.out.println("nil added");
				cardIds.add(string + "#");
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
		
		LinkedList<Card> curseList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, 
				Integer.toString(GameConstant.CURSE_VALUE)), CollectionsUtil.linkedList(CardType.CURSE), 
				"Curse", GameConstant.CURSE_COST), GameConstant.INIT_PILE_SIZE, curseList);
		this.tableForVictoryCards.put("Curse", curseList);
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

		
		// 1
//		LinkedList<Card> cellarList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[]{CardAction.ADD_ACTION_TO_PLAYER, CardAction.DISCARD_AND_DRAW}), CollectionsUtil.linkedList(new String[]{"1", "-1"})), 
//				CollectionsUtil.linkedList(CardType.ACTION), "Cellar", 2), 10, cellarList);
//		this.tableForActionCards.put("Cellar", cellarList);
//		Card.resetClassID();
//		
		
//		LinkedList<Card> chapelList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.TRASH_CARD, "4"), 
//				CollectionsUtil.linkedList(CardType.ACTION), "Chapel", 2), 10, chapelList);
//		this.tableForActionCards.put("Chapel", chapelList);
//		Card.resetClassID();
		
//		// 2
//		LinkedList<Card> chancellorList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] { CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, CardAction.DISCARD_CARD}), 
//				CollectionsUtil.linkedList(new String[] {"2", "Deck"})),CollectionsUtil.linkedList(CardType.ACTION), "Chancellor", 3), GameConstant.INIT_PILE_SIZE, chancellorList);
//		this.tableForActionCards.put("Chancellor", chancellorList);
//		Card.resetClassID();
		
//		LinkedList<Card> militiaList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, CardAction.DISCARD_OTHER_DOWNTO}), 
//				CollectionsUtil.linkedList(new String[] {"2", "3"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION, CardType.ATTACK}), "Militia", 4), GameConstant.INIT_PILE_SIZE, militiaList);
//		this.tableForActionCards.put("Militia", militiaList);
//		Card.resetClassID();
		
//		moat
		LinkedList<Card> moatList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.DRAW_CARD, CardAction.SEPERATOR, CardAction.DEFEND}), 
				CollectionsUtil.linkedList(new String[] {"2", "NIL", "NIL"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION, CardType.REACTION}), "Moat", 2), GameConstant.INIT_PILE_SIZE, moatList);
		this.tableForActionCards.put("Moat", moatList);
		Card.resetClassID();
		
//		village
		LinkedList<Card> villageList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER}), 
				CollectionsUtil.linkedList(new String[] {"1", "2"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Village", 3), GameConstant.INIT_PILE_SIZE, villageList);
		this.tableForActionCards.put("Village", villageList);
		Card.resetClassID();
		
//		woodcutter
		LinkedList<Card> woodCutterList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.ADD_PURCHASE, CardAction.ADD_TEMPORARY_MONEY_FOR_TURN}), 
				CollectionsUtil.linkedList(new String[] {"1", "2"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Woodcutter", 3), GameConstant.INIT_PILE_SIZE, woodCutterList);
		this.tableForActionCards.put("Woodcutter", woodCutterList);
		Card.resetClassID();
		
//		workshop
//		LinkedList<Card> workShopList = new LinkedList<Card>();		
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.GAIN_CARD, 
//				"4"), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Workshop", 3), GameConstant.INIT_PILE_SIZE, workShopList);
//		this.tableForActionCards.put("Workshop", workShopList);
//		Card.resetClassID();
		

		
		
//		bureaucrat
		LinkedList<Card> bureaucratList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.GAIN_CARD_DRAW_PILE, CardAction.REVEAL_CARD_OTHERS_PUT_IT_ON_TOP_OF_DECK}), 
				CollectionsUtil.linkedList(new String[] {"silver", "victory"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION, CardType.ATTACK}), "Bureaucrat", 0), GameConstant.INIT_PILE_SIZE, bureaucratList);
		this.tableForActionCards.put("Bureaucrat", bureaucratList);
		Card.resetClassID();
		
//		feast 
//		LinkedList<Card> feastList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.TRASH_CARD, CardAction.GAIN_CARD}), 
//				CollectionsUtil.linkedList(new String[] {"this", "5"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Feast", 0), GameConstant.INIT_PILE_SIZE, feastList);
//		this.tableForActionCards.put("Feast", feastList);
//		Card.resetClassID();
		
//		moneyLender
//		LinkedList<Card> moneylenderList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.TRASH_AND_ADD_TEMPORARY_MONEY}), 
//				CollectionsUtil.linkedList(new String[] {"Copper_3"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Moneylender", 0), GameConstant.INIT_PILE_SIZE, moneylenderList);
//		this.tableForActionCards.put("Moneylender", moneylenderList);
//		Card.resetClassID();
		
//		remodel
		LinkedList<Card> remodelList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.TRASH_AND_GAIN_MORE_THAN}), 
				CollectionsUtil.linkedList(new String[] {"1_2"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Remodel", 0), GameConstant.INIT_PILE_SIZE, remodelList);
		this.tableForActionCards.put("Remodel", remodelList);
		Card.resetClassID();
		
//		smithy
//		LinkedList<Card> smithyList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.DRAW_CARD}), 
//				CollectionsUtil.linkedList(new String[] {"3"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Smithy", 0), GameConstant.INIT_PILE_SIZE, smithyList);
//		this.tableForActionCards.put("Smithy", smithyList);
//		Card.resetClassID();
		
//		spy 
		LinkedList<Card> spyList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER, CardAction.REVEAL_CARD_ALL}), 
				CollectionsUtil.linkedList(new String[] {"1", "1", "NIL"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Spy", 0), GameConstant.INIT_PILE_SIZE, spyList);
		this.tableForActionCards.put("Spy", spyList);
		Card.resetClassID();
		

		LinkedList<Card> thiefList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.ALL_REVEAL_CARDS_TRASH_COINS_I_CAN_TAKE_DISCARD_OTHERS}), 
				CollectionsUtil.linkedList(new String[] {"2"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION, CardType.ATTACK}), "Thief", 0), GameConstant.INIT_PILE_SIZE, thiefList);
		this.tableForActionCards.put("Thief", thiefList);
		Card.resetClassID();
		
//		ThroneRoom
		
//		LinkedList<Card> throneRoomList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.CHOOSE_CARD_PLAY_TWICE}), 
//				CollectionsUtil.linkedList(new String[] {"Nil"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "ThroneRoom", 0), GameConstant.INIT_PILE_SIZE, throneRoomList);
//		this.tableForActionCards.put("ThroneRoom", throneRoomList);
//		Card.resetClassID();
		
//		CouncilRoom
		
//		LinkedList<Card> councilRoomList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.DRAW_CARD, CardAction.ADD_PURCHASE, CardAction.DRAW_CARD_OTHERS}), 
//				CollectionsUtil.linkedList(new String[] {"4", "1", "1"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "CouncilRoom", 0), GameConstant.INIT_PILE_SIZE, councilRoomList);
//		this.tableForActionCards.put("CouncilRoom", councilRoomList);
//		Card.resetClassID();
		
		LinkedList<Card> festivalList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.ADD_ACTION_TO_PLAYER, CardAction.ADD_PURCHASE, CardAction.ADD_TEMPORARY_MONEY_FOR_TURN}), 
				CollectionsUtil.linkedList(new String[] {"2", "1", "2"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Festival", 0), GameConstant.INIT_PILE_SIZE, festivalList);
		this.tableForActionCards.put("Festival", festivalList);
		Card.resetClassID();
		
//		Laboratory
//		LinkedList<Card> laboratoryList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER}), 
//				CollectionsUtil.linkedList(new String[] {"2", "1"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Laboratory", 0), GameConstant.INIT_PILE_SIZE, laboratoryList);
//		this.tableForActionCards.put("Laboratory", laboratoryList);
//		Card.resetClassID();

//		Library
//		LinkedList<Card> libraryList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.DRAW_CARD_UNTIL}), 
//				CollectionsUtil.linkedList(new String[] {"7_action"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Library", 0), GameConstant.INIT_PILE_SIZE, libraryList);
//		this.tableForActionCards.put("Library", libraryList);
//		Card.resetClassID();

//		Market
//		LinkedList<Card> marketList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER,
//				CardAction.ADD_PURCHASE, CardAction.ADD_TEMPORARY_MONEY_FOR_TURN}), 
//				CollectionsUtil.linkedList(new String[] {"1", "1", "1", "1"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Market", 0), GameConstant.INIT_PILE_SIZE, marketList);
//		this.tableForActionCards.put("Market", marketList);
//		Card.resetClassID();
		
//		Mine
//		LinkedList<Card> mineList = new LinkedList<Card>();
//		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND}), 
//				CollectionsUtil.linkedList(new String[] {"1_3"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Mine", 0), GameConstant.INIT_PILE_SIZE, mineList);
//		this.tableForActionCards.put("Mine", mineList);
//		Card.resetClassID();
		
//		Witch
		LinkedList<Card> witchList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[] {CardAction.DRAW_CARD, CardAction.GAIN_CARD_OTHERS}), 
				CollectionsUtil.linkedList(new String[] {"2", "curse"})), CollectionsUtil.linkedList(new CardType[]{CardType.ACTION}), "Witch", 0), GameConstant.INIT_PILE_SIZE, witchList);
		this.tableForActionCards.put("Witch", witchList);
		Card.resetClassID();		
		
//		Adventurer
		LinkedList<Card> adventurerList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.REVEAL_UNTIL_TREASURES, 
				"2"), CollectionsUtil.linkedList(CardType.ACTION), "Adventurer", 0), GameConstant.INIT_PILE_SIZE, adventurerList);
		this.tableForActionCards.put("Adventurer", adventurerList);
		Card.resetClassID();
	}

	/**
	 * maybe
	 */
	protected synchronized Card findAndRemoveCardFromBoard(String cardId) throws SynchronisationException {
		Matcher matcher = Pattern.compile("\\d+").matcher(cardId);
		matcher.find();	
		
		String key = cardId.substring(0, matcher.start());
		if (this.tableForTreasureCards.containsKey(key) || 
				this.tableForVictoryCards.containsKey(key) || this.tableForActionCards.containsKey(key)) {
			
			LinkedList<Card> cardList = findCardListFromBoard(cardId);
			return cardList.removeLast();
		} else {
			throw new SynchronisationException();
		}
	}
	
	public synchronized LinkedList<Card> getStartSet(){
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
	public LinkedList<Card> findCardListFromBoard(String cardId) throws SynchronisationException {
		Matcher matcher = Pattern.compile("\\d+").matcher(cardId);
		matcher.find();	
		
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
		if (cardList.getLast().getId().equals(cardId)){
			return cardList;
		}else{
			throw new SynchronisationException();
		}		
	}
	
	/**
	 * 
	 * @return true if three piles are empty
	 */
	public boolean checkThreePilesEmpty() {
		int counter = 0;
				System.out.println(counter);
		counter += amountOfPilesEmpty(this.tableForActionCards);
		if (counter >= GameConstant.EMPTY_PILES){
			return true;
		}
		counter += amountOfPilesEmpty(this.tableForTreasureCards);
		if (counter >= GameConstant.EMPTY_PILES){
			return true;
		}
		counter += amountOfPilesEmpty(this.tableForVictoryCards);
		if (counter >= GameConstant.EMPTY_PILES){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param table
	 * @return the number of empty lists in a hashMap
	 */
	public int amountOfPilesEmpty(LinkedHashMap<String, LinkedList<Card>> table) {
		int counter = 0;
		LinkedList<String> keys = new LinkedList<>(table.keySet());
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			if(table.get(key).isEmpty()){
				counter++;
				if (counter == GameConstant.EMPTY_PILES){
					return counter;
				}
			}
		}
		return counter;
	}
}