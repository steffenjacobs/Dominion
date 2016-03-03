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

	private int actions;
	private int purchases;
	private int money;

	private final int id;
	private static int playerID = 0;

	// draw()

	public Player(Deck deck) {
		this.deck = deck;
		this.id = playerID++;
		this.actions = GameConstant.INIT_ACTIONS;
		this.purchases = GameConstant.INIT_PURCHASES;
		this.money = GameConstant.INIT_MONEY;
	}

	public Player() {
		this(new Deck());
	}
	
	/**
	 * @return the deck
	 */
	public Deck getDeck() {
		return this.deck;
	}
	
	/**
	 * @param deck the deck to set
	 */
	public void setDeck(Deck deck) {
		this.deck = deck;
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
	 * @return the purchases
	 */
	public int getPurchases() {
		return purchases;
	}

	/**
	 * @param purchases the purchases to set
	 */
	public void setPurchases(int purchase) {
		this.purchases = purchase;
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

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * calls the static method which executes the actions
	 * 
	 * @author ladler - Lukas Adler
	 */
	public void doAction(String cardID) {
		Card serverCard = this.getDeck().getCardFromHand(cardID);
		// Player player = GameController.getActivePlayer();
		
		LinkedList<CardAction> actionsList = new LinkedList<CardAction>(
				serverCard.getActions().keySet());
		Iterator<CardAction> cardIt = actionsList.iterator();

		while (cardIt.hasNext()) {
			switch (cardIt.next()) {
			case ADD_ACTION_TO_PLAYER:
				actions += serverCard.getActions().get(CardAction.ADD_ACTION_TO_PLAYER);
				System.out.println("action added new value: " + actions);

				break;
			case ADD_PURCHASE:
				purchases += serverCard.getActions().get(
						CardAction.ADD_PURCHASE);
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				money += serverCard.getActions().get(
						CardAction.ADD_TEMPORARY_MONEY_FOR_TURN);
				break;
			case DRAW_CARD:
				// serverCard.getActions().get(CardAction.DRAW_CARD);
				break;
			case GAIN_CARD:
				System.out.println("GAIN: "
						+ serverCard.getActions().get(CardAction.GAIN_CARD));
				break;
			case DISCARD_CARD:
				System.out.println("DISCARD: "
						+ serverCard.getActions().get(CardAction.DISCARD_CARD));
				break;
			case TRASH_CARD:
				System.out.println("TRASH: "
						+ serverCard.getActions().get(CardAction.TRASH_CARD));
				break;
			case PUT_BACK:
				System.out.println("PUT_BACK: "
						+ serverCard.getActions().get(CardAction.PUT_BACK));
				break;
			case REVEAL_CARD:
				System.out.println("REVEAL: "
						+ serverCard.getActions().get(CardAction.REVEAL_CARD));
				break;
			default:
				// call
				break;
			}
		}
	}

	public static void main(String[] args) {
		Player p = new Player();
		System.out.println(p.getDeck().toString());
//		p.getDeck().shuffle();
		Card silver = new Card(CollectionsUtil.linkedHashMapAction(
				CollectionsUtil.linkedList(CardAction.IS_TREASURE),
				CollectionsUtil.linkedList(GameConstant.SILVER_VALUE)),
				CollectionsUtil.linkedList(CardType.TREASURE), "Silver", 0);
		CollectionsUtil.addCardToList(silver, p.getDeck().getDiscardPile());
		System.out.println("\n" + p.getDeck().toString());
//		p.getDeck().shuffle();
		System.out.println("\n" + p.getDeck().toString());
	}
}