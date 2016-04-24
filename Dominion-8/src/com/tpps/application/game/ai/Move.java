package com.tpps.application.game.ai;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.tpps.application.game.card.Card;

public class Move {

	private ListMultimap<String, Card> nextTurn;
	
	public Move() {
		this.nextTurn = LinkedListMultimap.create();
	}

	/**
	 * @return the nextTurn
	 */
	public ListMultimap<String, Card> getNextTurn() {
		return nextTurn;
	}

	/**
	 * @param nextTurn the nextTurn to set
	 */
	public void setNextTurn(ListMultimap<String, Card> nextTurn) {
		this.nextTurn = nextTurn;
	}
	
}
