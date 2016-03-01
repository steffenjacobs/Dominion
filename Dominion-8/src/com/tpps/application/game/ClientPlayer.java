package com.tpps.application.game;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.game.card.ServerCard;
import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * 
 * */

public class ClientPlayer {

	private Deck deck;
	// private int id;
	// private static int port;

	public ClientPlayer() {
		this.deck = new Deck();
		// this.id = GameController.getPlayerID();
		// this.port = ;
	}

	public ClientPlayer(Deck deck, int id) {
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
		ClientPlayer player = new ClientPlayer();
		ServerCard silver = new ServerCard(CollectionsUtil
				.linkedHashMapAction(CollectionsUtil.arrayList(CardAction.NONE), CollectionsUtil.arrayList(2)),
				CollectionsUtil.arrayList(CardType.SILVER), "Silver", 0);
		System.out.println(player.deck.toString());
		player.deck.shuffle();
		
		player.deck.addCard(silver, player.deck.getDiscardPile());
		System.out.println("\n" + player.deck.toString());
		player.deck.shuffle();
		System.out.println("\n" + player.deck.toString());
	}
}
