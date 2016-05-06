package com.tpps.application.game.ai;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.tpps.application.game.CardName;
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

/*
 * - board anschauen, wenn angriffskarten gekauft werden dann defensiv kaufen 
 * - wenn es nix bringt, mehr karten zu ziehen, ggf. aktionskarten nicht spielen
 * - LinkedListMultimap mit "buy" oder "play" und karte als Spielplan aufbauen
 * - wenn es der potentiell letzte Zug ist, soll die Blacklist ignoriert werden und evtl ein Anwesen gekauft werden
 * - Kommentare anpassen (computing etc.)
 * - schauen ob die AI nicht cheatet
 * - checkEndPhase(): 3 niedrigsten Kartenstapel insgesamt unter 7 Karten
 * - in play und buy an Lukas vergleichen ob ich was vergessen hab: GC, SGPH, ...
 * - enhance AI name list (Charlie Chaplin usw)
 */

/**
 * Global AI class. If you get stomped, don't worry, it's intended.
 * 
 * @author Nicolas Wipfler
 */
public class ArtificialIntelligence {

	private ServerGamePacketHandler packetHandler;
	private Player player;

	private Strategy strategy;

	private List<String> buySequence;
	private List<String> blacklist;
	private boolean endPhase;

	private static final int ENDPHASE_TURN = 22;
	private static final int TIME_DELAY = 650;

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
	private void playCard(Card card) {
		if (card != null && this.player.getActions() > 0) {
			sendPacket(new PacketPlayCard(card.getId(), player.getClientID()));
		} else {
			GameLog.log(MsgType.AI, this.player.getPlayerName() + " played 'null' card");
		}
	}

