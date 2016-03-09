package com.tpps.application.network.game;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import com.tpps.application.game.Deck;
import com.tpps.application.game.GameBoard;
import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.gameSession.packets.PacketBuyCard;
import com.tpps.application.network.gameSession.packets.PacketClientShouldDisconect;
import com.tpps.application.network.gameSession.packets.PacketEnableDisable;
import com.tpps.application.network.gameSession.packets.PacketOpenGuiAndEnableOne;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.application.network.gameSession.packets.PacketReconnect;
import com.tpps.application.network.gameSession.packets.PacketSendBoard;
import com.tpps.application.network.gameSession.packets.PacketSendClientId;
import com.tpps.application.network.gameSession.packets.PacketSendHandCards;
import com.tpps.application.network.gameSession.packets.PacketSendPlayedCardsToAllClients;
import com.tpps.application.network.gameSession.packets.PacketUpdateCoins;
import com.tpps.application.network.gameSession.packets.PacketUpdateValues;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;

/**
 * 
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
				String cardID = ((PacketPlayCard) packet).getCardID();
				System.out.println(server.getGameController().getGamePhase());

				if (this.server.getGameController().checkCardExistsAppendToPlayedCardList(cardID)) {
					Player activePlayer = this.server.getGameController().getActivePlayer();

					server.sendMessage(port, new PacketUpdateValues(activePlayer.getActions(), activePlayer.getBuys(),
							activePlayer.getCoins()));
					server.sendMessage(port, new PacketSendHandCards(CollectionsUtil.getCardIDs(this.server.getGameController().getActivePlayer().getDeck().getCardHand())));
					server.broadcastMessage(new PacketSendPlayedCardsToAllClients(
							CollectionsUtil.getCardIDs(this.server.getGameController().getPlayedCards())));
				}

				// server.sendMessage(port, new
				// PacketSendHandCards(activePlayer.getDeck().getCardHandIds()));
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
				server.broadcastMessage(new PacketSendPlayedCardsToAllClients(
						CollectionsUtil.getCardIDs(this.server.getGameController().getPlayedCards())));
				server.sendMessage(port,
						new PacketUpdateCoins(server.getGameController().getActivePlayer().getCoins()));
				break;
			case END_TURN:
				// alle Karten ablegen
				
				nextActivePlayer(port);

				break;
			default:
				System.out.println("unknown packed type");
				break;

			}
		} catch (IOException ie) {
			ie.printStackTrace();
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
			System.out.println("Karten zum client geschickt: " + this.server.getGameController().getActivePlayer().getDeck().getCardHand().size());
			
			this.server.getGameController().endTurn();
			Deck deck = this.server.getGameController().getActivePlayer().getDeck();
			System.out.println("DiscardPile: " + Arrays.toString(deck.getDiscardPile().toArray()));
			System.out.println("DrawPile: " + Arrays.toString(deck.getDrawPile().toArray()));
			System.out.println("Hand: " + Arrays.toString(deck.getCardHand().toArray()));
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
			server.getGameController().addPlayer(new Player(clientId, port));
			server.sendMessage(port, new PacketSendClientId(clientId));
			if (server.getGameController().getPlayers().size() == 4) {
				server.getGameController().startGame();
				setUpGui();
				System.out.println("gameStarted");
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
		System.out.println("Wie viel actions: " + gameBoard.getActionCardIDs());
		server.broadcastMessage(new PacketSendBoard(gameBoard.getTreasureCardIDs(), gameBoard.getVictoryCardIDs(),
				gameBoard.getActionCardIDs()));

		LinkedList<Player> players = server.getGameController().getPlayers();
		for (int i = 0; i < GameConstant.HUMAN_PLAYERS; i++) {
			server.sendMessage(players.get(i).getPort(),
					new PacketSendHandCards(CollectionsUtil.getCardIDs(players.get(i).getDeck().getCardHand())));
		}

	}
}
