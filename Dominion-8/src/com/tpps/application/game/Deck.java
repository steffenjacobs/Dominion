package com.tpps.application.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.ServerCard;
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
	private List<ServerCard> drawPile;
	private List<ServerCard> discardPile;

	// TODO: remove estate and copper (only for testing purposes)
	// TODO: replace Action.COUNT_FOR_VICTORY with null or create another
	// constructor? Same with Action.NONE
	// for copper
	private final ServerCard estate = new ServerCard(CollectionsUtil.linkedHashMapAction(
			CollectionsUtil.arrayList(CardAction.COUNT_FOR_VICTORY),
			CollectionsUtil.arrayList(2)),
			CollectionsUtil.arrayList(CardType.VICTORY), "Estate", 2);
	private final ServerCard copper = new ServerCard(CollectionsUtil.linkedHashMapAction(
			CollectionsUtil.arrayList(CardAction.NONE),
			CollectionsUtil.arrayList(0)),
			CollectionsUtil.arrayList(CardType.COPPER), "Copper", 0);

	public int getDeckSize() {
		return deckSize;
	}

	public void setDeckSize(int deckSize) {
		this.deckSize = deckSize;
	}

	public List<ServerCard> getDrawPile() {
		return drawPile;
	}

	public void setDrawPile(List<ServerCard> drawPile) {
		this.drawPile = drawPile;
	}

	public List<ServerCard> getDiscardPile() {
		return discardPile;
	}

	public void setDiscardPile(List<ServerCard> discardPile) {
		this.discardPile = discardPile;
	}

	protected Deck() {
		this.drawPile = new ArrayList<ServerCard>();
		this.discardPile = new ArrayList<ServerCard>();
		this.deckSize = 0;
		init();
	}

	protected Deck(List<ServerCard> draw, List<ServerCard> discard) {
		this.drawPile = draw;
		this.discardPile = discard;
		this.deckSize = draw.size() + discard.size();
	}

	protected void init() {
		if (drawPile != null) {
			addCardToDraw(estate, 3);
			addCardToDraw(copper, 7);
			shuffle();
		}
	}

	public void shuffle() {
		List<ServerCard> cards = new ArrayList<ServerCard>();
		cards.addAll(this.discardPile);
		cards.addAll(this.drawPile);
		Collections.shuffle(cards);
		this.discardPile = new ArrayList<ServerCard>();
		this.drawPile = cards;
	}

	public boolean addCardToDraw(ServerCard card) {
		this.deckSize++;
		return this.drawPile.add(card);
	}

	public boolean addCardToDraw(ServerCard card, int amount) {
		boolean flag = true;
		for (int i = 0; i < amount; i++) {
			flag &= addCardToDraw(card);
		}
		return flag;
	}

	public boolean addCardToDiscard(ServerCard card) {
		this.deckSize++;
		return this.discardPile.add(card);
	}

	public boolean addCardToDiscard(ServerCard card, int amount) {
		boolean flag = true;
		for (int i = 0; i < amount; i++) {
			flag &= addCardToDiscard(card);
		}
		return flag;
	}

	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		Iterator<ServerCard> itrDraw = drawPile.iterator();
		Iterator<ServerCard> itrDisc = discardPile.iterator();
		sBuf.append("drawPile:    <");
		if (drawPile.isEmpty()) {
			sBuf.append("empty");
		} else {
			while (itrDraw.hasNext()) {
				sBuf.append("<" + ((ServerCard) itrDraw.next()).getName() + ">");
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
				sBuf.append("<" + ((ServerCard) itrDisc.next()).getName() + ">");
				if (itrDisc.hasNext()) {
					sBuf.append(" ");
				}
			}
		}
		sBuf.append(">");
		return sBuf.toString();
	}
}
