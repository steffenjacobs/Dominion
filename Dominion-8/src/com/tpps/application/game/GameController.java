package com.tpps.application.game;

import java.util.HashMap;

public class GameController {

	private HashMap<Player, Integer> players;
	private boolean gameNotFinished = true;

	public GameController() {
		for (int i = 0; i < 4 /*
								 * Hard Code, Anzahl an realen Spielern ohne die
								 * KI's bzw. max. Anzahl Spieler
								 */; i++) {
			players.put(new Player(), i);
		}
	}

	/** GETTER AND SETTER */
	public HashMap<Player, Integer> getPlayers() {
		return players;
	}

	public void setPlayers(HashMap<Player, Integer> players) {
		this.players = players;
	}

	public boolean isGameNotFinished() {
		return gameNotFinished;
	}

	public void setGameNotFinished(boolean gameNotFinished) {
		this.gameNotFinished = gameNotFinished;
	}

	/** CONTROLLER LOGIC */
	public void startGame() {
		while (gameNotFinished) {
			for (Player p : players.keySet()) {
				nextTurn(p);
				checkGameFinished();
			}
		}
	}

	public void nextTurn(Player player) {
		/* Player Turn */
		checkGameFinished();
	}

	public void checkGameFinished() {
		/* Checkt die Stapel durch, ob 3 Stapel leer sind bzw. Provinzen leer */
		/* Wenn ja: */
		setGameNotFinished(false);
	}

	/** ID STUFF (irgendwie nicht so konsistent, wie am besten machen? */
	// public int getPlayerID(Player player) {
	// return players.get(player);
	// }
	//
	// public boolean setPlayerID(Player player, int id) {
	// for (Player p : players.keySet())
	// if (players.get(p) == id) {
	// player.setID(getValidID());
	// return false;
	// }
	// player.setID(id);
	// return true;
	// }
	//
	// private int getValidID() {
	// return Collections.max(players.values())+ 1;
	// }
}
