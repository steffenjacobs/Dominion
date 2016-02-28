package com.tpps.application.game;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * 
 * */

public class Player {

	private final Deck deck;
	// private final String name;
	// private final int id;

	public Player() {
		this.deck = new Deck();
	}

	public Player(Deck deck) {
		this.deck = deck;
	}

	/**
	 * calls the static method which executes the actions
	 * 
	 * @author ladler - Lukas Adler
	 */
	public void doAction() {
		// for (CardAction action : actions.keySet()) {
		// switch (action) {
		// case ADD_ACTION_TO_PLAYER:
		// // call
		// break;
		// case ADD_PURCHASE:
		// // call
		// break;
		// case ADD_TEMPORARY_MONEY_FOR_TURN:
		// // call
		// break;
		// case DRAW:
		// // call
		// break;
		// case GAIN:
		// // call
		// break;
		// case DISCARD:
		// // call
		// break;
		// case TRASH:
		// // call
		// break;
		// case PUT_BACK:
		// // call
		// break;
		// case REVEAL:
		// // call
		// break;
		// case NONE:
		// // call
		// break;
		// case COUNT_FOR_VICTORY:
		// // call
		// break;
		// default:
		// // call
		// break;
		// }
		// }
	}

	/**
	 * Test
	 */
	public static void main(String[] args) {
		Player p = new Player();
		System.out.println(p.deck.toString());
		p.deck.shuffle();
		Card silver = new Card(null, CollectionsUtil.arrayListType(CardType.SILVER), "Silver", 0);
		p.deck.addCardToDiscard(silver);
		System.out.println("\n" + p.deck.toString());
		p.deck.shuffle();
		System.out.println("\n" + p.deck.toString());
	}
}
