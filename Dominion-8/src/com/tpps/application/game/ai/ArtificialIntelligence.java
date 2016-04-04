package com.tpps.application.game.ai;

import java.util.LinkedList;

import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.ui.gameplay.GameWindow;

public class ArtificialIntelligence {

	private Player player;
	private LinkedList<Card> blacklist;
	
	//board anschauen, wenn angriffskarten gekauft werden dann defensiv kaufen
	//wenn es nix bringt, mehr karten zu ziehen, ggf. aktionskarten nicht spielen

	public ArtificialIntelligence(int clientID, int port, LinkedList<Card> initCards) {
		this.player = new Player(clientID, port, initCards);
	}

	public Player getPlayer() {
		return this.player;
	}

	public void executeTurn() {
//		checkMyTurn();

		LinkedList<Card> cardHand = this.player.getDeck().getCardHand();

		playTreasures();

		endTurn();
	}

	private boolean myTurn() {
		return GameServer.getInstance().getGameController().getActivePlayer().equals(this.player);
	}

	private void endTurn() {
		GameWindow.endTurn.onMouseClick();
	}

	private void playTreasures() {
		GameWindow.playTreasures.onMouseClick();
	}
}
