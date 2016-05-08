package com.tpps.application.game.ai;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

import com.tpps.application.game.CardName;
import com.tpps.application.game.GameBoard;
import com.tpps.application.game.GameConstant;
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

/**
 * Global AI class. If you get stomped, don't worry, it's intended.
 * 
 * 
 * In a few cases the AI is allowed to check if the deck contains a specific
 * card. It could be considered as cheating, but since I don't want to write
 * horrible code with many counters and flags which indicate if the AI already
 * bought a card or not, and how many cards she already has of a cardtype (a
 * human player can obviously count that), the AI is allowed to look it up in
 * the deck.
 * 
 * E.g. the AI plays a Chapel strategy. If it buys one in the first turn, there
 * is no need to buy it in the second turn again. There would have to be a
 * counter or flag, whether the AI already bought a chapel. If it can be looked
 * up in the deck though, no more terrifying flags and counters for all possible
 * combinations and cards are needed anymore.
 * 
 * 
 * @author Nicolas Wipfler
 */
public class ArtificialIntelligence {

	private ServerGamePacketHandler packetHandler;
	private Player player;

	private Strategy strategy;

	@SuppressWarnings("unused")
	private List<Card> boardActionCards;

	private List<String> buySequence;
	private List<String> blacklist;

	/**
	 * indicates if the game will shortly come to an end, so e.g. ESTATES will
	 * be bought in the (potential) last turn
	 */
	private boolean endPhase;
	/**
	 * indicates if the starting hand is 5/2 (true) or 4/3 (false)
	 */
	private boolean fiveTwoStart;
	/**
	 * the amount of CardType.ATTACK action cards on the board
	 */
	private int attacks;
	/**
	 * how many times the AI has been in discardMode
	 */
	private int discardModeCount;

	@SuppressWarnings("unused")
	private static final int ENDPHASE_TURN = 22;

	private static final int TIME_DELAY = 600;

	private static final double MOAT_RATIO_1 = 0.87;
	private static final double MOAT_RATIO_2 = 0.45;

	private static final String NO_BUY = "#";

	/**
	 * constructor of the Artificial Intelligence
	 * 
	 * @param player
	 *            the player which is controlled by the AI
	 * @param uuid
	 *            the sessionID of the AI instance
	 * @param packetHandler
	 *            a packetHandler to send packets
	 */
	public ArtificialIntelligence(Player player, UUID uuid, ServerGamePacketHandler packetHandler) {
		this.packetHandler = packetHandler;
		this.player = player;
		this.blacklist = CollectionsUtil.linkedList(new String[] { CardName.COPPER.getName(), CardName.ESTATE.getName(), CardName.CURSE.getName() });
		this.endPhase = false;
		this.discardModeCount = 0;
	}

	/* ---------- game executing ---------- */

	/**
	 * a method to send packets with the AI to interact with the server (and
	 * therefore with the game and other clients)
	 * 
	 * @param packet
	 *            the packet to send
	 */
	private void sendPacket(Packet packet) {
		new Thread(() -> {
			packetHandler.handleReceivedPacket(this.player.getPort(), packet);
		}).start();
	}

	/**
	 * basic method which plays the given card
	 * 
	 * @param card
	 *            the card to play
	 */
	private void play(Card card) {
		if (card != null && this.player.getActions() > 0) {
			sendPacket(new PacketPlayCard(card.getId(), this.player.getClientID()));
		} else {
			GameLog.log(MsgType.ERROR, this.player.getPlayerName() + " tried to play 'null' card or had no actions left.");
		}
	}

	/**
	 * play not only one but a list of cards
	 * 
	 * @param cards
	 *            the list of cards to be played
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	private void playCards(LinkedList<Card> cards) throws InterruptedException {
		if (cards != null && cards.size() > 0) {
			for (Card card : cards) {
				sleep();
				play(card);
			}
		}
	}

	/**
	 * send a PacketPlayTreasures() to play all treasure cards from the players
	 * cardHand
	 */
	private void playTreasures() {
		sendPacket(new PacketPlayTreasures());
	}

	/**
	 * method should play only as few as possible treasure cards (e.g. to not
	 * reveal to other players that AI has a gold on hand, if there is already
	 * enough money for a desired purchase even without playing the gold)
	 * 
	 * @param amountNeeded
	 *            the amount of treasures needed
	 */
	@SuppressWarnings("unused")
	private void playTreasures(int amountNeeded) {
		int amountAvailable = this.getTreasureCardsValue(getCardHand());
		if (amountAvailable <= amountNeeded) {
			playTreasures();
		} else {
			LinkedList<Card> allTreasureCards = this.getAllCardsFromType(CardType.TREASURE);
			int alreadyPlayed = 0;
			for (Card card : allTreasureCards) {
				if (alreadyPlayed < amountNeeded) {
					play(card);
					alreadyPlayed += Integer.valueOf(card.getActions().get(CardAction.IS_TREASURE));
				} else
					return;
			}
		}
	}

