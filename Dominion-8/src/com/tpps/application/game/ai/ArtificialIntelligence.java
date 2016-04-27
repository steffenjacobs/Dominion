package com.tpps.application.game.ai;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.UUID;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.network.game.ServerGamePacketHandler;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayCard;

/*
 * - board anschauen, wenn angriffskarten gekauft werden dann defensiv kaufen 
 * - wenn es nix bringt, mehr karten zu ziehen, ggf. aktionskarten nicht spielen
 * - LinkedListMultimap mit "buy" oder "play" und karte als Spielplan aufbauen
 * - wenn es der potentiell letzte Zug ist, soll die Blacklist ignoriert werden und evtl ein Anwesen gekauft werden
 */

/**
 * AI -> computes Move -> Information(Handler) needed -> Move is computed ->
 * GameHandler needed to execute turn
 * 
 * @author Nicolas Wipfler
 */
public class ArtificialIntelligence {

	private Player player;
	private boolean computing;

	private AIPacketHandler aiPacketHandler;
	private AIClient aiClient;
	private Move move;
	private InformationHandler information;

	/**
	 * constructor of the Artificial Intelligence
	 * 
	 * @param player
	 *            the player which is controlled by the AI
	 * @param uuid
	 *            the sessionID of the AI instance
	 */
	public ArtificialIntelligence(Player player, SocketAddress _address, UUID uuid, ServerGamePacketHandler packetHandler) {
		try {
			this.player = player;
			this.aiPacketHandler = new AIPacketHandler();
			this.aiClient = new AIClient(_address, aiPacketHandler);
			this.aiPacketHandler.setAiClient(aiClient);
			this.information = new InformationHandler();
			this.computing = false;
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
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
//		new Thread(new Runnable() {
//
//			public void run() {
//				while (aiClient.notFinished()) {
//					try {
//						Thread.sleep(500);
//						handleTurn();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//		}).start();
	}

	private void handleTurn() {
//		if (aiClient.myTurn(this.player)) {
//			executeMove();
//		} else {
//			if (!computing) {
//				determineMove();
//			}
//		}
	}
	
	/**
	 * execute the next turn of AI, which is determined by LinkedListMultimap
	 * nextTurn
	 */
	public void executeMove() {
		LinkedList<Card> cardHand = this.player.getDeck().getCardHand();
//		aiClient.playTreasures();
//		aiClient.endTurn();
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
	private void determineMove() {
		this.computing = true;
		/**
		 * method assigns a default turn as the next turn, if the
		 * determineMove() method gets interrupted before something useful is
		 * computed
		 */
		getDefaultMove();
	}

	/**
	 * assign a default turn to nextTurn
	 */
	private void getDefaultMove() {
		/**
		 * availableCoinsAtStartOfTurn has to be checked several times (for
		 * example after a 'draw card' action is performed and if there are
		 * enough coins to the desired action (/buy the desired card), don't
		 * draw any more cards e.g.
		 */
		int availableCoinsAtStartOfTurn = information.getTreasureCardsValue(this.player);

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
	
	/**
	 * @return the game
	 */
	public AIClient getGame() {
		return aiClient;
	}

	/**
	 * @param game
	 *            the game to set
	 */
	public void setGame(AIClient game) {
		this.aiClient = game;
	}

	/**
	 * @return the move
	 */
	public Move getMove() {
		return move;
	}

	/**
	 * @param move
	 *            the move to set
	 */
	public void setMove(Move move) {
		this.move = move;
	}

	/**
	 * @return the information
	 */
	public InformationHandler getInformation() {
		return information;
	}

	/**
	 * @param information
	 *            the information to set
	 */
	public void setInformation(InformationHandler information) {
		this.information = information;
	}

	/**
	 * @return the aiPacketHandler
	 */
	public AIPacketHandler getAiPacketHandler() {
		return aiPacketHandler;
	}

	/**
	 * @param aiPacketHandler the aiPacketHandler to set
	 */
	public void setAiPacketHandler(AIPacketHandler aiPacketHandler) {
		this.aiPacketHandler = aiPacketHandler;
	}
}