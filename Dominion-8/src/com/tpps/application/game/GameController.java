package com.tpps.application.game;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.technicalServices.network.game.SynchronisationException;
import com.tpps.technicalServices.network.game.TooMuchPlayerException;
import com.tpps.technicalServices.network.gameSession.packets.PacketDisable;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnable;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnableOthers;
import com.tpps.technicalServices.network.gameSession.packets.PacketPutBackCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendActiveButtons;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendBoard;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendHandCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendRevealCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketShowEndReactions;
import com.tpps.technicalServices.network.gameSession.packets.PacketTakeCards;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/**
 * @author Lukas Adler
 * @author Nicolas Wipfler
 * 
 */

/* --------Methoden nach Logik sortieren und sichtbarkeit anpassen-------- */

public class GameController {

	public String getActivePlayerName() {
		return null /* this.activePlayer.getName() */;
	}
	
	private LinkedList<Player> players;

	private boolean gameNotFinished, cardsEnabled;
	private Player activePlayer;
	private GameBoard gameBoard;
	private String gamePhase;
	private CopyOnWriteArrayList<Player> thiefList, spyList;

	/**
	 * 
	 */
	public GameController() {
		this.cardsEnabled = true;
		this.players = new LinkedList<Player>();
		this.thiefList = new CopyOnWriteArrayList<Player>();
		this.spyList = new CopyOnWriteArrayList<Player>();
		this.gameBoard = new GameBoard();
		this.gameNotFinished = true;
	}

	/**
	 * @return the cardsEnabled
	 */
	public boolean isCardsEnabled() {
		return cardsEnabled;
	}

	/**
	 * 
	 */
	public void setCardsEnabled() {
		this.cardsEnabled = true;
	}

	/**
	 * 
	 */
	public void setCardsDisabled() {
		this.cardsEnabled = false;
	}

	/**
	 * @param gameBoard
	 *            the gameBoard to set
	 */
	public void setGameBoard(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
	}

	/**
	 * @param gamePhase
	 *            the gamePhase to set
	 */
	public void setGamePhase(String gamePhase) {
		this.gamePhase = gamePhase;
	}

	/**
	 * @param thiefList
	 *            the thiefList to set
	 */
	public void setThiefList(CopyOnWriteArrayList<Player> thiefList) {
		this.thiefList = thiefList;
	}

	/**
	 * @param spyList
	 *            the spyList to set
	 */
	public void setSpyList(CopyOnWriteArrayList<Player> spyList) {
		this.spyList = spyList;
	}

	/**
	 * 
	 */
	public void setNextActivePlayer() {
		Player activePlayer = this.getActivePlayer();
		LinkedList<Player> players = this.getPlayers();
		for (int i = 0; i < GameConstant.HUMAN_PLAYERS; i++) {
			Player player = players.get(i);
			if (player.equals(activePlayer)) {
				this.setActivePlayer(players.get(i < GameConstant.HUMAN_PLAYERS - 1 ? i + 1 : 0));
				break;
			}
		}
	}

	/**
	 * 
	 * @param temporaryTrashPile
	 */
	public synchronized void updateTrashPile(LinkedList<Card> temporaryTrashPile) {
		CollectionsUtil.appendListToList(temporaryTrashPile, this.gameBoard.getTrashPile());
	}

