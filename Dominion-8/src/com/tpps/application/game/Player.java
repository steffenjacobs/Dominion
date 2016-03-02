package com.tpps.application.game;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.game.card.ServerCard;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/** ServerPlayer */
public class Player {

	private Deck deck;
	private int id;
	private int cardHandSize;
	// aktionen, käufe, etc.
	
	public Player(Deck deck, int id) {
		this.deck = deck;
		this.id = id;
		this.cardHandSize = GameConstant.INITIAL_CARD_HAND_SIZE;
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
	 * Test
	 */
	public static void main(String[] args) {
		Player p = new Player(0);
		System.out.println(p.getDeck().toString());
		p.getDeck().shuffle();
		ServerCard silver = new ServerCard(CollectionsUtil.linkedHashMapAction(
				CollectionsUtil.linkedList(CardAction.NONE),
				CollectionsUtil.linkedList(2)),
				CollectionsUtil.linkedList(CardType.SILVER), "Silver", 0, null);
		p.getDeck().addCard(CollectionsUtil.linkedList(silver),
				p.getDeck().getDiscardPile());
		System.out.println("\n" + p.getDeck().toString());
		p.getDeck().shuffle();
		System.out.println("\n" + p.getDeck().toString());
	}

}