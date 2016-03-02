package com.tpps.application.game;

import java.util.Iterator;
import java.util.LinkedList;

import com.tpps.technicalServices.util.GameConstant;

public class GameController {

	private LinkedList<Player> players;
	private boolean gameNotFinished = true;
	private static Player activePlayer;

	public GameController() {
		for (int i = 0; i < GameConstant.HUMAN_PLAYERS; i++) {
			players.addLast(new Player(i + 1));
		}
	}

	public LinkedList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(LinkedList<Player> players) {
		this.players = players;
	}
	
	public static Player getActivePlayer() {
		return activePlayer;
	}

	public static void setActivePlayer(Player aP) {
		activePlayer = aP;
	}

	public boolean isGameNotFinished() {
		return gameNotFinished;
	}

	public boolean setGameNotFinished(boolean gameNotFinished) {
		return this.gameNotFinished = gameNotFinished;
	}

	public boolean gameFinished() {
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

	public void turn(Player player) {
		/* Player Turn */
	}
}
