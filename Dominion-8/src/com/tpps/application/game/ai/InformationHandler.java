package com.tpps.application.game.ai;

import java.util.LinkedList;

import com.tpps.application.game.Player;
import com.tpps.application.storage.CardStorageController;

/***
 * InformationHandler provides all kind of information about the game and the
 * cardHand of the AI. It contains several methods with basic information for
 * making a decision of the next Move.
 *
 * @author Nicolas Wipfler
 */
public class InformationHandler {

	/**
	 * with the cardStore the AI can compare every handcard with the 'original
	 * card' of the backend
	 */
	private CardStorageController cardStore;
	private LinkedList<String> blacklist;

	public InformationHandler() {
		this.cardStore = new CardStorageController("cards.bin");
		this.blacklist = this.getCardNamesFromStorage("Curse", "Copper", "Estate");
	}

	/**
	 * @return the blacklist
	 */
	public LinkedList<String> getBlacklist() {
		return blacklist;
	}

	/**
	 * @param blacklist
	 *            the blacklist to set
	 */
	public void setBlacklist(LinkedList<String> blacklist) {
		this.blacklist = blacklist;
	}

	/**
	 * @return the cardStore
	 */
	public CardStorageController getCardStore() {
		return cardStore;
	}

	/**
	 * @param cardStore
	 *            the cardStore to set
	 */
	public void setCardStore(CardStorageController cardStore) {
		this.cardStore = cardStore;
	}

	/**
	 * this method is used to check if there is a card called 'name' in the cardStore
	 * so it takes the parameter 'names' and adds every cardname (when
	 * the card is truly available in cardStore) to the return list.
	 * 
	 * @param names
	 *            the names of all Cards which will be get from the CardStore
	 * @return a list of names with the names of only the available cards in the
	 *         game
	 */
	protected LinkedList<String> getCardNamesFromStorage(String... names) {
		LinkedList<String> list = new LinkedList<String>();
		for (String cardname : names) {
			list.addLast(cardStore.getCard(cardname).getName());
		}
		return list;
	}

	/**
	 * 
	 * @param player
	 *            the player to get the information from
	 * @return
	 */
	protected int getTreasureCardsValue(Player player) {
		return player.getDeck().getTreasureValueOfList(player.getDeck().getCardHand());
	}
}
