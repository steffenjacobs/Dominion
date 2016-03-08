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
		this.coins = GameConstant.INIT_MONEY;
		this.CLIENT_ID = clientID;
		this.port = port;
	}

	/**
	 * 
	 */
	public Player(int clientID, int port) {
		this(new Deck(), clientID, port);		
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

	/**
	 * calls the static method which executes the actions
	 * 
	 * @author Lukas Adler
	 */
	public void doAction(String cardID) {
		this.getDeck().draw();
		Card serverCard = this.getDeck().getCardFromHand(cardID);
		// Player player = GameController.getActivePlayer();
		LinkedList<CardAction> actionsList = new LinkedList<CardAction>(serverCard.getActions().keySet());
		Iterator<CardAction> cardIt = actionsList.iterator();
		System.out.println("DoAction");
		while (cardIt.hasNext()) {
			switch (cardIt.next()) {
			case ADD_ACTION_TO_PLAYER:
				actions += Integer.parseInt(serverCard.getActions().get(CardAction.ADD_ACTION_TO_PLAYER));
				break;
			case ADD_PURCHASE:
				buys += Integer.parseInt(serverCard.getActions().get(CardAction.ADD_PURCHASE));
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				coins += Integer.parseInt(serverCard.getActions().get(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN));
				break;
			case DRAW_CARD:
				 getDeck().draw(Integer.parseInt(serverCard.getActions().get(CardAction.DRAW_CARD)));
				break;
			case GAIN_CARD:
				System.out.println("GAIN: " + serverCard.getActions().get(CardAction.GAIN_CARD));
				break;
			case DISCARD_CARD:
				System.out.println("DISCARD: " + serverCard.getActions().get(CardAction.DISCARD_CARD));
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
				System.out.println("treasure received");
				break;
			case IS_VICTORY:
				System.out.println("is victory");
				break;
			default:
				break;
			}
		}
	}
}