	/**
	 * determine which cards have the most value and play all of these cards
	 * with CardType.ACTION
	 * 
	 * @throws InterruptedException
	 */
	private void playActionCards() throws InterruptedException {
		while (this.player.getDeck().cardHandContains(CardType.ACTION) && this.player.getActions() > 0) {
			sleep();
			/**
			 * in BIG_MONEY(_..) strategies, the execution of an action card
			 * will not give more buying power if there is already a treasure
			 * value >= 8 (so you're already able to buy the most valuable card:
			 * province). A second benefit is that the unplayed action card will
			 * be reshuffled and can be drawn earlier again.
			 */
			if (!this.strategy.equals(Strategy.DRAW_ADD_ACTION) && this.getTreasureCardsValue(getCardHand()) >= 8) {
				return;
			} else if (addActionCardAvailable()) {
				play(this.player.getDeck().cardWithAction(CardAction.ADD_ACTION_TO_PLAYER, getCardHand()));
			} else if (this.player.getDeck().cardHandContains(CardName.CHAPEL.getName()) && canBeTrashedByChapel() > 0) {
				LinkedList<Card> trashCards = getTrashWorthyCards();
				play(this.player.getDeck().getCardByNameFromHand(CardName.CHAPEL.getName()));
				if (!endPhase) {
					//trash estates
//					this.player.getDeck().(CardName.ESTATE.getName())== 1
					while (this.player.isTrashMode()) {
						
					}
				}
			}

			// Logik + Strategy, chapel handlen

			// ein estate nicht trashen

			// feast handlen

			LinkedList<Card> remainingActionCards = this.getAllCardsFromType(CardType.ACTION);
			Card tbp = this.player.getDeck().cardWithHighestCost(remainingActionCards);
			play(tbp);
		}
	}

	/**
	 * send a PacketEndActionPhase() to end the actionPhase and start the
	 * buyPhase
	 */
	private void setBuyPhase() {
		sendPacket(new PacketEndActionPhase());
	}

	/**
	 * 
	 * @param cardname
	 *            the cardname to get
	 * @return the card object with name cardname
	 */
	private Card getCardFromBoard(String cardname) throws NoSuchElementException {
		return this.player.getGameServer().getGameController().getGameBoard().getCardToBuyFromBoardWithName(cardname);
	}

	/**
	 * basic method which buys the given card
	 * 
	 * @param card
	 *            the card to buy
	 */
	private void buy(Card card) {
		if (card != null && this.player.getBuys() > 0 && this.player.getCoins() >= card.getCost()) {
			sendPacket(new PacketPlayCard(card.getId(), player.getClientID()));
		} else {
			GameLog.log(MsgType.ERROR, this.player.getPlayerName() + " tried to buy 'null' card or player had no buys left.");
		}
	}

	/**
	 * basic method which discards the given card
	 * 
	 * @param card
	 *            the card to discard
	 */
	private void discard(Card card) {
		if (card != null && this.player.isDiscardMode()) {
			sendPacket(new PacketPlayCard(card.getId(), this.player.getClientID()));
		} else {
			GameLog.log(MsgType.ERROR, this.player.getPlayerName() + " tried to discard 'null' card or player was not in discard mode.");
		}
	}

	/**
	 * send a PacketEndTurn() if the AI want's to end the turn 'manually' before
	 * it is ended automatically (e.g. if the player has no more buys left)
	 */
	private void endTurn() {
		GameLog.log(MsgType.AI, this.player.getPlayerName() + " ended Turn by itself.");
		sendPacket(new PacketEndTurn());
	}

	/**
	 * sleep for making AI play like a fast human player
	 * 
	 * @throws InterruptedException
	 */
	void sleep() throws InterruptedException {
		Thread.sleep(ArtificialIntelligence.TIME_DELAY);
	}

	/* ---------- turn handling ---------- */

	/**
	 * 
	 * start the AI, method is only called once when the game is initialized it
	 * then runs until the game is finished
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

	/**
	 * every TIME_DELAY the AI checks if it is its turn
	 * 
	 * case yes: execute the next move case no: handle a possible reaction Phase
	 * of the player (if an enemy played an attack)
	 */
	private void handleTurn() {
		if (myTurn()) {

			if (turn(1))
				determineStrategy();
			if (!endPhase)
				checkEndPhase();

			executeMove();
		} else if (this.player.isReactionMode()) {
			if (this.player.playsReactionCard()) {
				play(this.player.getDeck().getCardByTypeFromHand(CardType.REACTION));
			} else if (this.player.isDiscardMode()) {
				discardModeCount++;
				while (this.player.isDiscardMode()) {
					// && this.player.getDeck().getCardHand().size() > 3
					discardLeastValuableCard();
				}
			}
		}
	}