	/**
	 * play not only one but a list of cards
	 * 
	 * @param cards
	 *            the list of cards to be played
	 * @throws InterruptedException
	 */
	private void playCards(LinkedList<Card> cards) throws InterruptedException {
		if (cards != null && cards.size() > 0) {
			for (Card card : cards) {
				Thread.sleep(ArtificialIntelligence.TIME_DELAY);
				playCard(card);
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
	 * reveal to other players that it has a gold, if there is already enough
	 * money even without playing the gold)
	 * 
	 * @param amountNeeded
	 *            the amount of treasures needed
	 */
	/*
	 * private void playTreasures(int amountNeeded) { int amountAvailable =
	 * this.getTreasureCardsValue(getCardHand()); if (amountAvailable <=
	 * amountNeeded) { playTreasures(); } else { LinkedList<Card>
	 * allTreasureCards = this.getAllCardsFromType(CardType.TREASURE); for (Card
	 * card : allTreasureCards) { // spiele solange bis amountNeeded erreicht
	 * ist
	 * 
	 * } // Methode die alle Karten mit Namen CardName.COPPER.getName() //
	 * zurückgibt, fehlt
	 * 
	 * // LinkedList<Card> coppers = this.player.getDeck().; // LinkedList<Card>
	 * silvers = ; // LinkedList<Card> golds = ; } }
	 */

	/**
	 * determine which cards have the most value and play all of these cards
	 * with CardType.ACTION
	 * 
	 * @throws InterruptedException
	 */
	private void playActionCards() throws InterruptedException {
		// hier mal wegen .getActions schauen
		if (this.player.getDeck().cardHandContains(CardType.ACTION)) {
			while (this.player.getActions() > 0 && this.player.getDeck().cardHandContains(CardType.ACTION)) {
				if (addActionCardAvailable()) {
					LinkedList<Card> plusActionCards = this.player.getDeck().cardHandsWith(CardAction.ADD_ACTION_TO_PLAYER, this.player.getDeck().getCardHand());
					playCards(plusActionCards);
					continue;
				}
				// Logik + Strategy, chapel handlen

				LinkedList<Card> remainingActionCards = this.getAllCardsFromType(CardType.ACTION);
				Card tbp = this.player.getDeck().cardWithHighestCost(remainingActionCards);
				Thread.sleep(ArtificialIntelligence.TIME_DELAY);
				playCard(tbp);
				return; // for test purposes
			}
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
	private Card getCardFromBoard(String cardname) {
		return this.player.getGameServer().getGameController().getGameBoard().getCardToBuyFromBoardWithName(cardname);
	}

	/**
	 * basic method which buys the given card
	 * 
	 * @param card
	 *            the card to buy
	 */
	private void buyCard(Card card) {
		if (card != null && this.player.getBuys() > 0) {
			sendPacket(new PacketPlayCard(card.getId(), player.getClientID()));
		} else {
			GameLog.log(MsgType.AI, this.player.getPlayerName() + " bought 'null' card");
		}
	}
	
	/**
	 * basic method which discards the given card
	 * 
	 * @param card
	 *            the card to discard
	 */
	private void discardCard(Card card) {
		if (card != null && this.player.isDiscardMode()) {
			// sendPacket(new Packet(card.getId(), player.getClientID()));

			// hier
		} else {
			GameLog.log(MsgType.AI, this.player.getPlayerName() + " discarded 'null' card");
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

			if (firstTurn())
				determineStrategy();
			if (!endPhase)
				checkEndPhase();

			executeMove();
		} else if (this.player.isReactionMode()) {
			if (this.player.playsReactionCard()) {
				playCard(this.player.getDeck().getCardByTypeFromHand(CardType.REACTION));
			} else if (this.player.isDiscardMode()) {
				// Logik discard
				while (this.player.getDeck().getCardHand().size() > 3) {
					discardCardWithLowestValue();
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
			while(this.player.getActions() > 0) {
				Thread.sleep(ArtificialIntelligence.TIME_DELAY);
				this.playActionCards();
			}
			Thread.sleep(ArtificialIntelligence.TIME_DELAY);
			this.setBuyPhase();
			Thread.sleep(ArtificialIntelligence.TIME_DELAY);
			this.buySequence = determinePurchase();
			Thread.sleep(ArtificialIntelligence.TIME_DELAY);
			this.playTreasures();
			Thread.sleep(ArtificialIntelligence.TIME_DELAY);
			for (String buy : this.buySequence) {
				if (!getBlacklist().contains(buy) || endPhase) {
					Thread.sleep(ArtificialIntelligence.TIME_DELAY);
					this.buyCard(this.getCardFromBoard(buy));
				}
			}
			Thread.sleep(ArtificialIntelligence.TIME_DELAY);
			this.buySequence = new LinkedList<String>();
			if (myTurn()) {
				this.endTurn();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * method determines the purchases of this turn for the AI
	 * 
	 * @return a LinkedList with the determined Purchases
	 */
	private LinkedList<String> determinePurchase() {
		// https://dominionstrategy.com/big-money/

		// while einbauen, die kaeufe je nach remaining coins hinzufuegt solange
		// noch kaeufe da sind (durch blacklist wird eh abgefangen wenns nicht
		// gekauft werden soll)

		// Logik + Strategy

		LinkedList<String> result = new LinkedList<String>();
		int coins = getTreasureCardsValue(getCardHand());
		if (coins >= 8) {
			result.addLast(CardName.PROVINCE.getName());
		} else if (coins >= 6) {
			result.addLast(CardName.GOLD.getName());
		} else if (coins >= 3) {
			result.addLast(CardName.SILVER.getName());
		}
		return result;
	}

	/**
	 * method is called once in the first turn to determine a strategy according
	 * to the situation on the game board
	 */
	private void determineStrategy() {
		if (this.player.getGameServer().getGameController().getGameBoard().getTableForActionCards().get(CardName.WITCH.getName()) != null && iAmTheOnlyAI()) {
			this.strategy = Strategy.WITCH;
			return;
		} else if (this.player.getGameServer().getGameController().getGameBoard().getTableForActionCards().get(CardName.CHAPEL.getName()) != null) {
			this.strategy = Strategy.PLAIN_CHAPEL;
			return;
		} else if (this.player.getGameServer().getGameController().getGameBoard().getTableForActionCards().get(CardName.SMITHY.getName()) != null) {
			this.strategy = Strategy.SMITHY;
			return;
		} else
			this.strategy = Strategy.PLAIN;
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
	 * check several game states and set the endPhase if necessary
	 */
	private void checkEndPhase() {
		if (getProvinceAmount() < 4) {
			this.endPhase = true;
		} else if (this.player.getTurnNr() >= ArtificialIntelligence.ENDPHASE_TURN) {
			this.endPhase = true;
		} else if (this.player.getGameServer().getGameController().getGameBoard().amountOfPilesEmpty() == GameConstant.EMPTY_PILES.getValue() - 1) {
			this.endPhase = true;
		}
	}

	private void discardCardWithLowestValue() {
		if (this.player.getDeck().cardHandContains(CardType.CURSE)) {
			discardCard(this.player.getDeck().getCardByTypeFromHand(CardType.CURSE));
		}
		// hier
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
		return this.player.getDeck().cardHandsWith(CardAction.ADD_ACTION_TO_PLAYER, this.player.getDeck().getCardHand()).size() > 0;
	}

	/**
	 * 
	 * @return the amount of Provinces left on the board
	 */
	private int getProvinceAmount() {
		return this.player.getGameServer().getGameController().getGameBoard().getTableForVictoryCards().get(CardName.PROVINCE.getName()).size();
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
	 * @return whether it is the first turn of the AI
	 */
	private boolean firstTurn() {
		GameLog.log(MsgType.AI, "AI 4: it's the first turn (inside firstTurn())");
		return this.player.getTurnNr() == 1;
	}

	/**
	 * 
	 * @return the cardHand of the player
	 */
	private LinkedList<Card> getCardHand() {
		return this.player.getDeck().getCardHand();
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
	 * @param endPhase
	 *            the endPhase to set
	 */
	public void setEndPhase(boolean endPhase) {
		this.endPhase = endPhase;
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
}