package com.tpps.application.game.ai;

import java.util.LinkedList;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.ui.gameplay.GameWindow;

public class ArtificialIntelligence {

	private Player player;
	private LinkedList<Card> blacklist;
	private ListMultimap<String, Card> aiActions;

	// board anschauen, wenn angriffskarten gekauft werden dann defensiv kaufen
	// wenn es nix bringt, mehr karten zu ziehen, ggf. aktionskarten nicht
	// spielen
	// LinkedListMultimap mit "buy" oder "play" und karte als Spielplan aufbauen

	public ArtificialIntelligence(int clientID, int port,
			LinkedList<Card> initCards) {
		this.player = new Player(clientID, port, initCards);
		this.blacklist = new LinkedList<Card>();
		this.aiActions = LinkedListMultimap.create();
	}

	/**
	 * @return the blacklist
	 */
	public LinkedList<Card> getBlacklist() {
		return blacklist;
	}

	/**
	 * @param blacklist
	 *            the blacklist to set
	 */
	public void setBlacklist(LinkedList<Card> blacklist) {
		this.blacklist = blacklist;
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

	public Player getPlayer() {
		return this.player;
	}

	public void executeTurn() {
		myTurn();

		// LinkedList<Card> cardHand = this.player.getDeck().getCardHand();

		playTreasures();

		endTurn();
	}

	private boolean myTurn() {
		return GameServer.getInstance().getGameController().getActivePlayer()
				.equals(this.player);
	}

	private void endTurn() {
		GameWindow.endTurn.onMouseClick();
	}

	private void playTreasures() {
		GameWindow.playTreasures.onMouseClick();
	}

	public static void main(String[] args) {
		ListMultimap<String, Integer> map = LinkedListMultimap.create();
		map.put("a", 2);
		map.put("b", 3);
		map.put("a", 3);
		map.put("c", 2);
		for (Integer b : map.values()) {
			System.out.println(b);
		}
	}
}
