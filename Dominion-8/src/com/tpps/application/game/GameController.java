package com.tpps.application.game;

import java.util.Iterator;
import java.util.LinkedList;

public class GameController {

	private static LinkedList<Player> players;
	private static boolean gameNotFinished = true;
	private static Player activePlayer;

	public static LinkedList<Player> getPlayers() {
		return players;
	}

	public static void setPlayers(LinkedList<Player> players) {
		GameController.players = players;
	}
	
	public static Player getActivePlayer() {
		return activePlayer;
	}

	public static void setActivePlayer(Player aP) {
		activePlayer = aP;
	}

	public static boolean isGameNotFinished() {
		return gameNotFinished;
	}

	public static boolean setGameNotFinished(boolean gameNotFinished) {
		return GameController.gameNotFinished = gameNotFinished;
	}

	public static boolean gameFinished() {
		/* Checkt die Stapel durch, ob 3 Stapel leer sind bzw. Provinzen leer */
		/* Wenn ja: */
		return !setGameNotFinished(false);
	}

	/** CONTROLLER LOGIC; not sure whether the loops are necessary */
	public static void startGame() {
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

	public static void turn(Player player) {
		/* Player Turn */
	}
}
