package com.tpps.application.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * @author nwipfler - Nicolas Wipfler
 * */

public class Deck {

	// - Every player has a deck
	// - Deck has two Lists with CardObjects: drawPile and discardPile
	// - Deck provides functionality to manage the deck and shuffle cards etc.
	
	private int deckSize;
	private List<Card> drawPile;
	private List<Card> discardPile;

	// TODO: remove estate and copper (only for testing purposes)
	// TODO: replace Action.COUNT_FOR_VICTORY with null or create another constructor? Same with Action.NONE
	// for copper
	private final Card estate = new Card(
			CollectionsUtil.arrayListAction(CardAction.COUNT_FOR_VICTORY),
			CollectionsUtil.arrayListType(CardType.VICTORY), "Estate", 2);
	private final Card copper = new Card(
			CollectionsUtil.arrayListAction(CardAction.NONE),
			CollectionsUtil.arrayListType(CardType.COPPER), "Copper", 0);
	
	public int getDeckSize() {
		return deckSize;
	}

	public void setDeckSize(int deckSize) {
		this.deckSize = deckSize;
	}

	public List<Card> getDrawPile() {
		return drawPile;
	}

	public void setDrawPile(List<Card> drawPile) {
		this.drawPile = drawPile;
	}

	public List<Card> getDiscardPile() {
		return discardPile;
	}

	public void setDiscardPile(List<Card> discardPile) {
		this.discardPile = discardPile;
	}

	protected Deck() {
		this.drawPile = new ArrayList<Card>();
		this.discardPile = new ArrayList<Card>();
		this.deckSize = 0;
		init();
	}

	protected Deck(List<Card> draw, List<Card> discard) {
		this.drawPile = draw;
		this.discardPile = discard;
		this.deckSize = draw.size() + discard.size();
	}

	protected void init() {
		if (drawPile != null) {
			addToDraw(estate, 3);
			addToDraw(copper, 7);
			shuffle();
		}
	}

	public void shuffle() {
		List<Card> cards = new ArrayList<Card>();
		cards.addAll(this.discardPile);
		cards.addAll(this.drawPile);
		Collections.shuffle(cards);
		this.discardPile = new ArrayList<Card>();
		this.drawPile = cards;
	}

	public boolean addToDraw(Card card) {
		this.deckSize++;
		return this.drawPile.add(card);
	}

	public boolean addToDraw(Card card, int amount) {
		boolean flag = true;
		for (int i = 0; i < amount; i++) {
			flag &= addToDraw(card);
		}
		return flag;
	}

	public boolean addToDiscard(Card card) {
		this.deckSize++;
		return this.discardPile.add(card);
	}

	public boolean addToDiscard(Card card, int amount) {
		boolean flag = true;
		for (int i = 0; i < amount; i++) {
			flag &= addToDiscard(card);
		}
		return flag;
	}

	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		Iterator<Card> itrDraw = drawPile.iterator();
		Iterator<Card> itrDisc = discardPile.iterator();
		sBuf.append("drawPile:    <");
		if (drawPile.isEmpty()) {
			sBuf.append("empty");
		} else {
			while (itrDraw.hasNext()) {
				sBuf.append("<" + ((Card) itrDraw.next()).getName() + ">");
				if (itrDraw.hasNext()) {
					sBuf.append(" ");
				}
			}
		}
		sBuf.append(">\ndiscardPile: <");
		if (discardPile.isEmpty()) {
			sBuf.append("empty");
		} else {
			while (itrDisc.hasNext()) {
				sBuf.append("<" + ((Card) itrDisc.next()).getName() + ">");
				if (itrDisc.hasNext()) {
					sBuf.append(" ");
				}
			}
		}
		sBuf.append(">");
		return sBuf.toString();
	}
}
