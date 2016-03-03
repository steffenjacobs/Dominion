package com.tpps.application.game;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/**
 * @author nwipfler - Nicolas Wipfler
 */
public class Deck {

	private LinkedList<Card> drawPile;
	private LinkedList<Card> discardPile;
	private LinkedList<Card> cardHand;

	protected Deck() {
		this.drawPile = new LinkedList<Card>();
		this.discardPile = new LinkedList<Card>();
		this.cardHand = new LinkedList<Card>();
		init();
	}

	protected Deck(LinkedList<Card> draw, LinkedList<Card> discard, LinkedList<Card> cardHand) {
		this.drawPile = draw;
		this.discardPile = discard;
		this.cardHand = cardHand;
	}

	/**
	 * @return the drawPile
	 */
	public LinkedList<Card> getDrawPile() {
		return drawPile;
	}

	/**
	 * @param drawPile the drawPile to set
	 */
	public void setDrawPile(LinkedList<Card> drawPile) {
		this.drawPile = drawPile;
	}

	/**
	 * @return the discardPile
	 */
	public LinkedList<Card> getDiscardPile() {
		return discardPile;
	}

	/**
	 * @param discardPile the discardPile to set
	 */
	public void setDiscardPile(LinkedList<Card> discardPile) {
		this.discardPile = discardPile;
	}

	/**
	 * @return the cardHand
	 */
	public LinkedList<Card> getCardHand() {
		return cardHand;
	}

	/**
	 * @param cardHand the cardHand to set
	 */
	public void setCardHand(LinkedList<Card> cardHand) {
		this.cardHand = cardHand;
	}
	
	/**
	 * @return the overall decksize (size of drawPile AND discardPile AND cardHand)
	 * */
	public int getDeckSize() {
		return this.drawPile.size() + this.discardPile.size() + this.cardHand.size();
	}

	/**
	 * @param cardID individual id of the card as a String
	 * @param searchList the list where to search the cardID
	 * @return the card with cardID in searchList (without removing it from the list)
	 *         null if the list doesn't contain the card
	 * */
	// suche cardHand mit der card ID durch und returne card
	public Card getCardFromHand(String cardID) {
		return getCardFromPile(cardID, this.cardHand);
	}
	
	public Card getCardFromPile(String cardID, LinkedList<Card> searchList) {
		Iterator<Card> it = searchList.iterator();
		while (it.hasNext()) {
			Card card = it.next();
			if (card.getId().equals(cardID))
				return card;
		}
		return null;
	}

