package com.tpps.technicalServices.network.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.tpps.application.game.GameBoard;
import com.tpps.application.game.Player;
import com.tpps.application.game.ai.ArtificialIntelligence;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.chat.server.ChatController;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLog;
import com.tpps.technicalServices.network.gameSession.packets.PacketBuyCard;
import com.tpps.technicalServices.network.gameSession.packets.PacketClientShouldDisconect;
import com.tpps.technicalServices.network.gameSession.packets.PacketDisable;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnable;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnableDisable;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndActionPhase;
import com.tpps.technicalServices.network.gameSession.packets.PacketEndReactions;
import com.tpps.technicalServices.network.gameSession.packets.PacketOpenGuiAndEnableOne;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayCard;
import com.tpps.technicalServices.network.gameSession.packets.PacketPutBackCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketPutBackThiefCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketReconnect;
import com.tpps.technicalServices.network.gameSession.packets.PacketRegistratePlayerByServer;
import com.tpps.technicalServices.network.gameSession.packets.PacketRemoveExtraTable;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendActiveButtons;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendBoard;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendClientId;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendHandCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendPlayedCardsToAllClients;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendRevealCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketTakeCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketTakeThiefCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketTemporaryTrashCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketUpdateTreasures;
import com.tpps.technicalServices.network.gameSession.packets.PacketUpdateValues;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/**
 * @author ladler - Lukas Adler
 */
public class ServerGamePacketHandler extends PacketHandler {
	private GameServer server;

	public void setServer(GameServer server) {
		this.server = server;
	}

	/**
	 * checks which packet was sent and reacts on the packet
	 */
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		// ServerConnectionThread requester = parent.getClientThread(port);
		if (packet == null) {
			super.output("<- Empty Packet from (" + port + ")");
			return;
		}
		try {
			switch (packet.getType()) {
			case REGISTRATE_PLAYER_BY_SERVER:
				int clientId = GameServer.getCLIENT_ID();
				System.out.println("clientId: " + clientId);
				PacketRegistratePlayerByServer packetRegistratePlayerByServer = (PacketRegistratePlayerByServer) packet;
				if (packetRegistratePlayerByServer.getSessionID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))){
					System.out.println("add ai");
					addPlayerAndCheckPlayerCount(port, clientId, packetRegistratePlayerByServer.getUsername(), packetRegistratePlayerByServer.getSessionID());
				}
				else if (this.server.validSession(packetRegistratePlayerByServer.getUsername(),
						packetRegistratePlayerByServer.getSessionID())) {
					System.out.println("Connect valid Session username: " + packetRegistratePlayerByServer.getUsername() + 
							"sessionID: " + packetRegistratePlayerByServer.getSessionID());
					addPlayerAndCheckPlayerCount(port, clientId, packetRegistratePlayerByServer.getUsername(),
							packetRegistratePlayerByServer.getSessionID());
				} else {
					this.server.disconnect(port);
				}
				break;
			case RECONNECT:
				PacketReconnect packetReconnect = (PacketReconnect) packet;
				if (this.server.validSession(packetReconnect.getUsername(), packetReconnect.getSessionID())) {
					System.out.println("Reconnect valid Session username: " + packetReconnect.getUsername() + 
							"sessionID: " + packetReconnect.getSessionID());
					updatePortOfPlayer(port, packetReconnect);
				} else {
					this.server.disconnect(port);
				}
				break;
			case CARD_PLAYED:
				if (this.server.getGameController().isCardsEnabled()) {
					cardPlayed(port, packet);
				} else {
					System.out.println("no cards enabled");
				}
				break;
			case BUY_CARD:
				buyCardAndUpdateBoards(packet);
				break;
			case END_ACTION_PHASE:
				this.server.getGameController().setBuyPhase();
				break;
			case PLAY_TREASURES:
				this.server.getGameController().playTreasures();

