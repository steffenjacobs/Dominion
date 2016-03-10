package com.tpps.application.game;

import java.util.Iterator;
import java.util.LinkedList;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.Tuple;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/**
 * @author Nicolas Wipfler
 */
public class Player {

	private Deck deck;

	private final int id;
	private static int playerID = 0;
	
	private final int CLIENT_ID;
	private int port;
	
	private int actions;
	private int buys;
	private int coins;
	private boolean discardMode;
	private LinkedList<Card> playedCards;
	private Tuple<CardAction, Integer> discardAction;
	
	/**
	 * 
	 */
	public Player(Deck deck, int clientID, int port) {
		this.discardMode = false;
		this.deck = deck;
		this.id = playerID++;
		this.actions = GameConstant.INIT_ACTIONS;
		this.buys = GameConstant.INIT_PURCHASES;
		this.coins = GameConstant.INIT_TREASURES;
		this.CLIENT_ID = clientID;
		this.port = port;
		this.playedCards = new LinkedList<Card>();
	}

	/**
	 * 
	 */
	public Player(int clientID, int port) {
		this(new Deck(), clientID, port);		
	}
	
	public void resetPlayerValues(){
		this.coins = 0;
		this.buys = 1;
		this.actions = 1;
	}

	/**
	 * @return the deck
	 */
	public Deck getDeck() {
		return deck;
	}

	/**
	 * @param deck the deck to set
	 */
	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	/**
	 * @return the id
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return the CLIENT_ID
	 */
	public int getClientID() {
		return CLIENT_ID;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * @return the actions
	 */
	public int getActions() {
		return actions;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(int actions) {
		this.actions = actions;
	}

	/**
	 * @return the buys
	 */
	public int getBuys() {
		return buys;
	}
	
	
	/**
	 * 
	 * @return the played cards of the player in his turn
	 */
	public LinkedList<Card> getPlayedCards() {
		return playedCards;
	}
	
	/**
	 * new played cardsList
	 */
	public void refreshPlayedCardsList(){
		
	}

	/**
	 * @param buys the buys to set
	 */
	public void setBuys(int buys) {
		this.buys = buys;
	}

	/**
	 * @return the coins
	 */
	public int getCoins() {
		return coins;
	}

	/**
	 * @param coins the coins to set
	 */
	public void setCoins(int coins) {
		this.coins = coins;
	}
	
	public void playCard(String cardID){
		if (!discardMode){
			System.out.println("kein discard mode");
		 this.playedCards.addLast(doAction(cardID));
		}
		else{
			this.getDeck().getDiscardPile().add(doAction(cardID));
		}
	}
	
	public void playTreasures(){
		LinkedList<Card> cards = new LinkedList<Card>();
		LinkedList<String> treasureCards = this.getDeck().getTreasureCardsFromHand();
		for (Iterator<String> iterator = treasureCards.iterator(); iterator.hasNext();) {
			String cardId = (String) iterator.next();
			cards.add(doAction(cardId));			
			System.out.println("Treasures auf der Hand: " + cardId);
		}
//		this.activePlayer.getDeck().
////		CollectionsUtil.appendListToList(treasureCards, this.getPlayedCards());
		CollectionsUtil.appendListToList(cards, this.playedCards);
	}

	/**
	 * calls the static method which executes the actions
	 * 
	 * @author Lukas Adler
	 */
	public Card doAction(String cardID) {
		Card serverCard = this.getDeck().getCardFromHand(cardID);
		if (this.discardMode){
			discard(serverCard);
			return null;
		}else{
		
		boolean dontRemoveFlag = false;
		
		Iterator<CardAction> cardIterator = serverCard.getActions().keySet().iterator();
		this.actions--;
		System.out.println("DoAction");
		while (cardIterator.hasNext()) {
			CardAction act = cardIterator.next();
			String value = serverCard.getActions().get(act);
			switch (act) {
			case ADD_ACTION_TO_PLAYER:
				this.actions += Integer.parseInt(value);
				break;
			case ADD_PURCHASE:
				this.buys += Integer.parseInt(value);
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				this.coins += Integer.parseInt(value);
				break;
			case DRAW_CARD:
				this.getDeck().draw(Integer.parseInt(value));
				break;
			case GAIN_CARD:
				System.out.println(value);
				switch(value.toUpperCase()) {
					case "CURSE": break;
					case "SILVER": break;
					case "": break;
					default: break;
				}
				break;
			case DISCARD_AND_DRAW:
				this.discardMode = true;				
				this.discardAction = new Tuple<CardAction, Integer>(act, Integer.parseInt(value));
				break;
			case TRASH_CARD:
				this.getDeck().trash(serverCard, new LinkedList<Card>() /* HOW?? muss der trashPile uebergeben werden, wir koennen aber aus der trash Methode nicht drauf zugreifen */);
				// return?
				break;
			case PUT_BACK:
				this.getDeck().putBack(serverCard);
				break;
			case REVEAL_CARD:
				System.out.println("REVEAL: " + serverCard.getActions().get(CardAction.REVEAL_CARD));
				break;
			case IS_TREASURE:
				this.coins += Integer.parseInt(serverCard.getActions().get(CardAction.IS_TREASURE));
				break;
			case IS_VICTORY:
				// what?
				break;
			default:
				break;
			}
		}
		if (!dontRemoveFlag){
			System.out.println("card was removed");
			this.getDeck().getCardHand().remove(serverCard);
		}
		return serverCard;
	}
	}
	
	public void discard(Card card){
		switch (this.discardAction.getFirstEntry()) {
		case DISCARD_AND_DRAW:
			this.getDeck().getCardHand().remove(card);
			this.getDeck().draw(1);
			break;

		default:
			break;
		}
		
	}
	
}