	// CardAction.IS_VICTORY ; GameConstant.ESTATE_VALUE ; CardType.ESTATE ; "Estate"
	// redundant, create Constructor which only takes GameConstant.'CARDNAME' (e.g. GameConstant.COPPER) 
	// and add switch case to GameConstant class to compute the outcome/created card
	/**
	 * initializes the deck with 7 COPPER cards and 3 ESTATE cards, shuffles the deck and draws 5 cards from the drawPile
	 * */
	private void init() {
		if (this.drawPile != null) {
			CollectionsUtil.cloneCardToListAndResetCardId(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, GameConstant.ESTATE_VALUE),CollectionsUtil.linkedList(CardType.VICTORY),"Estate", GameConstant.ESTATE_COST), 3,	this.drawPile);
			CollectionsUtil.cloneCardToListAndResetCardId(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, GameConstant.COPPER_VALUE),CollectionsUtil.linkedList(CardType.TREASURE),"Copper", GameConstant.COPPER_COST), 7, this.drawPile);
			shuffleDrawPile();
		}
		buildCardHand();
	}

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
		if (this.getDeckSize() >= 5) {
			int size = this.drawPile.size();
			if (size >= 5) {
				//add 5 cards
			} else if (size == 0) {
				shuffleDrawPile();
				size = this.drawPile.size();
				CollectionsUtil.appendListToList(CollectionsUtil.getNextElements(size >= 5 ? 5
						: size, this.drawPile), this.cardHand);
			} else {
				if (this.getDeckSize() <= 5) {
					shuffleDrawPile();
					CollectionsUtil.appendListToList(
							CollectionsUtil.getNextElements(
									this.drawPile.size(), this.drawPile),
							this.cardHand);
				}
				CollectionsUtil.appendListToList(
						CollectionsUtil.getNextElements(size, this.drawPile),
						this.cardHand);
				shuffleDrawPile();
				CollectionsUtil.appendListToList(CollectionsUtil.getNextElements(5 - size,
						this.drawPile), this.cardHand);
			}
		}

		/* --- VARIANTE 2 --- */

		Iterator<Card> it = this.drawPile.iterator();
		int count = 0;
		while (it.hasNext() && count < 5) {
			this.cardHand.addLast(it.next());
		}
		if (count != 4) {
			shuffleDrawPile();
			while (count < 5 && it.hasNext()) {
				count++;
				/* hat java.util.NoSuchElementException geworfen */
				this.cardHand.addLast(it.next());
			}
		}
	}

	public void discardCardHand() {
		CollectionsUtil.appendListToList(cardHand, discardPile);
		this.cardHand = new LinkedList<Card>();
	}
	
	public void discardCard(Card card) {
		if (this.cardHand.contains(card)) {
			
		}
	}

	public void shuffleDrawPile() {
		Collections.shuffle(this.drawPile);
	}
	
	public void shuffleIfLessThan(int amount) {
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.addAll(this.discardPile);
		Collections.shuffle(cards);
		for (Card card : this.drawPile) {
			cards.addLast(card);
		}
		this.discardPile = new LinkedList<Card>();
		this.drawPile = cards;
	}

	
	public void drawCard() {
		this.cardHand.addLast(this.drawPile.removeLast());
	}
	
	/**
	 * adds 1 card from the drawPile to the cardHand of the player and removes
	 * this card from the drawPile. Logic of comparism should be added to
	 * shuffle() method
	 */
	public void draw(int amount) {
		if (this.drawPile.size() != 0) {
			// add card
		} else {
			shuffleIfLessThan(amount);
			if (this.drawPile.size() != 0) {
				// add card
			} else {
				/** keine Karte mehr vorhanden */
			}
		}
		
		this.shuffleIfLessThan(amount);
		for (int i = 0; i < amount; i++) {
			this.cardHand.addLast(this.drawPile.removeLast());
		}
	}

	/**
	 * @param card which will be added on top of the drawPile
	 * */
	public void putBack(Card card) {
		this.drawPile.addLast(card);
	}

	/**
	 * @return String representation of a deck object
	 * */
	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		Iterator<Card> itrDraw = this.drawPile.iterator();
		Iterator<Card> itrDisc = this.discardPile.iterator();
		Iterator<Card> itrCardHand = this.cardHand.iterator();
		sBuf.append("drawPile, size: " + this.drawPile.size() + " <");
		if (this.drawPile.isEmpty()) {
			sBuf.append("empty");
		} else {
			while (itrDraw.hasNext()) {
				sBuf.append("<" + ((Card) itrDraw.next()).getName() + ">");
				if (itrDraw.hasNext()) {
					sBuf.append(" ");
				}
			}
		}
		sBuf.append(">\ndiscPile, size: " + this.discardPile.size() + " <");
		if (this.discardPile.isEmpty()) {
			sBuf.append("empty");
		} else {
			while (itrDisc.hasNext()) {
				sBuf.append("<" + ((Card) itrDisc.next()).getName() + ">");
				if (itrDisc.hasNext()) {
					sBuf.append(" ");
				}
			}
		}
		sBuf.append(">\ncardHand, size: " + this.cardHand.size() + " <");
		if (this.cardHand.isEmpty()) {
			sBuf.append("empty");
		} else {
			while (itrCardHand.hasNext()) {
				sBuf.append("<" + ((Card) itrCardHand.next()).getName() + ">");
				if (itrCardHand.hasNext()) {
					sBuf.append(" ");
				}
			}
		}
		return sBuf.append(">").toString();
	}
}
