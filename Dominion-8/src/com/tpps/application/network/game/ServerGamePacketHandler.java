package com.tpps.application.network.game;

import java.io.IOException;
import java.util.LinkedList;

import com.tpps.application.game.Player;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.gameSession.packets.PacketClientShouldDisconect;
import com.tpps.application.network.gameSession.packets.PacketEnableDisable;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.application.network.gameSession.packets.PacketPlayTreasures;
import com.tpps.application.network.gameSession.packets.PacketReconnect;
import com.tpps.application.network.gameSession.packets.PacketSendClientId;
import com.tpps.application.network.gameSession.packets.PacketSendHandCards;
import com.tpps.application.network.gameSession.packets.PacketUpdateValues;
import com.tpps.technicalServices.util.GameConstant;

/**
 * 
 * @author ladler - Lukas Adler
 *
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
				Player activePlayer = server.getGameController().getActivePlayer();
				activePlayer.doAction(((PacketPlayCard)packet).getCardID());
				System.out.println("packet received from Client of type " + packet.getType() + " card id "
						+ ((PacketPlayCard)packet).getCardID());

				server.sendMessage(port, new PacketUpdateValues(activePlayer.getActions(), 
						activePlayer.getBuys(), activePlayer.getCoins()));
				
//				server.sendMessage(port, new PacketSendHandCards(activePlayer.getDeck().getCardHandIds()));
				break;
			case PLAY_TREASURES:
				server.sendMessage(port, new PacketPlayTreasures());
				break;
			case END_TURN:
//				alle Karten ablegen
//				this.server.getGameController().getActivePlayer().getDeck().
//				sollen hier auch gleich die neuen karten genommen werden
				nextActivePlayer();
//				neuer spieler nimmt Karten auf
				

				break;
			default:
				System.out.println("unknown packed type");
				break;

			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}

	private void nextActivePlayer() {

		Player activePlayer = this.server.getGameController().getActivePlayer();
		LinkedList<Player> players = this.server.getGameController().getPlayers();
		for (int i = 0; i < GameConstant.HUMAN_PLAYERS; i++) {

			Player player = players.get(i);
			if (player.equals(activePlayer)) {
				this.server.getGameController()
						.setActivePlayer(players.get(i < GameConstant.HUMAN_PLAYERS ? i + 1 : 0));
				break;
			}
		}
		try {
			server.broadcastMessage(new PacketEnableDisable(activePlayer.getClientId()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updatePortOfPlayer(int port, Packet packet) {
		for (int i = 0; i < GameConstant.HUMAN_PLAYERS; i++) {
			Player player = server.getGameController().getPlayers().get(i);
			if (player.getClientId() == ((PacketReconnect) packet).getClientId()) {
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
				server.broadcastMessage(
						new PacketEnableDisable(server.getGameController().getActivePlayer().getClientId()));
			}
			System.out.println(
					"registrate one more client to server with id: " + clientId + "listening on port: " + port);

		} catch (TooMuchPlayerException tmpe) {
			server.sendMessage(port, new PacketClientShouldDisconect());
			tmpe.printStackTrace();
		}
	}

}
