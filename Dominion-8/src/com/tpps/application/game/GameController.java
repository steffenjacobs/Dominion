package com.tpps.application.game;

import java.util.Arrays;
import java.util.LinkedList;

import com.tpps.application.network.game.TooMuchPlayerException;
import com.tpps.technicalServices.util.GameConstant;

public class GameController {

	private LinkedList<Player> players;
	private boolean gameNotFinished;
	private Player activePlayer;
	private GameBoard gameBoard;

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

	public LinkedList<Player> getPlayers() {
		return this.players;
	}

	public void setPlayers(LinkedList<Player> players) {
		this.players = players;
	}

	public Player getActivePlayer() {
		return this.activePlayer;
	}

	public void setActivePlayer(Player aP) {
		this.activePlayer = aP;
	}

	public boolean isGameNotFinished() {
		return this.gameNotFinished;
	}

	public boolean setGameNotFinished(boolean gameNotFinished) {
		return this.gameNotFinished = gameNotFinished;
	}

	/**
	 * 
	 * @param player
	 * @return if there are four players
	 * @throws TooMuchPlayerException
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
	 * 
	 * @return one of the four players who is randomly choosen
	 */
	private Player getRandomPlayer() {
		return this.players.get((int) (Math.random() * 4));
	}
	
	

	public GameBoard getGameBoard() {
		return gameBoard;
	}

	private boolean gameFinished() {
		/* Checkt die Stapel durch, ob 3 Stapel leer sind bzw. Provinzen leer */
		/* Wenn ja: */
		return !setGameNotFinished(false);
	}

	/** CONTROLLER LOGIC; not sure whether the loops are necessary */
	public void startGame() {
		System.out.println(Arrays.toString(this.activePlayer.getDeck().getCardHand().toArray()));
	}

	private void turn(Player player) {
		setActivePlayer(player);
	}
}
