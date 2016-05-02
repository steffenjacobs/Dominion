package com.tpps.application.game;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.logger.DrawAndShuffle;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/**
 * @author Nicolas Wipfler, Lukas Adler
 */
public class Deck {

	private LinkedList<Card> drawPile; 
	private LinkedList<Card> discardPile;
	private LinkedList<Card> cardHand;

	/**
	 * 
	 */
	public Deck(LinkedList<Card> initCards) {
		this.drawPile = new LinkedList<Card>();
		this.discardPile = new LinkedList<Card>();
		this.cardHand = new LinkedList<Card>();
		this.init(initCards);
	}

	/**
	 * 
	 * @param draw
	 * @param discard
	 * @param cardHand
	 */
	public Deck(LinkedList<Card> draw, LinkedList<Card> discard, LinkedList<Card> cardHand) {
		this.drawPile = draw;
		this.discardPile = discard;
		this.cardHand = cardHand;
	}
	
	/**
	 * initializes the deck with 7 COPPER cards and 3 ESTATE cards
	 * shuffles the deck and draws 5 cards from the drawPile
	 */
	private void init(LinkedList<Card> initCards) {
		if (this.drawPile != null) {
			CollectionsUtil.appendListToList(initCards, this.drawPile);		
			
			this.shuffleDrawPile();
		}
		this.draw(GameConstant.INIT_CARD_HAND_SIZE);
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
	 * 
	 * @return the amount of treasure cards which are not in cardHand 
	 * (so the amount treasure carsd in drawPile and discardPile)
	 */
	public int getTreasureAmountNotOnHand() {		
		return getTreasureAmountOfList(drawPile) + getTreasureAmountOfList(discardPile);		
	}
	
	/**
	 * 
	 * @param list the list to investigate
	 * @return the amount of treasure cards in the specific list
	 */
	public int getTreasureAmountOfList(LinkedList<Card> list) {
		int treasureCounter = 0;
		for (Iterator<Card> iterator = drawPile.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(CardType.TREASURE)){
				treasureCounter++;
			}			
		}
		return treasureCounter;
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	public int getTreasureValueOfList(LinkedList<Card> list) {
		int treasureCounter = 0;
		for (Iterator<Card> iterator = getCardsByTypeFromHand(CardType.TREASURE).iterator(); iterator.hasNext();) {
			treasureCounter += Integer.valueOf(iterator.next().getActions().get(CardAction.IS_TREASURE));
		}
		return treasureCounter;
	}
	
	/**
	 * @param cardID individual id of the card as a String
	 * @return the card with cardID in cardHand (without removing it from the list)
	 *         null if the list doesn't contain the card
	 */
	public Card getCardFromHand(String cardID) {
		return getCardFromPile(cardID, this.cardHand);
	}
	
	/**
	 * 
	 * @param name
	 * @return a card which has the given name null otherwise
	 */
	public Card getCardByNameFromHand(String name){
		for (Iterator<Card> iterator = getCardHand().iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getName().toLowerCase().equals(name.toLowerCase())){
				return card;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param type
	 * @return a card which contains the given type null otherwise
	 */
	public Card getCardByTypeFromHand(CardType type){
		for (Iterator<Card> iterator = getCardHand().iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)){
				return card;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return the victoryPoints for the deck
	 */
	public int getVictoryPoints(){
		int victoryPoints = 0;
		LinkedList<Card> victoryCards = getCardsByTypeFromDeck(CardType.VICTORY);
		for (Iterator<Card> iterator = victoryCards.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			victoryPoints += Integer.parseInt(card.getActions().get(CardAction.IS_VICTORY));
		}
		return victoryPoints;		
	}
	
	/**
	 * 
	 * @param type
	 * @return returns a list with all cards of the specified type
	 */
	public LinkedList<Card> getCardsByTypeFromDeck(CardType type) {
		LinkedList<Card> victoryCards = new LinkedList<Card>();
		for (Iterator<Card> iterator = this.cardHand.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)){
				victoryCards.add(card);
			}
		}
		
		for (Iterator<Card> iterator = this.drawPile.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)){
				victoryCards.add(card);
			}
		}
		
		for (Iterator<Card> iterator = this.discardPile.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)){
				victoryCards.add(card);
			}
		}
		
		return victoryCards;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public LinkedList<Card> getCardsByTypeFromHand(CardType type) {
		LinkedList<Card> typeCards = new LinkedList<Card>();
		for (Iterator<Card> iterator = getCardHand().iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)){
				typeCards.addLast(card);
			}
		}
		return typeCards;
	}
	
	/**
	 * 
	 * @return
	 */
	public LinkedList<String> getTreasureCardsFromHand(){
		LinkedList<Card> treasureCards = new LinkedList<Card>();
		for (Iterator<Card> iterator = cardHand.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(CardType.TREASURE)){
				treasureCards.add(card);
			}		
		}
		return CollectionsUtil.getCardIDs(treasureCards);
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
	 * calls discardCardHand() and draw(INIT_CARD_HAND_SIZE) 
	 * (discards the cardHand and redraws 5 cards for the new turn)
	 */
	public DrawAndShuffle refreshCardHand() {
		this.discardCardHand();
		return this.draw(GameConstant.INIT_CARD_HAND_SIZE);
	}
	
	/**
	 * if there are not enough cards in the drawPile (less than the drawAmount), 
	 * the method shuffles the discard pile and appends it "below" the draw pile
	 * @param drawAmount the amount which determines if the piles will be shuffled
	 */	
	private boolean shuffleIfLessThan(int drawAmount) {
		LinkedList<Card> cards = this.discardPile;
		boolean wasShuffled = false;
		if (drawAmount < (this.drawPile.size() + this.discardPile.size())) {
			if (this.drawPile.size() < drawAmount) {
				Collections.shuffle(cards);
				wasShuffled = true;
				for (Card card : this.drawPile) {
					cards.addLast(card);
				}
				this.discardPile = new LinkedList<Card>();
				this.drawPile = cards;
			}
		} else {
			Collections.shuffle(cards);
			wasShuffled = true;
			for (Card card : this.drawPile) {
				cards.addLast(card);
			}
			this.discardPile = new LinkedList<Card>();
			this.drawPile = cards;
		}
		return wasShuffled;
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
	 * appends drawPile to discardPile and creates a new list for drawPile
	 * (discards the drawPile)
	 */
	public void discardDrawPile() {
		CollectionsUtil.appendListToList(this.drawPile, this.discardPile);
		this.drawPile = new LinkedList<Card>();
	}
	
	/**
	 * discards all cards the player has
	 */
	public void discardDeck() {
		discardCardHand();
		discardDrawPile();		
	}
	
	/**
	 * if the drawPile is not empty, the method adds one card from drawPile to cardHand 
	 * and removes this card from drawPile
	 */
	public int draw() {
		if (!this.drawPile.isEmpty()) {
			this.cardHand.addLast(this.drawPile.removeLast());
			return 1;
		} else return 0;
	}
	
	/**
	 * calls shuffleIfLessThan(amount) so there will be enough cardsif the drawPile is not empty, the method adds 'amount' cards from drawPile to cardHand and removes
	 * this card from drawPile
	 * @param amount the amount of cards to draw
	 */
	public DrawAndShuffle draw(int amount) {
		boolean wasShuffled = this.shuffleIfLessThan(amount);
		int drawAmount = 0;
		for (int i = 0; i < amount; i++) {
			drawAmount += this.draw();
		}
		return new DrawAndShuffle(wasShuffled, drawAmount);
	}
	
	/**
	 * if the discardPile contains not enough card the shuffleIfLessThan(1) method is called. 
	 * @return one Card from the drawPile
	 */
	public Card removeSaveFromDrawPile() throws NoSuchElementException{
		this.shuffleIfLessThan(1);		
		return this.drawPile.removeLast();
	}
	
	/**
	 * 
	 * @return true if the cardHand contains a reaction card. false otherwise.
	 */
	public boolean cardHandContainsReactionCard() {
		for (Iterator<Card> iterator = cardHand.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(CardType.REACTION)){
				return true;
			}			
		}
		return false;		
	}
	
	public int amountHandActionCard() {
		int counter = 0; 
		for (Iterator<Card> iterator = cardHand.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(CardType.ACTION)){
				counter++;
			}			
		}
		return counter;	
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
	 * 
	 * @param card
	 * @param trashPile
	 */
	public void trash(Card card, LinkedList<Card> trashPile ) {
		this.getCardHand().remove(card);
		trashPile.addLast(card);
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