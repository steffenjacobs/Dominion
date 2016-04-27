package com.tpps.application.game.ai;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.tpps.application.game.card.Card;

/**
 * This class represents one move that can be made. It is used by the AI to
 * specify the move that it wants to perform on its turn. A move consists of a
 * LinkedListMultimap which maps a type ("buy" or "play") as a String to a Card.
 * 
 * @author Nicolas Wipfler
 * 
 */
public class Move {

	private ListMultimap<String, Card> nextTurn;

	public Move() {
		this.nextTurn = LinkedListMultimap.create();
	}
	
	public Move(ListMultimap<String, Card> nextTurn) {
		this.nextTurn = nextTurn;
	}
	
	public Move clone() {
		return new Move(nextTurn);
	}

	/**
	 * @return the nextTurn
	 */
	public ListMultimap<String, Card> getNextTurn() {
		return nextTurn;
	}

	/**
	 * @param nextTurn
	 *            the nextTurn to set
	 */
	public void setNextTurn(ListMultimap<String, Card> nextTurn) {
		this.nextTurn = nextTurn;
	}

	private void put(String name, Card card) {
		
	}
}
