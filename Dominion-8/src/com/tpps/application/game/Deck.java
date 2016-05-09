package com.tpps.application.game;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.logger.DrawAndShuffle;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * Deck class represents the piles in the game such as - drawPile - discardPile
 * and the - cardHand
 * 
 * Player owns a deck and interacts with these piles
 * 
 * A few methods might appear to be redundant, but since the game works just
 * fine now in almost every edge case, there is no need to remove working
 * methods right now. Would be future work to make the code more stable
 * 
 * @author Nicolas Wipfler, Lukas Adler
 */
public class Deck {

	private LinkedList<Card> drawPile;
	private LinkedList<Card> discardPile;
	private LinkedList<Card> cardHand;

	/**
	 * 
	 * @param initCards
	 *            the initial card set
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
	 *            the drawPile
	 * @param discard
	 *            the discardPile
	 * @param cardHand
	 *            the cardHand
	 */
	public Deck(LinkedList<Card> draw, LinkedList<Card> discard, LinkedList<Card> cardHand) {
		this.drawPile = draw;
		this.discardPile = discard;
		this.cardHand = cardHand;
	}

	/**
	 * initializes the deck with 7 COPPER cards and 3 ESTATE cards shuffles the
	 * deck and draws 5 cards from the drawPile
	 * 
	 * @param initCards
	 *            the initialization list of cards
	 */
	private void init(LinkedList<Card> initCards) {
		if (this.drawPile != null) {
			CollectionsUtil.appendListToList(initCards, this.drawPile);

			this.shuffleDrawPile();
		}
		this.draw(GameConstant.INIT_CARD_HAND_SIZE.getValue());
	}

	/**
	 * @return the drawPile
	 */
	public LinkedList<Card> getDrawPile() {
		return this.drawPile;
	}

	/**
	 * @param drawPile
	 *            the drawPile to set
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
	 * @param discardPile
	 *            the discardPile to set
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
	 * @param cardHand
	 *            the cardHand to set
	 */
	public void setCardHand(LinkedList<Card> cardHand) {
		this.cardHand = cardHand;
	}

	/**
	 * @return the overall decksize (= size of drawPile AND discardPile AND
	 *         cardHand)
	 */
	public int getDeckSize() {
		return this.drawPile.size() + this.discardPile.size() + this.cardHand.size();
	}

	/**
	 * 
	 * @return the amount of treasure cards which are not in cardHand (so the
	 *         amount treasure carsd in drawPile and discardPile)
	 */
	public int getTreasureAmountNotOnHand() {
		return getTreasureAmountOfList(drawPile) + getTreasureAmountOfList(discardPile);
	}

