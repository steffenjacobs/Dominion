package com.tpps.application.game.ai;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.game.ServerGamePacketHandler;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLog;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndActionPhase;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndTurn;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayCard;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayTreasures;
import com.tpps.technicalServices.util.CollectionsUtil;

/*
 * - board anschauen, wenn angriffskarten gekauft werden dann defensiv kaufen 
 * - wenn es nix bringt, mehr karten zu ziehen, ggf. aktionskarten nicht spielen
 * - LinkedListMultimap mit "buy" oder "play" und karte als Spielplan aufbauen
 * - wenn es der potentiell letzte Zug ist, soll die Blacklist ignoriert werden und evtl ein Anwesen gekauft werden
 */

/**
 * Global AI class. If you get stomped, don't worry.
 * 
 * @author Nicolas Wipfler
 */
public class ArtificialIntelligence {

	private ServerGamePacketHandler packetHandler;

	private Player player;
	private Move move;

	private List<String> blacklist;

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
		this.move = new Move();
		this.blacklist = CollectionsUtil.linkedList(new String[] { "Copper", "Estate", "Curse" });
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
		if (card != null) {
			sendPacket(new PacketPlayCard(card.getId(), player.getClientID()));
		} else {
			GameLog.log(MsgType.AI, " played 'null' card");
		}
	}

	private void playTreasures() {
		sendPacket(new PacketPlayTreasures());
	}

	private void playTreasures(int amountNeeded) {
		int amountAvailable = this.getTreasureCardsValue();
		if (amountAvailable <= amountNeeded) {
			playTreasures();
		} else {
			LinkedList<Card> allActionCards = this.getAllCardsFromType(CardType.TREASURE);
			for (Card card : allActionCards) {
				
			}
//			LinkedList<Card> coppers = ;
//			LinkedList<Card> silvers = ;
//			LinkedList<Card> golds = ;	
		}		
	}

	private void playAllActionCards() {
		LinkedList<Card> allActionCards = this.getAllCardsFromType(CardType.ACTION);
		// handle mit unterschiedlichen +Karten typen usw
		
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
			GameLog.log(MsgType.AI, " bought 'null' card");
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
			GameLog.log(MsgType.AI, this + " is handling a turn");

			determineMove();
			if (this.move.isReady()) {
				executeMove(this.move);
			} else {
				/**
				 * method assigns a default turn as the next turn, if the
				 * determineMove() method gets interrupted before something
				 * useful is computed
				 */
				executeMove(getDefaultMove());
			}
		} else if (this.player.isReactionMode()) {
			if (this.player.playsReactionCard()) {
				// handle den und auch alle anderen Modes
				
			}
		}
	}

	/**
	 * execute the next turn of AI, which is determined by LinkedListMultimap
	 * nextTurn
	 */
	private void executeMove(Move move) {
		GameLog.log(MsgType.AI, this + " is executing a turn");
		try {
			Thread.sleep(200);
			this.playTreasures(); // evtl playTreasures(amountNeeded)
			Thread.sleep(200);
			for (Card action : move.getPlaySequence().get(Execute.PLAY)) {
				this.playCard(action);
			}
			Thread.sleep(200);
			this.setBuyPhase();
			Thread.sleep(200);
			for (String buy : move.getBuySequence().get(Execute.BUY)) {
				if (!getBlacklist().contains(buy)) {
					this.buyCard(this.getCardFromBoard(buy));
				}
			}
			Thread.sleep(200);
			if (myTurn()) {
				this.player.getGameServer().broadcastMessage(new PacketBroadcastLog("AI end[ed]Turn() by itself"));
				this.endTurn();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * set computing to true so the computing will not be interrupted first
	 * assign a default turn which should be an alternative solution, if the
	 * compution of the next turn of the AI is interrupted
	 */
	private void determineMove() {
		// this.computing = true;
		// https://dominionstrategy.com/big-money/

		// LinkedList<Card> cardHand = this.getCardHand();

		
	}

	/**
	 * assign a default turn to nextTurn
	 * 
	 * coins has to be checked several times (for example after a 'draw
	 * card' action is performed and if there are enough coins to the
	 * desired action (/buy the desired card), don't draw any more cards
	 * e.g.
	 */
	private Move getDefaultMove() {
		Move result = new Move();
		// LinkedList<Card> cardHand = this.getCardHand();

		
		int coins = getTreasureCardsValue();
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
	 * @return
	 */
	private boolean myTurn() {
		Player activePlayer = this.player.getGameServer().getGameController().getActivePlayer();
		return activePlayer != null ? activePlayer.equals(this.player) : false;
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
	 * 
	 * @param player
	 *            the player to get the information from
	 * @return
	 */
	private int getTreasureCardsValue() {
		return this.player.getDeck().getTreasureValueOfList(this.getCardHand());
	}
	
	private LinkedList<Card> getAllCardsFromType(CardType cardType) {
		return this.player.getDeck().getCardsByTypeFromHand(cardType);
	}

	/**
	 * 
	 * @return the cardHand of the player
	 */
	private LinkedList<Card> getCardHand() {
		return this.player.getDeck().getCardHand();
	}

	@SuppressWarnings("unused")
	private int getPlayerActions() {
		return this.player.getActions();
	}

	@SuppressWarnings("unused")
	private int getPlayerBuys() {
		return this.player.getBuys();
	}

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
}