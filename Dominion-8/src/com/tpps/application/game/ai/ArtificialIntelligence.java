package com.tpps.application.game.ai;

import java.util.LinkedList;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.application.storage.CardStorageController;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.ui.gameplay.GameWindow;

public class ArtificialIntelligence {

	private Player player;
	private LinkedList<String> blacklist;
	private ListMultimap<String, Card> nextTurn;
	private CardStorageController cardStore;
	private boolean computing;

	// board anschauen, wenn angriffskarten gekauft werden dann defensiv kaufen
	// wenn es nix bringt, mehr karten zu ziehen, ggf. aktionskarten nicht
	// spielen
	// LinkedListMultimap mit "buy" oder "play" und karte als Spielplan aufbauen
	// Player Konstruktor ohne port?
	// wenn es der potentiell letzte Zug ist, soll die Blacklist ignoriert werden und evtl ein Anwesen gekauft werden

	/**
	 * constructor of the Artificial Intelligence
	 * 
	 * CLIENT_ID is managed by the GameServer and AI get's an ID according to it's position in the login queue
	 * player has the CLIENT_ID and the general startSet of cards (3x Estate, 7x Copper; see GameBoard for more information)
	 * blacklist basically blacklists all cards the AI should never buy, except special situations
	 * nextMove determines the steps if it's the AI's turn
	 * with the cardStore the AI can compare every handcard with the original card 'image' of the backend
	 * computing is a flag which indicates whether the AI is already computing the next turn or does nothing at the momant
	 */
	public ArtificialIntelligence() {
		int CLIENT_ID = GameServer.getCLIENT_ID();
		LinkedList<Card> startSet = GameServer.getInstance().getGameController().getGameBoard().getStartSet();

		/**
		 * FIX
		 * */
		this.player = new Player(CLIENT_ID, /* random default port */ 1995, startSet, "AI", GameServer.getInstance());
		/**
		 * FIX
		 * */
		
		this.blacklist = this.getCardNamesFromStorage("Curse", "Copper", "Estate");
		this.nextTurn = LinkedListMultimap.create();
		this.cardStore = new CardStorageController("cards.bin");
		this.computing = false;
	}

	/**
	 * @return the blacklist
	 */
	public LinkedList<String> getBlacklist() {
		return blacklist;
	}

	/**
	 * @param blacklist the blacklist to set
	 */
	public void setBlacklist(LinkedList<String> blacklist) {
		this.blacklist = blacklist;
	}

	/**
	 * @return the cardStore
	 */
	public CardStorageController getCardStore() {
		return cardStore;
	}

	/**
	 * @param cardStore the cardStore to set
	 */
	public void setCardStore(CardStorageController cardStore) {
		this.cardStore = cardStore;
	}

	/**
	 * @return the nextTurn
	 */
	public ListMultimap<String, Card> getNextTurn() {
		return nextTurn;
	}

	/**
	 * @param nextTurn the nextTurn to set
	 */
	public void setNextTurn(ListMultimap<String, Card> nextTurn) {
		this.nextTurn = nextTurn;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * this method is used to see in the cardStore if there is a card called 'name'
	 * so it takes the parameter 'names' and adds every cardname (when the card is truly available in cardStore)
	 * to the return list. 
	 * 
	 * @param names the names of all Cards which will be get from the CardStore
	 * @return a list of names with the names of only the available cards in the game
	 */
	private LinkedList<String> getCardNamesFromStorage(String... names) {
		LinkedList<String> list = new LinkedList<String>();
		for (String cardname : names) {
			list.addLast(cardStore.getCard(cardname).getName());
		}
		return list;
	}
	
	/**
	 * 
	 * @return if its the AIs turn
	 */
	private boolean myTurn() {
		return GameServer.getInstance().getGameController().getActivePlayer().equals(this.player);
	}

	/**
	 * 
	 * @return if the game is NOT finished (so when the method returns true, the game is still running)
	 */
	private boolean gameNotFinished() {
		return GameServer.getInstance().getGameController().isGameNotFinished();
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
	 * start the AI, method is only called once when the game is initialized
	 * it then runs until the game is finished
	 * 
	 * every 500ms the AI checks if it is its turn and if so, it executes the next Turn
	 * if its not the AIs turn, and if its not already computing the next turn, the AI is going to compute
	 * the next turn in the computeNextTurn() method
	 */
	public void start() {
		new Thread(new Runnable() {
			
			public void run() {
				while (gameNotFinished()) {
					try {
						Thread.sleep(500);
						if (myTurn()) {
							executeTurn();
						} else {
							 if (!computing) {
								 computeNextTurn();
							 }
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}).start();
	}

	/**
	 * execute the next turn of AI, which is determined by LinkedListMultimap nextTurn
	 */
	public void executeTurn() {
		// LinkedList<Card> cardHand = this.player.getDeck().getCardHand();
		playTreasures();
		endTurn();
		/** set computing flag false here, because otherwise the AI would compute a new turn before it has even executed the old */
		this.computing = false;
	}
	
	/**
	 * set computing to true so the computing will not be interrupted
	 * first assign a default turn which should be an alternative solution, if the compution of the next turn of the AI is interrupted
	 */
	private void computeNextTurn() {
		this.computing = true;
		/** method assigns a default turn as the next turn, if the computeNextTurn() method gets interrupted
		before something useful is computed */
		assignDefaultTurn();
	}
	
	/**
	 * assign a default turn to nextTurn
	 */
	private void assignDefaultTurn() {
		/** availableCoinsAtStartOfTurn has to be checked several times (for example after a 'draw card' action is performed 
		 *  and if there are enough coins to the desired action (/buy the desired card), don't draw any more cards e.g. */
		int availableCoinsAtStartOfTurn = getTreasureCardsValue();
		
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