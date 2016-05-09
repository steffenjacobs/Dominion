package com.tpps.application.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.logger.DrawAndShuffle;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.technicalServices.network.game.ServerGamePacketHandler;
import com.tpps.technicalServices.network.game.SynchronisationException;
import com.tpps.technicalServices.network.game.TooMuchPlayerException;
import com.tpps.technicalServices.network.game.WrongSyntaxException;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLog;
import com.tpps.technicalServices.network.gameSession.packets.PacketDisable;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnable;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnableDisable;
import com.tpps.technicalServices.network.gameSession.packets.PacketPutBackCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendActiveButtons;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendBoard;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendHandCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendPlayedCardsToAllClients;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendRevealCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketShowEndReactions;
import com.tpps.technicalServices.network.gameSession.packets.PacketShowEndScreen;
import com.tpps.technicalServices.network.gameSession.packets.PacketTakeCards;
import com.tpps.technicalServices.network.matchmaking.packets.PacketGameEnd;
import com.tpps.technicalServices.network.matchmaking.server.MatchmakingServer;
import com.tpps.technicalServices.util.CollectionsUtil;

/**
 * @author Lukas Adler
 * @author Nicolas Wipfler
 * 
 */
public class GameController {

	protected GameServer gameServer;

	private LinkedList<Player> players;

	private boolean gameNotFinished, cardsEnabled;
	private Player activePlayer;
	private boolean activePlayerNameAvailable;
	private GameBoard gameBoard;
	private String gamePhase;
	private CopyOnWriteArrayList<Player> thiefList, spyList;
	private List<Integer> aiPorts;

	/**
	 * creates the gameController, sets the gameServer sets all flag and creates
	 * all required Lists
	 * 
	 * @param gameServer
	 * @param selectedActionCards
	 *            the selected actioncards
	 */
	public GameController(GameServer gameServer, String[] selectedActionCards) {
		this.gameServer = gameServer;
		this.cardsEnabled = true;
		this.players = new LinkedList<Player>();
		this.thiefList = new CopyOnWriteArrayList<Player>();
		this.spyList = new CopyOnWriteArrayList<Player>();
		this.gameBoard = new GameBoard(selectedActionCards);
		this.gameNotFinished = true;
		this.activePlayerNameAvailable = false;
		this.aiPorts = new ArrayList<Integer>();
	}

	/**
	 * @return if cards are allowed to play
	 */
	public boolean isCardsEnabled() {
		return cardsEnabled;
	}

	/**
	 * cards can be played
	 */
	public void setCardsEnabled() {
		this.cardsEnabled = true;
	}

	/**
	 * cards can't be played
	 */
	public void setCardsDisabled() {
		this.cardsEnabled = false;
	}

	/**
	 * @param gameBoard
	 *            the gameBoard to set
	 * 
	 */
	public void setGameBoard(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
	}

	/**
	 * @param gamePhase
	 *            the gamePhase to set
	 * 
	 */
	public void setGamePhase(String gamePhase) {
		this.gamePhase = gamePhase;
	}

	/**
	 * @return the activePlayerNameAvailable
	 */
	public boolean isActivePlayerNameAvailable() {
		return activePlayerNameAvailable;
	}

	/**
	 * @param activePlayerNameAvailable
	 *            the activePlayerNameAvailable to set
	 */
	public void setActivePlayerNameAvailable(boolean activePlayerNameAvailable) {
		this.activePlayerNameAvailable = activePlayerNameAvailable;
	}

	/**
	 * @param thiefList
	 *            the thiefList to set
	 * 
	 */
	public void setThiefList(CopyOnWriteArrayList<Player> thiefList) {
		this.thiefList = thiefList;
	}

	/**
	 * @param spyList
	 *            the spyList to set
	 * 
	 */
	public void setSpyList(CopyOnWriteArrayList<Player> spyList) {
		this.spyList = spyList;
	}

	/**
	 * 
	 * @return the name of the active Player
	 */
	public String getActivePlayerName() {
		return this.activePlayer.getPlayerName();
	}

	/**
	 * determines the next active player
	 */
	public void setNextActivePlayer() {
		Player activePlayer = this.getActivePlayer();
		// LinkedList<Player> players = this.getPlayers();
		for (int i = 0; i < this.players.size(); i++) {
			Player player = players.get(i);
			if (player.getPlayerName().equals(activePlayer.getPlayerName())) {
				this.setActivePlayer(players.get(i < this.players.size() - 1 ? i + 1 : 0));
				this.getActivePlayer().incTurnNr();
				break;
			}
		}
	}

