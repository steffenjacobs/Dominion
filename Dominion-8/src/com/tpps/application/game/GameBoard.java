package com.tpps.application.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class GameBoard {

	private LinkedHashMap<String, LinkedList<Card>> tableForVictoryCards;
	private LinkedHashMap<String, LinkedList<Card>> tableForCoinCards;
	private LinkedHashMap<String, LinkedList<Card>> tableForActionCards;

	public GameBoard() {
		this.tableForVictoryCards = new LinkedHashMap<String, LinkedList<Card>>();
		this.tableForCoinCards = new LinkedHashMap<String, LinkedList<Card>>();
		this.tableForActionCards = new LinkedHashMap<String, LinkedList<Card>>();

		initHashMapCoinCards();
		initHashMapVictoryCards();
		initHashMapActionCards();
	}

	private void initHashMapCoinCards() {
		LinkedList<Card> copperList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, GameConstant.COPPER_VALUE),
						CollectionsUtil.linkedList(CardType.TREASURE), "Copper", GameConstant.COPPER_COST),
				10, copperList);
		this.tableForVictoryCards.put("Copper", copperList);
		Card.resetClassID();

		LinkedList<Card> silverList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, GameConstant.SILVER_VALUE),
						CollectionsUtil.linkedList(CardType.TREASURE), "Silver", GameConstant.SILVER_COST),
				10, silverList);
		this.tableForVictoryCards.put("Silver", silverList);
		Card.resetClassID();

		LinkedList<Card> goldList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, GameConstant.GOLD_VALUE),
								CollectionsUtil.linkedList(CardType.TREASURE), "Gold", GameConstant.GOLD_COST),
						10, silverList);
		this.tableForVictoryCards.put("Gold", goldList);
		Card.resetClassID();
	}

	private void initHashMapVictoryCards() {
		LinkedList<Card> estateList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, GameConstant.ESTATE_VALUE),
						CollectionsUtil.linkedList(CardType.VICTORY), "Estate", GameConstant.ESTATE_COST),
				10, estateList);
		this.tableForCoinCards.put("Estate", estateList);
		Card.resetClassID();

		LinkedList<Card> duchyList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, GameConstant.DUCHY_VALUE),
								CollectionsUtil.linkedList(CardType.VICTORY), "Duchy", GameConstant.DUCHY_COST),
						10, duchyList);
		this.tableForCoinCards.put("Duchy", duchyList);
		Card.resetClassID();

		LinkedList<Card> provinceList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(
				new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, GameConstant.PROVINCE_VALUE),
						CollectionsUtil.linkedList(CardType.VICTORY), "Province", GameConstant.PROVINCE_COST),
				10, provinceList);
		this.tableForCoinCards.put("Province", duchyList);
		Card.resetClassID();

	}

	private void initHashMapActionCards() {
		// 1
		LinkedList<Card> celarList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, 1),
						CollectionsUtil.linkedList(CardType.ACTION), "Celar", 2), 10, celarList);
		this.tableForActionCards.put("Cellar", celarList);
		Card.resetClassID();
		// 2
		LinkedList<Card> villageList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(
								CollectionsUtil.linkedHashMapAction(
										CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD,
												CardAction.ADD_ACTION_TO_PLAYER }),
						CollectionsUtil.linkedList(new Integer[] { 1, 2 })),
				CollectionsUtil.linkedList(CardType.ACTION), "Village", 3), 10, villageList);
		this.tableForActionCards.put("Village", villageList);
		Card.resetClassID();
		// 3
		LinkedList<Card> smithyList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.DRAW_CARD, 3),
				CollectionsUtil.linkedList(CardType.ACTION), "Smithy", 4), 10, smithyList);
		this.tableForActionCards.put("Smithy", smithyList);
		Card.resetClassID();
		// 4
		LinkedList<Card> woodCutterList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(
								CollectionsUtil.linkedHashMapAction(
										CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD,
												CardAction.ADD_ACTION_TO_PLAYER }),
						CollectionsUtil.linkedList(new Integer[] { 1, 2 })),
				CollectionsUtil.linkedList(CardType.ACTION), "Woodcutter", 3), 10, woodCutterList);
		this.tableForActionCards.put("Woodcutter", woodCutterList);
		Card.resetClassID();
		// 5
		celarList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, 1),
						CollectionsUtil.linkedList(CardType.ACTION), "Test1", 2), 10, celarList);
		this.tableForActionCards.put("Test1", celarList);
		Card.resetClassID();
		// 6
		villageList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(
								CollectionsUtil.linkedHashMapAction(
										CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD,
												CardAction.ADD_ACTION_TO_PLAYER }),
						CollectionsUtil.linkedList(new Integer[] { 1, 2 })),
				CollectionsUtil.linkedList(CardType.ACTION), "Test2", 3), 10, villageList);
		this.tableForActionCards.put("Test2", villageList);
		Card.resetClassID();
		// 7
		celarList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, 1),
						CollectionsUtil.linkedList(CardType.ACTION), "Test3", 2), 10, celarList);
		this.tableForActionCards.put("Test3", celarList);
		Card.resetClassID();
		// 8
		villageList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(
								CollectionsUtil.linkedHashMapAction(
										CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD,
												CardAction.ADD_ACTION_TO_PLAYER }),
						CollectionsUtil.linkedList(new Integer[] { 1, 2 })),
				CollectionsUtil.linkedList(CardType.ACTION), "Test4", 3), 10, villageList);
		this.tableForActionCards.put("Test4", villageList);
		Card.resetClassID();
		// 9
		celarList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, 1),
						CollectionsUtil.linkedList(CardType.ACTION), "Test5", 2), 10, celarList);
		this.tableForActionCards.put("Test5", celarList);
		Card.resetClassID();
		// 10
		villageList = new LinkedList<Card>();
		CollectionsUtil
				.cloneCardToList(
						new Card(
								CollectionsUtil.linkedHashMapAction(
										CollectionsUtil.linkedList(new CardAction[] { CardAction.DRAW_CARD,
												CardAction.ADD_ACTION_TO_PLAYER }),
						CollectionsUtil.linkedList(new Integer[] { 1, 2 })),
				CollectionsUtil.linkedList(CardType.ACTION), "Test6", 3), 10, villageList);
		this.tableForActionCards.put("Test6", villageList);
		Card.resetClassID();
	}

	public LinkedList<String> getCoinCardIds(LinkedHashMap<String, LinkedList<Card>> table) {
		Set<String> keys = this.tableForCoinCards.keySet();
		LinkedList<String> coinIds = new LinkedList<String>();
		LinkedList<Card> cardList;
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			cardList = this.tableForCoinCards.get(string);
			coinIds.add(cardList.get(cardList.size() - 1).getId());
		}
		return coinIds;
	}

	public LinkedList<String> getCoinCardIds() {
		return getCoinCardIds(this.tableForCoinCards);
	}

	public LinkedList<String> getVictoryCardIds() {
		return getCoinCardIds(this.tableForCoinCards);
	}

	public LinkedList<String> getActionCardIds() {
		return getCoinCardIds(this.tableForActionCards);
	}

	/**
	 * 
	 * @return a table containing all Cards
	 */
	public HashMap<String, LinkedList<Card>> getTable() {
		return tableForActionCards;
	}

}
