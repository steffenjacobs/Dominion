package com.tpps.application.game;

import java.util.LinkedHashMap;

public class GameController {

	private LinkedHashMap<ServerPlayer, Integer> players;
	private boolean gameNotFinished = true;

	public GameController() {
		for (int i = 0; i < 4 /*
								 * Hard Code, Anzahl an realen Spielern ohne die
								 * KI's bzw. max. Anzahl Spieler
								 */; i++) {
			players.put(new ServerPlayer(), i);
		}
	}

	/** GETTER AND SETTER */
	public LinkedHashMap<ServerPlayer, Integer> getPlayers() {
		return players;
	}

	public void setPlayers(LinkedHashMap<ServerPlayer, Integer> players) {
		this.players = players;
	}

	public boolean isGameNotFinished() {
		return gameNotFinished;
	}

	public void setGameNotFinished(boolean gameNotFinished) {
		this.gameNotFinished = gameNotFinished;
	}

	// not sure whether the loops are necessary
	/** CONTROLLER LOGIC */
	public void startGame() {
		while (gameNotFinished) {
			for (ServerPlayer p : players.keySet()) {
				nextTurn(p);
				checkGameFinished();
			}
		}
	}

	public void nextTurn(ServerPlayer player) {
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
