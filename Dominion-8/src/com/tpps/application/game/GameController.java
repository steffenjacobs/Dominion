package com.tpps.application.game;

import java.util.Arrays;
import java.util.LinkedList;

import com.tpps.application.game.card.Card;
import com.tpps.application.network.game.SynchronisationException;
import com.tpps.application.network.game.TooMuchPlayerException;
import com.tpps.technicalServices.util.GameConstant;

/**
 * @author Lukas Adler
 * @author Nicolas Wipfler
 */
public class GameController {

	private LinkedList<Player> players;
	private boolean gameNotFinished;
	private Player activePlayer;
	private GameBoard gameBoard;
	private String gamePhase;

	public GameController() {
		// new Setup().start();
		this.players = new LinkedList<Player>();
		this.gameBoard = new GameBoard();
		
		this.gameNotFinished = true;
	}

	public void setNextActivePlayer() {
		Player activePlayer = this.getActivePlayer();
		LinkedList<Player> players = this.getPlayers();
		for (int i = 0; i < GameConstant.HUMAN_PLAYERS; i++) {
			Player player = players.get(i);
			if (player.equals(activePlayer)) {
				this.setActivePlayer(players.get(i < GameConstant.HUMAN_PLAYERS ? i + 1 : 0));
				break;
			}
		}

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
	 * adds one player to the gameController if four players are registered on the gameController
	 * one randomly choosen player is set as active(this player will begin the game)
	 * @param player
	 * @throws TooMuchPlayerException if there connects one more player
	 */
	public void addPlayer(Player player) throws TooMuchPlayerException {
		if (this.players.size() < 4) {
			this.players.addLast(player);
			if (this.players.size() == 4) {
				this.activePlayer = getRandomPlayer();
			}
		} else {
			throw new TooMuchPlayerException();
		}
	}
	/**
	 * search for the card with the given cardId on the gameBoard if the card exists add this
	 * card to the discardPile of the active player. If the card not exists throw a 
	 * @param cardId
	 * @throws SynchronisationException
	 */
	public void buyOneCard(String cardId) throws SynchronisationException{
		Card card = gameBoard.findAndRemoveCardFromBoard(cardId);
		this.getActivePlayer().getDeck().getDiscardPile().add(card);
	}

	/** 
	 * @return one of the four players who is randomly choosen
	 */
	private Player getRandomPlayer() {
		return this.players.get((int) (Math.random() * 4));
	}
	
	

	public GameBoard getGameBoard() {
		return gameBoard;
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

	public void setDiscardPhase(){
		this.gamePhase = "discardPhase";
	}
	
	public void setActionPhase(){
		this.gamePhase = "actionPhase";
	}
	
	public void setBuyPhase(){
		this.gamePhase = "buyPhase";
	}
	
	public String getGamePhase(){
		return this.gamePhase;
	}

	/** 
	 * CONTROLLER LOGIC; not sure whether the loops are necessary 
	 */
	public void startGame() {	
		this.gamePhase = "actionPhase";
	}

	/**
	 * 
	 */
	// private void turn(Player player) {
	// // turn
	// setActivePlayer(player);
	// }
}