				server.sendMessage(port, new PacketSendHandCards(CollectionsUtil
						.getCardIDs(this.server.getGameController().getActivePlayer().getDeck().getCardHand())));
				server.broadcastMessage(new PacketSendPlayedCardsToAllClients(CollectionsUtil
						.getCardIDs(this.server.getGameController().getActivePlayer().getPlayedCards())));
				server.sendMessage(port,
						new PacketUpdateTreasures(server.getGameController().getActivePlayer().getCoins()));
				break;
			case END_TURN:
				// alle Karten ablegen

				nextActivePlayer(port);

				break;
			case END_DISCARD_MODE:
				this.server.getGameController().getActivePlayer().endDiscardAndDrawMode();

				this.server.sendMessage(port, new PacketSendHandCards(CollectionsUtil
						.getCardIDs(this.server.getGameController().getActivePlayer().getDeck().getCardHand())));
				break;
			case END_TRASH_MODE:

				this.server.getGameController().getActivePlayer().endTrashMode();

				break;
			case TAKE_CARDS:
				int clientID = ((PacketTakeCards) packet).getClientID();
				Player reactivePlayer = this.server.getGameController().getSpyList().get(0);
				Player player = server.getGameController().getClientById(clientID);
				reactivePlayer.takeRevealedCardsSetRevealModeFalse();
				reactivePlayer.setSpyFalse();
				this.server.getGameController().getSpyList().remove(reactivePlayer);
				System.out.println("spyList size take cards: " + this.server.getGameController().getSpyList().size());
				this.server.sendMessage(port, new PacketRemoveExtraTable());
				if (!this.server.getGameController().getSpyList().isEmpty()) {
					try {
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
								new PacketSendRevealCards(CollectionsUtil.getCardIDs(
										this.server.getGameController().getSpyList().get(0).getRevealList())));
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
								new PacketTakeCards(this.server.getGameController().getActivePlayer().getClientID()));
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
								new PacketPutBackCards(
										this.server.getGameController().getActivePlayer().getClientID()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if (this.server.getGameController().checkSpyFinish()) {
						resetGameWindowAfterRevealAction(port, player);
					}
				}
				break;
			case PUT_BACK_CARDS:
				clientID = ((PacketPutBackCards) packet).getClientID();
				reactivePlayer = this.server.getGameController().getSpyList().get(0);
				player = server.getGameController().getClientById(clientID);
				reactivePlayer.putBackRevealedCardsSetRevealModeFalse();
				reactivePlayer.setSpyFalse();
				this.server.getGameController().getSpyList().remove(reactivePlayer);
				this.server.sendMessage(port, new PacketRemoveExtraTable());
				if (!this.server.getGameController().getSpyList().isEmpty()) {
					try {
						server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
								new PacketTakeCards(this.server.getGameController().getActivePlayer().getClientID()));
						server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
								new PacketPutBackCards(
										this.server.getGameController().getActivePlayer().getClientID()));
						server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
								new PacketSendRevealCards(CollectionsUtil.getCardIDs(
										this.server.getGameController().getSpyList().get(0).getRevealList())));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if (this.server.getGameController().checkSpyFinish()) {
						resetGameWindowAfterRevealAction(port, player);
					}
				}

				break;
			case TAKE_THIEF_CARDS:
				takeThiefCards(port);
				this.server.getGameController().setCardsEnabled();
				resetGameWindowAfterRevealAction(port, this.server.getGameController().getActivePlayer());
				break;
			case PUT_BACK_THIEF_CARDS:
				putBackThiefCards(port);
				this.server.getGameController().setCardsEnabled();
				resetGameWindowAfterRevealAction(port, this.server.getGameController().getActivePlayer());
				break;
			case TAKE_DRAWED_CARD:
				takeDrawedCardStartDrawing();
				this.server.getGameController().setCardsEnabled();
				break;
			case SET_ASIDE_DRAWED_CARD:
				setAsideDrawedCardStartDrawing();
				this.server.getGameController().setCardsEnabled();
				break;
			case TEMPORARY_TRASH_CARDS:
				System.out.println("TemporaryTrashCards");
				clientID = ((PacketTemporaryTrashCards) packet).getClientID();
				player = server.getGameController().getClientById(clientID);

				break;

			case END_REACTIONS:
				Player player1 = this.server.getGameController()
						.getClientById(((PacketEndReactions) packet).getClientID());
				player1.setReactionCard(false);
				reactionFinishedTriggeredThroughThief(player1);
				reactionFinishedTriggerdThroughSpy(player1);
				reactionFinishedTriggeredThroughWitch(player1);
				reactionFinishedTriggeredThroughBureaucrat(player1);
				this.server.getGameController().isGameFinished();
				break;
			case DISCARD_DECK:
				this.server.getGameController().getActivePlayer().getDeck().discardDrawPile();

				break;
			case BROADCAST_LOG:
				this.server.broadcastMessage(packet);
				break;
			default:
				System.out.println("unknown packed type");
				break;

			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}

	private void reactionFinishedTriggeredThroughThief(Player player1) throws IOException {
		if (this.server.getGameController().getActivePlayer().isThief()) {
			player1.setReactionModeFalse();
			this.server.sendMessage(player1.getPort(), new PacketDisable());

			this.server.getGameController().reactOnThief(player1);
			this.server.getGameController().checkReactionModeFinishedAndEnableGuis();
		}
	}

	private void reactionFinishedTriggerdThroughSpy(Player player1) throws IOException {
		if (this.server.getGameController().getActivePlayer().isSpy()) {
			player1.setReactionModeFalse();
			this.server.sendMessage(player1.getPort(), new PacketDisable());

			this.server.getGameController().reactOnSpy(player1);
			this.server.getGameController().checkReactionModeFinishedAndEnableGuis();
		}

	}

	private void reactionFinishedTriggeredThroughWitch(Player player1) throws IOException {
		if (this.server.getGameController().getActivePlayer().isWitch()) {
			player1.setReactionModeFalse();
			this.server.sendMessage(player1.getPort(), new PacketDisable());
			try {
				player1.getDeck().getDiscardPile().add(this.server.getGameController().getGameBoard()
						.getTableForVictoryCards().get("Curse").removeLast());

				server.broadcastMessage(
						new PacketSendBoard(this.server.getGameController().getGameBoard().getTreasureCardIDs(),
								this.server.getGameController().getGameBoard().getVictoryCardIDs(),
								this.server.getGameController().getGameBoard().getActionCardIDs()));
			} catch (NoSuchElementException e) {
				GameLog.log(MsgType.GAME, "Not enough curses.\n");
			}
			this.server.getGameController().checkReactionModeFinishedAndEnableGuis();
		}
	}

	private void reactionFinishedTriggeredThroughBureaucrat(Player player) throws IOException {
		if (this.server.getGameController().getActivePlayer().isBureaucrat()) {
			player.setReactionModeFalse();
			this.server.sendMessage(player.getPort(), new PacketDisable());

			Card card = player.getDeck().getCardByTypeFromHand(CardType.VICTORY);
			if (card != null) {
				player.getDeck().getCardHand().remove(card);
				player.getDeck().getDrawPile().addLast(card);
				try {
					server.sendMessage(player.getPort(),
							new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.err.println("no victory card on hand");
			}
			server.broadcastMessage(
					new PacketSendBoard(this.server.getGameController().getGameBoard().getTreasureCardIDs(),
							this.server.getGameController().getGameBoard().getVictoryCardIDs(),
							this.server.getGameController().getGameBoard().getActionCardIDs()));
			this.server.getGameController().checkReactionModeFinishedAndEnableGuis();
		}
	}

	private void setAsideDrawedCardStartDrawing() {
		Player activePlayer = this.server.getGameController().getActivePlayer();
		activePlayer.getSetAsideCards().add(activePlayer.getDrawedCard());
		activePlayer.getDeck().getCardHand().remove(activePlayer.getDrawedCard());
		activePlayer.drawUntil();
	}

	private void takeDrawedCardStartDrawing() {
		System.out.println("take drawed card server gamePackethandler");
		Player activePlayer = this.server.getGameController().getActivePlayer();
		activePlayer.drawUntil();
	}

	private void putBackThiefCards(int port) {
		LinkedList<Player> players = this.server.getGameController().getPlayers();
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			System.out.println("discardPile size davor: " + player.getDeck().getDiscardPile().size());
			CollectionsUtil.appendListToList(player.getTemporaryTrashPile(), player.getDeck().getDiscardPile());
			System.out.println("discardPile size danach: " + player.getDeck().getDiscardPile().size());
			player.resetTemporaryTrashPile();
		}
		try {
			this.server.sendMessage(port, new PacketRemoveExtraTable());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void takeThiefCards(int port) {
		System.out.println("takeThiefCards");
		LinkedList<Player> players = this.server.getGameController().getPlayers();
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			System.out.println("discardPile size davor: "
					+ this.server.getGameController().getActivePlayer().getDeck().getDiscardPile().size());
			CollectionsUtil.appendListToList(player.getTemporaryTrashPile(),
					this.server.getGameController().getActivePlayer().getDeck().getDiscardPile());
			System.out.println("discardPile size danach: "
					+ this.server.getGameController().getActivePlayer().getDeck().getDiscardPile().size());
			player.resetTemporaryTrashPile();
		}
		try {
			this.server.sendMessage(port, new PacketRemoveExtraTable());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void cardPlayed(int port, Packet packet) throws IOException {
		String cardID = ((PacketPlayCard) packet).getCardID();
		int clientID = ((PacketPlayCard) packet).getClientID();
		System.out.println(server.getGameController().getGamePhase());

		// Player activePlayer =
		// this.server.getGameController().getActivePlayer();
		Player player = this.server.getGameController().getClientById(clientID);

		if (!player.playsReactionCard() && (player.isDiscardMode() || player.isTrashMode())) {
			System.out.println("im handler discard mode set");
			if (this.server.getGameController().checkCardExistsAndDiscardOrTrash(player, cardID)) {
				server.sendMessage(port,
						new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand())));
			}
			return;
		}

		if (this.server.getGameController().isVictoryCardOnHand(cardID) && !player.isDiscardMode()
				&& !player.isTrashMode()
				|| player.isReactionMode()
						&& !player.getDeck().getCardFromHand(cardID).getTypes().contains(CardType.REACTION)
				|| player.isRevealMode()) {
			System.out.println("nur returnen");
			return;
		}

		if (player.isThief()) {
			Player reactivePlayer = this.server.getGameController().getThiefList().get(0);
			System.out.println("size: " + this.server.getGameController().getThiefList().size());

			if (CollectionsUtil.getCardIDs(reactivePlayer.getRevealList()).contains(cardID)) {
				reactivePlayer.getTemporaryTrashPile()
						.add(CollectionsUtil.removeCardById(reactivePlayer.getRevealList(), cardID));
				CollectionsUtil.appendListToList(reactivePlayer.getRevealList(),
						reactivePlayer.getDeck().getDiscardPile());
				this.server.getGameController().getThiefList().remove(reactivePlayer);
				reactivePlayer.resetThiefMode();
				this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
						new PacketRemoveExtraTable());
				if (!this.server.getGameController().getThiefList().isEmpty()) {
					System.out.println("new Reactive player");
					reactivePlayer = this.server.getGameController().getThiefList().get(0);

					this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
							new PacketSendRevealCards(CollectionsUtil.getCardIDs(reactivePlayer.getRevealList())));
				} else {
					System.out.println("is empty");
					LinkedList<Player> players = new LinkedList<Player>(this.server.getGameController().getPlayers());

					players.remove(this.server.getGameController().getActivePlayer());
					boolean thiefFlag = this.server.getGameController().checkThiefFinish();

					if (thiefFlag) {
						System.out.println("alle karten schicken");
						this.server.getGameController().setCardsDisabled();
						this.server.getGameController().getActivePlayer().setThiefFalse();
						LinkedList<Card> allThiefCards = new LinkedList<Card>();
						for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
							Player player2 = (Player) iterator.next();
							CollectionsUtil.appendListToList(player2.getTemporaryTrashPile(), allThiefCards);
						}
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
								new PacketSendRevealCards(CollectionsUtil.getCardIDs(allThiefCards)));
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
								new PacketTakeThiefCards());
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
								new PacketPutBackThiefCards());
						player.setThiefFalse();
						this.server.getGameController().resetThiefList();
					}
				}
			}
			return;
		}

		if (player.isGainMode()) {

			if (this.server.getGameController().gain(cardID, player)) {

				this.server.broadcastMessage(
						new PacketSendBoard(this.server.getGameController().getGameBoard().getTreasureCardIDs(),
								this.server.getGameController().getGameBoard().getVictoryCardIDs(),

								this.server.getGameController().getGameBoard().getActionCardIDs()));
				this.server.getGameController().isGameFinished();
			}
			return;
		}

		if (this.server.getGameController().validateTurnAndExecute(cardID, player)) {
			System.out.println("validate turn: " + player.getActions() + "buys: " + player.getBuys() + "coins: "
					+ player.getCoins());

			this.server.sendMessage(port,
					new PacketUpdateValues(player.getActions(), player.getBuys(), player.getCoins()));
			if (player.getActions() == 0 && !player.isThief()) {
				server.sendMessage(port, new PacketEndActionPhase());
			}
			this.server.sendMessage(port,
					new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand())));
			this.server.broadcastMessage(
					new PacketSendPlayedCardsToAllClients(CollectionsUtil.getCardIDs(player.getPlayedCards())));
			this.server.getGameController().isGameFinished();
		} else {
			try {
				if (this.server.getGameController().checkBoardCardExistsAppendToDiscardPile(cardID)) {
					GameBoard gameBoard = this.server.getGameController().getGameBoard();
					this.server.broadcastMessage(new PacketSendBoard(gameBoard.getTreasureCardIDs(),
							gameBoard.getVictoryCardIDs(), gameBoard.getActionCardIDs()));
					this.server.sendMessage(port,
							new PacketUpdateValues(player.getActions(), player.getBuys(), player.getCoins()));
					if (player.getBuys() == 0) {
						nextActivePlayer(port);
					}
				}
			} catch (SynchronisationException | NoSuchElementException e) {
				GameLog.log(MsgType.GAME, "The card you wanted to buy is not on the board.");
			} catch (WrongSyntaxException e) {
				GameLog.log(MsgType.GAME, e.getMessage());
			}
			this.server.getGameController().isGameFinished();
			return;
		}
	}

	/**
	 * disables gameWindow removes the extra table for the revealed Cards send
	 * the active buttons to the game window to set it to the old values
	 * 
	 * @param port
	 * @param player
	 * @throws IOException
	 */
	private void resetGameWindowAfterRevealAction(int port, Player player) throws IOException {

		if (player.getActions() > 0) {
			this.server.sendMessage(port, new PacketSendActiveButtons(true, true, false));
		} else {
			this.server.sendMessage(port, new PacketSendActiveButtons(true, false, true));
		}
	}

	private void canActivePlayerContinue() throws IOException {
		boolean activePlayerCanContinue = true;
		for (Iterator<Player> iterator = this.server.getGameController().getPlayers().iterator(); iterator.hasNext();) {
			Player p = (Player) iterator.next();
			if (p.isRevealMode()) {
				activePlayerCanContinue = false;
				break;
			}
		}
		if (activePlayerCanContinue) {
			server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketEnable());
		}
	}

	private void buyCardAndUpdateBoards(Packet packet) throws IOException {
		try {
			GameBoard gameBoard = this.server.getGameController().getGameBoard();
			this.server.getGameController().buyOneCard(((PacketBuyCard) packet).getCardId());

			server.broadcastMessage(new PacketSendBoard(gameBoard.getTreasureCardIDs(), gameBoard.getVictoryCardIDs(),
					gameBoard.getActionCardIDs()));
		} catch (SynchronisationException e) {
			GameLog.log(MsgType.GAME, "The card you wanted to buy is not on the board.");
		} catch (WrongSyntaxException e) {
			GameLog.log(MsgType.GAME, e.getMessage());
		}
	}

	private void nextActivePlayer(int port) {
		try {
			this.server.getGameController().organizePilesAndrefreshCardHand();
			server.sendMessage(port, new PacketSendHandCards(CollectionsUtil
					.getCardIDs(this.server.getGameController().getActivePlayer().getDeck().getCardHand())));
			Player player = this.server.getGameController().getActivePlayer();
			server.broadcastMessage(
					new PacketBroadcastLog(MsgType.GAME, " -- " + player.getPlayerName() + "'s TURN ENDED -- "));
			this.server.getGameController().endTurn();

			server.sendMessage(port, new PacketUpdateValues(player.getActions(), player.getBuys(), player.getCoins()));
			server.broadcastMessage(
					new PacketEnableDisable(this.server.getGameController().getActivePlayer().getClientID()));
			server.broadcastMessage(new PacketBroadcastLog(MsgType.GAME, " ++ "
					+ this.server.getGameController().getActivePlayer().getPlayerName() + "'s TURN STARTED ++ "));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updatePortOfPlayer(int port, PacketReconnect packetReconnect) {
		LinkedList<Player> disconnectedPlayers = this.server.getDisconnectedUser();
		for (Iterator<Player> iterator = disconnectedPlayers.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();

			if (packetReconnect.getUsername().equals(player.getPlayerName())) {
				player.setPort(port);
				player.updateSessionID(packetReconnect.getSessionID());
			}
		}
	}
	
	/**
	 * 
	 * @param port
	 * @param username
	 * @param sessionID
	 */
	private void addAIAndCheckPlayerCount(int port, String username, UUID sessionID) {	
		Player player = new Player(-1, port, this.server.getGameController().getGameBoard().getStartSet(), username, sessionID, this.server);
		try {
			server.getGameController().addPlayer(player);
		} catch (TooMuchPlayerException e) {
			System.err.println("Haha steffen");
			this.server.disconnect(port);
		}
		new ArtificialIntelligence(player, sessionID).start();
	}

	/**
	 * 
	 * @param port
	 * @param clientId
	 * @throws IOException
	 */
	private void addPlayerAndCheckPlayerCount(int port, int clientId, String username, UUID sessionID) throws IOException {
		try {
			Player player = new Player(clientId, port,
					this.server.getGameController().getGameBoard().getStartSet(), username, sessionID, this.server);
			server.getGameController().addPlayer(player);
			server.sendMessage(port, new PacketSendClientId(clientId));
			
			if (sessionID.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
				new ArtificialIntelligence(player, sessionID).start();
				System.out.println("created a new artificial intelligence");				
			}
			
			if (server.getGameController().getPlayers().size() == GameConstant.HUMAN_PLAYERS) {
				ChatController.getInstance().createChatRoom(this.server.getGameController().getPlayerNames());
				server.getGameController().startGame();
				setUpGui();
			}
			System.out.println("registrate one more client to server with id: " + clientId + "listening on port: " + port);
			
		} catch (TooMuchPlayerException tmpe) {
			server.sendMessage(port, new PacketClientShouldDisconect());
			tmpe.printStackTrace();
		}
	}

	/**
	 * opens the gui for all Players sends the board with all buyable cards to
	 * all guis and sends the handcards to all Players
	 * 
	 * @throws IOException
	 */
	private void setUpGui() throws IOException {

		GameBoard gameBoard = this.server.getGameController().getGameBoard();

		server.broadcastMessage(
				new PacketOpenGuiAndEnableOne(server.getGameController().getActivePlayer().getClientID()));

		server.broadcastMessage(new PacketSendBoard(gameBoard.getTreasureCardIDs(), gameBoard.getVictoryCardIDs(),
				gameBoard.getActionCardIDs()));

		LinkedList<Player> players = server.getGameController().getPlayers();
		for (int i = 0; i < GameConstant.HUMAN_PLAYERS; i++) {
			server.sendMessage(players.get(i).getPort(),
					new PacketSendHandCards(CollectionsUtil.getCardIDs(players.get(i).getDeck().getCardHand())));
		}

	}
}