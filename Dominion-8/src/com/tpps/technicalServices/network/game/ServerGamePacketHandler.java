package com.tpps.technicalServices.network.game;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.game.CardName;
import com.tpps.application.game.GameBoard;
import com.tpps.application.game.GameConstant;
import com.tpps.application.game.Player;
import com.tpps.application.game.ai.ArtificialIntelligence;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.chat.packets.PacketVotekick;
import com.tpps.technicalServices.network.chat.server.ChatController;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLog;
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
import com.tpps.technicalServices.network.gameSession.packets.PacketUpdateValuesChangeButtons;
import com.tpps.technicalServices.util.CollectionsUtil;

import javafx.util.Pair;

/**
 * @author ladler - Lukas Adler
 */
public class ServerGamePacketHandler extends PacketHandler {
	private GameServer server;
	ChatController chatController;
	private ConcurrentHashMap<String, Color> colorMap;

	/**
	 * 
	 * @param server the server to set
	 */
	public void setServer(GameServer server) {
		this.server = server;
		chatController = new ChatController(this, this.server.getPort());
		this.colorMap = new ConcurrentHashMap<String, Color>();
	}

	/**
	 * 
	 * @return the color of the active Player (in chat)
	 */
	public Color getActivePlayerColor() {
		return this.colorMap.get(this.server.getGameController().getActivePlayerName());
	}

