package com.tpps.application.network.game;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import com.tpps.application.game.GameBoard;
import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.gameSession.packets.PacketBuyCard;
import com.tpps.application.network.gameSession.packets.PacketClientShouldDisconect;
import com.tpps.application.network.gameSession.packets.PacketDisable;
import com.tpps.application.network.gameSession.packets.PacketEnable;
import com.tpps.application.network.gameSession.packets.PacketEnableDisable;
import com.tpps.application.network.gameSession.packets.PacketEndActionPhase;
import com.tpps.application.network.gameSession.packets.PacketEndReactions;
import com.tpps.application.network.gameSession.packets.PacketOpenGuiAndEnableOne;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.application.network.gameSession.packets.PacketPutBackCards;
import com.tpps.application.network.gameSession.packets.PacketPutBackThiefCards;
import com.tpps.application.network.gameSession.packets.PacketReconnect;
import com.tpps.application.network.gameSession.packets.PacketRemoveExtraTable;
import com.tpps.application.network.gameSession.packets.PacketSendActiveButtons;
import com.tpps.application.network.gameSession.packets.PacketSendBoard;
import com.tpps.application.network.gameSession.packets.PacketSendClientId;
import com.tpps.application.network.gameSession.packets.PacketSendHandCards;
import com.tpps.application.network.gameSession.packets.PacketSendPlayedCardsToAllClients;
import com.tpps.application.network.gameSession.packets.PacketSendRevealCards;
import com.tpps.application.network.gameSession.packets.PacketTakeCards;
import com.tpps.application.network.gameSession.packets.PacketTakeThiefCards;
import com.tpps.application.network.gameSession.packets.PacketTemporaryTrashCards;
import com.tpps.application.network.gameSession.packets.PacketUpdateTreasures;
import com.tpps.application.network.gameSession.packets.PacketUpdateValues;
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

	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		ServerConnectionThread requester = parent.getClientThread(port);
		if (packet == null) {
			super.output("<- Empty Packet from (" + port + ")");
			return;
		}
		try {
			switch (packet.getType()) {
			case REGISTRATE_PLAYER_BY_SERVER:
				int clientId = GameServer.getCLIENT_ID();
				addPlayerAndCheckPlayerCount(port, clientId);
				break;
			case RECONNECT:
				updatePortOfPlayer(port, packet);
				break;
			case CARD_PLAYED:
				if (this.server.getGameController().isCardEnabled()){
				cardPlayed(port, packet);
				}else{
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
				Player player = GameServer.getInstance().getGameController().getClientById(clientID);
				player.takeRevealedCardsSetRevealModeFalse();
				GameBoard gameBoard = this.server.getGameController().getGameBoard();
				this.server.sendMessage(port, new PacketSendBoard(gameBoard.getTreasureCardIDs(),
						gameBoard.getVictoryCardIDs(), gameBoard.getActionCardIDs()));
				resetGameWindowAfterRevealAction(port, player);
				canActivePlayerContinue();
				break;
			case TAKE_THIEF_CARDS:
				takeThiefCards(port);
				this.server.getGameController().setCardsEnabled();
				break;
			case PUT_BACK_THIEF_CARDS:
				putBackThiefCards(port);
				this.server.getGameController().setCardsEnabled();
				break;
			case TEMPORARY_TRASH_CARDS:
				System.out.println("TemporaryTrashCards");
				clientID = ((PacketTemporaryTrashCards) packet).getClientID();
				player = GameServer.getInstance().getGameController().getClientById(clientID);

				break;
			case PUT_BACK:
				clientID = ((PacketPutBackCards) packet).getClientID();
				player = GameServer.getInstance().getGameController().getClientById(clientID);
				player.putBackCards();
				resetGameWindowAfterRevealAction(port, player);

				canActivePlayerContinue();
				break;
			case END_REACTIONS:
				Player player1 = this.server.getGameController()
						.getClientById(((PacketEndReactions) packet).getClientID());
				player1.setReactionCard(false);
				if (this.server.getGameController().getActivePlayer().isThief()) {
					player1.setReactionModeFalse();
					this.server.sendMessage(player1.getPort(), new PacketDisable());
					this.server.getGameController().checkReactionModeFinishedAndEnableGuis();
					this.server.getGameController().react(player1);
				}

				break;
			case DISCARD_DECK:
				this.server.getGameController().getActivePlayer().getDeck().discardDrawPile();

				break;
			default:
				System.out.println("unknown packed type");
				break;

			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}

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
			System.out.println("discardPile size davor: " +this.server.getGameController().getActivePlayer().getDeck().getDiscardPile().size());
			CollectionsUtil.appendListToList(player.getTemporaryTrashPile(),
					this.server.getGameController().getActivePlayer().getDeck().getDiscardPile());
			System.out.println("discardPile size danach: " +this.server.getGameController().getActivePlayer().getDeck().getDiscardPile().size());
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
				return;
			}
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
				this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(), new PacketRemoveExtraTable());
				if (!this.server.getGameController().getThiefList().isEmpty()) {
					System.out.println("new Reactive player");
					reactivePlayer = this.server.getGameController().getThiefList().get(0);
					
					this.server.sendMessage(this.server.getGameController().getActivePlayer().getPort(),
							new PacketSendRevealCards(CollectionsUtil.getCardIDs(reactivePlayer.getRevealList())));
				} else {
					System.out.println("is empty");
					LinkedList<Player> players = new LinkedList<Player>(this.server.getGameController().getPlayers());
					
					players.remove(this.server.getGameController().getActivePlayer());
					boolean thiefFlag = false;
					for (Iterator<Player> iterator = players.iterator(); iterator.hasNext();) {
						Player player2 = (Player) iterator.next();
						if (player2.isThief()) {
							thiefFlag = true;
							break;
						}
					}
					if (!thiefFlag) {
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

				server.broadcastMessage(
						new PacketSendBoard(this.server.getGameController().getGameBoard().getTreasureCardIDs(),
								this.server.getGameController().getGameBoard().getVictoryCardIDs(),

								this.server.getGameController().getGameBoard().getActionCardIDs()));

			}
			return;
		}

		if (this.server.getGameController().validateTurnAndExecute(cardID, player)) {
			System.out.println("validate turn: " + player.getActions() + "buys: " + player.getBuys() + "coins: "
					+ player.getCoins());

			server.sendMessage(port, new PacketUpdateValues(player.getActions(), player.getBuys(), player.getCoins()));
			if (player.getActions() == 0) {
				server.sendMessage(port, new PacketEndActionPhase());
			}
			server.sendMessage(port,
					new PacketSendHandCards(CollectionsUtil.getCardIDs(player.getDeck().getCardHand())));
			server.broadcastMessage(
					new PacketSendPlayedCardsToAllClients(CollectionsUtil.getCardIDs(player.getPlayedCards())));

		} else {
			try {
				if (this.server.getGameController().checkBoardCardExistsAppendToDiscardPile(cardID)) {
					GameBoard gameBoard = this.server.getGameController().getGameBoard();
					server.broadcastMessage(new PacketSendBoard(gameBoard.getTreasureCardIDs(),
							gameBoard.getVictoryCardIDs(), gameBoard.getActionCardIDs()));
					server.sendMessage(port,
							new PacketUpdateValues(player.getActions(), player.getBuys(), player.getCoins()));
					if (player.getBuys() == 0) {
						nextActivePlayer(port);
					}
				}
			} catch (SynchronisationException e) {
				e.printStackTrace();
			}
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
		this.server.sendMessage(port, new PacketDisable());
		this.server.sendMessage(port, new PacketRemoveExtraTable());

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
			e.printStackTrace();
		}
	}

	private void nextActivePlayer(int port) {
		try {
			this.server.getGameController().organizePilesAndrefreshCardHand();
			server.sendMessage(port, new PacketSendHandCards(CollectionsUtil
					.getCardIDs(this.server.getGameController().getActivePlayer().getDeck().getCardHand())));
			Player player = this.server.getGameController().getActivePlayer();

			this.server.getGameController().endTurn();
			System.out.println("server actions: " + player.getActions() + "buys: " + player.getBuys() + "coins: "
					+ player.getBuys());
			server.sendMessage(port, new PacketUpdateValues(player.getActions(), player.getBuys(), player.getCoins()));
			server.broadcastMessage(
					new PacketEnableDisable(this.server.getGameController().getActivePlayer().getClientID()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updatePortOfPlayer(int port, Packet packet) {
		for (int i = 0; i < GameConstant.HUMAN_PLAYERS; i++) {
			Player player = server.getGameController().getPlayers().get(i);
			if (player.getClientID() == ((PacketReconnect) packet).getClientId()) {
				player.setPort(port);
			}
		}
	}

	/**
	 * 
	 * @param port
	 * @param clientId
	 * @throws IOException
	 */
	private void addPlayerAndCheckPlayerCount(int port, int clientId) throws IOException {
		try {
			server.getGameController().addPlayer(
					new Player(clientId, port, this.server.getGameController().getGameBoard().getStartSet()));
			server.sendMessage(port, new PacketSendClientId(clientId));
			if (server.getGameController().getPlayers().size() == 4) {
				server.getGameController().startGame();
				setUpGui();

			}
			System.out.println(
					"registrate one more client to server with id: " + clientId + "listening on port: " + port);
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