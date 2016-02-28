package com.tpps.application.game;

import com.tpps.application.game.card.CardAction;
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

	public static void main(String[] args) {
		Player p = new Player();
		System.out.println(p.deck.toString());
		p.deck.shuffle();
		Card silver = new Card(
				CollectionsUtil.arrayListAction(CardAction.NONE),
				CollectionsUtil.arrayListType(CardType.SILVER), "Silver", 0);
		p.deck.addToDiscard(silver);
		System.out.println("\n" + p.deck.toString());
		p.deck.shuffle();
		System.out.println("\n" + p.deck.toString());
	}
}
