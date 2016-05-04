package com.tpps.application.game.ai;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.game.ServerGamePacketHandler;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndActionPhase;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndTurn;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayCard;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayTreasures;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/*
 * - board anschauen, wenn angriffskarten gekauft werden dann defensiv kaufen 
 * - wenn es nix bringt, mehr karten zu ziehen, ggf. aktionskarten nicht spielen
 * - LinkedListMultimap mit "buy" oder "play" und karte als Spielplan aufbauen
 * - wenn es der potentiell letzte Zug ist, soll die Blacklist ignoriert werden und evtl ein Anwesen gekauft werden
 * Kommentare anpassen (computing etc.)
 */

/**
 * Global AI class. If you get stomped, don't worry.
 * 
 * @author Nicolas Wipfler
 */
public class ArtificialIntelligence {

	private ServerGamePacketHandler packetHandler;
	private Player player;
	private List<String> blacklist;
	private boolean endPhase;
	
	private static final int ENDPHASE_TURN = 22;
	private static final int TIME_DELAY = 500;

	/**
	 * constructor of the Artificial Intelligence
	 * 
	 * @param player
	 *            the player which is controlled by the AI
	 * @param uuid
	 *            the sessionID of the AI instance
	 */
	public ArtificialIntelligence(Player player, UUID uuid, ServerGamePacketHandler packetHandler) {
		this.packetHandler = packetHandler;
		this.player = player;
		this.blacklist = CollectionsUtil.linkedList(new String[] { "Copper", "Estate", "Curse" });
		this.endPhase = false;
	}

	/* ---------- game executing ---------- */

	// keine Probleme mehr, es muss nur genau wie bei myTurn die ReactionPhase
	// gehandlet werden
	private void sendPacket(Packet packet) {
		new Thread(() -> {
			packetHandler.handleReceivedPacket(this.player.getPort(), packet);
		}).start();
	}

	private void playCard(Card card) {
		if (card != null && this.player.getActions() > 0) {
			sendPacket(new PacketPlayCard(card.getId(), player.getClientID()));
		} else {
			GameLog.log(MsgType.AI, "played 'null' card");
		}
	}

	private void playCards(LinkedList<Card> cards) {
		if (cards != null && cards.size() > 0) {
			for (Card card : cards) {
				playCard(card);
			}
		}
	}

	private void playTreasures() {
		sendPacket(new PacketPlayTreasures());
	}

	private void playTreasures(int amountNeeded) {
		int amountAvailable = this.getTreasureCardsValue(getCardHand());
		if (amountAvailable <= amountNeeded) {
			playTreasures();
		} else {
			LinkedList<Card> allTreasureCards = this.getAllCardsFromType(CardType.TREASURE);
			for (Card card : allTreasureCards) {
				// hier

			}
			// LinkedList<Card> coppers = ;
			// LinkedList<Card> silvers = ;
			// LinkedList<Card> golds = ;
		}
	}

	private void playAllActionCards() {
		if (this.player.getDeck().cardHandActionCardAmount() > 0) {
			while (this.player.getActions() > 0 && this.player.getDeck().cardHandContains(CardType.ACTION)) {
				if (addActionCardAvailable()) {
					LinkedList<Card> plusActionCards = this.player.getDeck().cardHandsWith(CardAction.ADD_ACTION_TO_PLAYER, this.player.getDeck().getCardHand());
					playCards(plusActionCards);
					continue;
				}				
				// Logik
				LinkedList<Card> remainingActionCards = this.getAllCardsFromType(CardType.ACTION);
				Card tbp = this.player.getDeck().cardWithHighestCost(remainingActionCards);
				playCard(tbp);
			}
		}

	}

	private void setBuyPhase() {
		sendPacket(new PacketEndActionPhase());
	}

	private Card getCardFromBoard(String cardname) {
		return this.player.getGameServer().getGameController().getGameBoard().getCardToBuyFromBoardWithName(cardname);
	}

	private void buyCard(Card card) {
		if (card != null) {
			sendPacket(new PacketPlayCard(card.getId(), player.getClientID()));
		} else {
			GameLog.log(MsgType.AI, "bought 'null' card");
		}
	}

	private void endTurn() {
		sendPacket(new PacketEndTurn());
	}

	/* ---------- turn handling ---------- */

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
						Thread.sleep(1000);
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
			GameLog.log(MsgType.AI, this + "is handling a turn");
			if (!endPhase)
				checkEndPhase();
			
