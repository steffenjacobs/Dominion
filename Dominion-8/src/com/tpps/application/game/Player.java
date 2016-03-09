package com.tpps.application.game;

import java.util.Iterator;
import java.util.LinkedList;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
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
	
	/**
	 * 
	 */
	public Player(Deck deck, int clientID, int port) {
		this.deck = deck;
		this.id = playerID++;
		this.actions = GameConstant.INIT_ACTIONS;
		this.buys = GameConstant.INIT_PURCHASES;
		this.coins = GameConstant.INIT_TREASURES;
		this.CLIENT_ID = clientID;
		this.port = port;
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
	
	public Card playCard(String cardID){		
		return doAction(cardID);
	}
	
	public LinkedList<Card> playTreasures(){
		LinkedList<Card> cards = new LinkedList<Card>();
		LinkedList<String> treasureCards = this.getDeck().getTreasureCardsFromHand();
		for (Iterator<String> iterator = treasureCards.iterator(); iterator.hasNext();) {
			String cardId = (String) iterator.next();
			cards.add(doAction(cardId));			
			System.out.println("Treasures auf der Hand: " + cardId);
		}
//		this.activePlayer.getDeck().
////		CollectionsUtil.appendListToList(treasureCards, this.getPlayedCards());
		return cards;
	}

	/**
	 * calls the static method which executes the actions
	 * 
	 * @author Lukas Adler
	 */
	public Card doAction(String cardID) {
		Card serverCard = this.getDeck().getCardFromHand(cardID);
		Iterator<CardAction> cardIterator = serverCard.getActions().keySet().iterator();
		this.actions--;
		System.out.println("DoAction");
		while (cardIterator.hasNext()) {
			CardAction act = cardIterator.next();
			String value = serverCard.getActions().get(act);
			switch (act) {
			case ADD_ACTION_TO_PLAYER:
				actions += Integer.parseInt(value);
				break;
			case ADD_PURCHASE:
				buys += Integer.parseInt(value);
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				coins += Integer.parseInt(value);
				break;
			case DRAW_CARD:
				 getDeck().draw(Integer.parseInt(value));
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
			case DISCARD_CARD:
				this.getDeck().discardCard(serverCard);
				break;
			case TRASH_CARD:
				System.out.println("TRASH: " + serverCard.getActions().get(CardAction.TRASH_CARD));
				break;
			case PUT_BACK:
				System.out.println("PUT_BACK: " + serverCard.getActions().get(CardAction.PUT_BACK));
				break;
			case REVEAL_CARD:
				System.out.println("REVEAL: " + serverCard.getActions().get(CardAction.REVEAL_CARD));
				break;
			case IS_TREASURE:
				this.coins += Integer.parseInt(serverCard.getActions().get(CardAction.IS_TREASURE));
				break;
			case IS_VICTORY:
				System.out.println("is victory");
				break;
			default:
				break;
			}
		}
		
		
		
		this.getDeck().getCardHand().remove(serverCard);
		return serverCard;
	}
}