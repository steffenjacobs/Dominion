package com.tpps.application.game.ai;

import java.util.LinkedList;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.application.storage.CardStorageController;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.ui.gameplay.GameWindow;

public class ArtificialIntelligence {

	private Player player;
	private LinkedList<String> blacklist;
	private ListMultimap<String, Card> aiActions;
	private CardStorageController cardStore;

	// board anschauen, wenn angriffskarten gekauft werden dann defensiv kaufen
	// wenn es nix bringt, mehr karten zu ziehen, ggf. aktionskarten nicht
	// spielen
	// LinkedListMultimap mit "buy" oder "play" und karte als Spielplan aufbauen

	public ArtificialIntelligence() {
		int CLIENT_ID = GameServer.getCLIENT_ID();
		LinkedList<Card> startSet = GameServer.getInstance().getGameController().getGameBoard().getStartSet();
		this.player = new Player(CLIENT_ID, 1337, startSet);
		this.blacklist = this.getCardsFromStorage("Curse");
		this.aiActions = LinkedListMultimap.create();
		this.cardStore = new CardStorageController("cards.bin");
	}

	/**
	 * @return the blacklist
	 */
	public LinkedList<String> getBlacklist() {
		return blacklist;
	}

	/**
	 * @param blacklist the blacklist to set
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
	 * @param cardStore the cardStore to set
	 */
	public void setCardStore(CardStorageController cardStore) {
		this.cardStore = cardStore;
	}

	/**
	 * @return the aiActions
	 */
	public ListMultimap<String, Card> getAiActions() {
		return aiActions;
	}

	/**
	 * @param aiActions
	 *            the aiActions to set
	 */
	public void setAiActions(ListMultimap<String, Card> aiActions) {
		this.aiActions = aiActions;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return this.player;
	}

	public void executeTurn() {
		myTurn();
		// LinkedList<Card> cardHand = this.player.getDeck().getCardHand();
		playTreasures();
		endTurn();
	}
	
	private LinkedList<String> getCardsFromStorage(String... names) {
		LinkedList<String> list = new LinkedList<String>();
		for (String cardname : names) {
			list.addLast(cardStore.getCard(cardname).getName());
		}
		return list;
	}

	private boolean myTurn() {
		return GameServer.getInstance().getGameController().getActivePlayer().equals(this.player);
	}

	private boolean gameNotFinished() {
		return GameServer.getInstance().getGameController().isGameNotFinished();
	}
	
	private void endTurn() {
		GameWindow.endTurn.onMouseClick();
	}

	private void playTreasures() {
		GameWindow.playTreasures.onMouseClick();
	}

	public void start() {
		new Thread(new Runnable() { 
			public void run() {
				while (gameNotFinished()) {
					while (myTurn()) {
					
					}	
				}
			}
		}).start();
	}
	
	public static void main(String[] args) {
		ListMultimap<String, Integer> map = LinkedListMultimap.create();
		map.put("a", 2);
		map.put("b", 3);
		map.put("a", 3);
		map.put("c", 2);
		for (String b : map.keys()) {
			System.out.println(b);
		}
	}
}