			Move tbe = determineMove();
			executeMove(tbe);
		} else if (this.player.isReactionMode()) {
			if (this.player.playsReactionCard()) {
				playCard(this.player.getDeck().getCardByTypeFromHand(CardType.REACTION));
//			} else if (this.player.isDiscardMode()) {
				
			}
		}
	}

	/**
	 * execute the next turn of AI, which is determined by LinkedListMultimap
	 * nextTurn
	 */
	private void executeMove(Move move) {
		GameLog.log(MsgType.AI, this + "is executing a turn");
		try {
			this.playAllActionCards();
			Thread.sleep(ArtificialIntelligence.TIME_DELAY);
			this.playTreasures(); // evtl playTreasures(amountNeeded)
			Thread.sleep(ArtificialIntelligence.TIME_DELAY);
//			for (Card action : move.getPlaySequence().get(Execute.PLAY)) {
//				this.playCard(action);
//			}
			Thread.sleep(ArtificialIntelligence.TIME_DELAY);
			this.setBuyPhase();
			Thread.sleep(ArtificialIntelligence.TIME_DELAY);
			for (String buy : move.getBuySequence().get(Execute.BUY)) {
				if (!getBlacklist().contains(buy) || endPhase) {
					this.buyCard(this.getCardFromBoard(buy));
				}
			}
			Thread.sleep(ArtificialIntelligence.TIME_DELAY);
			if (myTurn()) {
				GameLog.log(MsgType.AI, "ended Turn by itself.");
				this.endTurn();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * set computing to true so the computing will not be interrupted first
	 * assign a default turn which should be an alternative solution, if the
	 * compution of the next turn of the AI is interrupted
	 */
	private Move determineMove() {
		// https://dominionstrategy.com/big-money/

		Move result = new Move();
		// LinkedList<Card> cardHand = this.getCardHand();
		// hier

		// chapel handlen
		return getDefaultMove(); // aendern
	}

	/**
	 * assign a default turn to nextTurn
	 * 
	 * coins has to be checked several times (for example after a 'draw card'
	 * action is performed and if there are enough coins to the desired action
	 * (/buy the desired card), don't draw any more cards e.g.
	 */
	private Move getDefaultMove()  /* umbenennen in determineBuy oder so  */{
		Move result = new Move();
		// LinkedList<Card> cardHand = this.getCardHand();
		// hier
		
//		playAllActionCards(result); // das hier muss in executeMove ausgeführt werden, nicht in getMove
//		while einbauen, die käufe je nach remaining coins hinzufügt solange noch käufe da sind (durch blacklist wird eh abgefangen wenns nicht gekauft werden soll)
		// Logik
		int coins = getTreasureCardsValue(getCardHand());
		if (coins >= 8) {
			result.putBuy("Province");
		} else if (coins >= 6) {
			result.putBuy("Gold");
		} else if (coins >= 3) {
			result.putBuy("Silver");
		}
		return result;
	}

	/* ---------- game information ---------- */

	/**
	 * 
	 * @return if it's the AIs turn
	 */
	private boolean myTurn() {
		String activePlayerName = this.player.getGameServer().getGameController().getActivePlayerName();
		return activePlayerName != null ? activePlayerName.equals(this.player.getPlayerName()) : false;
	}

	/**
	 *
	 * @return if the game is NOT finished (so when the method returns true, the
	 *         game is still running)
	 */
	private boolean notFinished() {
		return this.player.getGameServer().getGameController().isGameNotFinished();
	}

	/**
	 * check several game states and set the endPhase if necessary
	 */
	private void checkEndPhase() {
		if (getProvinceAmount() < 4) {
			this.endPhase = true;
			// } else if (this.player.getGameServer().getGameController().getGameBoard().amountOfPilesEmpty() == GameConstant.EMPTY_PILES - 1) {
			// this.endPhase = true;
		} else if (this.player.getTurnNr() >= ArtificialIntelligence.ENDPHASE_TURN) {
			this.endPhase = true;
		}
		// hier: 3 niedrigsten Kartenstapel insgesamt unter 7 Karten
	}
	
	/**
	 * 
	 * @param cards the cardList to check
	 * @return the value of all treasure cards in the given list
	 */
	private int getTreasureCardsValue(LinkedList<Card> cards) {
		return this.player.getDeck().getTreasureValueOfList(cards);
	}

	/**
	 * 
	 * @param cardType the CardType to search for
	 * @return all cards from the players deck with type cardType
	 */
	private LinkedList<Card> getAllCardsFromType(CardType cardType) {
		return this.player.getDeck().getCardsByTypeFromHand(cardType);
	}

	/**
	 * 
	 * @return whether there is a card on the players cardhand which adds at least 1 action to the players
	 * remaining actions when played (e.g. Market)
	 */
	private boolean addActionCardAvailable() {
		return this.player.getDeck().cardHandsWith(CardAction.ADD_ACTION_TO_PLAYER, this.player.getDeck().getCardHand()).size() > 0;
	}
	
	/**
	 * 
	 * @return the amount of Provinces left on the board
	 */
	private int getProvinceAmount() {
		return this.player.getGameServer().getGameController().getGameBoard().getTableForVictoryCards().get("Province").size();
	}
	
	/**
	 * 
	 * @return whether the AI is the only non-human player
	 */
	private boolean iAmTheOnlyAI() {
		return this.player.getGameServer().getGameController().getArtificialPlayers().size() == 1;
	}
	
	/**
	 * 
	 * @return the cardHand of the player
	 */
	private LinkedList<Card> getCardHand() {
		return this.player.getDeck().getCardHand();
	}

	// TODO: remove
	@SuppressWarnings("unused")
	private int getPlayerActions() {
		return this.player.getActions();
	}

	// TODO: remove
	@SuppressWarnings("unused")
	private int getPlayerBuys() {
		return this.player.getBuys();
	}

	// TODO: remove
	@SuppressWarnings("unused")
	private int getPlayerCoins() {
		return this.player.getCoins();
	}

	/* ---------- getter & setter ---------- */

	/**
	 * @return the packetHandler
	 */
	public ServerGamePacketHandler getPacketHandler() {
		return packetHandler;
	}

	/**
	 * @param packetHandler
	 *            the packetHandler to set
	 */
	public void setPacketHandler(ServerGamePacketHandler packetHandler) {
		this.packetHandler = packetHandler;
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
	 * @return the blacklist
	 */
	public List<String> getBlacklist() {
		return blacklist;
	}

	/**
	 * @param blacklist
	 *            the blacklist to set
	 */
	public void setBlacklist(List<String> blacklist) {
		this.blacklist = blacklist;
	}

	/**
	 * @return the endPhase
	 */
	public boolean isEndPhase() {
		return endPhase;
	}

	/**
	 * @param endPhase the endPhase to set
	 */
	public void setEndPhase(boolean endPhase) {
		this.endPhase = endPhase;
	}
}