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
import com.tpps.technicalServices.network.gameSession.packets.PacketEndTrashMode;
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
	 * basic method which plays the given card
	 * 
	 * @param card
	 *            the card to play
	 */
	private void playAction(Card card) {
		if (card != null && this.player.getActions() > 0) {
			sendPacket(new PacketPlayCard(card.getId(), this.player.getClientID()));
		} else {
			GameLog.log(MsgType.ERROR, this.player.getPlayerName() + " tried to play 'null' card or had no actions left.");
		}
	}

	/**
	 * basic method which plays a reaction card if the AI is not the active
	 * player at the moment
	 * 
	 * @param card
	 *            the card to play
	 */
	private void playReaction(Card card) {
		if (card != null && card.getTypes().contains(CardType.REACTION)) {
			sendPacket(new PacketPlayCard(card.getId(), this.player.getClientID()));
		} else {
			GameLog.log(MsgType.ERROR, this.player.getPlayerName() + " tried to play 'null' card or had no actions left.");
		}
	}

	/**
	 * basic method which discards the given card
	 * 
	 * @param card
	 *            the card to discard
	 */
	private void playDiscard(Card card) {
		if (card != null && this.player.isDiscardMode()) {
			sendPacket(new PacketPlayCard(card.getId(), this.player.getClientID()));
		} else {
			GameLog.log(MsgType.ERROR, this.player.getPlayerName() + " tried to discard 'null' card or player was not in discard mode.");
		}
	}

	/**
	 * basic method which trashes the given card
	 * 
	 * @param card
	 *            the card to discard
	 */
	private void playTrash(Card card) {
		if (card != null && this.player.isTrashMode()) {
			sendPacket(new PacketPlayCard(card.getId(), this.player.getClientID()));
		} else {
			GameLog.log(MsgType.ERROR, this.player.getPlayerName() + " tried to discard 'null' card or player was not in discard mode.");
		}
	}

	/**
	 * before calling this method, it has to be made sure that there is a chapel
	 * on hand
	 * 
	 * the method plays the chapel card and all of its dependencies
	 * 
	 * @throws InterruptedException
	 */
	private void playChapel() throws InterruptedException {
		LinkedList<Card> trashCards = getTrashWorthyCards();
		playAction(this.player.getDeck().getCardByNameFromHand(CardName.CHAPEL.getName()));
		sleep();
		trash(trashCards);
		if (this.player.isTrashMode()) {
			endTrash();
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
				playAction(card);
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

		GameLog.log(MsgType.GAME_INFO, "playActionCards(), cardPrint:");
		debugCardPrint("cardHand", getCardHand());
		
		while (this.player.getDeck().cardHandContains(CardType.ACTION) && this.player.getActions() > 0) {
			GameLog.log(MsgType.DEBUG, "erste While in playActionCards()");
			sleep();
//			while (this.player.getGameServer().getGameController().playerStillInReactionMode()) {
//				GameLog.log(MsgType.DEBUG, "Debug Ausgabe: Endlosschleife? other players are still in reaction mode");
//				sleep();
//			}
			debugCardPrint("cardHand", getCardHand());
			/**
			 * in BIG_MONEY(_..) strategies, the execution of an action card
			 * will not give more buying power if there is already a treasure
			 * value >= 8 (so you're already able to buy the most valuable card:
			 * province). A second benefit is that the unplayed action card will
			 * be reshuffled and can be drawn earlier again.
			 */
			if (!this.strategy.equals(Strategy.DRAW_ADD_ACTION) && this.getTreasureCardsValue(getCardHand()) >= 8) {
				return;
			}
			/**
			 * if there is an action card in cardHand which gives the player +1
			 * action (ADD_ACTION_TO_PLAYER), play this card first
			 */
			if (addActionCardAvailable()) {
				playAction(this.player.getDeck().cardWithAction(CardAction.ADD_ACTION_TO_PLAYER, getCardHand()));
				sleep();
				while (this.player.getGameServer().getGameController().playerStillInReactionMode()) {
					GameLog.log(MsgType.DEBUG, "Debug Ausgabe: Endlosschleife? other players are still in reaction mode");
					sleep();
				}
				continue;
			}
			/**
			 * otherwise, if there is only 1 ACTION card on hand, play it
			 */
			GameLog.log(MsgType.DEBUG, "cardHandAmount ACTION: " + this.player.getDeck().cardHandAmount(CardType.ACTION));
			if (this.player.getDeck().cardHandAmount(CardType.ACTION) == 1) {
				GameLog.log(MsgType.INFO, "1 action");
				if (this.player.getDeck().cardHandContains(CardName.CHAPEL.getName())) {
					GameLog.log(MsgType.INFO, "and it is played");
					playChapel();
					sleep();
					continue;
				} else {
					GameLog.log(MsgType.INFO, "and it is played");
					playAction(this.player.getDeck().getCardByTypeFromHand(CardType.ACTION));
					sleep();
					while (this.player.getGameServer().getGameController().playerStillInReactionMode()) {
						GameLog.log(MsgType.DEBUG, "Debug Ausgabe: Endlosschleife? other players are still in reaction mode");
						sleep();
					}
					continue;
				}
			}
			/**
			 * if there are more than one ACTION cards on hand, decide depending
			 * on the strategy and on the trashWorthyCards, what to play
			 */
			if (this.player.getDeck().cardHandAmount(CardType.ACTION) >= 2) {
				/**
				 * if there are 3 trashWorthy cards or more (but the amount of
				 * coppers in hand is less than 3) play Chapel
				 */
				if (getTrashWorthyCards().size() >= 3 && this.player.getDeck().cardHandAmount(CardName.COPPER.getName()) < 3 && this.player.getDeck().cardHandContains(CardName.CHAPEL.getName())) {
					playChapel();
					sleep();
					continue;
				} else {
					switch (this.strategy) {
					case BIG_MONEY:
					case BIG_MONEY_CHAPEL:
					case BIG_MONEY_CHAPEL_MILITIA:
						if (this.player.getDeck().cardHandContains(CardName.MILITIA.getName())) {
							playAction(this.player.getDeck().getCardByNameFromHand(CardName.MILITIA.getName()));
							while (this.player.getGameServer().getGameController().playerStillInReactionMode()) {
								GameLog.log(MsgType.DEBUG, "Debug Ausgabe: Endlosschleife? other players are still in reaction mode");
								sleep();
							}
							sleep();
							continue;
						}
					case BIG_MONEY_CHAPEL_WITCH:
					case BIG_MONEY_WITCH:
						if (this.player.getDeck().cardHandContains(CardName.WITCH.getName())) {
							playAction(this.player.getDeck().getCardByNameFromHand(CardName.WITCH.getName()));
							while (this.player.getGameServer().getGameController().playerStillInReactionMode()) {
								GameLog.log(MsgType.DEBUG, "Debug Ausgabe: Endlosschleife? other players are still in reaction mode");
								sleep();
							}
							sleep();
							continue;
						}
					case DRAW_ADD_ACTION:
					default:
						if (this.player.getDeck().cardHandContains(CardName.WITCH.getName())) {
							playAction(this.player.getDeck().getCardByNameFromHand(CardName.WITCH.getName()));
							while (this.player.getGameServer().getGameController().playerStillInReactionMode()) {
								GameLog.log(MsgType.DEBUG, "Debug Ausgabe: Endlosschleife? other players are still in reaction mode");
								sleep();
							}
							sleep();
							continue;
						} else if (this.player.getDeck().cardHandContains(CardName.MILITIA.getName())) {
							playAction(this.player.getDeck().getCardByNameFromHand(CardName.MILITIA.getName()));
							while (this.player.getGameServer().getGameController().playerStillInReactionMode()) {
								GameLog.log(MsgType.DEBUG, "Debug Ausgabe: Endlosschleife? other players are still in reaction mode");
								sleep();
							}
							sleep();
							continue;
						} else {
							LinkedList<Card> remainingActionCards = this.getAllCardsFromType(CardType.ACTION);
							playAction(remainingActionCards.get(new Random().nextInt(remainingActionCards.size())));
							while (this.player.getGameServer().getGameController().playerStillInReactionMode()) {
								GameLog.log(MsgType.DEBUG, "Debug Ausgabe: Endlosschleife? other players are still in reaction mode");
								sleep();
							}
							sleep();
							continue;
						}
					}
				}
			}
		}
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
					playAction(card);
					alreadyPlayed += Integer.valueOf(card.getActions().get(CardAction.IS_TREASURE));
				} else
					return;
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
	 * send a PacketEndActionPhase() to end the actionPhase and start the
	 * buyPhase
	 */
	private void setBuyPhase() {
		sendPacket(new PacketEndActionPhase());
	}

	/**
	 * send a PacketEndTurn() if the AI wants to end the turn 'manually' before
	 * it is ended automatically (e.g. if the player has no more buys left)
	 */
	private void endTurn() {
		// GameLog.log(MsgType.AI, this.player.getPlayerName() + " ended Turn by
		// itself.");
		sendPacket(new PacketEndTurn());
	}

	/**
	 * send a PacketEndTrashMode() if the AI wants to end the trashMode
	 * 'manually' before it is ended automatically (e.g. if it already discarded
	 * 4 cards with chapel)
	 */
	private void endTrash() {
		sendPacket(new PacketEndTrashMode());
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
			if (this.player.getDeck().cardHandContains(CardType.REACTION)) {
				playReaction(this.player.getDeck().getCardByTypeFromHand(CardType.REACTION));
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
		debugCardPrint("cardHand", getCardHand());
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
		// GameLog.log(MsgType.AI_DEBUG, "buy outer: " + buy);
		if (!buy.equals(ArtificialIntelligence.NO_BUY) && (!blacklist.contains(buy) || endPhase)) {
			sleep();
			try {
				Card cardToBuy = this.getCardFromBoard(buy);
				this.buy(cardToBuy);
				// GameLog.log(MsgType.AI_DEBUG, "buy inner: " + buy);
			} catch (NoSuchElementException nsee) {
				GameLog.log(MsgType.AI_DEBUG, "buy NoSuchElementException <<<<< " + buy);
				if (this.player.getCoins() >= 6 && !buy.equals(CardName.GOLD.getName()) && cardAvailableOnBoard(CardName.GOLD.getName()))
					this.buy(this.getCardFromBoard(CardName.GOLD.getName()));
				else if (this.player.getCoins() >= 3 && !buy.equals(CardName.SILVER.getName()) && cardAvailableOnBoard(CardName.SILVER.getName()))
					this.buy(this.getCardFromBoard(CardName.SILVER.getName()));
				else if (endPhase && this.player.getCoins() >= 2 && !buy.equals(CardName.ESTATE.getName()) && cardAvailableOnBoard(CardName.ESTATE.getName())) {
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
			GameLog.log(MsgType.AI_DEBUG, "getting PurchaseSequence: Strategy: " + this.strategy + ", treasureValue: " + getTreasureCardsValue(getCardHand()) + ", purchase: " + purchase
					+ ", new treasureValue: " + treasureValue);
		}
		return result;
	}

	/**
	 * depending on the start, strategy and board situation, AI determines the
	 * next purchase
	 * 
	 * @param treasureValue
	 *            the available value of coins for this purchase
	 * @return the computed purchase
	 */
	private String determinePurchase(int treasureValue) {
		GameBoard board = this.player.getGameServer().getGameController().getGameBoard();

		double attacksOriginally = attacks * GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue();
		double attacksBoughtByEnemies = attacksOriginally - board.getSizeOfPilesOnBoardWithType(CardType.ATTACK) - this.player.getDeck().containsAmountOf(CardType.ATTACK);
		double attacksAvailableRatio = (attacksOriginally - attacksBoughtByEnemies) / attacksOriginally;
		// GameLog.log(MsgType.AI_DEBUG, "attacksOriginally: " +
		// attacksOriginally + ", attacksEnemies: " + attacksBoughtByEnemies +
		// ", attacksAvailableRatio: " + attacksAvailableRatio);
		/**
		 * if a the ratio is not computed accurately, set its value to 1 so
		 * there won't be any exceptions or false positive buys
		 */
		if (attacksAvailableRatio > 1.0 || attacksAvailableRatio < 0.1) {
			attacksAvailableRatio = 1;
		}
		/**
		 * a bound at which point of the game, duchys will be bought by the AI:
		 * if the attacks are still all available (or there is no attack on the
		 * board ofc), the bound is at 5 because the AI can start to buy duchies
		 * earlier (without Militia/Witch the game is faster and the duchies
		 * have not that much of a negative impact) if attacks have been bought,
		 * the bound is at 3, because it is harder to compensate duchies
		 */
		int provinceBound = attacksAvailableRatio == 1.0 ? 5 : 3;

		/**
		 * first two turns are handled seperately, because they are a crucial
		 * part in the game and the rules on how to behave in this turns differ
		 * from the rest of the game in some cases
		 */
		if (turn(1) || turn(2)) {
			switch (strategy) {
			case BIG_MONEY:
				if (cardAvailableOnBoard(CardName.SMITHY.getName())) {
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
					/**
					 * no FEAST support yet
					 */
					if (treasureValue == 4) {
						// if
						// (cardAvailableOnBoard(CardName.FEAST.getName()))
						// {
						// return CardName.FEAST.getName();
						// } else {
						return CardName.SILVER.getName();
						// }
					} else {
						if (cardAvailableOnBoard(CardName.VILLAGE.getName())) {
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
			if (treasureValue >= 8 && cardAvailableOnBoard(CardName.PROVINCE.getName()) && (this.player.getDeck().containsAmountOf(CardName.GOLD.getName(), this.player.getPlayedCards()) > 2 || this.player.getTurnNr() >= 6) ) {
				return CardName.PROVINCE.getName();
			} else if (treasureValue >= 6 && !this.strategy.equals(Strategy.BIG_MONEY_CHAPEL)) {
				if (this.player.getDeck().containsAmountOf(CardName.GOLD.getName(), this.player.getPlayedCards()) > 7 && this.getPileSize(CardName.PROVINCE.getName()) < 6
						&& cardAvailableOnBoard(CardName.DUCHY.getName())) {
					return CardName.DUCHY.getName();
				} else if (cardAvailableOnBoard(CardName.GOLD.getName()))
					return CardName.GOLD.getName();
			}
			switch (strategy) {
			case BIG_MONEY: // 2 Moats (first has to be bought before silver)
				/**
				 * if there are at least 5 coins available and <= 5 PROVINCEs on
				 * the board, start to buy DUCHYs because they won't have a huge
				 * impact on the deck anymore in this phase of the game
				 */
				if (treasureValue >= 5 && this.getPileSize(CardName.PROVINCE.getName()) <= provinceBound && cardAvailableOnBoard(CardName.DUCHY.getName()))
					return CardName.DUCHY.getName();
				/**
				 * special case for a second smithy, because it's only in a few
				 * situations really good
				 */
				else if (treasureValue >= 4 && this.player.getTurnNr() >= 6 && cardAvailableOnBoard(CardName.SMITHY.getName()) && this.player.getDeck().containsAmountOf(CardType.ACTION) < 2)
					return CardName.SMITHY.getName();
				/**
				 * if other players buy ATTACKs, the player has already a CURSE
				 * in deck or was already more than 2 times in discard mode, the
				 * first MOAT will be bought
				 */
				else if (treasureValue >= 2 && cardAvailableOnBoard(CardName.MOAT.getName()) && !this.player.getDeck().contains(CardName.MOAT.getName(), this.player.getPlayedCards())
						&& (discardModeCount > 2 || this.player.getDeck().contains(CardName.CURSE.getName(), this.player.getPlayedCards()) || attacksAvailableRatio < MOAT_RATIO_1))
					return CardName.MOAT.getName();
				/**
				 * classic SILVER with 3 coins
				 */
				else if (treasureValue >= 3)
					return CardName.SILVER.getName();
				/**
				 * if MOAT_RATIO_2*100% of the ATTACKs are left on the board,
				 * buy a second MOAT (also not too good for BM, but necessary if
				 * there are so many ATTACKs played)
				 */
				else if (treasureValue >= 2 && cardAvailableOnBoard(CardName.MOAT.getName()) && attacksAvailableRatio < MOAT_RATIO_2
						&& this.player.getDeck().containsAmountOf(CardName.MOAT.getName(), this.player.getPlayedCards()) < 2)
					return CardName.MOAT.getName();
			case BIG_MONEY_CHAPEL: // 2 Moats
				/**
				 * ADVENTURER if the special case is available, GOLD otherwise
				 */
				if (treasureValue >= 6) {
					if (this.player.getDeck().containsAmountOf(CardName.GOLD.getName(), this.player.getPlayedCards()) >= 5 && cardAvailableOnBoard(CardName.ADVENTURER.getName())
							&& !this.player.getDeck().contains(CardName.ADVENTURER.getName(), this.player.getPlayedCards())) {
						return CardName.ADVENTURER.getName();
					} else if (this.player.getDeck().containsAmountOf(CardName.GOLD.getName(), this.player.getPlayedCards()) > 7 && this.getPileSize(CardName.PROVINCE.getName()) < 6
							&& cardAvailableOnBoard(CardName.DUCHY.getName())) {
						return CardName.DUCHY.getName();
					} else if (cardAvailableOnBoard(CardName.GOLD.getName()))
						return CardName.GOLD.getName();
				}
				/**
				 * if there are at least 5 coins available and <= 5 PROVINCEs on
				 * the board, start to buy DUCHYs because they won't have a huge
				 * impact on the deck anymore in this phase of the game
				 */
				else if (treasureValue >= 5 && this.getPileSize(CardName.PROVINCE.getName()) <= provinceBound && cardAvailableOnBoard(CardName.DUCHY.getName()))
					return CardName.DUCHY.getName();
				/**
				 * if no chapel has been bought yet, buy the first and only one
				 */
				else if (treasureValue >= 2 && !this.player.getDeck().contains(CardName.CHAPEL.getName(), this.player.getPlayedCards()) && cardAvailableOnBoard(CardName.CHAPEL.getName()))
					return CardName.CHAPEL.getName();
				/**
				 * if other players buy ATTACKs, the player has already a CURSE
				 * in deck or was already more than 3 times in discard mode, the
				 * first MOAT will be bought
				 */
				else if (treasureValue >= 2 && cardAvailableOnBoard(CardName.MOAT.getName()) && !this.player.getDeck().contains(CardName.MOAT.getName(), this.player.getPlayedCards())
						&& (discardModeCount > 3 || this.player.getDeck().contains(CardName.CURSE.getName(), this.player.getPlayedCards()) || attacksAvailableRatio < MOAT_RATIO_1))
					return CardName.MOAT.getName();
				/**
				 * classic SILVER with 3 coins
				 */
				else if (treasureValue >= 3 && cardAvailableOnBoard(CardName.SILVER.getName()))
					return CardName.SILVER.getName();
				/**
				 * if MOAT_RATIO_2*100% of the ATTACKs are left on the board,
				 * buy a second MOAT (also not too good for BM, but necessary if
				 * there are so many ATTACKs played)
				 */
				else if (treasureValue >= 2 && cardAvailableOnBoard(CardName.MOAT.getName()) && attacksAvailableRatio < MOAT_RATIO_2
						&& this.player.getDeck().containsAmountOf(CardName.MOAT.getName(), this.player.getPlayedCards()) < 2)
					return CardName.MOAT.getName();
			case BIG_MONEY_CHAPEL_MILITIA: // 1 Moat and 2 Militias
				/**
				 * if there are at least 5 coins available and <= 5 PROVINCEs on
				 * the board, start to buy DUCHYs because they won't have a huge
				 * impact on the deck anymore in this phase of the game
				 */
				if (treasureValue >= 5 && this.getPileSize(CardName.PROVINCE.getName()) <= provinceBound && cardAvailableOnBoard(CardName.DUCHY.getName()))
					return CardName.DUCHY.getName();
				/**
				 * if there are <2 MILITIAs in the deck, buy one
				 */
				else if (treasureValue >= 4 && this.player.getDeck().containsAmountOf(CardName.MILITIA.getName(), this.player.getPlayedCards()) < 2 && cardAvailableOnBoard(CardName.MILITIA.getName()))
					return CardName.MILITIA.getName();
				/**
				 * if no chapel has been bought yet, buy the first and only one
				 */
				else if (treasureValue >= 2 && !this.player.getDeck().contains(CardName.CHAPEL.getName(), this.player.getPlayedCards()) && cardAvailableOnBoard(CardName.CHAPEL.getName()))
					return CardName.CHAPEL.getName();
				/**
				 * classic SILVER with 3 coins
				 */
				else if (treasureValue >= 3 && cardAvailableOnBoard(CardName.SILVER.getName()))
					return CardName.SILVER.getName();
				/**
				 * if other players buy ATTACKs, the player has already a CURSE
				 * in deck or was already more than 3 times in discard mode, the
				 * first MOAT will be bought
				 */
				else if (treasureValue >= 2 && cardAvailableOnBoard(CardName.MOAT.getName()) && !this.player.getDeck().contains(CardName.MOAT.getName(), this.player.getPlayedCards())
						&& (discardModeCount > 3 || this.player.getDeck().contains(CardName.CURSE.getName(), this.player.getPlayedCards()) || attacksAvailableRatio < MOAT_RATIO_1))
					return CardName.MOAT.getName();
			case BIG_MONEY_CHAPEL_WITCH: // 1 Moat and 2 Witches
				/**
				 * if there are <2 WITCHES, buy one OR <= 5 PROVINCEs on the
				 * board, start to buy DUCHYs because they won't have a huge
				 * impact on the deck anymore in this phase of the game. If both
				 * conditions are false, buy SILVER
				 */
				if (treasureValue >= 5) {
					if (this.player.getDeck().containsAmountOf(CardName.WITCH.getName(), this.player.getPlayedCards()) < 2 && cardAvailableOnBoard(CardName.WITCH.getName()))
						return CardName.WITCH.getName();
					else if (this.getPileSize(CardName.PROVINCE.getName()) <= provinceBound && cardAvailableOnBoard(CardName.DUCHY.getName()))
						return CardName.DUCHY.getName();
					else if (cardAvailableOnBoard(CardName.SILVER.getName()))
						return CardName.SILVER.getName();
				}
				/**
				 * if no chapel has been bought yet, buy the first and only one
				 */
				else if (treasureValue >= 2 && !this.player.getDeck().contains(CardName.CHAPEL.getName(), this.player.getPlayedCards()) && cardAvailableOnBoard(CardName.CHAPEL.getName()))
					return CardName.CHAPEL.getName();
				/**
				 * if other players buy ATTACKs, the player has already a CURSE
				 * in deck or was already more than 3 times in discard mode, the
				 * first MOAT will be bought
				 */
				else if (treasureValue >= 2 && cardAvailableOnBoard(CardName.MOAT.getName()) && !this.player.getDeck().contains(CardName.MOAT.getName(), this.player.getPlayedCards())
						&& (discardModeCount > 3 || this.player.getDeck().contains(CardName.CURSE.getName(), this.player.getPlayedCards()) || attacksAvailableRatio < MOAT_RATIO_1))
					return CardName.MOAT.getName();
				/**
				 * classic SILVER with 3 coins
				 */
				else if (treasureValue >= 3)
					return CardName.SILVER.getName();
			case BIG_MONEY_WITCH: // 1 Moat and 2 Witches
				/**
				 * if there are <2 WITCHES, buy one OR <= 5 PROVINCEs on the
				 * board, start to buy DUCHYs because they won't have a huge
				 * impact on the deck anymore in this phase of the game. If both
				 * conditions are false, buy SILVER
				 */
				if (treasureValue >= 5) {
					if (this.player.getDeck().containsAmountOf(CardName.WITCH.getName(), this.player.getPlayedCards()) < 2 && cardAvailableOnBoard(CardName.WITCH.getName()))
						return CardName.WITCH.getName();
					else if (this.getPileSize(CardName.PROVINCE.getName()) <= provinceBound && cardAvailableOnBoard(CardName.DUCHY.getName()))
						return CardName.DUCHY.getName();
					else if (cardAvailableOnBoard(CardName.SILVER.getName()))
						return CardName.SILVER.getName();
				}
				/**
				 * classic SILVER with 3 coins
				 */
				else if (treasureValue >= 3 && cardAvailableOnBoard(CardName.SILVER.getName()))
					return CardName.SILVER.getName();
				/**
				 * if other players buy ATTACKs, the player has already a CURSE
				 * in deck or was already more than 3 times in discard mode, the
				 * first MOAT will be bought
				 */
				else if (treasureValue >= 2 && cardAvailableOnBoard(CardName.MOAT.getName()) && !this.player.getDeck().contains(CardName.MOAT.getName(), this.player.getPlayedCards())
						&& (discardModeCount > 3 || this.player.getDeck().contains(CardName.CURSE.getName(), this.player.getPlayedCards()) || attacksAvailableRatio < MOAT_RATIO_1))
					return CardName.MOAT.getName();
			case DRAW_ADD_ACTION: // 3 Moats Max.
				/**
				 * with 5 coins, buy an action card (random) which has either
				 * +ACTIONs, +DRAW_CARDs or both.
				 */
				if (treasureValue >= 5) {
					LinkedList<String> addAndDrawList = CollectionsUtil.join(board.getActionCardsWithActionWhichCost(CardAction.ADD_ACTION_TO_PLAYER, 5),
							board.getActionCardsWithActionWhichCost(CardAction.DRAW_CARD, 5));
					String card = addAndDrawList.get(new Random().nextInt(addAndDrawList.size()));
					if (cardAvailableOnBoard(card))
						return card;
				}
				/**
				 * if other players buy ATTACKs, the player has already a CURSE
				 * in deck or was already more than 3 times in discard mode, the
				 * first MOAT will be bought
				 */
				else if (treasureValue >= 2 && cardAvailableOnBoard(CardName.MOAT.getName()) && !this.player.getDeck().contains(CardName.MOAT.getName(), this.player.getPlayedCards())
						&& (discardModeCount > 3 || this.player.getDeck().contains(CardName.CURSE.getName(), this.player.getPlayedCards()) || attacksAvailableRatio < MOAT_RATIO_1))
					return CardName.MOAT.getName();
				/**
				 * try to play FESTIVAL, LABORATORY, COUNCILROOM etc.. and in
				 * the end a MILITIA. that's why this is bought here.
				 */
				else if (treasureValue >= 4 && !this.player.getDeck().contains(CardName.MILITIA.getName(), this.player.getPlayedCards()) && cardAvailableOnBoard(CardName.MILITIA.getName())
						&& this.player.getTurnNr() >= 7)
					// punish potential Councilroom with Militia
					return CardName.MILITIA.getName();
				else if (treasureValue >= 3) {
					if (Math.random() < 0.5) {
						if (cardAvailableOnBoard(CardName.VILLAGE.getName())) {
							return CardName.VILLAGE.getName();
						}
					}
					if (cardAvailableOnBoard(CardName.SILVER.getName()))
						return CardName.SILVER.getName();
				} else if (treasureValue >= 2 && cardAvailableOnBoard(CardName.MOAT.getName()) && attacksAvailableRatio < MOAT_RATIO_2
						&& this.player.getDeck().containsAmountOf(CardName.MOAT.getName(), this.player.getPlayedCards()) < 3)
					return CardName.MOAT.getName();
			default:
				if (cardAvailableOnBoard(CardName.ESTATE.getName())) {
					return CardName.ESTATE.getName();
				} else
					return ArtificialIntelligence.NO_BUY;
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

		if (cardAvailableOnBoard(CardName.CHAPEL.getName())) {
			if (cardAvailableOnBoard(CardName.MILITIA.getName()) && cardAvailableOnBoard(CardName.WITCH.getName())) {
				if (fiveTwoStart) {
					this.strategy = Strategy.BIG_MONEY_CHAPEL_WITCH;
					return;
				} else {
					this.strategy = Strategy.BIG_MONEY_CHAPEL_MILITIA;
					return;
				}
			} else if (cardAvailableOnBoard(CardName.WITCH.getName())) {
				this.strategy = Strategy.BIG_MONEY_CHAPEL_WITCH;
				return;
			} else if (cardAvailableOnBoard(CardName.MILITIA.getName()) && iAmTheOnlyAI()) {
				this.strategy = Strategy.BIG_MONEY_CHAPEL_MILITIA;
				return;
			} else {
				this.strategy = Strategy.BIG_MONEY_CHAPEL;
				return;
			}
		} else if (cardAvailableOnBoard(CardName.WITCH.getName())) {
			this.strategy = Strategy.BIG_MONEY_WITCH;
			return;
		} else if (cardAvailableOnBoard(CardName.SMITHY.getName()) && !fiveTwoStart) {
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
	 * @return whether the strategy DRAW_ADD_ACTION will can be successful
	 */
	private boolean drawAddActionStrategyPossible() {
		int count = 0;
		ArrayList<String> cardNames = CollectionsUtil.getArrayList(new String[] { CardName.COUNCILROOM.getName(), CardName.FESTIVAL.getName(), CardName.LABORATORY.getName(), CardName.MARKET.getName(),
				CardName.VILLAGE.getName(), CardName.WITCH.getName() });

		for (String name : cardNames) {
			if (cardAvailableOnBoard(name)) {
				count++;
			}
		}
		return count >= 3;
	}

	/**
	 * 
	 * @param name
	 *            the name of the card
	 * @param type
	 *            the type of the card
	 * @return whether the
	 */
	private boolean cardAvailableOnBoard(String name) {
		GameBoard board = this.player.getGameServer().getGameController().getGameBoard();
		if (board.getTableForActionCards().containsKey(name) && !board.getTableForActionCards().get(name).isEmpty()) {
			// GameLog.log(MsgType.AI_DEBUG, "<< card available on board: " +
			// name);
			return true;
		} else if (board.getTableForTreasureCards().containsKey(name) && !board.getTableForTreasureCards().get(name).isEmpty()) {
			// GameLog.log(MsgType.AI_DEBUG, "<< card available on board: " +
			// name);
			return true;
		} else if (board.getTableForVictoryCards().containsKey(name) && !board.getTableForVictoryCards().get(name).isEmpty()) {
			// GameLog.log(MsgType.AI_DEBUG, "<< card available on board: " +
			// name);
			return true;
		} else
			return false;
	}

	/**
	 * 
	 * @return whether there is only one estate left in the deck
	 */
	private boolean lastEstateInDeck() {
		return this.player.getDeck().containsAmountOf(CardName.ESTATE.getName(), this.player.getPlayedCards()) == 1;
	}

	/**
	 * 
	 * @param card
	 *            the card to check if its worth to trash
	 * @return whether the card is worth to trash or not
	 */
	private boolean isTrashWorthy(Card card) {
		return (card.getName().equals(CardName.ESTATE.getName()) && !lastEstateInDeck()) || card.getName().equals(CardName.COPPER.getName()) || card.getName().equals(CardName.CURSE.getName());
	}

	/**
	 * 
	 * @param name
	 *            the name to search for
	 * @param list
	 *            the list to search in
	 * @return whether the card with name is contained in list
	 */
	@SuppressWarnings("unused")
	private boolean listContains(String name, LinkedList<Card> list) {
		for (Card c : list)
			if (c.getName().equals(name))
				return true;
		return false;
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
	 * @return the amount of Provinces left on the board
	 */
	private int getPileSize(String cardname) {
		return this.player.getGameServer().getGameController().getGameBoard().getTableForVictoryCards().get(cardname).size();
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
	 * @return the cardHand of the player
	 */
	private LinkedList<Card> getCardHand() {
		return this.player.getDeck().getCardHand();
	}

	/**
	 * 
	 * @return list of cards on hand that can be trashed by the chapel
	 */
	private LinkedList<Card> getTrashWorthyCards() {
		LinkedList<Card> resultList = new LinkedList<Card>();
		for (Card c : getCardHand()) {
			if (this.isTrashWorthy(c)) {
				resultList.add(c);
			}
		}
		return resultList;
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
		GameLog.log(MsgType.GAME_INFO, "discardLeastValuableCard()");
		debugCardPrint("cardHand: ", getCardHand());

		int treasureValue = getTreasureCardsValue(getCardHand());

		/** BIG_MONEY(_..) discarding */
		if (!this.strategy.equals(Strategy.DRAW_ADD_ACTION)) {
			/** if card hand doesn't contain a Chapel */
			if (!this.player.getDeck().cardHandContains(CardName.CHAPEL.getName())) {
				/** discard a CURSE */
				if (this.player.getDeck().cardHandContains(CardType.CURSE)) {
					playDiscard(this.player.getDeck().getCardByTypeFromHand(CardType.CURSE));
					return;
				}
				/** discard a random VICTORY card */
				if (this.player.getDeck().cardHandContains(CardType.VICTORY)) {
					playDiscard(this.player.getDeck().getCardByTypeFromHand(CardType.VICTORY));
					return;
				}
				/**
				 * or if the treasureValue on card hand is >= 6, discard the
				 * action card with the lowest cost
				 */
				if (treasureValue >= 6) {
					if (this.player.getDeck().cardHandContains(CardType.ACTION)) {
						playDiscard(this.player.getDeck().cardWithLowestCost(getCardHand(), CardType.ACTION));
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
				if (getTrashWorthyCards().size() < 2 || treasureValue >= 7) {
					playDiscard(this.player.getDeck().getCardByNameFromHand(CardName.CHAPEL.getName()));
					return;
				}
				/** discard any VICTORY card but Estate */
				LinkedList<Card> victoryOnCardHand = this.player.getDeck().getCardsByTypeFrom(CardType.VICTORY, this.getCardHand());
				if (victoryOnCardHand != null && victoryOnCardHand.size() > 0) {
					for (Card c : victoryOnCardHand) {
						if (!c.getName().equals(CardName.ESTATE.getName())) {
							playDiscard(c);
							return;
						}
					}
				}
			}
			/** discard a COPPER */
			if (this.player.getDeck().cardHandContains(CardName.COPPER.getName())) {
				playDiscard(this.player.getDeck().getCardByNameFromHand(CardName.COPPER.getName()));
				return;
			}
			/** discard another TREASURE card */
			if (this.player.getDeck().cardHandContains(CardType.TREASURE)) {
				GameLog.log(MsgType.ERROR, "the method must not get here (unless it has a card hand like 5x gold");
				playDiscard(this.player.getDeck().cardWithLowestCost(getCardHand(), CardType.TREASURE));
				return;
			}
			/** DRAW_ADD_ACTION discarding */
		} else {
			/** discard a CURSE */
			if (this.player.getDeck().cardHandContains(CardType.CURSE)) {
				playDiscard(this.player.getDeck().getCardByTypeFromHand(CardType.CURSE));
				return;
			}
			/** discard a random VICTORY card */
			if (this.player.getDeck().cardHandContains(CardType.VICTORY)) {
				playDiscard(this.player.getDeck().getCardByTypeFromHand(CardType.VICTORY));
				return;
			}
			/** discard a COPPER */
			if (this.player.getDeck().cardHandContains(CardName.COPPER.getName())) {
				playDiscard(this.player.getDeck().getCardByNameFromHand(CardName.COPPER.getName()));
				return;
			}
			/** discard the ACTION card with lowest cost on hand */
			if (this.player.getDeck().cardHandContains(CardType.ACTION)) {
				playDiscard(this.player.getDeck().cardWithLowestCost(getCardHand(), CardType.ACTION));
				return;
			}
			/** discard another TREASURE card */
			if (this.player.getDeck().cardHandContains(CardType.TREASURE)) {
				GameLog.log(MsgType.ERROR, "the method must not get here (unless it has a card hand like 5x gold");
				playDiscard(this.player.getDeck().cardWithLowestCost(getCardHand(), CardType.TREASURE));
				return;
			} else {
				playDiscard(getCardHand().get(new Random().nextInt(getCardHand().size())));
			}
		}
	}

	/**
	 * trashes the cards according to game situation and strategy
	 * 
	 * @param trashCards
	 *            the cards to trash (CURSE, COPPER, ESTATE)
	 * @throws InterruptedException
	 */
	private void trash(LinkedList<Card> trashCards) throws InterruptedException {
		int treasureCardsValue;
		debugCardPrint(">>>>>>>>>>>>>>>> trashCards", trashCards);
		if (trashCards.isEmpty())
			return;
		for (Card c : trashCards) {
			sleep();
			treasureCardsValue = getTreasureCardsValue(getCardHand());
			GameLog.log(MsgType.INFO, "treasureCardsValue in trash for: " + treasureCardsValue);
			if (this.player.isTrashMode() && c.getName().equals(CardName.ESTATE.getName()) || c.getName().equals(CardName.COPPER.getName()) || c.getName().equals(CardName.CURSE.getName())) {
				GameLog.log(MsgType.INFO, "trashin dem cards here");
				if (c.getName().equals(CardName.CURSE.getName())) {
					playTrash(c);
				} else if (c.getName().equals(CardName.ESTATE.getName())) {
					if (!endPhase)
						playTrash(c);
				} else if (c.getName().equals(CardName.COPPER.getName())) {
					if (treasureCardsValue >= 6)
						continue;
					else if (treasureCardsValue >= 5) {
						switch (this.strategy) {
						case BIG_MONEY:
						case BIG_MONEY_CHAPEL:
						case BIG_MONEY_CHAPEL_MILITIA:
							if (!(this.getPileSize(CardName.PROVINCE.getName()) <= 5))
								playTrash(c);
							break;
						case BIG_MONEY_WITCH:
						case BIG_MONEY_CHAPEL_WITCH:
							if (!(this.player.getDeck().containsAmountOf(CardName.WITCH.getName(), this.player.getPlayedCards()) < 2 && cardAvailableOnBoard(CardName.WITCH.getName()))
									&& !(this.getPileSize(CardName.PROVINCE.getName()) <= 5))
								playTrash(c);
							break;
						case DRAW_ADD_ACTION:
							continue;
						}
					} else if (treasureCardsValue >= 4) {
						switch (this.strategy) {
						case BIG_MONEY_CHAPEL_MILITIA:
							if (!(this.player.getDeck().containsAmountOf(CardName.MILITIA.getName(), this.player.getPlayedCards()) < 2 && cardAvailableOnBoard(CardName.MILITIA.getName())))
								playTrash(c);
							break;
						case BIG_MONEY:
						case BIG_MONEY_CHAPEL:
						case BIG_MONEY_CHAPEL_WITCH:
						case BIG_MONEY_WITCH:
						case DRAW_ADD_ACTION:
						default:
							playTrash(c);
							break;
						}
					} else if (treasureCardsValue == 3) {
						continue;
					} else if (treasureCardsValue <= 2) {
						playTrash(c);
					}
				}
			} else
				return;
		}
	}

	/**
	 * prints only the cardHand in a short format for debugging purposes
	 * 
	 * @param cards
	 *            the cardlist to print
	 */
	private void debugCardPrint(String prefix, LinkedList<Card> cards) {
		StringBuffer logString = new StringBuffer();
		logString.append("Cardnames, " + prefix + ": - ");
		for (Card card : cards) {
			logString.append(card.getName() + " - ");
		}
		GameLog.log(MsgType.AI_DEBUG, logString.toString());
	}

	/* getters & setters, needed for JUnit */

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

	/**
	 * @return the fiveTwoStart
	 */
	public boolean isFiveTwoStart() {
		return fiveTwoStart;
	}

	/**
	 * @param fiveTwoStart
	 *            the fiveTwoStart to set
	 */
	public void setFiveTwoStart(boolean fiveTwoStart) {
		this.fiveTwoStart = fiveTwoStart;
	}

	/**
	 * @return the attacks
	 */
	public int getAttacks() {
		return attacks;
	}

	/**
	 * @param attacks
	 *            the attacks to set
	 */
	public void setAttacks(int attacks) {
		this.attacks = attacks;
	}

	/**
	 * @return the discardModeCount
	 */
	public int getDiscardModeCount() {
		return discardModeCount;
	}

	/**
	 * @param discardModeCount
	 *            the discardModeCount to set
	 */
	public void setDiscardModeCount(int discardModeCount) {
		this.discardModeCount = discardModeCount;
	}

	/**
	 * @return the timeDelay
	 */
	public static int getTimeDelay() {
		return TIME_DELAY;
	}

	/**
	 * @return the moatRatio1
	 */
	public static double getMoatRatio1() {
		return MOAT_RATIO_1;
	}

	/**
	 * @return the moatRatio2
	 */
	public static double getMoatRatio2() {
		return MOAT_RATIO_2;
	}

	/**
	 * @return the noBuy
	 */
	public static String getNoBuy() {
		return NO_BUY;
	}
}