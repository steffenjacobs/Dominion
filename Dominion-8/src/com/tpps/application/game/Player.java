package com.tpps.application.game;

import java.util.Iterator;
import java.util.LinkedList;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/** ServerPlayer */
public class Player {

	private Deck deck;
	private int id;
	private int cardHandSize;
	private int actions;
	private int purchase;
	private int money;
	// aktionen, käufe, etc.

	// draw()
	// addAction() etc.

	public Player(Deck deck, int id) {
		this.deck = deck;
		this.id = id;
		this.cardHandSize = GameConstant.INIT_CARD_HAND_SIZE;
//		this.actions =
//		this.purchase = 
	}

	public Player(int id) {
		this(new Deck(), id);
		this.id = id;
	}

	/**
	 * @return the purchase
	 */
	public int getPurchase() {
		return purchase;
	}

	/**
	 * @param purchase the purchase to set
	 */
	public void setPurchase(int purchase) {
		this.purchase = purchase;
	}

	/**
	 * @return the money
	 */
	public int getMoney() {
		return money;
	}

	/**
	 * @param money the money to set
	 */
	public void setMoney(int money) {
		this.money = money;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Deck getDeck() {
		return this.deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	public int getCardHandSize() {
		return this.cardHandSize;
	}

	public void setCardHandSize(int cardHandSize) {
		this.cardHandSize = cardHandSize;
	}
	
	public int getActions() {
		return actions;
	}

	public void setActions(int actions) {
		this.actions = actions;
	}

	public int getPurchases() {
		return purchase;
	}

	public void setPurchases(int purchase) {
		this.purchase = purchase;
	}

	/**
	 * calls the static method which executes the actions
	 * 
	 * @author ladler - Lukas Adler
	 */
	public void doAction(String cardID) {
		Card serverCard = this.getDeck().getCard(cardID, this.getDeck().getCardHand());
		// Player player = GameController.getActivePlayer();

		LinkedList<CardAction> actionsList = new LinkedList<CardAction>(serverCard.getActions().keySet());
		Iterator<CardAction> cardIt = actionsList.iterator();

		while (cardIt.hasNext()) {
			switch (cardIt.next()) {
			case ADD_ACTION_TO_PLAYER:
				actions += serverCard.getActions().get(CardAction.ADD_ACTION_TO_PLAYER);
				break;
			case ADD_PURCHASE:
				purchase += serverCard.getActions().get(CardAction.ADD_PURCHASE);
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				money += serverCard.getActions().get(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN);
				break;
			case DRAW_CARD:
//				serverCard.getActions().get(CardAction.DRAW_CARD);
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
			default:
				// call
				break;
			}
		}
	}

	public static void main(String[] args) {
		Player p = new Player(0);
		System.out.println(p.getDeck().toString());
		p.getDeck().shuffle();
		Card silver = new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(CardAction.IS_TREASURE), CollectionsUtil.linkedList(GameConstant.SILVER_VALUE)) , CollectionsUtil.linkedList(CardType.TREASURE), "Silver", 0);
		CollectionsUtil.addCardToList(silver, p.getDeck().getDiscardPile());
		System.out.println("\n" + p.getDeck().toString());
		p.getDeck().shuffle();
		System.out.println("\n" + p.getDeck().toString());
	}
}