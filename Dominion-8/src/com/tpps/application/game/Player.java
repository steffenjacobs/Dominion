package com.tpps.application.game;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.game.card.ServerCard;
import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * 
 * */

public class Player {

	private Deck deck;
	// private int id;
	// private static int port;

	public Player() {
		this.deck = new Deck();
		// this.id = GameController.getPlayerID();
		// this.port = ;
	}

	public Player(Deck deck, int id) {
		this.deck = deck;
		// this.id = GameController.getPlayerID();
	}

	public Deck getDeck() {
		return this.deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	// public int getPlayerID() {
	// return this.id;
	// }
	//
	// public void setID(int validID) {
	// this.id = validID;
	// }

	/**
	 * Test
	 */
	public static void main(String[] args) {
		Player p = new Player();
		System.out.println(p.deck.toString());
		p.deck.shuffle();
		ServerCard silver = new ServerCard(CollectionsUtil
				.linkedHashMapAction(CollectionsUtil.arrayList(CardAction.NONE), CollectionsUtil.arrayList(2)),
				CollectionsUtil.arrayList(CardType.SILVER), "Silver", 0);
		p.deck.addCard(silver, p.deck.getDiscardPile());
		System.out.println("\n" + p.deck.toString());
		p.deck.shuffle();
		System.out.println("\n" + p.deck.toString());
	}
}