	/**
	 * 
	 * @param player
	 * @param cardID
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean checkCardExistsAndDiscardOrTrash(Player player, String cardID) throws IOException {
		Card card = player.getDeck().getCardFromHand(cardID);
		if (card != null) {
			player.discardOrTrash(cardID, this.getGameBoard().getTrashPile());
			return true;
		}
		return false;
	}

	/**
	 * checks whether a card which was clicked exists and if it is allowed to
	 * play this card in this phase of the game. If it is allowed the card is
	 * played
	 * 
	 * @param cardID
	 * @throws IOException
	 * @throws SynchronisationException
	 */
	public synchronized boolean validateTurnAndExecute(String cardID, Player player) throws IOException {

		Card card = player.getDeck().getCardFromHand(cardID);
		if (card != null) {
			if (player.isReactionMode() && card.getTypes().contains(CardType.REACTION)) {
				System.out.println("spielt reaktionskarte");
				player.playCard(cardID);
				GameServer.getInstance().sendMessage(player.getPort(), new PacketSendActiveButtons(true, true, false));
				return true;
			}
			if (this.gamePhase.equals("actionPhase")) {

				if (card.getTypes().contains(CardType.ACTION) && this.getActivePlayer().getActions() > 0) {
					this.getActivePlayer().playCard(cardID);
					if (this.getActivePlayer().getActions() == 0) {
						this.setBuyPhase();
					}
					return true;
				}
			}
			if (this.gamePhase.equals("buyPhase")) {
				if (card.getTypes().contains(CardType.TREASURE)) {
					// GameLog.log(MsgType.DEBUG, "the card is a Treasure
					// clicked in the buyphase");
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
	 * @return
	 */
	public synchronized boolean gain(String cardID, Player player) {
		System.out.println("gain");
		try {
			LinkedList<Card> cardList = this.getGameBoard().findCardListFromBoard(cardID);
			Card card = cardList.getLast();
			if (card.getCost() <= player.getGainValue()) {
				getGameBoard().findAndRemoveCardFromBoard(cardID);
				player.setGainModeFalse();
				if (player.isOnHand()) {
					player.setOnHandFalse();
					player.getDeck().getCardHand().add(card);
					GameServer.getInstance().sendMessage(player.getPort(), new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand())));
					return true;
				}
				player.getDeck().getDiscardPile().add(card);
				return true;
			}
		} catch (SynchronisationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 
	 * @param cardID
	 * @return
	 * @throws SynchronisationException
	 */
	public synchronized boolean checkBoardCardExistsAppendToDiscardPile(String cardID) throws SynchronisationException {
		System.out.println("checkBoardCardExists");
		LinkedList<Card> cards = this.getGameBoard().findCardListFromBoard(cardID);
		Card card = cards.get(cards.size() - 1);
		Player player = this.getActivePlayer();
		if (this.gamePhase.equals("buyPhase") && player.getBuys() > 0 && player.getCoins() >= card.getCost()) {
			player.setBuys(player.getBuys() - 1);
			player.setCoins(player.getCoins() - card.getCost());
			cards.removeLast();
			CollectionsUtil.addCardToList(card, player.getDeck().getDiscardPile());
			return true;
		}
		return false;
	}

	/**
	 * checks if the card according to the given cardId is a tresure card on the
	 * hand
	 * 
	 * @param cardId
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
	 * calls the play Treasures method of the player adds the returned treasure
	 * cards from the player cardHand to the playedCard list
	 * 
	 * @throws IOException
	 */
	public synchronized void playTreasures() throws IOException {
		this.getActivePlayer().playTreasures();
	}

	/**
	 * 
	 */
	public synchronized void organizePilesAndrefreshCardHand() {

		CollectionsUtil.appendListToList(this.getActivePlayer().getPlayedCards(), this.getActivePlayer().getDeck().getDiscardPile());
		this.getActivePlayer().getDeck().refreshCardHand();
		this.getActivePlayer().refreshPlayedCardsList();
	}

	/**
	 * 
	 * @param value
	 */
	public synchronized void discardOtherDownto(String value) {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (!player.equals(activePlayer)) {
				if (player.getDeck().cardHandContainsReactionCard()) {
					player.setReactionCard(true);
					try {
						GameServer.getInstance().sendMessage(player.getPort(), new PacketShowEndReactions());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				player.setDiscardMode();
				player.setDiscardOrTrashAction(CardAction.DISCARD_CARD, player.getDeck().getCardHand().size() - Integer.parseInt(value));

				player.setReactionMode();
				try {
					GameServer.getInstance().broadcastMessage(player.getPort(), new PacketEnableOthers(this.activePlayer.getClientID()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * let every player who hasn't a reaction card reveal two cards which are
	 * sent to the activePlayer
	 */
	public synchronized void revealAndTakeCardsDiscardOthers() {
		boolean reactivePlayer = false;
		for (Iterator<Player> iterator = this.players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			player.setThief();
			if (!player.equals(this.activePlayer)) {
				if (player.getDeck().cardHandContainsReactionCard()) {
					reactivePlayer = true;
					player.setThiefFalse();
					try {
						GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new PacketDisable());
					} catch (IOException e1) {

						e1.printStackTrace();
					}
					player.setReactionCard(true);
					player.setReactionMode();
					try {
						GameServer.getInstance().sendMessage(player.getPort(), new PacketShowEndReactions());
						GameServer.getInstance().sendMessage(player.getPort(), new PacketEnable());
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
		System.out.println("im gamecontrolloer thiefList size: " + thiefList.size());
		if (thiefList.size() > 0) {
			try {
				GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new PacketSendActiveButtons(false, false, false));
				GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new PacketSendRevealCards(CollectionsUtil.getCardIDs(thiefList.get(0).getRevealList())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (!reactivePlayer) {
			System.out.println("thief false");
			this.activePlayer.setThiefFalse();
		}
	}

	/**
	 * 
	 */
	public synchronized void revealCardAll() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			player.setSpy();
			if (!player.equals(activePlayer)) {
				if (player.getDeck().cardHandContainsReactionCard()) {
					player.setSpyFalse();
					try {
						GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new PacketDisable());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					player.setReactionCard(true);
					player.setReactionMode();
					try {
						GameServer.getInstance().sendMessage(player.getPort(), new PacketShowEndReactions());
						GameServer.getInstance().sendMessage(player.getPort(), new PacketEnable());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					player.setRevealMode();
					player.getRevealList().add(player.getDeck().removeSaveFromDrawPile());
					System.out.println("was is in revealList:  " + Arrays.toString(player.getRevealList().toArray()));
					this.spyList.add(player);
				}
			}
		}
		this.activePlayer.setRevealMode();
		this.activePlayer.getRevealList().add(activePlayer.getDeck().removeSaveFromDrawPile());
		System.out.println("was is in revealList:  " + Arrays.toString(this.activePlayer.getRevealList().toArray()));
		this.spyList.add(this.activePlayer);
		System.out.println("im gamecontrolloer spyList size: " + this.spyList.size());
		if (this.spyList.size() > 0) {
			try {
				GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new PacketTakeCards(this.activePlayer.getClientID()));
				GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new PacketPutBackCards(this.activePlayer.getClientID()));
				GameServer.getInstance().sendMessage(this.getActivePlayer().getPort(), new PacketSendRevealCards(CollectionsUtil.getCardIDs(spyList.get(0).getRevealList())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	public synchronized void gainCurseOthers() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			player.setWitch();
			if (!player.equals(activePlayer)) {

				if (player.getDeck().cardHandContainsReactionCard()) {
					player.setWitchFalse();
					try {
						GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new PacketDisable());
					} catch (IOException e1) {

						e1.printStackTrace();
					}
					player.setReactionCard(true);
					player.setReactionMode();
					try {
						GameServer.getInstance().sendMessage(player.getPort(), new PacketShowEndReactions());
						GameServer.getInstance().sendMessage(player.getPort(), new PacketEnable());
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					try {
						player.getDeck().getDiscardPile().add(this.gameBoard.getTableForVictoryCards().get("Curse").removeLast());
					} catch (NoSuchElementException e) {
						GameLog.log(MsgType.GAME, "Not enough curses.\n");
					}
					player.setWitchFalse();
				}
			}
		}
		checkReactionModeFinishedAndEnableGuis();
		try {
			GameServer.getInstance().broadcastMessage(new PacketSendBoard(this.getGameBoard().getTreasureCardIDs(), getGameBoard().getVictoryCardIDs(), getGameBoard().getActionCardIDs()));
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
			if (!player.equals(activePlayer)) {

				if (player.getDeck().cardHandContainsReactionCard()) {
					player.setBureaucratFalse();
					if (sendPacketDisable) {
						sendPacketDisable = false;
						try {
							GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new PacketDisable());
						} catch (IOException e1) {

							e1.printStackTrace();
						}
					}
					player.setReactionCard(true);
					player.setReactionMode();
					try {
						GameServer.getInstance().sendMessage(player.getPort(), new PacketShowEndReactions());
						GameServer.getInstance().sendMessage(player.getPort(), new PacketEnable());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Card card = player.getDeck().getCardByTypeFromHand(CardType.VICTORY);

					if (card != null) {
						player.getDeck().getCardHand().remove(card);
						player.getDeck().getDrawPile().addLast(card);
						try {
							GameServer.getInstance().sendMessage(player.getPort(), new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand())));
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						System.err.println("no victory card on hand");
					}
					player.setBureaucratFalse();
					System.out.println(Arrays.toString(player.getDeck().getDrawPile().toArray()));
					System.out.println();
				}
			}
		}
		try {
			GameServer.getInstance().broadcastMessage(new PacketSendBoard(this.getGameBoard().getTreasureCardIDs(), getGameBoard().getVictoryCardIDs(), getGameBoard().getActionCardIDs()));
		} catch (IOException e) {

			e.printStackTrace();
		}
		checkReactionModeFinishedAndEnableGuis();
	}

	/**
	 * 
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
			System.out.println("witch false");
			this.activePlayer.setWitchFalse();
		}
	}

	/**
	 * 
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
			System.out.println("bureaucrat false");
			this.activePlayer.setBureaucratFalse();
		}
	}

	/**
	 * 
	 * @return
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
		System.out.println("kein thief" + thiefFlag);
		return thiefFlag;
	}

	/**
	 * 
	 * @return
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
			System.out.println("spy false");
			return spyFlag;
		}
		return spyFlag;
	}

	/**
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
					GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new PacketSendActiveButtons(false, false, false));
					GameServer.getInstance().sendMessage(activePlayer.getPort(), new PacketSendRevealCards(CollectionsUtil.getCardIDs(thiefList.get(0).getRevealList())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			player.setThiefFalse();
		}
		System.out.println("react new thieflist size: " + thiefList.size());
	}

	/**
	 * 
	 * @param player
	 */
	public void reactOnSpy(Player player) {
		player.setRevealMode();
		player.getRevealList().add(player.getDeck().removeSaveFromDrawPile());
		spyList.add(player);
		if (spyList.size() == 1) {
			try {
				GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new PacketTakeCards(this.activePlayer.getClientID()));
				GameServer.getInstance().sendMessage(this.activePlayer.getPort(), new PacketPutBackCards(this.activePlayer.getClientID()));
				GameServer.getInstance().sendMessage(activePlayer.getPort(), new PacketSendRevealCards(CollectionsUtil.getCardIDs(spyList.get(0).getRevealList())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @return if all player have played his/ her reactionCards
	 */
	private boolean allReactionCardsPlayed() {
		boolean allReactionCardsPlayedFlag = true;

		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (player.playsReactionCard()) {
				allReactionCardsPlayedFlag = false;
				break;
			}
		}
		return allReactionCardsPlayedFlag;
	}

	/**
	 * 
	 */
	public synchronized void drawOthers() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			if (!player.equals(activePlayer)) {
				player.getDeck().draw();
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

		try {
			GameServer.getInstance().sendMessage(this.getActivePlayer().getPort(), new PacketEnable());
		} catch (IOException e) {
			e.printStackTrace();
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
	 */
	public LinkedList<Player> getPlayers() {
		return this.players;
	}

	/**
	 * 
	 * @param clientId
	 * @return
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
	 * 
	 */
	public void setPlayers(LinkedList<Player> players) {
		this.players = players;
	}

	/**
	 * 
	 */
	public synchronized Player getActivePlayer() {
		return this.activePlayer;
	}

	/**
	 * 
	 */
	public void setActivePlayer(Player aP) {
		this.activePlayer = aP;
	}

	/**
	 * 
	 */
	public boolean isGameNotFinished() {
		return this.gameNotFinished;
	}

	/**
	 * 
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
	public void addPlayer(Player player) throws TooMuchPlayerException {
		if (this.players.size() < 4) {
			this.players.addLast(player);
			if (this.players.size() == 4) {
				this.activePlayer = getRandomPlayer();
			}
		} else {
			throw new TooMuchPlayerException();
		}
	}

	/**
	 * search for the card with the given cardId on the gameBoard if the card
	 * exists add this card to the discardPile of the active player. If the card
	 * not exists throw a
	 * 
	 * @param cardId
	 * @throws SynchronisationException
	 */
	public void buyOneCard(String cardId) throws SynchronisationException {
		Card card = gameBoard.findAndRemoveCardFromBoard(cardId);
		this.getActivePlayer().getDeck().getDiscardPile().add(card);
	}

	/**
	 * @return one of the four players who is randomly choosen
	 */
	private Player getRandomPlayer() {
		return this.players.get((int) (Math.random() * 4));
	}

	/**
	 * 
	 * @return
	 */
	public synchronized GameBoard getGameBoard() {
		return gameBoard;
	}

	/**
	 * 
	 */
	public void setDiscardPhase() {
		System.out.println("DiscardPhaseWasSet");
		this.gamePhase = "discardPhase";
	}

	/**
	 * 
	 */
	public void setActionPhase() {
		System.out.println("ActionPhaseWasSet");
		this.gamePhase = "actionPhase";
	}

	/**
	 * 
	 */
	public synchronized void setBuyPhase() {
		System.out.println("BuyPhaseWasSet");
		this.gamePhase = "buyPhase";
	}

	/**
	 * 
	 * @return
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
	 * 
	 */
	public void isGameFinished() {
		if (this.gameBoard.getTableForVictoryCards().get("Province").isEmpty()) {
			endGame();
		} else if (this.gameBoard.checkThreePilesEmpty()) {
			endGame();
		}
	}

	/**
	 * 
	 */
	public void endGame() {
		setGameNotFinished(false);
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			try {
				GameServer.getInstance().sendMessage(player.getPort(), new PacketDisable());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		GameServer.getInstance().newGame();
	}

	/**
	 * 
	 * @return the List which shows which player have to play the thief action
	 */
	public CopyOnWriteArrayList<Player> getThiefList() {
		return this.thiefList;
	}

	/**
	 * 
	 * @return
	 */
	public CopyOnWriteArrayList<Player> getSpyList() {
		return this.spyList;
	}

	/**
	 * 
	 */
	public void resetThiefList() {
		this.thiefList = new CopyOnWriteArrayList<Player>();
	}
}
