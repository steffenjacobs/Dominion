package com.tpps.application.game.ai;

import java.util.LinkedList;

import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.technicalServices.network.game.GameServer;

public class ArtificialIntelligence /* implements Runnable */ {

	private Player player;
	// private int sleepTime;

	public ArtificialIntelligence(int clientID, int port, LinkedList<Card> initCards) {
		this.player = new Player(clientID, port, initCards);
		// this.sleepTime = 200;
	}

	public Player getPlayer() {
		return this.player;
	}

	// public int getSleepTime() {
	// return this.sleepTime;
	// }

	public void executeNextTurn() {
		/* Logic */
		checkMyTurn();

		// LinkedList<Card> cardHand = this.player.getDeck().getCardHand();

		playTreasures();

		endTurn();
	}

	private boolean checkMyTurn() {
		return GameServer.getInstance().getGameController().getActivePlayer().getID() == this.player.getID();
	}

	private void endTurn() {

	}

	private void playTreasures() {
		// GameServer.getInstance().getGameController().getGameBoard().
	}

	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	//
	// }
}
