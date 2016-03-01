package com.tpps.application.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.game.card.ServerCard;
import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * @author nwipfler - Nicolas Wipfler
 */

public class Deck {

	/***********************************************************/
	/**														   */
	/** DECK muss eine SORTED LIST sein */
	/** (siehe zB Spion) */
	/** nicht vergessen zu ändern */
	/**								 	 			 	   	   */
	/***********************************************************/

	private int deckSize;
	private List<ServerCard> drawPile;
	private List<ServerCard> discardPile;
	private List<ServerCard> cardHand;

	// TODO: remove estate and copper (only for testing purposes)
	// TODO: replace Action.COUNT_FOR_VICTORY with null or create another
	// constructor? Same with Action.NONE for copper
	private final ServerCard estate = new ServerCard(CollectionsUtil
			.linkedHashMapAction(CollectionsUtil.arrayList(CardAction.COUNT_FOR_VICTORY), CollectionsUtil.arrayList(2)),
			CollectionsUtil.arrayList(CardType.VICTORY), "Estate", 2);
	private final ServerCard copper = new ServerCard(CollectionsUtil
			.linkedHashMapAction(CollectionsUtil.arrayList(CardAction.NONE), CollectionsUtil.arrayList(0)),
			CollectionsUtil.arrayList(CardType.COPPER), "Copper", 0);

	protected Deck() {
		this.drawPile = new ArrayList<ServerCard>();
		this.discardPile = new ArrayList<ServerCard>();
		this.cardHand = new ArrayList<ServerCard>();
		this.deckSize = 0;
		init();
	}

	protected Deck(List<ServerCard> draw, List<ServerCard> discard, List<ServerCard> cardHand) {
		this.drawPile = draw;
		this.discardPile = discard;
		this.cardHand = cardHand;
		this.deckSize = draw.size() + discard.size() + cardHand.size();
	}

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

	public List<ServerCard> getCardHand() {
		return this.cardHand;
	}

	public void setCardHand(List<ServerCard> cardHand) {
		this.cardHand = cardHand;
	}

	protected void init() {
		if (this.drawPile != null) {
			addCard(this.estate, 3, this.drawPile);
			addCard(this.copper, 7, this.drawPile);
			shuffle();
		}
		buildCardHand();
	}
	
	
	/** TESTING, z.B. draw Methode kann dann ausgelagert werden in Logik Klasse, ist nur zum Testen */

	/**
	 * redraws 5 Cards for the Player
	 * 
	 * <<<<<<<<<<<<<<<<method should use the draw() method below>>>>>>>>>>, which uses the shuffle()
	 * 
	 * method shuffle() should always check, if there are less cards in the
	 * drawPile than the amount of cards we want to draw. if this is the case:
	 * 
	 * LIST Nachziehstapel HAS 3 CARDS 
	 * LIST Ablagestapel HAS 20 CARDS
	 * we want to draw 4 cards
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
				this.addCard(CollectionsUtil.getNextElements(5, this.drawPile), this.cardHand);
			} else if (size == 0) {
				shuffle();
				size = this.drawPile.size();
				this.addCard(CollectionsUtil.getNextElements(size >= 5 ? 5 : size, this.drawPile), this.cardHand);
			} else {
				if (this.getDeckSize() <= 5) {
					shuffle();
					this.addCard(CollectionsUtil.getNextElements(this.drawPile.size(), this.drawPile), this.cardHand);
				}
				this.addCard(CollectionsUtil.getNextElements(size, this.drawPile), this.cardHand);
				shuffle();
				this.addCard(CollectionsUtil.getNextElements(5 - size, this.drawPile), this.cardHand);
			}
		}

		/* --- VARIANTE 2 --- */

		// Iterator<ServerCard> it = this.drawPile.iterator();
		// int count = 0;
		// while (it.hasNext() && count < 5) {
		// this.addCard(it.next(), this.cardHand);
		// }
		// if (count != 4) {
		// shuffle();
		// while (count < 5) {
		// count++;
		// /* hat java.util.NoSuchElementException geworfen*/
		// this.addCard(it.next(), this.cardHand);
		// }
		// }
	}
	
	public void shuffle() {
		List<ServerCard> cards = new ArrayList<ServerCard>();
		cards.addAll(this.discardPile);
		cards.addAll(this.drawPile);
		Collections.shuffle(cards);
		this.discardPile = new ArrayList<ServerCard>();
		this.drawPile = cards;
	}
    
	
	/**
	 * adds 1 card from the drawPile to the cardHand of the player and removes
	 * this card from the drawPile. Logic of comparism should be added to
	 * shuffle() method
	 */
	public void draw() {
		if (this.drawPile.size() != 0) {
			this.addCard(CollectionsUtil.getNextElements(1, this.drawPile), this.cardHand);
		} else {
			shuffle();
			if (this.drawPile.size() != 0) {
				this.addCard(CollectionsUtil.getNextElements(1, this.drawPile), this.cardHand);
			} else {
				/** keine Karte mehr vorhanden */
			}
		}
	}

	public void putBack(ServerCard card) {
		
	}

	/** ENDOF TESTING*/
	
	
	/**
	 * adds a single Card to the list in parameters
	 */
	public boolean addCard(ServerCard card, List<ServerCard> list) {
		this.deckSize++;
		return list.add(card);
	}

	/**
	 * adds the same card 'amount'-times to the list in parameters
	 */
	public boolean addCard(ServerCard card, int amount, List<ServerCard> list) {
		boolean flag = true;
		for (int i = 0; i < amount; i++) {
			flag &= addCard(card, list);
		}
		return flag;
	}

	/**
	 * adds a list of cards to the (destination-)list in parameters
	 */
	public boolean addCard(List<ServerCard> cards, List<ServerCard> destination) {
		boolean flag = true;
		for (ServerCard card : cards) {
			flag &= addCard(card, destination);
		}
		return flag;
	}

	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		Iterator<ServerCard> itrDraw = drawPile.iterator();
		Iterator<ServerCard> itrDisc = discardPile.iterator();
		Iterator<ServerCard> itrCardHand = cardHand.iterator();
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
		sBuf.append(">\ncardHand: <");
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
		return sBuf.toString();
	}
}
