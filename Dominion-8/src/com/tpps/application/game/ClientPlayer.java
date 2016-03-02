package com.tpps.application.game;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.game.card.ServerCard;
import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
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
		ServerCard eins = new ServerCard(CollectionsUtil.linkedHashMapAction(
				CollectionsUtil.linkedList(CardAction.NONE),
				CollectionsUtil.linkedList(2)),
				CollectionsUtil.linkedList(CardType.SILVER), "Eins", 0, null);
		ServerCard zwei = new ServerCard(CollectionsUtil.linkedHashMapAction(
				CollectionsUtil.linkedList(CardAction.NONE),
				CollectionsUtil.linkedList(2)),
				CollectionsUtil.linkedList(CardType.SILVER), "Zwei", 0, null);
		ServerCard estate = new ServerCard(CollectionsUtil.linkedHashMapAction(
				CollectionsUtil.linkedList(CardAction.COUNT_FOR_VICTORY),
				CollectionsUtil.linkedList(2)),
				CollectionsUtil.linkedList(CardType.VICTORY), "Estate", 2, null);
		ServerCard copper = new ServerCard(CollectionsUtil.linkedHashMapAction(
				CollectionsUtil.linkedList(CardAction.NONE),
				CollectionsUtil.linkedList(0)),
				CollectionsUtil.linkedList(CardType.COPPER), "Copper", 0, null);
		player.deck = new Deck(CollectionsUtil.linkedList(estate),
				CollectionsUtil.linkedList(new ServerCard[] { zwei, estate,
						estate, estate, copper }),
				CollectionsUtil.linkedList(new ServerCard[] { copper, copper,
						copper, copper, copper }));
		player.deck.addCard(CollectionsUtil.linkedList(eins), player.getDeck()
				.getDrawPile());
		System.out.println("\n" + player.deck.toString());
		player.deck.shuffle();
		System.out.println("\n" + player.deck.toString());
		player.deck.draw();
		System.out.println("\n" + player.deck.toString());
		player.deck.putBack(player.deck.getCardHand().removeLast());
		System.out.println("\n" + player.deck.toString());
	}
}
