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

	private ListMultimap<Execute, Card> playSequence;
	private ListMultimap<Execute, String> buySequence;
	private boolean ready;

	public Move() {
		this.playSequence = LinkedListMultimap.create();
		this.buySequence = LinkedListMultimap.create();
		this.ready = false;
	}
	
	public Move(ListMultimap<Execute, Card> playSequence, ListMultimap<Execute, String> buySequence, boolean ready) {
		this.playSequence = playSequence;
		this.buySequence = buySequence;
		this.ready = ready;
	}
	
	public Move clone() {
		return new Move(playSequence, buySequence, ready);
	}
	
	public ListMultimap<Execute, Card> getPlaySequence() {
		return this.playSequence;
	}
	
	public ListMultimap<Execute, String> getBuySequence() {
		return this.buySequence;
	}
	
	public boolean isReady() {
		return this.ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	protected void putPlay(Card card) {
		this.playSequence.put(Execute.PLAY, card);
	}
	
	protected void putBuy(String cardname) {
		this.buySequence.put(Execute.BUY, cardname);
	}
}
