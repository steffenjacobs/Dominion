package com.tpps.application.game;


/**
 * 
 * */

public class ServerPlayer extends Player {

	public ServerPlayer() {
		this.deck = new Deck();
		this.cardHandSize = 5;
		// this.id = GameController.getPlayerID();
		// this.port = ;
	}

	public ServerPlayer(Deck deck, int id) {
		this.deck = deck;
		// this.id = GameController.getPlayerID();
	}
}
