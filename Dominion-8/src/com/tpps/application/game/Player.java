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
	// aktionen, käufe, etc.

	// draw()
	// addAction() etc.

	public Player(Deck deck, int id) {
		this.deck = deck;
		this.id = id;
		this.cardHandSize = GameConstant.INIT_CARD_HAND_SIZE;
	}

	public Player(int id) {
		this(new Deck(), id);
		this.id = id;
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

	/**
	 * calls the static method which executes the actions
	 * 
	 * @author ladler - Lukas Adler
	 */
	public void doAction(String cardID) {
		Card serverCard = this.getDeck().getCardFromHand(cardID);
		// Player player = GameController.getActivePlayer();

		LinkedList<CardAction> actionsList = new LinkedList<CardAction>(serverCard.getActions().keySet());
		Iterator<CardAction> cardIt = actionsList.iterator();

		while (cardIt.hasNext()) {
			switch (cardIt.next()) {
			case ADD_ACTION_TO_PLAYER:
				// player.addAction(this.actions.get(ADD_ACTION_TO_PLAYER));
				// sentPackage(player, numbAction);
				System.out.println(
						"ADD_ACTION_TO_PLAYER: " + serverCard.getActions().get(CardAction.ADD_ACTION_TO_PLAYER));
				break;
			case ADD_PURCHASE:
				System.out.println("ADD_PURCHASE: " + serverCard.getActions().get(CardAction.ADD_PURCHASE));
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				System.out.println("ADD_TEMPORARY_MONEY_FOR_TURN: "
						+ serverCard.getActions().get(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN));
				break;
			case DRAW:
				System.out.println("DRAW: " + serverCard.getActions().get(CardAction.DRAW));
				break;
			case GAIN:
				System.out.println("GAIN: " + serverCard.getActions().get(CardAction.GAIN));
				break;
			case DISCARD:
				System.out.println("DISCARD: " + serverCard.getActions().get(CardAction.DISCARD));
				break;
			case TRASH:
				System.out.println("TRASH: " + serverCard.getActions().get(CardAction.TRASH));
				break;
			case PUT_BACK:
				System.out.println("PUT_BACK: " + serverCard.getActions().get(CardAction.PUT_BACK));
				break;
			case REVEAL:
				System.out.println("REVEAL: " + serverCard.getActions().get(CardAction.REVEAL));
				break;
			case NONE:
				System.out.println("NONE: " + serverCard.getActions().get(CardAction.NONE));
				break;
			case COUNT_FOR_VICTORY:
				System.out.println("COUNT_VOR_VICTORY: " + serverCard.getActions().get(CardAction.COUNT_FOR_VICTORY));
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
		Card silver = new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(CardAction.NONE),
				CollectionsUtil.linkedList(2)), CollectionsUtil.linkedList(CardType.SILVER), "Silver", 0, null);
		p.getDeck().addCard(CollectionsUtil.linkedList(silver), p.getDeck().getDiscardPile());
		System.out.println("\n" + p.getDeck().toString());
		p.getDeck().shuffle();
		System.out.println("\n" + p.getDeck().toString());
	}
}