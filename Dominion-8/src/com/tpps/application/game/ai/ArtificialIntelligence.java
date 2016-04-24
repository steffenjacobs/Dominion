package com.tpps.application.game.ai;

import java.util.UUID;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.tpps.application.game.Player;
import com.tpps.ui.gameplay.GameWindow;

/**
 * board anschauen, wenn angriffskarten gekauft werden dann defensiv kaufen wenn
 * es nix bringt, mehr karten zu ziehen, ggf. aktionskarten nicht spielen
 * LinkedListMultimap mit "buy" oder "play" und karte als Spielplan aufbauen
 * Player Konstruktor ohne port? wenn es der potentiell letzte Zug ist, soll die
 * Blacklist ignoriert werden und evtl ein Anwesen gekauft werden
 */

public class ArtificialIntelligence {

	private Player player;
	private boolean computing;

	private GameView game;
	private Move move;

	/**
	 * 
	 * 
	 * move determines the steps if it's the AI's turn 
	 * with the cardStore the AI can compare every handcard with the
	 * original card 'image' of the backend computing is a flag which indicates
	 * whether the AI is already computing the next turn or does nothing at the
	 * moment
	 */
	
	/**
	 * constructor of the Artificial Intelligence
	 * 
	 * @param player the player which is controlled by the AI
	 * @param uuid
	 */
	public ArtificialIntelligence(Player player, UUID uuid) {
		this.player = player;
		this.game = new GameView(this.player.getGameServer());
		this.move = new Move();
		this.computing = false;
	}

	/**
	 * end the turn of AI
	 */
	private void endTurn() {
		GameWindow.endTurn.onMouseClick();
	}

	/**
	 * play all treasures of AI
	 */
	private void playTreasures() {
		GameWindow.playTreasures.onMouseClick();
	}

	/**
	 * start the AI, method is only called once when the game is initialized it
	 * then runs until the game is finished
	 * 
	 * every 500ms the AI checks if it is its turn and if so, it executes the
	 * next Turn if its not the AIs turn, and if its not already computing the
	 * next turn, the AI is going to compute the next turn in the
	 * computeNextTurn() method
	 */
	public void start() {
		new Thread(new Runnable() {

			public void run() {
				while (game.notFinished()) {
					try {
						Thread.sleep(500);
						handleTurn();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}).start();
	}

	/**
	 * execute the next turn of AI, which is determined by LinkedListMultimap
	 * nextTurn
	 */
	public void executeTurn() {
		// LinkedList<Card> cardHand = this.player.getDeck().getCardHand();
		playTreasures();
		endTurn();
		/**
		 * set computing flag false here, because otherwise the AI would compute
		 * a new turn before it has even executed the old
		 */
		this.computing = false;
	}

	/**
	 * set computing to true so the computing will not be interrupted first
	 * assign a default turn which should be an alternative solution, if the
	 * compution of the next turn of the AI is interrupted
	 */
	private void computeNextTurn() {
		this.computing = true;
		/**
		 * method assigns a default turn as the next turn, if the
		 * computeNextTurn() method gets interrupted before something useful is
		 * computed
		 */
		assignDefaultTurn();
	}

	/**
	 * assign a default turn to nextTurn
	 */
	private void assignDefaultTurn() {
		/**
		 * availableCoinsAtStartOfTurn has to be checked several times (for
		 * example after a 'draw card' action is performed and if there are
		 * enough coins to the desired action (/buy the desired card), don't
		 * draw any more cards e.g.
		 */
		int availableCoinsAtStartOfTurn = getTreasureCardsValue();

	}

	private void handleTurn() {
		if (game.myTurn(this.player)) {
			executeTurn();
		} else {
			if (!computing) {
				computeNextTurn();
			}
		}
	}

	/**
	 * 
	 * @return the value of all treasure cards in the cardHand of the AI
	 */
	private int getTreasureCardsValue() {
		return this.player.getDeck().getTreasureValueOfList(this.player.getDeck().getCardHand());
	}

	/**
	 * main for testing purposes of LinkedListMultimap
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ListMultimap<String, Integer> map = LinkedListMultimap.create();
		map.put("a", 2);
		map.put("b", 3);
		map.put("a", 3);
		map.put("c", 2);
		System.out.println("Keys:");
		for (String b : map.keys()) {
			System.out.println(b);
		}
		System.out.println("\nKeySet:");
		for (String b : map.keySet()) {
			System.out.println(b);
		}
		System.out.println("\nValues:");
		for (Integer i : map.values()) {
			System.out.println(i);
		}
	}
}