package com.tpps.application.game.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * @author nwipfler - Nicolas Wipfler
 * */

public class Deck {

	// - Every player has a deck
	// - Deck has two Lists with CardObjects: drawPile and discardPile
	// - Deck provides functionality to manage the deck and shuffle cards etc.

	private int deckSize;
	private List<CardObject> drawPile;
	private List<CardObject> discardPile;
	
	// TODO: remove estate and copper (only for testing purposes)
	// TODO: replace Action.COUNT_FOR_VICTORY with null? Same with Action.NONE for copper
	private final CardObject estate = new CardObject(
			CollectionsUtil.arrayListAction(Action.COUNT_FOR_VICTORY),
			CollectionsUtil.arrayListType(Type.VICTORY), "Estate", 2);
	private final CardObject copper = new CardObject(
			CollectionsUtil.arrayListAction(Action.NONE),
			CollectionsUtil.arrayListType(Type.COPPER), "Copper", 0);

	protected Deck() {
		this.drawPile = new ArrayList<CardObject>();
		this.discardPile = new ArrayList<CardObject>();
		this.deckSize = 0;
		init();
	}

	protected Deck(List<CardObject> draw, List<CardObject> discard) {
		this.drawPile = draw;
		this.discardPile = discard;
		this.deckSize = draw.size() + discard.size();
	}

	protected void init() {
		if (drawPile != null) {
			addCardToDrawPile(estate, 3);
			addCardToDrawPile(copper, 7);
			shuffle();
		}
	}

	public void shuffle() {
		List<CardObject> cards = new ArrayList<CardObject>();
		cards.addAll(this.discardPile);
		cards.addAll(this.drawPile);
		Collections.shuffle(cards);
		this.discardPile = new ArrayList<CardObject>();
		this.drawPile = cards;
	}

	public boolean addCardToDrawPile(CardObject card) {
		this.deckSize++;
		return this.drawPile.add(card);
	}

	public boolean addCardToDiscardPile(CardObject card) {
		this.deckSize++;
		return this.discardPile.add(card);
	}

	public boolean addCardToDrawPile(CardObject card, int amount) {
		boolean flag = true;
		for (int i = 0; i < amount; i++) {
			flag &= addCardToDrawPile(card);
		}
		return flag;
	}

	public boolean addCardToDiscardPile(CardObject card, int amount) {
		boolean flag = true;
		for (int i = 0; i < amount; i++) {
			flag &= addCardToDiscardPile(card);
		}
		return flag;
	}
}
