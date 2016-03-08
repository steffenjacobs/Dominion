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
 * @author Nicolas Wipfler
 */
public class Deck {

	private LinkedList<Card> drawPile; 
	private LinkedList<Card> discardPile;
	private LinkedList<Card> cardHand;

	public Deck() {
		this.drawPile = new LinkedList<Card>();
		this.discardPile = new LinkedList<Card>();
		this.cardHand = new LinkedList<Card>();
		this.init();
	}

	public Deck(LinkedList<Card> draw, LinkedList<Card> discard, LinkedList<Card> cardHand) {
		this.drawPile = draw;
		this.discardPile = discard;
		this.cardHand = cardHand;
	}
	
	/**
	 * initializes the deck with 7 COPPER cards and 3 ESTATE cards
	 * shuffles the deck and draws 5 cards from the drawPile
	 */
	private void init() {
		if (this.drawPile != null) {
			CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_VICTORY, Integer.toString(GameConstant.ESTATE_VALUE)),CollectionsUtil.linkedList(CardType.VICTORY),"Estate", GameConstant.ESTATE_COST), 3,	this.drawPile);
			Card.resetClassID();
			CollectionsUtil.cloneCardToList(new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, Integer.toString(GameConstant.COPPER_VALUE)),CollectionsUtil.linkedList(CardType.TREASURE),"Copper", GameConstant.COPPER_COST), 7, this.drawPile);
			Card.resetClassID();
			this.shuffleDrawPile();
		}
		this.draw(5);
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

	/**
	 * @param cardHand the cardHand to set
	 */
	public void setCardHand(LinkedList<Card> cardHand) {
		this.cardHand = cardHand;
	}
	
	/**
	 * @return the overall decksize (= size of drawPile AND discardPile AND cardHand)
	 */
	public int getDeckSize() {
		return this.drawPile.size() + this.discardPile.size() + this.cardHand.size();
	}

	/**
	 * @param cardID individual id of the card as a String
	 * @return the card with cardID in cardHand (without removing it from the list)
	 *         null if the list doesn't contain the card
	 */
	// suche cardHand mit der card ID durch und returne card
	public Card getCardFromHand(String cardID) {
		return getCardFromPile(cardID, this.cardHand);
	}
	
	public LinkedList<Card> getTreasureCardsFromHand(){
		LinkedList<Card> treasureCards = new LinkedList<Card>();
		for (Iterator<Card> iterator = cardHand.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(CardType.TREASURE)){
				treasureCards.add(card);
			}		
		}
		return treasureCards;
	}
	
	/**
	 * @param cardID individual id of the card as a String
	 * @param searchPile the list where to search the cardID
	 * @return the card with cardID in searchPile (without removing it from the list)
	 *         null if the list doesn't contain the card
	 */
	public Card getCardFromPile(String cardID, LinkedList<Card> searchPile) {
		Iterator<Card> it = searchPile.iterator();
		while (it.hasNext()) {
			Card card = it.next();
			if (card.getId().equals(cardID))
				return card;
		}
		return null;
	}
	
	/**
	 * calls discardCardHand() and draw(5) 
	 * (discards the cardHand and redraws 5 cards for the new turn)
	 */
	public void refreshCardHand() {
		this.discardCardHand();
		this.draw(5);
	}
	
	/**
	 * if there are not enough cards in the drawPile (less than the drawAmount), 
	 * the method shuffles the discard pile and appends it "below" the draw pile
	 * @param drawAmount the amount which determines if the piles will be shuffled
	 */	
	private void shuffleIfLessThan(int drawAmount) {
		LinkedList<Card> cards = this.discardPile;
		if (drawAmount < (this.drawPile.size() + this.discardPile.size())) {
			if (this.drawPile.size() < drawAmount) {
				Collections.shuffle(cards);
				for (Card card : this.drawPile) {
					cards.addLast(card);
				}
				this.discardPile = new LinkedList<Card>();
				this.drawPile = cards;
			}
		} else {
			Collections.shuffle(cards);
			for (Card card : this.drawPile) {
				cards.addLast(card);
			}
			this.discardPile = new LinkedList<Card>();
			this.drawPile = cards;
		}
	}
	
	/**
	 * shuffles only the drawPile
	 * used in init()
	 */
	private void shuffleDrawPile() {
		Collections.shuffle(this.drawPile);
	}

	/**
	 * if cardHand contains the card, it will be removed from cardHand and added to discardPile
	 * @param card the card to discard
	 */
	public void discardCard(Card card) {
		if (this.cardHand.contains(card)) {
			this.cardHand.remove(card);
			this.discardPile.addLast(card);
		}
	}
	
	/**
	 * appends cardHand to discardPile and creates a new list for cardHand
	 * (discards the cardHand)
	 */
	public void discardCardHand() {
		CollectionsUtil.appendListToList(this.cardHand, this.discardPile);
		this.cardHand = new LinkedList<Card>();
	}
	
	/**
	 * if the drawPile is not empty, the method adds one card from drawPile to cardHand 
	 * and removes this card from drawPile
	 */
	public void draw() {
		if (!this.drawPile.isEmpty())
			this.cardHand.addLast(this.drawPile.removeLast());
	}
	
	/**
	 * calls shuffleIfLessThan(amount) so there will be enough cardsif the drawPile is not empty, the method adds 'amount' cards from drawPile to cardHand and removes
	 * this card from drawPile
	 * @param amount the amount of cards to draw
	 */
	public void draw(int amount) {
		this.shuffleIfLessThan(amount);
		for (int i = 0; i < amount; i++) {
			this.draw();
		}
	}

	/**
	 * @param card which will be put back on top of the drawPile
	 */
	public void putBack(Card card) {
		this.drawPile.addLast(card);
	}
	
	/**
	 * @param cards list of cards which will be put back on top of the drawPile
	 */
	public void putBack(LinkedList<Card> cards) {
		for (Card card : cards) {
			this.putBack(card);
		}
	}

	/**
	 * @return String representation of a deck object
	 */
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