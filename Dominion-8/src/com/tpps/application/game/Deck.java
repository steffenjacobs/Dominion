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
		this.init();
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
		return this.drawPile;
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
		return this.discardPile;
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
		return this.cardHand;
	}
	
	public LinkedList<String> getCardHandIds(){
		LinkedList<String> cardHandIds = new LinkedList<String>();
		for (Iterator<Card> iterator = cardHand.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			cardHandIds.add(card.getId());			
		}
		return cardHandIds;
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
			CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, GameConstant.ESTATE_VALUE),CollectionsUtil.linkedList(CardType.VICTORY),"Estate", GameConstant.ESTATE_COST), 3,	this.drawPile);
			Card.resetClassID();
			CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, GameConstant.COPPER_VALUE),CollectionsUtil.linkedList(CardType.TREASURE),"Copper", GameConstant.COPPER_COST), 7, this.drawPile);
			Card.resetClassID();
			this.shuffleDrawPile();
		}
		this.draw(5);
	}

	public void refreshCardHand() {
		this.discardCardHand();
		this.draw(5);
	}

	public void discardCard(Card card) {
		if (this.cardHand.contains(card)) {
			this.cardHand.remove(card);
			this.discardPile.addLast(card);
		}
	}
	
	private void discardCardHand() {
		CollectionsUtil.appendListToList(this.cardHand, this.discardPile);
		this.cardHand = new LinkedList<Card>();
	}
	
	/**
	 * (Methode heißt shuffle, weil es das regulaere Mischen ist;
	 *  kann auch shuffleDiscardBelowDrawPile() genannt werden
	 * shuffles the discard pile and appends it "below" the draw pile 
	 * (legt gemischten Ablage- unter Nachziehstapel)
	 * */
	private void shuffle() {
		LinkedList<Card> cards = new LinkedList<Card>();
		cards.addAll(this.discardPile);
		Collections.shuffle(cards);
		for (Card card : this.drawPile) {
			cards.addLast(card);
		}
		this.discardPile = new LinkedList<Card>();
		this.drawPile = cards;
	}
	
	private void shuffleIfLessThan(int drawAmount) {
		if (drawAmount < (this.drawPile.size() + this.discardPile.size())) {
			if (this.drawPile.size() < drawAmount) {
				int remaining = drawAmount - this.drawPile.size();
				draw(this.drawPile.size());
				shuffle();
				draw(remaining);
			} else {
				draw(drawAmount);
			}	
		} else {
			shuffle();
			draw(this.drawPile.size());
		}
	}
	
	private void shuffleDrawPile() {
		Collections.shuffle(this.drawPile);
	}
	
	public void draw() {
		this.cardHand.addLast(this.drawPile.removeLast());
	}
	
	/**
	 * adds 1 card from the drawPile to the cardHand of the player and removes
	 * this card from the drawPile. Logic of comparism should be added to
	 * shuffle() method
	 */
	private void draw(int amount) {
//		this.shuffleIfLessThan(amount);
		for (int i = 0; i < amount; i++) {
			this.draw();
		}
	}

	/**
	 * @param card which will be added on top of the drawPile
	 * */
	private void putBack(Card card) {
		this.drawPile.addLast(card);
	}
	
	/**
	 * 
	 * */
	private void putBack(LinkedList<Card> cards) {
		for (Card card : cards) {
			this.putBack(card);
		}
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
