package com.tpps.application.network.game;

import java.io.IOException;

import com.tpps.application.game.Player;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.application.network.gameSession.packets.PacketSentClientId;
import com.tpps.application.network.packet.Packet;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class ServerGamePacketHandler extends PacketHandler {
	private GameServer server;

	public GameServer getServer() {
		return server;
	}

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
				try{
				server.getGameController().addPlayer(new Player(clientId, port));
				System.out.println(
						"registrate one more client to server with id: " + clientId + "listening on port: " + port);
				server.sendMessage(port, new PacketSentClientId(clientId));
				}
				catch (TooMuchPlayerException tmpe) {
					server.getClientThread(port).interrupt();
					
					server.getClientThread(port).closeSockets();
					
					tmpe.printStackTrace();
				}
				break;
			case CARD_PLAYED:
				server.getGameController().getActivePlayer().doAction(((PacketPlayCard) packet).getCardID());
				System.out.println("packet received from Client of type " + packet.getType() + " card id "
						+ ((PacketPlayCard) packet).getCardID());

				server.sendMessage(port, new PacketPlayCard("Chappel2", "anna"));

				break;
			case END_TURN:

				// GameController.
				break;
			default:
				System.out.println("unknown packed type");
				break;

			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}

}
