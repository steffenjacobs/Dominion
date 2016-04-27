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

	private ListMultimap<ExecutionType, Card> playSequence;
	private ListMultimap<ExecutionType, String> buySequence;

	public Move() {
		this.playSequence = LinkedListMultimap.create();
		this.buySequence = LinkedListMultimap.create();
	}
	
	public Move(ListMultimap<ExecutionType, Card> playSequence, ListMultimap<ExecutionType, String> buySequence) {
		this.playSequence = playSequence;
		this.buySequence = buySequence;
	}
	
	public Move clone() {
		return new Move(playSequence, buySequence);
	}
	
	public ListMultimap<ExecutionType, Card> getPlaySequence() {
		return this.playSequence;
	}
	
	public ListMultimap<ExecutionType, String> getBuySequence() {
		return this.buySequence;
	}

	protected void putPlay(Card card) {
		this.playSequence.put(ExecutionType.PLAY, card);
	}
	
	protected void putBuy(String cardname) {
		this.buySequence.put(ExecutionType.BUY, cardname);
	}
}
