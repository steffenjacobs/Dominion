package com.tpps.application.game.ai;

import java.util.UUID;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.game.ServerGamePacketHandler;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndActionPhase;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndTurn;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayCard;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayTreasures;

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

	private ServerGamePacketHandler packetHandler;

	private Player player;
	private InformationHandler information;
	private boolean computing;

	// private AIPacketHandler aiPacketHandler;
	// private AIClient aiClient;

	/**
	 * constructor of the Artificial Intelligence
	 * 
	 * @param player
	 *            the player which is controlled by the AI
	 * @param uuid
	 *            the sessionID of the AI instance
	 */
	public ArtificialIntelligence(Player player, /* SocketAddress _address, */UUID uuid, ServerGamePacketHandler packetHandler) {
		this.player = player;
		// this.aiPacketHandler = new AIPacketHandler();
		// this.aiClient = new AIClient(_address, aiPacketHandler);
		// this.aiPacketHandler.setAiClient(aiClient);
		this.information = new InformationHandler();
		this.computing = false;
		this.packetHandler = packetHandler;
	}

	/*
	 * keine Probleme mehr, es muss nur genau wie bei myTurn die ReactionPhase
	 * gehandlet werden
	 */
	private boolean myTurn() {
		return this.player.getGameServer().getGameController().getActivePlayer().equals(player);
	}

	/**
	 *
	 * @return if the game is NOT finished (so when the method returns true, the
	 *         game is still running)
	 */
	protected boolean notFinished() {
		return this.player.getGameServer().getGameController().isGameNotFinished();
	}

	private void sendPacket(Packet packet) {
		new Thread(() -> {
			packetHandler.handleReceivedPacket(this.player.getPort(), packet);
		}).start();
	}

	private void playCard(Card card) {
		if (card != null) {
			sendPacket(new PacketPlayCard(card.getId(), player.getClientID()));
		} else {
			System.out.println("AI played 'null' card");

		}
	}

	private Card getCardFromBoard(String cardname) {
		return this.player.getGameServer().getGameController().getGameBoard().getCardToBuyFromBoardWithName(cardname);
	}

	public void buyCard(Card card) {
		if (card != null) {
			sendPacket(new PacketPlayCard(card.getId(), player.getClientID()));
		} else {
			System.out.println("AI bought 'null' card");
		}
	}

	private void setBuyPhase() {
		sendPacket(new PacketEndActionPhase());
	}

	private void playTreasures() {
		sendPacket(new PacketPlayTreasures());
	}

	protected void endTurn() {
		sendPacket(new PacketEndTurn());
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
				while (notFinished()) {
					try {
						Thread.sleep(2000);
						handleTurn();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}).start();
	}

	private void handleTurn() {
		if (myTurn()) {
			System.out.println(this + " is handling a turn");
			executeMove();
		} else {
			// if (!computing) {
			// determineMove();
			// }
		}
	}

	/**
	 * execute the next turn of AI, which is determined by LinkedListMultimap
	 * nextTurn
	 */
	public void executeMove() {

		System.out.println(this + " is executing a turn");
		// LinkedList<Card> cardHand = this.player.getDeck().getCardHand();
		try {
			Move defaultMove = this.getDefaultMove();
			System.out.println("before sleep");
			Thread.sleep(500);
			System.out.println("after sleep");
			this.playTreasures();
//			Thread.sleep(500);
			for (Card action : defaultMove.getPlaySequence().get(ExecutionType.PLAY)) {
				this.playCard(action);
			}
//			Thread.sleep(250);
			this.setBuyPhase();
//			Thread.sleep(250);
			for (String buy : defaultMove.getBuySequence().get(ExecutionType.BUY)) {
				if (!information.getBlacklist().contains(buy)) {
					this.buyCard(this.getCardFromBoard(buy));
				}
			}
//			Thread.sleep(500);
			/**
			 * set computing flag false here, because otherwise the AI would
			 * compute a new turn before it has even executed the old
			 */
			this.computing = false;
			this.endTurn();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * set computing to true so the computing will not be interrupted first
	 * assign a default turn which should be an alternative solution, if the
	 * compution of the next turn of the AI is interrupted
	 */
	protected void determineMove() {
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
	private Move getDefaultMove() {
		/**
		 * availableCoinsAtStartOfTurn has to be checked several times (for
		 * example after a 'draw card' action is performed and if there are
		 * enough coins to the desired action (/buy the desired card), don't
		 * draw any more cards e.g.
		 */
		Move result = new Move();
		int coins = information.getTreasureCardsValue(this.player);
		if (coins >= 8) {
			result.putBuy("Province");
		} else if (coins >= 6) {
			result.putBuy("Gold");
		} else if (coins >= 3) {
			result.putBuy("Silver");
		}
		return result;
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
		System.out.println(map.get("a"));
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

	// /**
	// * @return the game
	// */
	// public AIClient getGame() {
	// return aiClient;
	// }
	//
	// /**
	// * @param game
	// * the game to set
	// */
	// public void setGame(AIClient game) {
	// this.aiClient = game;
	// }

	// /**
	// * @return the aiPacketHandler
	// */
	// public AIPacketHandler getAiPacketHandler() {
	// return aiPacketHandler;
	// }
	//
	// /**
	// * @param aiPacketHandler
	// * the aiPacketHandler to set
	// */
	// public void setAiPacketHandler(AIPacketHandler aiPacketHandler) {
	// this.aiPacketHandler = aiPacketHandler;
	// }
}