	/**
	 * checks which packet was sent and reacts on the packet
	 */
	@Override
	public synchronized void handleReceivedPacket(int port, Packet packet) {
		// ServerConnectionThread requester = parent.getClientThread(port);
		if (packet == null) {
			super.output("<- Empty Packet from (" + port + ")");
			return;
		}
		boolean skipflag = false;
		Player refActivePlayer = this.server.getGameController().getActivePlayer();

		try {
			switch (packet.getType()) {				
			case REGISTRATE_PLAYER_BY_SERVER:
				int clientId = GameServer.getCLIENT_ID();
				GameLog.log(MsgType.MM ,"clientId: " + clientId);
				PacketRegistratePlayerByServer packetRegistratePlayerByServer = (PacketRegistratePlayerByServer) packet;

				if (packetRegistratePlayerByServer.getSessionID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
					GameLog.log(MsgType.INFO , "Username: " + packetRegistratePlayerByServer.getUsername());

					addPlayerAndCheckPlayerCount(this.server.getGameController().getAiPort(), clientId, packetRegistratePlayerByServer.getUsername(), packetRegistratePlayerByServer.getSessionID());
				} else if (this.server.validSession(packetRegistratePlayerByServer.getUsername(), packetRegistratePlayerByServer.getSessionID())) {
					GameLog.log(MsgType.MM ,"Connect valid Session username: " + packetRegistratePlayerByServer.getUsername() + "sessionID: " + packetRegistratePlayerByServer.getSessionID());
					addPlayerAndCheckPlayerCount(port, clientId, packetRegistratePlayerByServer.getUsername(), packetRegistratePlayerByServer.getSessionID());
				} else {
					this.server.disconnect(port);
				}
				return;
			case RECONNECT:
				PacketReconnect packetReconnect = (PacketReconnect) packet;
				if (this.server.validSession(packetReconnect.getUsername(), packetReconnect.getSessionID())) {
					GameLog.log(MsgType.MM ,"Reconnect valid Session username: " + packetReconnect.getUsername() + "sessionID: " + packetReconnect.getSessionID());
					updatePortOfPlayer(port, packetReconnect);
					this.server.getDisconnectedUser().remove(this.server.getGameController().getPlayerByUserName(packetReconnect.getUsername()));
					server.broadcastMessage(new PacketEnableDisable(this.server.getGameController().getActivePlayer().getClientID(), this.server.getGameController().getActivePlayerName(), false));
				} else {
					this.server.disconnect(port);
				}
				return;
			case VOTEKICK:
				voteKick(port, packet);				
				return;	//oder doch break ?
			case CARD_PLAYED:
				if (this.server.getGameController().getPlayerByPort(port).getPlayerName().equals(this.server.getGameController().getActivePlayerName())
						|| this.server.getGameController().getPlayerByPort(port).getDeck().getCardFromHand(((PacketPlayCard) packet).getCardID()) != null) {
					if (this.server.getGameController().isCardsEnabled()) {
						// if
						// (this.server.getGameController().getActivePlayer().equals(this.server.getGameController().getPlayerByPort(port))){
						// skipflag = true;
						// }
						if (this.server.getGameController().getActivePlayer().isPlayTwiceEnabled()) {
							if (!this.server.getGameController().getActivePlayer().getDeck().getCardFromHand(((PacketPlayCard) packet).getCardID()).getTypes().contains(CardType.ACTION)) {
								skipflag = true;
							}
						}
						cardPlayed(port, packet);
					} else {
						GameLog.log(MsgType.GAME_INFO ,"no cards enabled");
					}
				} else {
					this.server.sendMessage(port, new PacketEnable("react"));
					return;
				}
				break;
			case BUY_CARD:
				buyCardAndUpdateBoards(packet);
				break;
			case END_ACTION_PHASE:
				if (this.server.getGameController().getPlayerByPort(port).getPlayerName().equals(this.server.getGameController().getActivePlayerName())) {
					this.server.getGameController().getActivePlayer().endActionPhase();
					this.server.getGameController().setBuyPhase();
				} else {
					this.server.sendMessage(port, new PacketEnable("react"));
					this.server.sendMessage(port, new PacketSendActiveButtons(true, true, false));
					return;
				}
				break;
			case PLAY_TREASURES:
				if (this.server.getGameController().getPlayerByPort(port).getPlayerName().equals(this.server.getGameController().getActivePlayerName())) {
					this.server.getGameController().playTreasures();
					this.server.sendMessage(port, new PacketSendHandCards(CollectionsUtil.getCardIDs(this.server.getGameController().getActivePlayer().getDeck().getCardHand())));
					this.server.broadcastMessage(new PacketSendPlayedCardsToAllClients(CollectionsUtil.getCardIDs(this.server.getGameController().getActivePlayer().getPlayedCards())));
					this.server.sendMessage(port, new PacketUpdateTreasures(server.getGameController().getActivePlayer().getCoins()));
				} else {
					this.server.sendMessage(port, new PacketEnable("react"));
					return;
				}
				break;
			case END_TURN:
				if (this.server.getGameController().getPlayerByPort(port).getPlayerName().equals(this.server.getGameController().getActivePlayerName())) {
					this.nextActivePlayer(port);
				} else {
					this.server.sendMessage(port, new PacketEnable("react"));
					return;
				}
				break;
			case END_DISCARD_MODE:
				this.server.getGameController().getActivePlayer().endDiscardAndDrawMode();

				PacketSendHandCards packetSendHandCards = new PacketSendHandCards(CollectionsUtil.getCardIDs(this.server.getGameController().getActivePlayer().getDeck().getCardHand()));
				if (this.server.getGameController().getActivePlayer().getPlayTwiceCard() == null && !this.server.getGameController().getActivePlayer().isDiscardMode()
						&& !this.server.getGameController().getActivePlayer().isTrashMode()) {
					if (this.server.getGameController().getActivePlayer().getActions() > 0) {
						packetSendHandCards.setChangeButtons("action");
					} else {
						packetSendHandCards.setChangeButtons("playTreasures");
					}
				}

				this.server.sendMessage(port, packetSendHandCards);
				break;
			case END_TRASH_MODE:
				this.server.getGameController().getActivePlayer().endTrashMode();

				PacketSendActiveButtons packetSendActiveButtons;
				if (this.server.getGameController().getActivePlayer().getPlayTwiceCard() == null && !this.server.getGameController().getActivePlayer().isDiscardMode()
						&& !this.server.getGameController().getActivePlayer().isTrashMode()) {
					if (this.server.getGameController().getActivePlayer().getActions() > 0) {
						packetSendActiveButtons = new PacketSendActiveButtons(true, true, false);
					} else {
						packetSendActiveButtons = new PacketSendActiveButtons(true, false, true);
					}
					server.sendMessage(port, packetSendActiveButtons);
				}

				break;
			case TAKE_CARDS:
				int clientID = ((PacketTakeCards) packet).getClientID();
				Player reactivePlayer = this.server.getGameController().getSpyList().get(0);
				Player player = server.getGameController().getClientById(clientID);
				reactivePlayer.takeRevealedCardsSetRevealModeFalse();
				reactivePlayer.setSpyFalse();
				this.server.getGameController().getSpyList().remove(reactivePlayer);
				GameLog.log(MsgType.GAME_INFO ,"spyList size take cards: " + this.server.getGameController().getSpyList().size());
				// this.server.sendMessage(port, new PacketRemoveExtraTable());
				if (!this.server.getGameController().getSpyList().isEmpty()) {
					try {
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
								new PacketSendRevealCards(CollectionsUtil.getCardIDs(this.server.getGameController().getSpyList().get(0).getRevealList())));
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketTakeCards(this.server.getGameController().getActivePlayer().getClientID()));
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketPutBackCards(this.server.getGameController().getActivePlayer().getClientID()));
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
				// this.server.sendMessage(port, new PacketRemoveExtraTable());
				if (!this.server.getGameController().getSpyList().isEmpty()) {
					try {
						server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketTakeCards(this.server.getGameController().getActivePlayer().getClientID()));
						server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketPutBackCards(this.server.getGameController().getActivePlayer().getClientID()));
						server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
								new PacketSendRevealCards(CollectionsUtil.getCardIDs(this.server.getGameController().getSpyList().get(0).getRevealList())));
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
				GameLog.log(MsgType.GAME_INFO ,"TemporaryTrashCards");
				clientID = ((PacketTemporaryTrashCards) packet).getClientID();
				player = server.getGameController().getClientById(clientID);
				break;
			case END_REACTIONS:
				endReactions(port, packet);
				break;
			case DISCARD_DECK:
				this.server.getGameController().getActivePlayer().getDeck().discardDrawPile();
				break;
			case BROADCAST_LOG:
				this.server.broadcastMessage(packet);
				break;
			case BROADCAST_LOG_MULTI_COLOR:
				this.server.broadcastMessage(packet);
				break;
			default:
				GameLog.log(MsgType.PACKET ,"unknown packed type");
				break;
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		Player playTwiceActivePlayer = this.server.getGameController().getActivePlayer();
		if (this.server.getGameController().getActivePlayer() != null && this.server.getGameController().getActivePlayer().isPlayTwice()
				&& this.server.getGameController().allReactionCardsPlayed() && this.server.getGameController().allPlayerDiscarded() && this.server.getGameController().allPlayerTrashed()
				&& this.server.getGameController().allPlayerGained() && this.server.getGameController().allPlayersRevealed() && this.server.getGameController().allReveaListsEmpty()
				&& this.server.getGameController().getSpyList().isEmpty() && this.server.getGameController().allTemporaryTrashPilesEmpty() && !playTwiceActivePlayer.isWitch()
				&& !playTwiceActivePlayer.isBureaucrat() && playTwiceActivePlayer.getPlayTwiceCard() != null) {

			GameLog.log(MsgType.GAME_INFO ,"playTwice");
			playTwiceActivePlayer.setPlayTwiceFalse();
			playTwiceActivePlayer.setPlayTwiceEnabled();
			playTwiceActivePlayer.decrementPlayTwiceCounter();
			if (playTwiceActivePlayer.getPlayTwiceCounter() == 0) {
				playTwiceActivePlayer.setPlayTwiceEnabledFalse();
			}

			playTwiceActivePlayer.setSecondTimePlayed();

			GameLog.log(MsgType.GAME_INFO ,"play twice card: " + playTwiceActivePlayer.getPlayTwiceCard().getId());
			try {
				new Thread(() -> {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handleReceivedPacket(playTwiceActivePlayer.getPort(), new PacketPlayCard(playTwiceActivePlayer.getPlayTwiceCard().getId(), playTwiceActivePlayer.getClientID()));
				}).start();
			} catch (NullPointerException e) {
				System.err.println("this should not happen.");
			}
		} else {
			if (refActivePlayer != null && this.server.getGameController().getPlayerByPort(port).getPlayerName().equals(this.server.getGameController().getActivePlayer().getPlayerName())
					&& refActivePlayer.getPlayerName().equals(this.server.getGameController().getActivePlayer().getPlayerName())) {
				try {
					if (this.server.getGameController().allReactionCardsPlayed()) {
						GameLog.log(MsgType.INFO ,"enable the aktive player again");
						if (this.server.getGameController().getActivePlayer().getPlayTwiceCard() == null || !this.server.getGameController().getActivePlayer().getPlayTwiceCard().equals(CardName.MILITIA.getName()))
							this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketEnable("my turn"));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("player: ");
			} else if (this.server.getGameController().getPlayerByPort(port).isReactionMode() && this.server.getGameController().getPlayerByPort(port).isDiscardMode()) {
				try {
					this.server.sendMessage(port, new PacketEnable("react"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (!skipflag) {
				if (this.server.getGameController().getActivePlayer() != null && this.server.getGameController().getActivePlayer().isPlayTwiceEnabled()) {
					GameLog.log(MsgType.GAME_INFO ,"richtige karte gespielt");
					this.server.getGameController().getActivePlayer().setPlayTwice();
					this.server.getGameController().getActivePlayer().setPlayTwiceEnabledFalse();
				}
			}
		}
	}

	private void voteKick(int port, Packet packet) throws IOException {
		// ----------------------
		// Votekick logik für ingame,
		// zu diesem Zeitpunkt, ist der
		// user schon aus dem chatroom raus				
		// ----------------------
		
		PacketVotekick votekickPacket = (PacketVotekick) packet;
		if (this.server.getGameController().getPlayerByUserName(votekickPacket.getUser()) != null) {
			GameLog.log(MsgType.INFO, "This gameserver " + this.server.getPort() + " received a votekickpacket");
			
			if (votekickPacket.getUser().equals(this.server.getGameController().getActivePlayerName())) {				
				this.server.getGameController().setNextActivePlayer();
				System.out.println("new active Player: " + this.server.getGameController().getActivePlayer().getPlayerName());
				this.server.broadcastMessage(this.server.getGameController().getPlayerByUserName(votekickPacket.getUser()).getPort(),
				new PacketEnableDisable(this.server.getGameController().getActivePlayer().getClientID(),
						this.server.getGameController().getActivePlayerName(), true));
			}
			
			GameLog.log(MsgType.INFO, "The User gets kicked: " + votekickPacket.getUser());
			Player kickedPlayer = this.server.getGameController().getPlayerByUserName(votekickPacket.getUser());
			this.server.getGameController().getPlayers()
					.remove(kickedPlayer);
			System.out.println("größe: " + this.server.getGameController().getPlayers().size());
			
			this.server.sendMessage(kickedPlayer.getPort(), votekickPacket);
			
			for (Iterator<Player> iterator = this.server.getGameController().getPlayers().iterator(); iterator
					.hasNext();) {
				Player player = (Player) iterator.next();
				player.setAllModesFalse();
				this.server.getGameController().resetThiefList();
				this.server.getGameController().resetSpyList();				
			}
		}
	}

/**	
	 * executes the action when the end reaction button is pressed
	 * 
	 * @param port
	 * @param packet
	 * @throws IOException
	 */
	private void endReactions(int port, Packet packet) throws IOException {
		Player player1 = this.server.getGameController().getClientById(((PacketEndReactions) packet).getClientID());
		player1.setReactionCard(false);
		reactionFinishedTriggeredThroughThief(player1);
		reactionFinishedTriggerdThroughSpy(player1);
		reactionFinishedTriggeredThroughWitch(player1);
		reactionFinishedTriggeredThroughBureaucrat(player1);

		if (player1.isReactionMode()) {
			if (player1.getDeck().getCardHand().size() <= 3) {
				player1.setReactionModeFalse();

				boolean allReactionCarsPlayedFlag = this.server.getGameController().allReactionCardsPlayed();

				if (allReactionCarsPlayedFlag) {
					// this.server.sendMessage(port,
					// new
					// PacketDisable(this.server.getGameController().getActivePlayerName()
					// + "'s turn"));
					this.server.getGameController().checkReactionModeFinishedAndEnableGuis();
				} else {
					this.server.sendMessage(port, new PacketDisable("wait on reaction"));
				}
				// this.server.getGameController().checkReactionModeFinishedAndEnableGuis();
			}
		}
		this.server.getGameController().isGameFinished();
	}

	private void reactionFinishedTriggeredThroughThief(Player player1) throws IOException {
		if (this.server.getGameController().getActivePlayer().isThief()) {
			player1.setReactionModeFalse();
			this.server.sendMessage(player1.getPort(), new PacketDisable("wait on reaction"));

			this.server.getGameController().reactOnThief(player1);
			this.server.getGameController().checkReactionModeFinishedAndEnableGuis();
		}
	}

	private void reactionFinishedTriggerdThroughSpy(Player player1) throws IOException {
		if (this.server.getGameController().getActivePlayer().isSpy()) {
			player1.setReactionModeFalse();

			boolean allReactionCarsPlayedFlag = this.server.getGameController().allReactionCardsPlayed();

			if (allReactionCarsPlayedFlag) {
				// this.server.sendMessage(player1.getPort(), new PacketDisable(
				// this.server.getGameController().getActivePlayerName() + "'s
				// turn"));
				this.server.getGameController().checkReactionModeFinishedAndEnableGuis();
			} else {
				this.server.sendMessage(player1.getPort(), new PacketDisable("wait on reaction"));
			}

			this.server.getGameController().reactOnSpy(player1);

		}

	}

	private void reactionFinishedTriggeredThroughWitch(Player player1) throws IOException {
		if (this.server.getGameController().getActivePlayer().isWitch()) {
			player1.setReactionModeFalse();
			this.server.sendMessage(player1.getPort(), new PacketDisable("wait on reaction"));
			try {
				player1.getDeck().getDiscardPile().add(this.server.getGameController().getGameBoard().getTableForVictoryCards().get(CardName.CURSE.getName()).removeLast());

				server.broadcastMessage(new PacketSendBoard(this.server.getGameController().getGameBoard().getTreasureCardIDs(), this.server.getGameController().getGameBoard().getVictoryCardIDs(),
						this.server.getGameController().getGameBoard().getActionCardIDs()));
			} catch (NoSuchElementException e) {
				GameLog.log(MsgType.EXCEPTION, "Not enough curses.\n");
			}
			this.server.getGameController().checkReactionModeFinishedAndEnableGuis();
		}
	}

	private void reactionFinishedTriggeredThroughBureaucrat(Player player) throws IOException {
		if (this.server.getGameController().getActivePlayer().isBureaucrat()) {
			player.setReactionModeFalse();
			this.server.sendMessage(player.getPort(), new PacketDisable("wait on reaction"));

			Card card = player.getDeck().getCardByTypeFromHand(CardType.VICTORY);
			if (card != null) {
				player.getDeck().getCardHand().remove(card);
				player.getDeck().getDrawPile().addLast(card);
				try {
					server.sendMessage(player.getPort(), new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.err.println("no victory card on hand");
			}
			server.broadcastMessage(new PacketSendBoard(this.server.getGameController().getGameBoard().getTreasureCardIDs(), this.server.getGameController().getGameBoard().getVictoryCardIDs(),
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
		GameLog.log(MsgType.GAME_INFO ,"take drawed card server gamePackethandler");
		Player activePlayer = this.server.getGameController().getActivePlayer();
		activePlayer.drawUntil();
	}

	private void putBackThiefCards(int port) {
		LinkedList<Player> players = this.server.getGameController().getPlayers();
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			GameLog.log(MsgType.GAME_INFO ,"discardPile size davor: " + player.getDeck().getDiscardPile().size());
			CollectionsUtil.appendListToList(player.getTemporaryTrashPile(), player.getDeck().getDiscardPile());
			GameLog.log(MsgType.GAME_INFO ,"discardPile size danach: " + player.getDeck().getDiscardPile().size());
			player.resetTemporaryTrashPile();
		}
		// try {
		// this.server.sendMessage(port, new PacketRemoveExtraTable());
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	private void takeThiefCards(int port) {
		GameLog.log(MsgType.GAME_INFO ,"takeThiefCards");
		LinkedList<Player> players = this.server.getGameController().getPlayers();
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			GameLog.log(MsgType.GAME_INFO ,"discardPile size davor: " + this.server.getGameController().getActivePlayer().getDeck().getDiscardPile().size());
			CollectionsUtil.appendListToList(player.getTemporaryTrashPile(), this.server.getGameController().getActivePlayer().getDeck().getDiscardPile());
			GameLog.log(MsgType.GAME_INFO ,"discardPile size danach: " + this.server.getGameController().getActivePlayer().getDeck().getDiscardPile().size());
			player.resetTemporaryTrashPile();
		}
		// try {
		// this.server.sendMessage(port, new PacketRemoveExtraTable());
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	private void cardPlayed(int port, Packet packet) throws IOException {
		String cardID = ((PacketPlayCard) packet).getCardID();
		int clientID = ((PacketPlayCard) packet).getClientID();
		GameLog.log(MsgType.GAME_INFO ,server.getGameController().getGamePhase());

		Player player = this.server.getGameController().getClientById(clientID);

		if (!player.playsReactionCard() && (player.isDiscardMode() || player.isTrashMode())) {
			GameLog.log(MsgType.GAME_INFO ,"im handler discard mode set");
			if (this.server.getGameController().checkCardExistsAndDiscardOrTrash(player, cardID)) {
				PacketSendHandCards packetSendHandCards = new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand()));
				if (player.getPlayTwiceCard() == null && !player.isDiscardMode() && !player.isTrashMode() && !player.isGainMode()) {
					if (player.getActions() > 0) {
						packetSendHandCards.setChangeButtons("action");
					} else {
						packetSendHandCards.setChangeButtons("playTreasures");
					}
				}
				server.sendMessage(port, packetSendHandCards);
			}
			return;
		}

		if (this.server.getGameController().isVictoryCardOnHand(cardID) && !player.isDiscardMode() && !player.isTrashMode()
				|| player.isReactionMode() && !player.getDeck().getCardFromHand(cardID).getTypes().contains(CardType.REACTION) || player.isRevealMode()) {
			GameLog.log(MsgType.GAME_INFO ,"nur returnen");
			return;
		}

		if (player.isThief()) {
			Player reactivePlayer = this.server.getGameController().getThiefList().get(0);
			GameLog.log(MsgType.GAME_INFO ,"size: " + this.server.getGameController().getThiefList().size());

			if (CollectionsUtil.getCardIDs(reactivePlayer.getRevealList()).contains(cardID)) {
				reactivePlayer.getTemporaryTrashPile().add(CollectionsUtil.removeCardById(reactivePlayer.getRevealList(), cardID));
				CollectionsUtil.appendListToList(reactivePlayer.getRevealList(), reactivePlayer.getDeck().getDiscardPile());
				this.server.getGameController().getThiefList().remove(reactivePlayer);
				reactivePlayer.resetThiefMode();
				// this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
				// new PacketRemoveExtraTable());
				if (!this.server.getGameController().getThiefList().isEmpty()) {
					GameLog.log(MsgType.GAME_INFO ,"new Reactive player");
					reactivePlayer = this.server.getGameController().getThiefList().get(0);

					this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketSendRevealCards(CollectionsUtil.getCardIDs(reactivePlayer.getRevealList())));
				} else {
					GameLog.log(MsgType.GAME_INFO ,"is empty");
					LinkedList<Player> players = new LinkedList<Player>(this.server.getGameController().getPlayers());

					players.remove(this.server.getGameController().getActivePlayer());
					boolean thiefFlag = this.server.getGameController().checkThiefFinish();

					if (thiefFlag) {
						GameLog.log(MsgType.GAME_INFO ,"alle karten schicken");
						this.server.getGameController().setCardsDisabled();
						this.server.getGameController().getActivePlayer().setThiefFalse();
						LinkedList<Card> allThiefCards = new LinkedList<Card>();
						for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
							Player player2 = (Player) iterator.next();
							CollectionsUtil.appendListToList(player2.getTemporaryTrashPile(), allThiefCards);
						}
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketSendRevealCards(CollectionsUtil.getCardIDs(allThiefCards)));
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketTakeThiefCards());
						this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketPutBackThiefCards());
						player.setThiefFalse();
						this.server.getGameController().resetThiefList();
					}
				}
			}
			return;
		}
		if (player.isGainMode()) {
			if (this.server.getGameController().gain(cardID, player)) {
				this.server.broadcastMessage(new PacketSendBoard(this.server.getGameController().getGameBoard().getTreasureCardIDs(),
						this.server.getGameController().getGameBoard().getVictoryCardIDs(), this.server.getGameController().getGameBoard().getActionCardIDs()));
				if (player.getPlayTwiceCard() == null) {
					if (player.getActions() > 0) {
						this.server.sendMessage(port, new PacketSendActiveButtons(true, true, false));
					} else {
						this.server.sendMessage(port, new PacketSendActiveButtons(true, false, true));
					}
				}
				this.server.getGameController().isGameFinished();
			}
			return;
		}
		if (this.server.getGameController().validateTurnAndExecute(cardID, player)) {
			afterCardWasPlayed(port, player);
		} else {
			try {
				if (this.server.getGameController().checkBoardCardExistsAppendToDiscardPile(cardID)) {
					GameBoard gameBoard = this.server.getGameController().getGameBoard();
					this.server.broadcastMessage(new PacketSendBoard(gameBoard.getTreasureCardIDs(), gameBoard.getVictoryCardIDs(), gameBoard.getActionCardIDs()));
					this.server.sendMessage(port, new PacketUpdateValuesChangeButtons(player.getActions(), player.getBuys(), player.getCoins(), ""));
					if (player.getBuys() == 0) {
						nextActivePlayer(port);
					}
				}
			} catch (SynchronisationException | NoSuchElementException e) {
				GameLog.log(MsgType.EXCEPTION, "The card you wanted to buy is not on the board.");
			} catch (WrongSyntaxException e) {
				GameLog.log(MsgType.EXCEPTION, e.getMessage());
			}
			this.server.getGameController().isGameFinished();
			return;
		}
	}

	private void afterCardWasPlayed(int port, Player player) throws IOException {
		GameLog.log(MsgType.GAME_INFO ,"validate turn: " + player.getActions() + "buys: " + player.getBuys() + "coins: " + player.getCoins());

//		if (player.getActions() == 0 && !player.isThief()) {
//			server.sendMessage(port, new PacketEndActionPhase());
//		}
		if (this.server.getGameController().getActivePlayer().getPlayTwiceCard() == null || !this.server.getGameController().getActivePlayer().getPlayTwiceCard().getName().equals(CardName.WITCH.getName())) {
			this.server.sendMessage(port, new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand())));
		}

		if (this.server.getGameController().getPlayerByPort(port).getPlayerName().equals(this.server.getGameController().getActivePlayer().getPlayerName())) {
			this.server.broadcastMessage(new PacketSendPlayedCardsToAllClients(CollectionsUtil.getCardIDs(player.getPlayedCards())));
		}

		String changeButtons = "";
		if (player.getPlayerName().equals(this.server.getGameController().getActivePlayer().getPlayerName())) {

			if (this.server.getGameController().getActivePlayer().isDiscardMode() || this.server.getGameController().getActivePlayer().isTrashMode()
					|| this.server.getGameController().getActivePlayer().isGainMode()) {
				GameLog.log(MsgType.GAME_INFO ,"remove");
				changeButtons = "remove";
			} else {
				if (this.server.getGameController().getActivePlayer().getPlayTwiceCard() == null) {
					if (!this.server.getGameController().getActivePlayer().isSpy() && !this.server.getGameController().getActivePlayer().isThief())
						if (this.server.getGameController().getActivePlayer().getActions() > 0 && this.server.getGameController().getGamePhase().equals("actionPhase")) {
							GameLog.log(MsgType.GAME_INFO ,"actions");
							changeButtons = "actions";
						} else {
							GameLog.log(MsgType.GAME_INFO ,"playtreasures");
							changeButtons = "playTreasures";
						}
				}
			}

		}
		this.server.sendMessage(port, new PacketUpdateValuesChangeButtons(player.getActions(), player.getBuys(), player.getCoins(), changeButtons));
		this.server.getGameController().isGameFinished();
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
		this.server.sendMessage(port, new PacketRemoveExtraTable());
	}

	@SuppressWarnings("unused")
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
			server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketEnable("react"));
		}
	}

	private void buyCardAndUpdateBoards(Packet packet) throws IOException {
		try {
			GameBoard gameBoard = this.server.getGameController().getGameBoard();
			// this.server.getGameController().buyOneCard(((PacketBuyCard)
			// packet).getCardId());
			this.server.getGameController().buyOneCard(((PacketPlayCard) packet).getCardID());
			this.server.broadcastMessage(new PacketSendBoard(gameBoard.getTreasureCardIDs(), gameBoard.getVictoryCardIDs(), gameBoard.getActionCardIDs()));
		} catch (SynchronisationException e) {
			GameLog.log(MsgType.EXCEPTION, "The card you wanted to buy is not on the board:\n" + e.getMessage());
		} catch (WrongSyntaxException e) {
			GameLog.log(MsgType.EXCEPTION, e.getMessage());
		}
	}

	private void nextActivePlayer(int port) {
		try {
			GameLog.log(MsgType.DEBUG, "ich bin in nextActivePlayer");
			this.server.getGameController().organizePilesAndrefreshCardHand();
			this.server.sendMessage(port, new PacketSendHandCards(CollectionsUtil.getCardIDs(this.server.getGameController().getActivePlayer().getDeck().getCardHand())));
			// i think it's not used
			// this.server.sendMessage(port, new
			// PacketUpdateValues(this.server.getGameController().getActivePlayer().getActions(),
			// this.server.getGameController().getActivePlayer().getBuys(),
			// this.server.getGameController().getActivePlayer().getCoins()));
			this.server.getGameController().endTurn();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// this.server.broadcastMessage(
			// new PacketBroadcastLog("-----
			// ",this.server.getGameController().getActivePlayerName(),": turn "
			// + this.server.getGameController().getActivePlayer().getTurnNr() +
			// " -----",this.getActivePlayerColor()));
			this.server.broadcastMessage(new PacketBroadcastLog("----- ", this.server.getGameController().getActivePlayerName(),
					": turn " + this.server.getGameController().getActivePlayer().getTurnNr() + " -----", this.server.getGameController().getActivePlayer().getLogColor()));
			this.server.broadcastMessage(new PacketEnableDisable(this.server.getGameController().getActivePlayer().getClientID(), this.server.getGameController().getActivePlayerName(), true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * log the prepped text of GameLog to the logPanel
	 */
	public void logPrepText() {
		for (Integer i : GameLog.getPrepText().keySet()) {
			Pair<String, Color> res = GameLog.getPrepText().get(i);
			Color c = res.getValue();
			String s = res.getKey();
			try {
				this.server.broadcastMessage(new PacketBroadcastLog("", "", s, c));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		GameLog.resetPrepText();
	}

	/**
	 * 
	 * @param port
	 * @param packetReconnect
	 */
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
	 * @param clientId
	 * @throws IOException
	 */
	private void addPlayerAndCheckPlayerCount(int port, int clientId, String username, UUID sessionID) throws IOException {
		try {
			Player player = new Player(clientId, port, this.server.getGameController().getGameBoard().getStartSet(), username, sessionID, this.server);
			this.server.getGameController().addPlayerAndChooseRandomActivePlayer(player);
			this.server.sendMessage(port, new PacketSendClientId(clientId));
			if (sessionID.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
				new ArtificialIntelligence(player,
						/*
						 * new InetSocketAddress("127.0.0.1" ,
						 * this.server.getPort()),
						 */sessionID, this).start();
				this.colorMap.put(username, new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
				GameLog.log(MsgType.AI ,"created a new artificial intelligence");
			}
			if (server.getGameController().getPlayers().size() == GameConstant.PLAYERS.getValue()) {
				// TODO: connect chatroom correctly "without AI"
				LinkedList<Player> serverPlayers = this.server.getGameController().getPlayers();
				ArrayList<String> chatPlayers = new ArrayList<String>();
				for (Iterator<Player> iterator = serverPlayers.iterator(); iterator.hasNext();) {
					Player temp = iterator.next();
					if (!temp.getSessionID().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
						chatPlayers.add(temp.getPlayerName());
					}
				}
				GameLog.log(MsgType.CHAT ,"chatplayers: " + chatPlayers);
				this.chatController.createChatRoom(chatPlayers);

				// this.server.getGameController().startGame();
				// setUpGui();
			}
			GameLog.log(MsgType.MM ,"registrate one more client to server with id: " + player.getClientID() + "listening on port: " + player.getPort());
		} catch (TooMuchPlayerException tmpe) {
			this.server.sendMessage(port, new PacketClientShouldDisconect());
			tmpe.printStackTrace();
		}
	}

	/**
	 * 
	 * 
	 * @author jhuhn
	 * @param colorMap
	 *            user colors
	 */
	public void startGame(HashMap<String, Color> colorMap) {
		this.colorMap.putAll(colorMap);
		server.getGameController().startGame();
		try {
			setUpGui();
		} catch (IOException e) {
			e.printStackTrace();
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
		this.server.broadcastMessage(new PacketOpenGuiAndEnableOne(this.server.getGameController().getActivePlayer().getClientID(), this.server.getGameController().getActivePlayerName()));
		this.server.broadcastMessage(new PacketSendBoard(gameBoard.getTreasureCardIDs(), gameBoard.getVictoryCardIDs(), gameBoard.getActionCardIDs()));
		LinkedList<Player> players = this.server.getGameController().getPlayers();
		for (int i = 0; i < GameConstant.PLAYERS.getValue(); i++) {
			this.server.sendMessage(players.get(i).getPort(), new PacketSendHandCards(CollectionsUtil.getCardIDs(players.get(i).getDeck().getCardHand())));
		}
		this.logPrepText();
	}

	/**
	 * @return the chancontroller instance to create or delete chatrooms
	 */
	public ChatController getChatController() {
		return chatController;
	}
}
