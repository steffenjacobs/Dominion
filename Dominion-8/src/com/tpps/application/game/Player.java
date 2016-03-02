package com.tpps.application.game;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.game.card.ServerCard;
import com.tpps.technicalServices.util.CollectionsUtil;

public class Player {

	protected Deck deck;
	private int id;
	protected int cardHandSize;
	private static int port;

	public Player() {
		super();
	}

	public Deck getDeck() {
		return this.deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	public void setCardHandSize(int cardHandSize) {
		this.cardHandSize = cardHandSize;
	}

	public int getCardHandSize() {
		return this.cardHandSize;
	}

	/**
	 * Test
	 */
	public static void main(String[] args) {
		ServerPlayer p = new ServerPlayer();
		System.out.println(p.deck.toString());
		p.deck.shuffle();
		ServerCard silver = new ServerCard(CollectionsUtil.linkedHashMapAction(
				CollectionsUtil.linkedList(CardAction.NONE),
				CollectionsUtil.linkedList(2)),
				CollectionsUtil.linkedList(CardType.SILVER), "Silver", 0, null);
		p.deck.addCard(CollectionsUtil.linkedList(silver),
				p.deck.getDiscardPile());
		System.out.println("\n" + p.deck.toString());
		p.deck.shuffle();
		System.out.println("\n" + p.deck.toString());
	}

}