	/**
	 * adds the temporaryTrashPile to the trashPile
	 * 
	 * @param temporaryTrashPile
	 */
	public synchronized void updateTrashPile(LinkedList<Card> temporaryTrashPile) {
		CollectionsUtil.appendListToList(temporaryTrashPile, this.gameBoard.getTrashPile());
	}

	/**
	 * check if the card exists if the card exists calls the discardOrTrash
	 * method
	 * 
	 * @param player
	 * @param cardID
	 * @return trueif the card exists false otherwise
	 * @throws IOException
	 */
	public synchronized boolean checkCardExistsAndDiscardOrTrash(Player player, String cardID) throws IOException {
		Card card = player.getDeck().getCardFromHand(cardID);
		if (card != null && (this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard() == null
				|| !this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard().getId().equals(card.getId()))) {

			player.discardOrTrash(cardID, this.getGameBoard().getTrashPile());
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 
	 * @param cardID
	 *            the cardID of the card
	 * @param player
	 *            the player
	 * @return whether a card which was clicked exists and if it is allowed to
	 *         play this card in this phase of the game. If it is allowed the
	 *         card is played
	 * @throws IOException
	 * @throws SynchronisationException
	 */
	public synchronized boolean validateTurnAndExecute(String cardID, Player player) throws IOException {
		Card card = player.getDeck().getCardFromHand(cardID);
		if (card != null) {
			if (player.isReactionMode() && card.getTypes().contains(CardType.REACTION)) {
				GameLog.log(MsgType.GAME_INFO, "spielt reaktionskarte");
				player.playCard(cardID);
				if (this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard() == null
						|| (!this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard().getName().equals(CardName.MILITIA.getName())
								&& !this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard().getName().equals(CardName.WITCH.getName())
								&& !this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard().getName().equals(CardName.BUREAUCRAT.getName())
								&& !(this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard().getName().equals(CardName.THIEF.getName())))) {
					this.gameServer.sendMessage(player.getPort(), new PacketSendActiveButtons(true, true, false));
				}
				return true;
			}
			if (this.gamePhase.equals("actionPhase")) {

				if (card.getTypes().contains(CardType.ACTION) && this.getActivePlayer().getActions() > 0) {
					this.getActivePlayer().playCard(cardID);
					if (this.getActivePlayer().getActions() == 0) {
						this.getActivePlayer().endActionPhase();
						this.setBuyPhase();
					}
					return true;
				}
			}
			if (this.gamePhase.equals("buyPhase")) {
				if (card.getTypes().contains(CardType.TREASURE)) {
					GameLog.log(MsgType.DEBUG, "the card is a Treasure clicked in the buyphase");
					this.getActivePlayer().playCard(cardID);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param cardID
	 * @param player
	 * @return if the card exists on the board and gains the card if the
	 *         gainValue is higher than the costs of the card card is gained on
	 *         the hand if the onHand flag is set
	 */
	public synchronized boolean gain(String cardID, Player player) {
		try {
			LinkedList<Card> cardList = this.getGameBoard().findCardListFromBoard(cardID);
			Card card = cardList.getLast();
			if (card.getCost() <= player.getGainValue()) {
				getGameBoard().findAndRemoveCardFromBoard(cardID);
				player.setGainModeFalse();
				if (player.isOnHand()) {
					player.setOnHandFalse();
					player.getDeck().getCardHand().add(card);
					this.gameServer.sendMessage(player.getPort(), new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand())));
					return true;
				}
				player.getDeck().getDiscardPile().add(card);
				this.gameServer
						.broadcastMessage(new PacketBroadcastLog("", this.getActivePlayerName(), " - gains " + card.getName(), this.gameServer.getGameController().getActivePlayer().getLogColor()));
				return true;
			}
		} catch (WrongSyntaxException e) {
			GameLog.log(MsgType.ERROR, e.getMessage());
		} catch (SynchronisationException e) {
			GameLog.log(MsgType.ERROR, "Card is not on the board please click on an another card.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * checks if the card is on the board and adds the card with the param
	 * cardId to the trashPile
	 * 
	 * @param cardID
	 *            the id of the card
	 * @return true if the card exists and all conditions are fullfilled to buy
	 *         the card
	 * @throws SynchronisationException
	 * @throws NoSuchElementException
	 * @throws WrongSyntaxException
	 */
	public synchronized boolean checkBoardCardExistsAppendToDiscardPile(String cardID) throws SynchronisationException, NoSuchElementException, WrongSyntaxException {
//		GameLog.log(MsgType.INFO, "checkBoardCardExists");
		LinkedList<Card> cards = this.gameBoard.findCardListFromBoard(cardID);
		Card card = cards.getLast();
		Player player = this.getActivePlayer();
		if (this.gamePhase.equals("buyPhase") && player.getBuys() > 0 && player.getCoins() >= card.getCost()) {
			player.setBuys(player.getBuys() - 1);
			player.setCoins(player.getCoins() - card.getCost());
			cards.removeLast();
			CollectionsUtil.addCardToList(card, player.getDeck().getDiscardPile());
			try {
				this.gameServer
						.broadcastMessage(new PacketBroadcastLog("", this.getActivePlayerName(), " - buys " + card.getName(), this.gameServer.getGameController().getActivePlayer().getLogColor()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param cardId
	 * @return if the card according to the given cardId is a victory card on
	 *         the hand
	 */
	public synchronized boolean isVictoryCardOnHand(String cardId) {
		Card card = this.getActivePlayer().getDeck().getCardFromHand(cardId);
		if (card == null) {
			return false;
		} else {
			if (card.getTypes().contains(CardType.VICTORY)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * calls the play Treasures method of the player.
	 * 
	 * @throws IOException
	 */
	public synchronized void playTreasures() throws IOException {
		this.getActivePlayer().playTreasures();
	}

	/**
	 * adds the played cards to the discardPile calls the refreshCardHand()
	 * method of the player calls the refresPlayedCardsList()
	 */
	public synchronized void organizePilesAndrefreshCardHand() {
		try {
			CollectionsUtil.appendListToList(this.getActivePlayer().getPlayedCards(), this.getActivePlayer().getDeck().getDiscardPile());
			DrawAndShuffle das = this.getActivePlayer().getDeck().refreshCardHand();
			if (das.wasShuffled()) {
				// this.gameServer.broadcastMessage(new
				// PacketBroadcastLog("",this.getActivePlayerName(),
				// " - shuffles
				// deck",((ServerGamePacketHandler)this.gameServer.getHandler()).getActivePlayerColor()));
				this.gameServer.broadcastMessage(new PacketBroadcastLog("", this.getActivePlayerName(), " - shuffles deck", this.gameServer.getGameController().getActivePlayer().getLogColor()));
			}
			// this.gameServer.broadcastMessage(new
			// PacketBroadcastLog("",this.getActivePlayerName()," - draws " +
			// das.getDrawAmount() +
			// "
			// cards",((ServerGamePacketHandler)this.gameServer.getHandler()).getActivePlayerColor()));
			this.gameServer.broadcastMessage(
					new PacketBroadcastLog("", this.getActivePlayerName(), " - draws " + das.getDrawAmount() + " cards", this.gameServer.getGameController().getActivePlayer().getLogColor()));
			this.getActivePlayer().refreshPlayedCardsList();
		} catch (IOException e) {
			GameLog.log(MsgType.EXCEPTION, e.getMessage());
		}
	}

	/**
	 * every player who hasn't a reaction has to discard (cardHandSize - value)
	 * cards for that purpose the players without reaction card are set into the
	 * discardMode for players with reaction card the reaction card flag is set.
	 * 
	 * @param value
	 */
	public synchronized void discardOtherDownto(String value) {
		boolean sendPacketDisable = true;
		boolean sendEnableFlag = true;
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();

			if (!player.getPlayerName().equals(activePlayer.getPlayerName())) {
				sendEnableFlag = true;

				if (player.getDeck().cardHandContains(CardType.REACTION)) {
					player.setReactionCard(true);
					player.setReactionMode();

					if (sendPacketDisable) {
						sendPacketDisable = false;
						try {
							this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketDisable("wait on reaction"));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}

					try {
						this.gameServer.sendMessage(player.getPort(), new PacketShowEndReactions());
						this.gameServer.sendMessage(player.getPort(), new PacketEnable("react"));
						sendEnableFlag = false;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (player.getDeck().getCardHand().size() > Integer.parseInt(value)) {
					GameLog.log(MsgType.GAME_INFO, "mehr als 3 karten");
					player.setReactionMode();
					player.setDiscardMode();
					player.setDiscardOrTrashAction(CardAction.DISCARD_CARD, player.getDeck().getCardHand().size() - Integer.parseInt(value));
					try {
						if (sendEnableFlag) {
							GameLog.log(MsgType.PACKET, "send packet react");
							this.gameServer.sendMessage(player.getPort(), new PacketEnable("react"));
						}
						if (sendPacketDisable) {
							sendPacketDisable = false;
							GameLog.log(MsgType.PACKET, "sendpacket disable");
							this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketDisable("wait on reaction"));

						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	/**
	 * let every player who hasn't a reaction card reveal two cards if they are
	 * treasure cards they are send to the activePlayer so that he can choose
	 * which card he wants to take. All other revealed cards are put on the
	 * discard pile. players with reaction card are set into the reaction mode
	 * 
	 */
	public synchronized void revealAndTakeCardsDiscardOthers() {
		boolean reactivePlayer = false;
		for (Iterator<Player> iterator = this.players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			player.setThief();
			if (!player.getPlayerName().equals(this.activePlayer.getPlayerName())) {
				if (player.getDeck().cardHandContains(CardType.REACTION)) {
					reactivePlayer = true;
					player.setThiefFalse();
					try {
						this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketDisable("wait on reaction"));
					} catch (IOException e1) {

						e1.printStackTrace();
					}
					player.setReactionCard(true);
					player.setReactionMode();
					try {
						this.gameServer.sendMessage(player.getPort(), new PacketShowEndReactions());
						this.gameServer.sendMessage(player.getPort(), new PacketEnable("react"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Card card = player.getDeck().removeSaveFromDrawPile();
					if (card.getTypes().contains(CardType.TREASURE)) {
						player.getRevealList().add(card);
					} else {
						player.getDeck().getDiscardPile().add(card);
					}
					if (player.getRevealList().size() > 0) {
						thiefList.add(player);
					} else {
						player.setThiefFalse();
					}
				}
			}
		}
		GameLog.log(MsgType.GAME_INFO, "im gamecontrolloer thiefList size: " + thiefList.size());
		if (thiefList.size() > 0) {
			try {
				this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketSendActiveButtons(false, false, false));
				this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketSendRevealCards(CollectionsUtil.getCardIDs(thiefList.get(0).getRevealList())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (!reactivePlayer) {
			GameLog.log(MsgType.GAME_INFO, "thief false");
			this.activePlayer.setThiefFalse();
		}
	}

	/**
	 * let every player who hasn't a reaction reveal one card and add him to the
	 * spyList for every player in this list the active player can decide
	 * whether the player has to put the card on the drawPile or on the
	 * discardPile sent to the activePlayer players with reaction card are set
	 * into the reaction mode
	 */
	public synchronized void revealCardAll() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			player.setSpy();
			if (!player.getPlayerName().equals(activePlayer.getPlayerName())) {
				if (player.getDeck().cardHandContains(CardType.REACTION)) {
					player.setSpyFalse();
					try {
						this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketDisable("wait on reaction"));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					player.setReactionCard(true);
					player.setReactionMode();
					try {
						this.gameServer.sendMessage(player.getPort(), new PacketShowEndReactions());
						this.gameServer.sendMessage(player.getPort(), new PacketEnable("react"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					player.setRevealMode();
					player.getRevealList().add(player.getDeck().removeSaveFromDrawPile());
					this.spyList.add(player);
				}
			}
		}
		this.activePlayer.setRevealMode();
		this.activePlayer.getRevealList().add(activePlayer.getDeck().removeSaveFromDrawPile());
		this.spyList.add(this.activePlayer);
		GameLog.log(MsgType.GAME_INFO, "im gamecontrolloer spyList size: " + this.spyList.size());
		if (this.spyList.size() > 0) {
			try {
				this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketTakeCards(this.activePlayer.getClientID()));
				this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketPutBackCards(this.activePlayer.getClientID()));
				this.gameServer.sendMessage(this.getActivePlayer().getPort(), new PacketSendRevealCards(CollectionsUtil.getCardIDs(spyList.get(0).getRevealList())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * let every player who hasn't a reaction card gain one curse players with
	 * reaction card are set into the reaction mode
	 */
	public synchronized void gainCurseOthers() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			player.setWitch();
			if (!player.getPlayerName().equals(activePlayer.getPlayerName())) {

				if (player.getDeck().cardHandContains(CardType.REACTION)) {
					player.setWitchFalse();
					try {
						this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketDisable("wait on reaction"));
					} catch (IOException e1) {

						e1.printStackTrace();
					}
					player.setReactionCard(true);
					player.setReactionMode();
					try {
						this.gameServer.sendMessage(player.getPort(), new PacketShowEndReactions());
						this.gameServer.sendMessage(player.getPort(), new PacketEnable("react"));
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					try {
						player.getDeck().getDiscardPile().add(this.gameBoard.getTableForVictoryCards().get(CardName.CURSE.getName()).removeLast());
					} catch (NoSuchElementException e) {
						GameLog.log(MsgType.ERROR, "Not enough curses.\n");
					}
					player.setWitchFalse();
				}
			}
		}
		checkReactionModeFinishedAndEnableGuis();
		try {
			this.gameServer.broadcastMessage(new PacketSendBoard(this.getGameBoard().getTreasureCardIDs(), getGameBoard().getVictoryCardIDs(), getGameBoard().getActionCardIDs()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * if the player has a reaction card the player is set in the reaction mode.
	 * If not the method checks if the player has a victory card on his hand
	 * puts it on top of the drawPile if the player has one does nothing
	 * otherwise
	 */
	public void revealCardOthersPutItOnTopOfDeck() {
		boolean sendPacketDisable = true;
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			player.setBureaucrat();
			if (!player.getPlayerName().equals(activePlayer.getPlayerName())) {

				if (player.getDeck().cardHandContains(CardType.REACTION)) {
					player.setBureaucratFalse();
					if (sendPacketDisable) {
						sendPacketDisable = false;
						try {
							this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketDisable("wait on reaction"));
						} catch (IOException e1) {

							e1.printStackTrace();
						}
					}
					player.setReactionCard(true);
					player.setReactionMode();
					try {
						this.gameServer.sendMessage(player.getPort(), new PacketShowEndReactions());
						this.gameServer.sendMessage(player.getPort(), new PacketEnable("react"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Card card = player.getDeck().getCardByTypeFromHand(CardType.VICTORY);

					if (card != null) {
						player.getDeck().getCardHand().remove(card);
						player.getDeck().getDrawPile().addLast(card);
						try {
							this.gameServer.sendMessage(player.getPort(), new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand())));
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						System.err.println("no victory card on hand");
					}
					player.setBureaucratFalse();
				}
			}
		}
		try {
			this.gameServer.broadcastMessage(new PacketSendBoard(this.getGameBoard().getTreasureCardIDs(), getGameBoard().getVictoryCardIDs(), getGameBoard().getActionCardIDs()));
		} catch (IOException e) {

			e.printStackTrace();
		}
		checkReactionModeFinishedAndEnableGuis();
	}

	/**
	 * if all players except the active player are not in the witch mode the
	 * witch mode is set false for the active player
	 */
	public void checkWitchFinish() {
		boolean witchFlag = true;
		LinkedList<Player> players = new LinkedList<Player>(this.players);
		players.remove(this.activePlayer);
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.isWitch()) {
				witchFlag = false;
				break;
			}
		}
		if (witchFlag) {
			GameLog.log(MsgType.GAME_INFO, "witch false");
			this.activePlayer.setWitchFalse();
		}
	}

	/**
	 * if all players except the active player are not in the bureaucrat mode
	 * the bureaucrat mode is set false for the active player
	 */
	public void checkBureaucratFinish() {
		boolean bureaucratFlag = true;
		LinkedList<Player> players = new LinkedList<Player>(this.players);
		players.remove(this.activePlayer);
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.isBureaucrat()) {
				bureaucratFlag = false;
				break;
			}
		}
		if (bureaucratFlag) {
			GameLog.log(MsgType.GAME_INFO, "bureaucrat false");
			this.activePlayer.setBureaucratFalse();
		}
	}

	/**
	 * @return true if all players except the active player are not in the thief
	 *         mode. false otherwise
	 */
	public boolean checkThiefFinish() {
		boolean thiefFlag = true;
		LinkedList<Player> players = new LinkedList<Player>(this.players);

		players.remove(this.activePlayer);

		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player2 = (Player) iterator.next();
			if (player2.isThief()) {
				thiefFlag = false;
				break;
			}
		}
		GameLog.log(MsgType.GAME_INFO, "kein thief" + thiefFlag);
		return thiefFlag;
	}

	/**
	 * if all players except the active player are not in the spy mode the spy
	 * mode is set false for the active player
	 * 
	 * @return true if the condition above is fulfilled. false otherwise
	 */
	public boolean checkSpyFinish() {
		boolean spyFlag = true;
		LinkedList<Player> players = new LinkedList<Player>(this.players);
		players.remove(this.activePlayer);
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.isSpy()) {
				spyFlag = false;
				break;
			}
		}
		if (spyFlag) {
			this.activePlayer.setSpyFalse();
			GameLog.log(MsgType.GAME_INFO, "spy false");
			return spyFlag;
		}
		return spyFlag;
	}

	/**
	 * let the player reveal two cards if they are treasure cards they are send
	 * to the activePlayer so that he can choose which card he wants to take.
	 * 
	 * @param player
	 */
	public void reactOnThief(Player player) {
		// if (allReactionCardsPlayed()) {
		// try {
		// GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new
		// PacketEnable());
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		Card card = player.getDeck().removeSaveFromDrawPile();
		if (card.getTypes().contains(CardType.TREASURE)) {

			player.getRevealList().add(card);
		} else {
			player.getDeck().getDiscardPile().add(card);
		}
		if (player.getRevealList().size() > 0) {
			player.setThief();
			thiefList.add(player);
			if (thiefList.size() == 1) {
				try {
					this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketSendActiveButtons(false, false, false));
					this.gameServer.sendMessage(activePlayer.getPort(), new PacketSendRevealCards(CollectionsUtil.getCardIDs(thiefList.get(0).getRevealList())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			player.setThiefFalse();
		}
		GameLog.log(MsgType.GAME_INFO, "react new thieflist size: " + thiefList.size());
	}

	/**
	 * 
	 * lets the player reveal one card and add him to the spyList for every
	 * player in this list the active player can decide whether the player has
	 * to put the card on the drawPile or on the discardPile
	 * 
	 * @param player
	 */
	public void reactOnSpy(Player player) {
		player.setRevealMode();
		player.getRevealList().add(player.getDeck().removeSaveFromDrawPile());
		spyList.add(player);
		if (spyList.size() == 1) {
			try {
				this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketTakeCards(this.activePlayer.getClientID()));
				this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketPutBackCards(this.activePlayer.getClientID()));
				this.gameServer.sendMessage(this.activePlayer.getPort(), new PacketSendRevealCards(CollectionsUtil.getCardIDs(spyList.get(0).getRevealList())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @return if all player have played his/ her reactionCards
	 */
	public boolean allReactionCardsPlayed() {
		boolean allReactionCardsPlayedFlag = true;

		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.playsReactionCard() || player.isReactionMode()) {
				allReactionCardsPlayedFlag = false;
				GameLog.log(MsgType.GAME_INFO, player.getPlayerName() + "spielt reaktionskarte: " + player.playsReactionCard() + "player ist reaktionsmodues: " + player.isReactionMode());
				break;
			}
		}
//		GameLog.log(MsgType.GAME_INFO, "alle reaktionskarten gespielt? :" + allReactionCardsPlayedFlag);
		return allReactionCardsPlayedFlag;
	}

	/**
	 * let all the players except the active player draw one card
	 */
	public synchronized void drawOthers() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (!player.getPlayerName().equals(activePlayer.getPlayerName())) {
				player.getDeck().draw();
				try {
					this.gameServer.sendMessage(player.getPort(), new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * enables the gui of the active Playe disables all others if the reaction
	 * mode is finished. returns otherwise
	 */
	public synchronized void checkReactionModeFinishedAndEnableGuis() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.isReactionMode()) {
				return;
			}
		}
		if (thiefList.size() == 0) {
			this.getActivePlayer().setThiefFalse();
		}

		checkSpyFinish();
		checkWitchFinish();
		checkBureaucratFinish();
		if (this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard() == null
				|| (!this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard().getName().equals(CardName.MILITIA.getName())
						&& !this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard().getName().equals(CardName.WITCH.getName())
						&& !this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard().getName().equals(CardName.BUREAUCRAT.getName())
						&& !(this.gameServer.getGameController().getActivePlayer().getPlayTwiceCard().getName().equals(CardName.THIEF.getName())
								&& this.gameServer.getGameController().getThiefList().isEmpty()))) {
			try {
				GameLog.log(MsgType.GAME_INFO, "reaktion beendet gespielte karten" + Arrays.toString(CollectionsUtil.getCardIDs(this.activePlayer.getPlayedCards()).toArray()));
				this.gameServer.broadcastMessage(new PacketSendPlayedCardsToAllClients(CollectionsUtil.getCardIDs(this.activePlayer.getPlayedCards())));
				this.gameServer.broadcastMessage(
						new PacketEnableDisable(this.gameServer.getGameController().getActivePlayer().getClientID(), this.gameServer.getGameController().getActivePlayerName(), false));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * sets the nextActivePlayer, resets the PlayerValues, set the gamePhase to
	 * ActionPhase
	 */
	public synchronized void endTurn() {
		this.getActivePlayer().resetPlayerValues();
		this.getActivePlayer().refreshPlayedCardsList();
		this.setNextActivePlayer();
		this.setActionPhase();
	}

	/**
	 * 
	 * @return the users which have a user with a valid session id
	 */
	public LinkedList<Player> getHumanPlayers() {
		LinkedList<Player> humanPlayers = new LinkedList<Player>();
		for (Iterator<Player> iterator = this.players.iterator(); iterator.hasNext();) {
			Player player = iterator.next();
			if (!player.getSessionID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
				humanPlayers.add(player);
			}
		}
		return humanPlayers;
	}

	/**
	 * 
	 * @return the users which have a user with a valid session id
	 */
	public LinkedList<Player> getArtificialPlayers() {
		LinkedList<Player> artificialPlayers = new LinkedList<Player>();
		for (Iterator<Player> iterator = this.players.iterator(); iterator.hasNext();) {
			Player player = iterator.next();
			if (player.getSessionID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
				artificialPlayers.add(player);
			}
		}
		return artificialPlayers;
	}

	/**
	 * @return the player list
	 */
	public LinkedList<Player> getPlayers() {
		return this.players;
	}

	/**
	 * 
	 * @param userName
	 * @return the player with the given userName null if not exists
	 */
	public Player getPlayerByUserName(String userName) {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.getPlayerName().equals(userName)) {
				return player;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param port
	 *            of the player
	 * @return the player with the given port null if not exists
	 */
	public Player getPlayerByPort(int port) {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.getPort() == port) {
				return player;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param clientId
	 * @return the player who has this clientId. null otherwise
	 */
	public synchronized Player getClientById(int clientId) {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.getClientID() == clientId) {
				return player;
			}
		}
		return null;
	}

	/**
	 * @return the activePlayer
	 */
	public synchronized Player getActivePlayer() {
		return this.activePlayer;
	}

	/**
	 * sets the activePlayer with the given param
	 * 
	 * @param activePlayer
	 */
	public void setActivePlayer(Player activePlayer) {
		this.activePlayer = activePlayer;
	}

	/**
	 * @return if the gameIsNotFinished
	 */
	public boolean isGameNotFinished() {
		return this.gameNotFinished;
	}

	/**
	 * set the game not finishe with the given param
	 * 
	 * @param gameNotFinished
	 */
	public void setGameNotFinished(boolean gameNotFinished) {
		this.gameNotFinished = gameNotFinished;
	}

	/**
	 * adds one player to the gameController if four players are registered on
	 * the gameController one randomly choosen player is set as active(this
	 * player will begin the game)
	 * 
	 * @param player
	 * @throws TooMuchPlayerException
	 *             if there connects one more player
	 */
	public void addPlayerAndChooseRandomActivePlayer(Player player) throws TooMuchPlayerException {
		if (this.players.size() < GameConstant.PLAYERS.getValue()) {
			this.players.addLast(player);
			if (this.players.size() == GameConstant.PLAYERS.getValue()) {
				this.activePlayer = getRandomPlayer();
				this.activePlayer.incTurnNr();
				this.activePlayerNameAvailable = true;
				try {
					// this.gameServer.broadcastMessage(
					// new PacketBroadcastLog("----- ",
					// this.activePlayer.getPlayerName(), ": turn " +
					// this.activePlayer.getTurnNr() + " -----",
					// ((ServerGamePacketHandler)this.gameServer.getHandler()).getActivePlayerColor()));
					this.gameServer.broadcastMessage(new PacketBroadcastLog("----- ", this.activePlayer.getPlayerName(), ": turn " + this.activePlayer.getTurnNr() + " -----",
							this.gameServer.getGameController().getActivePlayer().getLogColor()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			throw new TooMuchPlayerException();
		}
	}

	/**
	 * search for the card with the given cardId on the gameBoard if the card
	 * exists add this card to the discardPile of the active player. If the card
	 * not exists throw a SynchronisationException
	 * 
	 * @param cardId
	 * @throws SynchronisationException
	 * @throws WrongSyntaxException
	 */
	public void buyOneCard(String cardId) throws SynchronisationException, WrongSyntaxException {
		Card card = gameBoard.findAndRemoveCardFromBoard(cardId);
		this.getActivePlayer().getDeck().getDiscardPile().add(card);
		GameLog.log(MsgType.GAME, this.getActivePlayerName() + " - buys " + card.getName());
	}

	/**
	 * @return one of the four players who is randomly chosen
	 */
	public Player getRandomPlayer() {
		return this.players.get((int) (Math.random() * players.size()));
	}

	/**
	 * 
	 * @return the gameBoard
	 */
	public synchronized GameBoard getGameBoard() {
		return this.gameBoard;
	}

	/**
	 * sets the action phase
	 */
	public void setActionPhase() {
//		GameLog.log(MsgType.GAME_INFO, "ActionPhaseWasSet");
		this.gamePhase = "actionPhase";
	}

	/**
	 * sets the buy phase
	 */
	public synchronized void setBuyPhase() {
//		GameLog.log(MsgType.GAME_INFO, "BuyPhaseWasSet");
		this.gamePhase = "buyPhase";
	}

	/**
	 * 
	 * @return the game phase
	 */
	public String getGamePhase() {
		return this.gamePhase;
	}

	/**
	 * sets the gamePhase on actionPhase
	 */
	public void startGame() {
		this.gamePhase = "actionPhase";
	}

	/**
	 * checks if the game is finishe. if true the endGame method is called
	 */
	public void isGameFinished() {
		if (this.gameBoard.getTableForVictoryCards().get(CardName.PROVINCE.getName()).isEmpty()) {
			GameLog.log(MsgType.GAME_INFO, "province empty");
			endGame();
		} else if (this.gameBoard.checkPilesEmpty(GameConstant.EMPTY_PILES.getValue())) {
			GameLog.log(MsgType.GAME_INFO, "three piles empty");
			endGame();
		}
	}

	/**
	 * @param players
	 *            the players to get the cards from
	 * @return a list of all played cards from the given players
	 */
	public LinkedList<Card> getAllPlayedCards(Player... players) {
		LinkedList<Card> playedCards = new LinkedList<Card>();
		for (Iterator<Player> iterator = this.players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			playedCards.addAll(player.getPlayedCards());
		}
		return playedCards;
	}

	/**
	 * @return a list of the sorted playernames
	 */
	public LinkedList<String> getPlayerNamesSorted() {
		LinkedList<String> sortedNames = new LinkedList<String>(Arrays.asList(getPlayerNames()));
		Collections.sort(sortedNames, (String name1, String name2) -> name1.compareTo(name2));
		return sortedNames;
	}

	/**
	 * 
	 * @return String array which contains the names of all players
	 */
	public String[] getPlayerNames() {
		LinkedList<String> names = new LinkedList<String>();

		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			names.add(player.getPlayerName());
		}
		String[] temp = new String[names.size()];
		return names.toArray(temp);
	}

	/**
	 * disable all guis of all players. send a message to matchmaking server
	 * with all players and tells who has won. calls the newGame method of the
	 * gameServer.
	 */
	public void endGame() {
		ServerGamePacketHandler gamePacketHandler = (ServerGamePacketHandler) this.gameServer.getHandler();
		gamePacketHandler.getChatController().deleteChatroom();

		setGameNotFinished(false);
		try {
			PacketShowEndScreen packetShowEndScreen = new PacketShowEndScreen();
			int i = 1;
			for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
				Player player = (Player) iterator.next();
				packetShowEndScreen.add("player" + i++, player.getPlayerName(), player.getDeck().getVictoryPoints());
			}
			this.gameServer.getListenerManager().unregisterListener(this.gameServer.getGameServerNetworkListener());
			this.gameServer.broadcastMessage(packetShowEndScreen);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Client client;
		try {
			client = new Client(new InetSocketAddress(Addresses.getLocalHost(), MatchmakingServer.getStandardPort()), new PacketHandler() {

				@Override
				public void handleReceivedPacket(int port, Packet packet) {

				}
			}, false);
			GameLog.log(MsgType.MM, "send message to matchmakingserver");
			client.sendMessage(new PacketGameEnd(getPlayerNames(), getWinningPlayer().getPlayerName(), this.gameServer.getPort()));
			// this.gameServer.newGame();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return the player who has won the game
	 */
	public Player getWinningPlayer() {
		int maxVictoryPoints = -1;
		Player winningPlayer = null;
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			int victoryPoints = player.getDeck().getVictoryPoints();
			if (victoryPoints > maxVictoryPoints) {
				maxVictoryPoints = victoryPoints;
				winningPlayer = player;
			}
		}
		return winningPlayer;
	}

	/**
	 * 
	 * @return the List which shows which players have to play the thief action
	 */
	public CopyOnWriteArrayList<Player> getThiefList() {
		return this.thiefList;
	}

	/**
	 * 
	 * @return the List which shows which players have to play the spy action
	 */
	public CopyOnWriteArrayList<Player> getSpyList() {
		return this.spyList;
	}

	/**
	 * resets the thiefList
	 */
	public void resetThiefList() {
		this.thiefList = new CopyOnWriteArrayList<Player>();
	}
	
	
	/**
	 * resets the spyList
	 */
	public void resetSpyList() {
		this.spyList = new CopyOnWriteArrayList<Player>();
	}

	/**
	 * 
	 * @return whether all reveal lists are empty
	 */
	public boolean allReveaListsEmpty() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (!player.getRevealList().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return whether all temporarytrash lists are empty
	 */
	public boolean allTemporaryTrashPilesEmpty() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (!player.getTemporaryTrashPile().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return whether all players are out of reveal mode
	 */
	public boolean allPlayersRevealed() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.isRevealMode()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return whether all players have gained
	 */
	public boolean allPlayerGained() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.isGainMode()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return whether all players have trashed
	 */
	public boolean allPlayerTrashed() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.isTrashMode()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return whether all players have discarded
	 */
	public boolean allPlayerDiscarded() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.isDiscardMode()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return a negative, so far unused aiPort (ai needs a port to be able to
	 *         play, but doesn't need it in terms of communication)
	 */
	public int getAiPort() {
		int ran;
		do {
			ran = new Random().nextInt(1024) * (-1);
		} while (aiPorts.contains(ran));
		aiPorts.add(ran);
		return ran;
	}
}
