package com.tpps.application.game;

import java.util.Iterator;
import java.util.LinkedList;

public class GameController {

	private LinkedList<Player> players;
	private boolean gameNotFinished;
	private Player activePlayer;

	public GameController() {
//		new Setup().start();
		this.players = new LinkedList<Player>();
		this.gameNotFinished = true;
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

	public void addPlayer(Player player) {
		this.players.addLast(player);
	}

	private boolean gameFinished() {
		/* Checkt die Stapel durch, ob 3 Stapel leer sind bzw. Provinzen leer */
		/* Wenn ja: */
		return !setGameNotFinished(false);
	}

	/** CONTROLLER LOGIC; not sure whether the loops are necessary */
	public void startGame() {
		Iterator<Player> it = players.iterator();
		while (gameNotFinished) {
			while (it.hasNext()) {
				if (gameFinished()) {
					break;
				}
				turn(it.next());
			}
		}
	}

	private void turn(Player player) {
		setActivePlayer(player);
	}
}