	/**
	 * execute the next turn of the AI
	 */
	private void executeMove() {
		GameLog.log(MsgType.AI, this.player.getPlayerName() + " is executing a turn");
		try {
			sleep();
			this.playActionCards();
			sleep();
			this.setBuyPhase();
			sleep();
			this.buySequence = getPurchaseSequence();
			sleep();
			this.playTreasures();
			sleep();
			for (String buy : this.buySequence) {
				checkAndBuy(buy);
			}
			sleep();
			this.buySequence = new LinkedList<String>();
			if (myTurn()) {
				this.endTurn();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * check if the card is available on the board and buy it
	 * 
	 * @param buy
	 *            the name of the card to buy
	 * @throws InterruptedException
	 */
	private void checkAndBuy(String buy) throws InterruptedException {
		GameLog.log(MsgType.AI_DEBUG, "buy outer: " + buy);
		if (!buy.equals(ArtificialIntelligence.NO_BUY) && (!blacklist.contains(buy) || endPhase)) {
			sleep();
			try {
				Card cardToBuy = this.getCardFromBoard(buy);
				this.buy(cardToBuy);
				GameLog.log(MsgType.AI_DEBUG, "buy inner: " + buy);
			} catch (NoSuchElementException nsee) {
				GameLog.log(MsgType.AI_DEBUG, "buy NSEE <<<<< " + buy);
				if (this.player.getCoins() >= 6 && this.player.getGameServer().getGameController().getGameBoard().getTableForTreasureCards().containsKey(CardName.GOLD.getName()))
					this.buy(this.getCardFromBoard(CardName.GOLD.getName()));
				else if (this.player.getCoins() >= 3 && this.player.getGameServer().getGameController().getGameBoard().getTableForTreasureCards().containsKey(CardName.SILVER.getName()))
					this.buy(this.getCardFromBoard(CardName.SILVER.getName()));
				else if (endPhase && this.player.getCoins() >= 2 && this.player.getGameServer().getGameController().getGameBoard().getTableForVictoryCards().containsKey(CardName.ESTATE.getName())) {
					this.buy(this.getCardFromBoard(CardName.ESTATE.getName()));
				} else
					return;
			}
		}
	}

	/**
	 * method determines the purchases of this turn for the AI
	 * 
	 * @return a LinkedList with the determined Purchases
	 */
	private LinkedList<String> getPurchaseSequence() {
		int treasureValue = getTreasureCardsValue(getCardHand());
		LinkedList<String> result = new LinkedList<String>();
		for (int i = 0; i < this.player.getBuys(); i++) {
			String purchase = determinePurchase(treasureValue);
			int purchaseCost = this.player.getGameServer().getGameController().getGameBoard().getCostOfCardByName(purchase);
			if (purchaseCost != -1) {
				treasureValue -= purchaseCost;
			}
			result.add(purchase);
			GameLog.log(MsgType.AI_DEBUG, "getting PurchaseSequence... Strategy: " + this.strategy + ", treasureValue: " + getTreasureCardsValue(getCardHand()) + ", purchase: " + purchase
					+ ", new treasureValue: " + treasureValue);
		}
		return result;
	}

	private String determinePurchase(int treasureValue) {

		GameBoard board = this.player.getGameServer().getGameController().getGameBoard();

		int attacksOriginally = attacks * GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue();
		int attacksBoughtByEnemies = attacksOriginally - board.getSizeOfPilesOnBoardWithType(CardType.ATTACK) - this.player.getDeck().containsAmountOf(CardType.ATTACK);
		int attacksAvailableRatio = attacksBoughtByEnemies / attacksOriginally;

		/**
		 * first two turns are handled seperately, because they are a crucial
		 * part in the game and the rules on how to behave in this turns differ
		 * from the rest of the game in some cases
		 */
		if (turn(1) || turn(2)) {
			switch (strategy) {
			case BIG_MONEY:
				if (board.getTableForActionCards().containsKey(CardName.SMITHY.getName())) {
					if (!fiveTwoStart) {
						if (treasureValue == 4) {
							return CardName.SMITHY.getName();
						} else
							return CardName.SILVER.getName();
					}
				}
				if (fiveTwoStart) {
					if (treasureValue == 5) {
						LinkedList<String> fiveCostCards = board.getActionCardsWhichCost(5);
						if (fiveCostCards.contains(CardName.COUNCILROOM.getName())) {
							return CardName.COUNCILROOM.getName();
						} else if (fiveCostCards.contains(CardName.LABORATORY.getName())) {
							return CardName.LABORATORY.getName();
						} else if (fiveCostCards.contains(CardName.MARKET.getName()))
							return CardName.MARKET.getName();
						else {
							return fiveCostCards.get(new Random().nextInt(fiveCostCards.size()));
						}
					} else
						return ArtificialIntelligence.NO_BUY;
				} else {
					return CardName.SILVER.getName();
				}
				/**
				 * difference to extra CHAPEL_WITCH case would be to buy Witch
				 * AND Chapel at 5/2 but I think it's a bad idea to buy two
				 * terminal action cards in the first two turns.
				 */
			case BIG_MONEY_CHAPEL_WITCH:
			case BIG_MONEY_CHAPEL_MILITIA:
			case BIG_MONEY_CHAPEL:
				if (treasureValue >= 4) {
					return CardName.SILVER.getName();
				} else if (treasureValue <= 3) {
					return CardName.CHAPEL.getName();
				} else
					return ArtificialIntelligence.NO_BUY;
			case BIG_MONEY_WITCH:
				if (fiveTwoStart && treasureValue == 5) {
					return CardName.WITCH.getName();
				} else if (!fiveTwoStart) {
					return CardName.SILVER.getName();
				}
				return ArtificialIntelligence.NO_BUY;
			case DRAW_ADD_ACTION:
				if (fiveTwoStart) {
					if (treasureValue == 5) {
						LinkedList<String> addAndDrawList = CollectionsUtil.join(board.getActionCardsWithActionWhichCost(CardAction.ADD_ACTION_TO_PLAYER, 5),
								board.getActionCardsWithActionWhichCost(CardAction.DRAW_CARD, 5));
						return addAndDrawList.get(new Random().nextInt(addAndDrawList.size()));
					} else
						return ArtificialIntelligence.NO_BUY;
				} else {
					if (treasureValue == 4) {
						if (board.getTableForActionCards().containsKey(CardName.FEAST.getName())) {
							return CardName.FEAST.getName();
						} else {
							return CardName.SILVER.getName();
						}
					} else {
						if (board.getTableForActionCards().containsKey(CardName.VILLAGE.getName())) {
							return CardName.VILLAGE.getName();
						} else {
							return CardName.SILVER.getName();
						}
					}
				}
			default:
				return ArtificialIntelligence.NO_BUY;
			}
		} else {
			switch (strategy) {
			case BIG_MONEY: // 2 Moats (first has to be bought before silver)
				if (treasureValue >= 8) {
					return CardName.PROVINCE.getName();
				} else if (treasureValue >= 6) {
					return CardName.GOLD.getName();
				} else if (treasureValue >= 5 && this.getPileSize(CardName.PROVINCE.getName()) <= 5) {
					return CardName.DUCHY.getName();
				} else if (treasureValue >= 4 && this.player.getTurnNr() >= 6 && board.getTableForActionCards().containsKey(CardName.SMITHY.getName())
						&& this.player.getDeck().containsAmountOf(CardType.ACTION) < 2) {
					return CardName.SMITHY.getName();
				} else if (treasureValue >= 2 && board.getTableForActionCards().containsKey(CardName.MOAT.getName()) && !this.player.getDeck().contains(CardName.MOAT.getName())
						&& (discardModeCount > 2 || this.player.getDeck().contains(CardName.CURSE.getName()) || attacksAvailableRatio < MOAT_RATIO_1)) {
					return CardName.MOAT.getName();
				} else if (treasureValue >= 3) {
					return CardName.SILVER.getName();
				} else if (treasureValue >= 2 && board.getTableForActionCards().containsKey(CardName.MOAT.getName()) && attacksAvailableRatio < MOAT_RATIO_2
						&& this.player.getDeck().containsAmountOf(CardName.MOAT.getName()) < 2) {
					return CardName.MOAT.getName();
				} else
					return CardName.ESTATE.getName();
			case BIG_MONEY_CHAPEL: // 2 Moats
				if (treasureValue >= 8) {
					return CardName.PROVINCE.getName();
				} else if (treasureValue >= 6) {
					if (this.player.getDeck().containsAmountOf(CardName.GOLD.getName()) >= 5 && board.getTableForActionCards().containsKey(CardName.ADVENTURER.getName())
							&& !this.player.getDeck().contains(CardName.ADVENTURER.getName())) {
						return CardName.ADVENTURER.getName();
					} else
						return CardName.GOLD.getName();
				} else if (treasureValue >= 5 && this.getPileSize(CardName.PROVINCE.getName()) <= 5) {
					return CardName.DUCHY.getName();
				} else if (treasureValue >= 2 && !this.getPlayer().getDeck().contains(CardName.CHAPEL.getName())) {
					return CardName.CHAPEL.getName();
				} else if (treasureValue >= 2 && board.getTableForActionCards().containsKey(CardName.MOAT.getName()) && !this.player.getDeck().contains(CardName.MOAT.getName())
						&& (discardModeCount > 3 || this.player.getDeck().contains(CardName.CURSE.getName()) || attacksAvailableRatio < MOAT_RATIO_1)) {
					return CardName.MOAT.getName();
				} else if (treasureValue >= 3) {
					return CardName.SILVER.getName();
				} else if (treasureValue >= 2 && board.getTableForActionCards().containsKey(CardName.MOAT.getName()) && attacksAvailableRatio < MOAT_RATIO_2
						&& this.player.getDeck().containsAmountOf(CardName.MOAT.getName()) < 2) {
					return CardName.MOAT.getName();
				} else
					return CardName.ESTATE.getName();
			case BIG_MONEY_CHAPEL_MILITIA: // 1 Moat and 2 Militias
				if (treasureValue >= 8) {
					return CardName.PROVINCE.getName();
				} else if (treasureValue >= 6) {
					return CardName.GOLD.getName();
				} else if (treasureValue >= 5 && this.getPileSize(CardName.PROVINCE.getName()) <= 5) {
					return CardName.DUCHY.getName();
				} else if (treasureValue >= 4 && this.player.getDeck().containsAmountOf(CardName.MILITIA.getName()) < 2 && board.getTableForActionCards().containsKey(CardName.MILITIA.getName())) {
					return CardName.MILITIA.getName();
				} else if (treasureValue >= 2 && !this.getPlayer().getDeck().contains(CardName.CHAPEL.getName())) {
					return CardName.CHAPEL.getName();
				} else if (treasureValue >= 3) {
					return CardName.SILVER.getName();
				} else if (treasureValue >= 2 && board.getTableForActionCards().containsKey(CardName.MOAT.getName()) && !this.player.getDeck().contains(CardName.MOAT.getName())
						&& (discardModeCount > 3 || this.player.getDeck().contains(CardName.CURSE.getName()) || attacksAvailableRatio < MOAT_RATIO_1)) {
					return CardName.MOAT.getName();
				} else
					return CardName.ESTATE.getName();
			case BIG_MONEY_CHAPEL_WITCH: // No Moats and 2 Witches
				if (treasureValue >= 8) {
					return CardName.PROVINCE.getName();
				} else if (treasureValue >= 6) {
					return CardName.GOLD.getName();
				} else if (treasureValue >= 5) {
					if (this.player.getDeck().containsAmountOf(CardName.WITCH.getName()) < 2 && board.getTableForActionCards().containsKey(CardName.WITCH.getName()))
						return CardName.WITCH.getName();
					else if (this.getPileSize(CardName.PROVINCE.getName()) <= 5)
						return CardName.DUCHY.getName();
					else
						return CardName.SILVER.getName();
				} else if (treasureValue >= 2 && !this.getPlayer().getDeck().contains(CardName.CHAPEL.getName())) {
					return CardName.CHAPEL.getName();
				} else if (treasureValue >= 3) {
					return CardName.SILVER.getName();
				} else
					return CardName.ESTATE.getName();
			case BIG_MONEY_WITCH: // 1 Moat and 2 Witches
				if (treasureValue >= 8) {
					return CardName.PROVINCE.getName();
				} else if (treasureValue >= 6) {
					return CardName.GOLD.getName();
				} else if (treasureValue >= 5) {
					if (this.player.getDeck().containsAmountOf(CardName.WITCH.getName()) < 2 && board.getTableForActionCards().containsKey(CardName.WITCH.getName()))
						return CardName.WITCH.getName();
					else if (this.getPileSize(CardName.PROVINCE.getName()) <= 5)
						return CardName.DUCHY.getName();
					else
						return CardName.SILVER.getName();
				} else if (treasureValue >= 3) {
					return CardName.SILVER.getName();
				} else if (treasureValue >= 2 && board.getTableForActionCards().containsKey(CardName.MOAT.getName()) && !this.player.getDeck().contains(CardName.MOAT.getName())
						&& (discardModeCount > 3 || this.player.getDeck().contains(CardName.CURSE.getName()) || attacksAvailableRatio < MOAT_RATIO_1)) {
					return CardName.MOAT.getName();
				} else
					return CardName.ESTATE.getName();
			case DRAW_ADD_ACTION: // 3 Moats Max.
				if (treasureValue >= 8) {
					return CardName.PROVINCE.getName();
				} else if (treasureValue >= 6) {
					return CardName.GOLD.getName();
				} else if (treasureValue >= 5) {
					LinkedList<String> addAndDrawList = CollectionsUtil.join(board.getActionCardsWithActionWhichCost(CardAction.ADD_ACTION_TO_PLAYER, 5),
							board.getActionCardsWithActionWhichCost(CardAction.DRAW_CARD, 5));
					return addAndDrawList.get(new Random().nextInt(addAndDrawList.size()));
				} else if (treasureValue >= 2 && board.getTableForActionCards().containsKey(CardName.MOAT.getName()) && !this.player.getDeck().contains(CardName.MOAT.getName())
						&& (discardModeCount > 3 || this.player.getDeck().contains(CardName.CURSE.getName()) || attacksAvailableRatio < MOAT_RATIO_1)) {
					return CardName.MOAT.getName();
				} else if (treasureValue >= 4 && !this.player.getDeck().contains(CardName.MILITIA.getName()) && board.getTableForActionCards().containsKey(CardName.MILITIA.getName())
						&& this.player.getTurnNr() >= 7) {
					// punish potential Councilroom with Militia
					return CardName.MILITIA.getName();
				} else if (treasureValue >= 3) {
					if (Math.random() < 0.5)
						if (board.getTableForActionCards().containsKey(CardName.VILLAGE.getName()))
							return CardName.VILLAGE.getName();
					return CardName.SILVER.getName();
				} else if (treasureValue >= 2 && board.getTableForActionCards().containsKey(CardName.MOAT.getName()) && attacksAvailableRatio < MOAT_RATIO_2
						&& this.player.getDeck().containsAmountOf(CardName.MOAT.getName()) < 3) {
					return CardName.MOAT.getName();
				} else
					return CardName.ESTATE.getName();
			default:
				return CardName.ESTATE.getName();
			}
		}
	}

	/**
	 * method is called once in the first turn to determine a strategy according
	 * to the situation on the game board
	 */
	private void determineStrategy() {
		GameBoard board = player.getGameServer().getGameController().getGameBoard();

		int cardHandValue = getTreasureCardsValue(getCardHand());
		this.fiveTwoStart = (cardHandValue == 5 || cardHandValue == 2) ? true : false;
		this.attacks = board.amountOfPilesWithType(CardType.ATTACK);

		if (board.getTableForActionCards().containsKey(CardName.CHAPEL.getName())) {
			if (board.getTableForActionCards().containsKey(CardName.MILITIA.getName()) && board.getTableForActionCards().containsKey(CardName.WITCH.getName())) {
				if (fiveTwoStart) {
					this.strategy = Strategy.BIG_MONEY_CHAPEL_WITCH;
					return;
				} else {
					this.strategy = Strategy.BIG_MONEY_CHAPEL_MILITIA;
					return;
				}
			} else if (board.getTableForActionCards().containsKey(CardName.WITCH.getName())) {
				this.strategy = Strategy.BIG_MONEY_CHAPEL_WITCH;
				return;
			} else if (board.getTableForActionCards().containsKey(CardName.MILITIA.getName()) && iAmTheOnlyAI()) {
				this.strategy = Strategy.BIG_MONEY_CHAPEL_MILITIA;
				return;
			} else {
				this.strategy = Strategy.BIG_MONEY_CHAPEL;
				return;
			}
		} else if (board.getTableForActionCards().containsKey(CardName.WITCH.getName())) {
			this.strategy = Strategy.BIG_MONEY_WITCH;
			return;
		} else if (board.getTableForActionCards().containsKey(CardName.SMITHY.getName()) && !fiveTwoStart) {
			this.strategy = Strategy.BIG_MONEY;
			return;
		} else if (this.drawAddActionStrategyPossible()) {
			this.strategy = Strategy.DRAW_ADD_ACTION;
			return;
		} else
			this.strategy = Strategy.BIG_MONEY;
	}

	/* ---------- game information ---------- */

	/**
	 * 
	 * @return if it's the AIs turn
	 */
	private boolean myTurn() {
		if (this.player.getGameServer().getGameController().isActivePlayerNameAvailable()) {
			String activePlayerName = this.player.getGameServer().getGameController().getActivePlayerName();
			return activePlayerName.equals(this.player.getPlayerName());
		}
		return false;
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
	 * check different game(board) states and set the endPhase if necessary
	 */
	private void checkEndPhase() {
		if (getPileSize(CardName.PROVINCE.getName()) < 4) {
			this.endPhase = true;
		} else if (this.player.getGameServer().getGameController().getGameBoard().getSizeOfSmallestPilesOnBoard(3) <= 5) {
			this.endPhase = true;
		}
	}

	/**
	 * the least valuable card of the players cardhand will be discarded
	 * 
	 * method had to cover several cases which are very important for the AI if
	 * it plays a chapel to discard the right cards. otherwise it would have a
	 * huge impact on the performance and speed of the AI (in terms of how fast
	 * it can win). That's why there are so many if/elses
	 */
	private void discardLeastValuableCard() {
		GameLog.log(MsgType.GAME_INFO, "In AI.discardLeastValuableCard(), see next line for debugCardHandPrint()");
		this.player.getDeck().debugCardHandPrint();

		int treasureValue = getTreasureCardsValue(getCardHand());

		/** BIG_MONEY(_..) discarding */
		if (!this.strategy.equals(Strategy.DRAW_ADD_ACTION)) {
			/** if card hand doesn't contain a Chapel */
			if (!this.player.getDeck().cardHandContains(CardName.CHAPEL.getName())) {
				/** discard a CURSE */
				if (this.player.getDeck().cardHandContains(CardType.CURSE)) {
					discard(this.player.getDeck().getCardByTypeFromHand(CardType.CURSE));
					return;
				}
				/** discard a random VICTORY card */
				if (this.player.getDeck().cardHandContains(CardType.VICTORY)) {
					discard(this.player.getDeck().getCardByTypeFromHand(CardType.VICTORY));
					return;
				}
				/**
				 * or if the treasureValue on card hand is >= 6, discard the
				 * action card with the lowest cost
				 */
				if (treasureValue >= 6) {
					if (this.player.getDeck().cardHandContains(CardType.ACTION)) {
						discard(this.player.getDeck().cardWithLowestCost(getCardHand(), CardType.ACTION));
						return;
					}
				}
			}
			/** if card hand contains a Chapel */
			if (this.player.getDeck().cardHandContains(CardName.CHAPEL.getName())) {
				/**
				 * if there are less than two cards which can possibly be
				 * trashed by the chapel, discard the chapel itself if the
				 * treasureValue on card hand is >= 7, discard the action card
				 * with the lowest cost
				 */
				if (canBeTrashedByChapel() < 2 || treasureValue >= 7) {
					discard(this.player.getDeck().getCardByNameFromHand(CardName.CHAPEL.getName()));
					return;
				}
				/** discard any VICTORY card but Estate */
				LinkedList<Card> victoryOnCardHand = this.player.getDeck().getCardsByTypeFrom(CardType.VICTORY, this.getCardHand());
				if (victoryOnCardHand != null && victoryOnCardHand.size() > 0) {
					for (Card c : victoryOnCardHand) {
						if (!c.getName().equals(CardName.ESTATE.getName())) {
							discard(c);
							return;
						}
					}
				}
			}
			/** discard a COPPER */
			if (this.player.getDeck().cardHandContains(CardName.COPPER.getName())) {
				discard(this.player.getDeck().getCardByNameFromHand(CardName.COPPER.getName()));
				return;
			}
			/** discard another TREASURE card */
			if (this.player.getDeck().cardHandContains(CardType.TREASURE)) {
				GameLog.log(MsgType.ERROR, "the method must not get here (unless it has a card hand like 5x gold");
				discard(this.player.getDeck().cardWithLowestCost(getCardHand(), CardType.TREASURE));
				return;
			}
			/** DRAW_ADD_ACTION discarding */
		} else {
			/** discard a CURSE */
			if (this.player.getDeck().cardHandContains(CardType.CURSE)) {
				discard(this.player.getDeck().getCardByTypeFromHand(CardType.CURSE));
				return;
			}
			/** discard a random VICTORY card */
			if (this.player.getDeck().cardHandContains(CardType.VICTORY)) {
				discard(this.player.getDeck().getCardByTypeFromHand(CardType.VICTORY));
				return;
			}
			/** discard a COPPER */
			if (this.player.getDeck().cardHandContains(CardName.COPPER.getName())) {
				discard(this.player.getDeck().getCardByNameFromHand(CardName.COPPER.getName()));
				return;
			}
			/** discard the ACTION card with lowest cost on hand */
			if (this.player.getDeck().cardHandContains(CardType.ACTION)) {
				discard(this.player.getDeck().cardWithLowestCost(getCardHand(), CardType.ACTION));
				return;
			}
			/** discard another TREASURE card */
			if (this.player.getDeck().cardHandContains(CardType.TREASURE)) {
				GameLog.log(MsgType.ERROR, "the method must not get here (unless it has a card hand like 5x gold");
				discard(this.player.getDeck().cardWithLowestCost(getCardHand(), CardType.TREASURE));
				return;
			}
		}
	}

	/**
	 * 
	 * @param cards
	 *            the cardList to check
	 * @return the value of all treasure cards in the given list
	 */
	private int getTreasureCardsValue(LinkedList<Card> cards) {
		return this.player.getDeck().getTreasureValueOfList(cards);
	}

	/**
	 * 
	 * @param cardType
	 *            the CardType to search for
	 * @return all cards from the players deck with type cardType
	 */
	private LinkedList<Card> getAllCardsFromType(CardType cardType) {
		return this.player.getDeck().getCardsByTypeFrom(cardType, getCardHand());
	}

	/**
	 * 
	 * @return whether there is a card on the players cardhand which adds at
	 *         least 1 action to the players remaining actions when played (e.g.
	 *         Market)
	 */
	private boolean addActionCardAvailable() {
		return !this.player.getDeck().cardsWithAction(CardAction.ADD_ACTION_TO_PLAYER, this.player.getDeck().getCardHand()).isEmpty();
	}

	/**
	 * 
	 * @return the amount of Provinces left on the board
	 */
	private int getPileSize(String cardname) {
		return this.player.getGameServer().getGameController().getGameBoard().getTableForVictoryCards().get(cardname).size();
	}

	/**
	 * 
	 * @return whether the AI is the only non-human player
	 */
	private boolean iAmTheOnlyAI() {
		return this.player.getGameServer().getGameController().getArtificialPlayers().size() == 1;
	}

	/**
	 * @param turn
	 *            the turn to ask for
	 * @return whether it is the first turn of the AI
	 */
	private boolean turn(int turn) {
		return this.player.getTurnNr() == turn;
	}

	/**
	 * 
	 * @return the cardHand of the player
	 */
	private LinkedList<Card> getCardHand() {
		return this.player.getDeck().getCardHand();
	}

	/**
	 * 
	 * @return whether the strategy DRAW_ADD_ACTION will can be successful
	 */
	private boolean drawAddActionStrategyPossible() {
		int count = 0;
		GameBoard board = player.getGameServer().getGameController().getGameBoard();
		ArrayList<String> cardNames = CollectionsUtil.getArrayList(new String[] { CardName.COUNCILROOM.getName(), CardName.FESTIVAL.getName(), CardName.LABORATORY.getName(), CardName.MARKET.getName(),
				CardName.VILLAGE.getName(), CardName.WITCH.getName() });

		for (String name : cardNames) {
			if (board.getTableForActionCards().containsKey(name)) {
				count++;
			}
		}
		return count >= 3;
	}

	/**
	 * 
	 * @return how many cards on card hand are trash worthy and would be trashed
	 *         by the chapel
	 */
	private int canBeTrashedByChapel() {
		LinkedList<Card> cards = getCardHand();
		int canBeTrashed = 0;
		for (Card card : cards) {
			if ((card.getName().equals(CardName.ESTATE.getName()) && !lastEstateInDeck(card)) || card.getName().equals(CardName.COPPER.getName()) || card.getName().equals(CardName.CURSE.getName())) {
				canBeTrashed++;
			}
		}
		return canBeTrashed;
	}
	
	private LinkedList<Card> getTrashWorthyCards() {
		LinkedList<Card> resultList = new LinkedList<Card>();
		for (Card c : getCardHand()) {
			if (this.player.getDeck().containsAmountOf(CardName.ESTATE.getName()) > 1) {
				
			}
		}
		return resultList;
	}
	
	private boolean lastEstateInDeck(Card c) {
		return this.player.getDeck().containsAmountOf(CardName.ESTATE.getName()) == 1;
	}
	
	private boolean isTrashWorthy(Card card) {
		return (card.getName().equals(CardName.ESTATE.getName()) && !lastEstateInDeck(card)) || card.getName().equals(CardName.COPPER.getName()) || card.getName().equals(CardName.CURSE.getName());
	}

	/* ---------- getters & setters ---------- */

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
	 * @return the strategy
	 */
	public Strategy getStrategy() {
		return strategy;
	}

	/**
	 * @param strategy
	 *            the strategy to set
	 */
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * @return the buySequence
	 */
	public List<String> getBuySequence() {
		return buySequence;
	}

	/**
	 * @param buySequence
	 *            the buySequence to set
	 */
	public void setBuySequence(List<String> buySequence) {
		this.buySequence = buySequence;
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
	 * @param endPhase
	 *            the endPhase to set
	 */
	public void setEndPhase(boolean endPhase) {
		this.endPhase = endPhase;
	}
}