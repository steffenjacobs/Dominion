package com.tpps.application.game.ai;

import java.util.LinkedList;

import com.tpps.application.game.Player;
import com.tpps.application.storage.CardStorageController;
import com.tpps.technicalServices.network.game.GameServer;

public class GameView {

	private GameServer gameServer;
	private LinkedList<String> blacklist;
	private CardStorageController cardStore;

	/**
	 * blacklist basically blacklists all cards the AI should never buy, except special situations
	 * 
	 * @param gameServer the GameServer which contains all relevant game information for the AI
	 */
	public GameView(GameServer gameServer) {
		this.gameServer = gameServer;
		this.blacklist = this.getCardNamesFromStorage("Curse", "Copper", "Estate");
		this.cardStore = new CardStorageController("cards.bin");
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
	 * this method is used to see in the cardStore if there is a card called
	 * 'name' so it takes the parameter 'names' and adds every cardname (when
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
	 * @return if the game is NOT finished (so when the method returns true, the
	 *         game is still running)
	 */
	protected boolean notFinished() {
		return this.gameServer.getGameController().isGameNotFinished();
	}

	/**
	 * 
	 * @return if its the AIs turn
	 */
	protected boolean myTurn(Player player) {
		return this.gameServer.getGameController().getActivePlayer().equals(player);
	}
}
