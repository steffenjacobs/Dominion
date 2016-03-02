package com.tpps.application.game;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.game.card.ServerCard;
import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * 
 * */

public class ServerPlayer {

	private Deck deck;
	private int id;

	// private static int port;

	public ServerPlayer() {
		this.deck = new Deck();
		// this.id = GameController.getPlayerID();
		// this.port = ;
	}

	public ServerPlayer(Deck deck, int id) {
		this.deck = deck;
		// this.id = GameController.getPlayerID();
	}

	public Deck getDeck() {
		return this.deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	// public int getPlayerID() {
	// return this.id;
	// }
	//
	// public void setID(int validID) {
	// this.id = validID;
	// }

	/**
	 * Test
	 */
	public static void main(String[] args) {
		ServerPlayer p = new ServerPlayer();
		System.out.println(p.deck.toString());
		p.deck.shuffle();
		ServerCard silver = new ServerCard(CollectionsUtil.linkedHashMapAction(
				CollectionsUtil.linkedList(CardAction.NONE),
				CollectionsUtil.linkedList(2)),
				CollectionsUtil.linkedList(CardType.SILVER), "Silver", 0, null);
		p.deck.addCard(CollectionsUtil.linkedList(silver),
				p.deck.getDiscardPile());
		System.out.println("\n" + p.deck.toString());
		p.deck.shuffle();
		System.out.println("\n" + p.deck.toString());
	}
}
