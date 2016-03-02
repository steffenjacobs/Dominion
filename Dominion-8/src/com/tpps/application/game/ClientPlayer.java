package com.tpps.application.game;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class ClientPlayer extends Player {

	public ClientPlayer() {
		this.deck = new Deck();
		// this.id = GameController.getPlayerID();
		// this.port = ;
	}

	public ClientPlayer(Deck deck, int id) {
		this.deck = deck;
		// this.id = GameController.getPlayerID();
	}

	// public int getPlayerID() {
	// return this.id;
	// }
	//
	// public void setID(int validID) {
	// this.id = validID;
	// }

}