	/**
	 * 
	 * @param list
	 *            the list to check
	 * @return the amount of treasure cards in the given list
	 */
	public int getTreasureAmountOfList(LinkedList<Card> list) {
		int treasureCounter = 0;
		for (Iterator<Card> iterator = drawPile.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(CardType.TREASURE)) {
				treasureCounter++;
			}
		}
		return treasureCounter;
	}

	/**
	 * 
	 * @param list
	 *            the list to check
	 * @return the value of all treasure cards in the given list
	 */
	public int getTreasureValueOfList(LinkedList<Card> list) {
		int treasureCounter = 0;
		for (Iterator<Card> iterator = getCardsByTypeFrom(CardType.TREASURE, this.cardHand).iterator(); iterator.hasNext();) {
			treasureCounter += Integer.valueOf(iterator.next().getActions().get(CardAction.IS_TREASURE));
		}
		return treasureCounter;
	}

	/**
	 * @param cardID
	 *            individual id of the card as a String
	 * @return the card with cardID in cardHand (without removing it from the
	 *         list) null if the list doesn't contain the card
	 */
	public Card getCardFromHand(String cardID) {
		return getCardFromPile(cardID, this.cardHand);
	}

	/**
	 * 
	 * @param name
	 *            the name to search for
	 * @return a card which has the given name, null otherwise
	 */
	public Card getCardByNameFromHand(String name) {
		for (Iterator<Card> iterator = getCardHand().iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getName().toLowerCase().equals(name.toLowerCase())) {
				return card;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param type
	 *            the CardType to search for
	 * @return a card which contains the given type, null otherwise
	 */
	public Card getCardByTypeFromHand(CardType type) {
		for (Iterator<Card> iterator = getCardHand().iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)) {
				return card;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return the victoryPoints of the deck
	 */
	public int getVictoryPoints() {
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
	 *            the CardType to search for
	 * @return a list with all cards of the specified type
	 */
	public LinkedList<Card> getCardsByTypeFromDeck(CardType type) {
		LinkedList<Card> cards = new LinkedList<Card>();
		for (Iterator<Card> iterator = this.cardHand.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)) {
				cards.add(card);
			}
		}
		for (Iterator<Card> iterator = this.drawPile.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)) {
				cards.add(card);
			}
		}
		for (Iterator<Card> iterator = this.discardPile.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)) {
				cards.add(card);
			}
		}
		return cards;
	}

	/**
	 * 
	 * @param type
	 *            the cardType to search for
	 * @param cardLists
	 *            the cardLists to check
	 * @return a list of all cards of CardType type in the given lists
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<Card> getCardsByTypeFromList(CardType type, LinkedList<Card>... cardLists) {
		LinkedList<Card> result = new LinkedList<Card>();
		for (LinkedList<Card> cardList : cardLists) {
			for (Iterator<Card> iterator = cardList.iterator(); iterator.hasNext();) {
				Card card = (Card) iterator.next();
				if (card.getTypes().contains(type)) {
					result.add(card);
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param type
	 *            the CardType to search for
	 * @param cardList
	 *            the list to check
	 * @return a list of all cards of CardType type from the given list
	 */
	public LinkedList<Card> getCardsByTypeFrom(CardType type, LinkedList<Card> cardList) {
		LinkedList<Card> typeCards = new LinkedList<Card>();
		for (Iterator<Card> iterator = cardList.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)) {
				typeCards.addLast(card);
			}
		}
		return typeCards;
	}

	/**
	 * 
	 * @param type
	 *            the CardType to search for
	 * @param cardList
	 *            the list to check
	 * @return a list of all cardIDs of CardType type from the given list
	 */
	public LinkedList<String> getCardIDsByTypeFrom(CardType type, LinkedList<Card> cardList) {
		LinkedList<Card> treasureCards = new LinkedList<Card>();
		for (Iterator<Card> iterator = cardList.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(CardType.TREASURE)) {
				treasureCards.add(card);
			}
		}
		return CollectionsUtil.getCardIDs(treasureCards);
	}

	/**
	 * @param cardID
	 *            individual id of the card as a String
	 * @param searchPile
	 *            the list where to search the cardID
	 * @return the card with cardID in searchPile (without removing it from the
	 *         list) null if the list doesn't contain the card
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
	 * calls discardCardHand() and draw(INIT_CARD_HAND_SIZE) (discards the
	 * cardHand and redraws 5 cards for the new turn)
	 * 
	 * @return a DrawAndShuffle object which is used by the log to determine
	 *         what happened
	 */
	public DrawAndShuffle refreshCardHand() {
		this.discardCardHand();
		return this.draw(GameConstant.INIT_CARD_HAND_SIZE.getValue());
	}

	/**
	 * if there are not enough cards in the drawPile (less than the drawAmount),
	 * the method shuffles the discard pile and appends it "below" the draw pile
	 * 
	 * @param drawAmount
	 *            the amount which determines if the piles will be shuffled
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
	 * shuffles only the drawPile used in init()
	 */
	private void shuffleDrawPile() {
		Collections.shuffle(this.drawPile);
	}

	/**
	 * if cardHand contains the card, it will be removed from cardHand and added
	 * to discardPile
	 * 
	 * @param card
	 *            the card to discard
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
	 * if the drawPile is not empty, the method adds one card from drawPile to
	 * cardHand and removes this card from drawPile
	 * 
	 * @return 1 if a card was drawn, 0 otherwise
	 */
	public int draw() {
		if (!this.drawPile.isEmpty()) {
			this.cardHand.addLast(this.drawPile.removeLast());
			return 1;
		} else
			return 0;
	}

	/**
	 * calls shuffleIfLessThan(amount) so there will be enough cardsif the
	 * drawPile is not empty, the method adds 'amount' cards from drawPile to
	 * cardHand and removes this card from drawPile
	 * 
	 * @param amount
	 *            the amount of cards to draw
	 * 
	 * @return a DrawAndShuffle object which is used by the GameLog to log what
	 *         happened
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
	 * if the discardPile contains not enough card the shuffleIfLessThan(1)
	 * method is called.
	 * 
	 * @return one Card from the drawPile
	 * @throws NoSuchElementException
	 */
	public Card removeSaveFromDrawPile() throws NoSuchElementException {
		this.shuffleIfLessThan(1);
		return this.drawPile.removeLast();
	}
	
	/**
	 * 
	 * @param name the cardname to search
	 * @return whether the deck contains a card with name *name*
	 */
	public boolean contains(String name) {
		for (Card c : this.drawPile) {
			if (c.getName().equals(name))
				return true;
		}
		for (Card c : this.discardPile) {
			if (c.getName().equals(name))
				return true;
		}
		for (Card c : this.cardHand) {
			if (c.getName().equals(name))
				return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param name the cardname to search
	 * @return the amount of found cards in the deck with the given name
	 */
	public int containsAmountOf(String name) {
		int count = 0;
		for (Card c : this.drawPile) {
			if (c.getName().equals(name))
				count++;
		}
		for (Card c : this.discardPile) {
			if (c.getName().equals(name))
				count++;
		}
		for (Card c : this.cardHand) {
			if (c.getName().equals(name))
				count++;
		}
		return count;
	}
	
	/**
	 * 
	 * @param type the cardtype to search for
	 * @return the amount of found cards in the deck with the given type
	 */
	public int containsAmountOf(CardType type) {
		int count = 0;
		for (Card c : this.drawPile) {
			if (c.getTypes().contains(type))
				count++;
		}
		for (Card c : this.discardPile) {
			if (c.getTypes().contains(type))
				count++;
		}
		for (Card c : this.cardHand) {
			if (c.getTypes().contains(type))
				count++;
		}
		return count;
	}

	/**
	 * 
	 * @param type
	 *            the cardType to search for
	 * @return true if the cardHand contains a card of the CardType type. false
	 *         otherwise.
	 */
	public boolean cardHandContains(CardType type) {
		for (Iterator<Card> iterator = cardHand.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param name
	 *            the name to search for
	 * @return true if the cardHand contains a card with name 'name'. false
	 *         otherwise.
	 */
	public boolean cardHandContains(String name) {
		for (Iterator<Card> iterator = cardHand.iterator(); iterator.hasNext();) {
			String cardname = iterator.next().getName();
			if (cardname.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param type the type to search for in cardHand
	 * @return the amount of cards with type in cardHand
	 */
	public int cardHandAmount(CardType type) {
		int counter = 0;
		for (Iterator<Card> iterator = cardHand.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getTypes().contains(type)) {
				counter++;
			}
		}
		return counter;
	}
	
	/**
	 * 
	 * @param name the name to search for in cardHand
	 * @return the amount of cards with type in cardHand
	 */
	public int cardHandAmount(String name) {
		int counter = 0;
		for (Iterator<Card> iterator = cardHand.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getName().equals(name)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * 
	 * example: cardWithAction(CardAction.ADD_ACTION_TO_PLAYER,
	 * player.getDeck().getCardHand()) returns a card which gives one more
	 * action to the player when played.
	 *
	 * @param action
	 *            the CardAction to consider
	 * @param cards
	 *            the cardList to check
	 * @return a card with action *action*
	 */
	public Card cardWithAction(CardAction action, LinkedList<Card> cards) {
		for (Card card : cards) {
			if (card.getActions().containsKey(action)) {
				return card;
			}
		}
		return null;
	}

	/**
	 * 
	 * example: cardsWithAction(CardAction.ADD_ACTION_TO_PLAYER,
	 * player.getDeck().getCardHand()) returns a list of all cards which give
	 * one more action to the player when played.
	 *
	 * @param action
	 *            the CardAction to consider
	 * @param cards
	 *            the cardList to check
	 * @return a list of cards with actions of type *action*
	 */
	public LinkedList<Card> cardsWithAction(CardAction action, LinkedList<Card> cards) {
		LinkedList<Card> cardList = new LinkedList<Card>();
		for (Card card : cards) {
			if (card.getActions().containsKey(action)) {
				cardList.addLast(card);
			}
		}
		return cardList;
	}

	/**
	 * 
	 * example: cardsWithAction(CardAction.ADD_ACTION_TO_PLAYER,
	 * player.getDeck()) returns a list of all cards which give one more action
	 * to the player when played.
	 *
	 * @param action
	 *            the CardAction to consider
	 * @param deck
	 *            the deck to check
	 * @return a list of cards with actions of type *action*
	 */
	public LinkedList<Card> cardsWithAction(CardAction action, Deck deck) {
		LinkedList<Card> cardList = new LinkedList<Card>();
		for (Card card : deck.getDiscardPile()) {
			if (card.getActions().containsKey(action)) {
				cardList.addLast(card);
			}
		}
		for (Card card : deck.getDrawPile()) {
			if (card.getActions().containsKey(action)) {
				cardList.addLast(card);
			}
		}
		for (Card card : deck.getCardHand()) {
			if (card.getActions().containsKey(action)) {
				cardList.addLast(card);
			}
		}
		return cardList;
	}

	/**
	 * 
	 * @param cards
	 *            the list of cards to check
	 * @return the card of the list with the highest cost
	 */
	public Card cardWithHighestCost(LinkedList<Card> cards) {
		Card maxCostCard = null;
		if (cards != null && cards.size() > 0) {
			if (cards.size() == 1) {
				return cards.get(0);
			} else {
				for (Card card : cards) {
					if (maxCostCard == null) {
						maxCostCard = card;
					} else if (card.getCost() > maxCostCard.getCost()) {
						maxCostCard = card;
					}
				}
			}
		}
		return maxCostCard;
	}

	/**
	 * 
	 * @param type
	 *            cardType of the searched card
	 * @param cards
	 *            the list of cards to check
	 * @return the card of the list with the lowest cost
	 */
	public Card cardWithLowestCost(LinkedList<Card> cards, CardType type) {
		Card minCostCard = null;
		LinkedList<Card> opCards = this.getCardsByTypeFrom(type, cards);
		if (opCards != null && cards.size() > 0) {
			if (opCards.size() == 1) {
				return opCards.get(0);
			} else {
				for (Card card : opCards) {
					if (minCostCard == null) {
						minCostCard = card;
					} else if (card.getCost() < minCostCard.getCost()) {
						minCostCard = card;
					}
				}
			}
		}
		return minCostCard;
	}

	/**
	 * @param card
	 *            which will be put back on top of the drawPile
	 */
	public void putBack(Card card) {
		this.drawPile.addLast(card);
	}

	/**
	 * @param cards
	 *            list of cards which will be put back on top of the drawPile
	 */
	public void putBack(LinkedList<Card> cards) {
		for (Card card : cards) {
			this.putBack(card);
		}
	}

	/**
	 * 
	 * @param card
	 *            the card to trash
	 * @param trashPile
	 *            the trashPile to add the card to
	 */
	public void trash(Card card, LinkedList<Card> trashPile) {
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