package com.tpps.application.game;

import java.util.Arrays;
import java.util.LinkedList;

import com.tpps.application.network.game.TooMuchPlayerException;

public class GameController {

	private LinkedList<Player> players;
	private boolean gameNotFinished;
	private Player activePlayer;

	public GameController() {
		// new Setup().start();
		this.players = new LinkedList<Player>();
		this.gameNotFinished = true;
	}

	/**
	 * 
	 */
	public LinkedList<Player> getPlayers() {
		return this.players;
	}

	/**
	 * 
	 */
	public void setPlayers(LinkedList<Player> players) {
		this.players = players;
	}

	/**
	 * 
	 */
	public Player getActivePlayer() {
		return this.activePlayer;
	}

	/**
	 * 
	 */
	public void setActivePlayer(Player aP) {
		this.activePlayer = aP;
	}
	
	/**
	 * 
	 */
	public boolean isGameNotFinished() {
		return this.gameNotFinished;
	}

	/**
	 * 
	 */
	public void setGameNotFinished(boolean gameNotFinished) {
		this.gameNotFinished = gameNotFinished;
	}

	/**
	 * @param player
	 * @return if there are four players
	 * @throws TooMuchPlayerException
	 */
	public void addPlayer(Player player) throws TooMuchPlayerException {
		if (this.players.size() < 4) {
			this.players.addLast(player);
			if (this.players.size() == 4){
				this.activePlayer = getRandomPlayer();				
			}
		} else {
			throw new TooMuchPlayerException();
		}
	}

	/** 
	 * @return one of the four players who is randomly choosen
	 */
	private Player getRandomPlayer() {
		return this.players.get((int)(Math.random()*4));
	}

	/**
	 * 
	 */
	// private boolean gameFinished() {
	// /* Checkt die Stapel durch, ob 3 Stapel leer sind bzw. Provinzen leer */
	// /* Wenn ja: */
	// setGameNotFinished(false);
	// return false; // überarbeiten
	// }

	/**
	 * 
	 */
	public void startGame() {	
		System.out.println(Arrays.toString(this.activePlayer.getDeck().getCardHand().toArray()));
	}

	/**
	 * 
	 */
	// private void turn(Player player) {
	// // turn
	// setActivePlayer(player);
	// }
}
