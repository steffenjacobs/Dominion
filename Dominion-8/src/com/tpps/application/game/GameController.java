package com.tpps.application.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.network.game.TooMuchPlayerException;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

public class GameController {

	private LinkedList<Player> players;
	private HashMap<String, LinkedList<Card>> table;
	private boolean gameNotFinished;
	private Player activePlayer;

	public GameController() {
		// new Setup().start();
		this.players = new LinkedList<Player>();
		this.table = new HashMap<String, LinkedList<Card>>();
		initHashMap();
		this.gameNotFinished = true;
	}
	
	
	private void initHashMap(){
		LinkedList<Card> copperList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, 
				GameConstant.COPPER_VALUE),CollectionsUtil.linkedList(CardType.TREASURE),"Copper", GameConstant.COPPER_COST), 9, copperList);		
		this.table.put("Copper", copperList);
		Card.resetClassID();
		
		LinkedList<Card> silverList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, 
				GameConstant.SILVER_VALUE),CollectionsUtil.linkedList(CardType.TREASURE),"Silver", GameConstant.SILVER_COST), 9, silverList);
		this.table.put("Silver", silverList);
		Card.resetClassID();
		
		LinkedList<Card> goldList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, 
				GameConstant.GOLD_VALUE),CollectionsUtil.linkedList(CardType.TREASURE),"Gold", GameConstant.GOLD_COST), 9, silverList);
		this.table.put("Gold", goldList);
		Card.resetClassID();
//		1
		LinkedList<Card> celarList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, 
				1),CollectionsUtil.linkedList(CardType.ACTION),"Celar", 2), 9, celarList);
		this.table.put("Cellar", celarList);
		Card.resetClassID();
//		2
		LinkedList<Card> villageList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[]{CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER}), 
				CollectionsUtil.linkedList(new Integer[]{1, 2})),CollectionsUtil.linkedList(CardType.ACTION),"Village", 3), 9, villageList);
		this.table.put("Village", villageList);
		Card.resetClassID();
//		3
		celarList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, 
				1),CollectionsUtil.linkedList(CardType.ACTION),"Celar", 2), 9, celarList);
		this.table.put("Cellar", celarList);
		Card.resetClassID();
//		4
		villageList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[]{CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER}), 
				CollectionsUtil.linkedList(new Integer[]{1, 2})),CollectionsUtil.linkedList(CardType.ACTION),"Village", 3), 9, villageList);
		this.table.put("Village", villageList);
		Card.resetClassID();
//		5
		celarList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, 
				1),CollectionsUtil.linkedList(CardType.ACTION),"Celar", 2), 9, celarList);
		this.table.put("Cellar", celarList);
		Card.resetClassID();
//		6
		villageList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[]{CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER}), 
				CollectionsUtil.linkedList(new Integer[]{1, 2})),CollectionsUtil.linkedList(CardType.ACTION),"Village", 3), 9, villageList);
		this.table.put("Village", villageList);
		Card.resetClassID();
//		7
		celarList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, 
				1),CollectionsUtil.linkedList(CardType.ACTION),"Celar", 2), 9, celarList);
		this.table.put("Cellar", celarList);
		Card.resetClassID();
//		8
		villageList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[]{CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER}), 
				CollectionsUtil.linkedList(new Integer[]{1, 2})),CollectionsUtil.linkedList(CardType.ACTION),"Village", 3), 9, villageList);
		this.table.put("Village", villageList);
		Card.resetClassID();
//		9
		celarList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.ADD_ACTION_TO_PLAYER, 
				1),CollectionsUtil.linkedList(CardType.ACTION),"Celar", 2), 9, celarList);
		this.table.put("Cellar", celarList);
		Card.resetClassID();
//		10
		villageList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(new CardAction[]{CardAction.DRAW_CARD, CardAction.ADD_ACTION_TO_PLAYER}), 
				CollectionsUtil.linkedList(new Integer[]{1, 2})),CollectionsUtil.linkedList(CardType.ACTION),"Village", 3), 9, villageList);
		this.table.put("Village", villageList);
		Card.resetClassID();
		
		
		LinkedList<Card> estateList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, 
				GameConstant.ESTATE_VALUE),CollectionsUtil.linkedList(CardType.VICTORY),"Estate", GameConstant.ESTATE_COST), 9, estateList);		
		this.table.put("Copper", estateList);
		Card.resetClassID();
		
		LinkedList<Card> duchyList = new LinkedList<Card>();
		CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, 
				GameConstant.DUCHY_VALUE),CollectionsUtil.linkedList(CardType.VICTORY),"Duchy", GameConstant.DUCHY_COST), 9, duchyList);
		this.table.put("Silver", duchyList);
		Card.resetClassID();
				
	}

	public LinkedList<Player> getPlayers() {
		return this.players;
	}

	public void setPlayers(LinkedList<Player> players) {
		this.players = players;
	}

	public Player getActivePlayer() {
		return this.activePlayer;
	}

	public void setActivePlayer(Player aP) {
		this.activePlayer = aP;
	}

	public boolean isGameNotFinished() {
		return this.gameNotFinished;
	}

	public boolean setGameNotFinished(boolean gameNotFinished) {
		return this.gameNotFinished = gameNotFinished;
	}

	/**
	 * 
	 * @param player
	 * @return if there are four players
	 * @throws TooMuchPlayerException
	 */
	public void addPlayer(Player player) throws TooMuchPlayerException {
		if (this.players.size() < 4) {
			this.players.addLast(player);
			if (this.players.size() == 4){
				this.activePlayer = getRandomPlayer();				
			}
		} else {
			throw new TooMuchPlayerException();
			

		}

	}

	/**
	 * 
	 * @return one of the four players who is randomly choosen
	 */
	private Player getRandomPlayer() {
		return this.players.get((int)(Math.random()*4));
	}

	private boolean gameFinished() {
		/* Checkt die Stapel durch, ob 3 Stapel leer sind bzw. Provinzen leer */
		/* Wenn ja: */
		return !setGameNotFinished(false);
	}

	/** CONTROLLER LOGIC; not sure whether the loops are necessary */
	public void startGame() {	
		System.out.println(Arrays.toString(this.activePlayer.getDeck().getCardHand().toArray()));
	}

	private void turn(Player player) {
		setActivePlayer(player);
	}
}
