package com.tpps.application.game;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.game.card.ServerCard;
import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * @author nwipfler - Nicolas Wipfler
 */

public class Deck {

	private LinkedList<ServerCard> drawPile;
	private LinkedList<ServerCard> discardPile;
	private LinkedList<ServerCard> cardHand;

	protected Deck() {
		this.drawPile = new LinkedList<ServerCard>();
		this.discardPile = new LinkedList<ServerCard>();
		this.cardHand = new LinkedList<ServerCard>();
		init();
	}

	protected Deck(LinkedList<ServerCard> draw, LinkedList<ServerCard> discard, LinkedList<ServerCard> cardHand) {
		this.drawPile = draw;
		this.discardPile = discard;
		this.cardHand = cardHand;
	}

	public int getDeckSize() {
		return this.drawPile.size() + this.discardPile.size() + this.cardHand.size();
	}

	public LinkedList<ServerCard> getDrawPile() {
		return this.drawPile;
	}

	public void setDrawPile(LinkedList<ServerCard> drawPile) {
		this.drawPile = drawPile;
	}

	public LinkedList<ServerCard> getDiscardPile() {
		return discardPile;
	}

	public void setDiscardPile(LinkedList<ServerCard> discardPile) {
		this.discardPile = discardPile;
	}

	public LinkedList<ServerCard> getCardHand() {
		return this.cardHand;
	}

	public void setCardHand(LinkedList<ServerCard> cardHand) {
		this.cardHand = cardHand;
	}

	public ServerCard getCard(ServerCard card) {
		// suche cardHand mit der card ID durch und returne card
		return null;
	}

	protected void init() {
		if (this.drawPile != null) {
			// TODO: replace Action.COUNT_FOR_VICTORY with null or create
			// another constructor? Same with Action.NONE for copper
			addCard(new ServerCard(
					CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(CardAction.COUNT_FOR_VICTORY),
							CollectionsUtil.linkedList(2)),
					CollectionsUtil.linkedList(CardType.VICTORY), "Estate", 2, null), 3, this.drawPile);
			addCard(new ServerCard(
					CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(CardAction.NONE),
							CollectionsUtil.linkedList(0)),
					CollectionsUtil.linkedList(CardType.COPPER), "Copper", 0, null), 7, this.drawPile);
			shuffle();
		}
		buildCardHand();
	}

	/**
	 * TESTING, z.B. draw Methode kann dann ausgelagert werden in Logik Klasse,
	 * ist nur zum Testen
	 */

	/**
	 * redraws 5 Cards for the Player
	 * 
	 * <<<<<<<<<<<<<<<<method should use the draw() method below>>>>>>>>>>,
	 * which uses the shuffle()
	 * 
	 * method shuffle() should always check, if there are less cards in the
	 * drawPile than the amount of cards we want to draw. if this is the case:
	 * 
	 * LIST Nachziehstapel HAS 3 CARDS LIST Ablagestapel HAS 20 CARDS we want to
	 * draw 4 cards
	 * 
	 * shuffle should mix the Ablagestapel and add all remaining cards from
	 * Nachziehstapel on the top of the new list, so at first the 3 'old' cards
	 * will be drawn, and after that 1 card of the new shuffled list
	 */
	public void buildCardHand() {
		/* --- VARIANTE 1 --- */
		// if (this.getDeckSize() >= 5) {
		// int size = this.drawPile.size();
		// if (size >= 5) {
		// this.addCard(CollectionsUtil.getNextElements(5, this.drawPile),
		// this.cardHand);
		// } else if (size == 0) {
		// shuffle();
		// size = this.drawPile.size();
		// this.addCard(CollectionsUtil.getNextElements(size >= 5 ? 5 : size,
		// this.drawPile), this.cardHand);
		// } else {
		// if (this.getDeckSize() <= 5) {
		// shuffle();
		// this.addCard(CollectionsUtil.getNextElements(this.drawPile.size(),
		// this.drawPile), this.cardHand);
		// }
		// this.addCard(CollectionsUtil.getNextElements(size, this.drawPile),
		// this.cardHand);
		// shuffle();
		// this.addCard(CollectionsUtil.getNextElements(5 - size,
		// this.drawPile), this.cardHand);
		// }
		// }

		/* --- VARIANTE 2 --- */

		Iterator<ServerCard> it = this.drawPile.iterator();
		int count = 0;
		while (it.hasNext() && count < 5) {
			this.cardHand.addLast(it.next());
		}
		if (count != 4) {
			shuffle();
			while (count < 5) {
				count++;
				/* hat java.util.NoSuchElementException geworfen */
				this.cardHand.addLast(it.next());
			}
		}
	}

	// public void discardCardHand() {}

	public void shuffle() {
		LinkedList<ServerCard> cards = new LinkedList<ServerCard>();
		cards.addAll(this.discardPile);
		Collections.shuffle(cards);
		for (ServerCard card : this.drawPile) {
			cards.addLast(card);
		}
		this.discardPile = new LinkedList<ServerCard>();
		this.drawPile = cards;
	}

	/**
	 * adds 1 card from the drawPile to the cardHand of the player and removes
	 * this card from the drawPile. Logic of comparism should be added to
	 * shuffle() method
	 */
	public void draw() {
		// if (this.drawPile.size() != 0) {
		// this.addCard(CollectionsUtil.getNextElements(1, this.drawPile),
		// this.cardHand);
		// } else {
		// shuffle();
		// if (this.drawPile.size() != 0) {
		// this.addCard(CollectionsUtil.getNextElements(1, this.drawPile),
		// this.cardHand);
		// } else {
		// /** keine Karte mehr vorhanden */
		// }
		// }
		this.cardHand.addLast(this.drawPile.removeLast());
	}

	public void putBack(ServerCard card) {
		this.drawPile.addLast(card);
	}

	/** ENDOF TESTING */

	// addCard in CollectionsUtil oder sinnlos? JA
	/**
	 * adds the same card 'amount'-times to the list in parameters
	 */
	public void addCard(ServerCard card, int amount, LinkedList<ServerCard> destination) {
		for (int i = 0; i < amount; i++) {
			destination.addLast((ServerCard) card.clone());
		}
	}

	/**
	 * adds a list of cards to the (destination-)list in parameters
	 */
	public void addCard(LinkedList<ServerCard> cards, LinkedList<ServerCard> destination) {
		for (ServerCard card : cards) {
			destination.addLast(card);
		}
	}

	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		Iterator<ServerCard> itrDraw = drawPile.iterator();
		Iterator<ServerCard> itrDisc = discardPile.iterator();
		Iterator<ServerCard> itrCardHand = cardHand.iterator();
		sBuf.append("drawPile, size: " + drawPile.size() + " <");
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
		sBuf.append(">\ndiscPile, size: " + discardPile.size() + " <");
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
		sBuf.append(">\ncardHand, size: " + cardHand.size() + " <");
		if (cardHand.isEmpty()) {
			sBuf.append("empty");
		} else {
			while (itrCardHand.hasNext()) {
				sBuf.append("<" + ((ServerCard) itrCardHand.next()).getName() + ">");
				if (itrCardHand.hasNext()) {
					sBuf.append(" ");
				}
			}
		}
		return sBuf.append(">").toString();
	}
}
