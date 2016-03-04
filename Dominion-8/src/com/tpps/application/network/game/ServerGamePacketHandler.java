package com.tpps.application.network.game;

import java.io.IOException;
import java.util.Iterator;

import com.tpps.application.game.GameController;
import com.tpps.application.game.Player;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.gameSession.packets.PacketClientShouldDisconect;
import com.tpps.application.network.gameSession.packets.PacketEnableDisable;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.application.network.gameSession.packets.PacketSendClientId;
import com.tpps.application.network.packet.Packet;
import com.tpps.technicalServices.util.GameConstant;

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
				addPlayerAndCheckPlayerCount(port, clientId);
				break;
			case CARD_PLAYED:
				server.getGameController().getActivePlayer().doAction(((PacketPlayCard) packet).getCardID());
				System.out.println("packet received from Client of type " + packet.getType() + " card id "
						+ ((PacketPlayCard) packet).getCardID());

				server.sendMessage(port, new PacketPlayCard("Chappel2", "anna"));

				break;
			case END_TURN:

//				 this.server.getGameController().getActivePlayer()
				 
//				 this.server.getGameController().getPlayers().g
				 for (int i = 0; i < GameConstant.HUMAN_PLAYERS; i++) {
					
					
				}
				break;
			default:
				System.out.println("unknown packed type");
				break;

			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}

	/**
	 * 
	 * @param port
	 * @param clientId
	 * @throws IOException
	 */
	private void addPlayerAndCheckPlayerCount(int port, int clientId) throws IOException {
		try{				
		server.getGameController().addPlayer(new Player(clientId, port));
		server.sendMessage(port, new PacketSendClientId(clientId));
		if (server.getGameController().getPlayers().size() == 4){					
			server.broadcastMessage(new PacketEnableDisable(server.getGameController().getActivePlayer().getClientId()));
		}
		System.out.println(
				"registrate one more client to server with id: " + clientId + "listening on port: " + port);
		
		}
		catch (TooMuchPlayerException tmpe) {					
			server.sendMessage(port, new PacketClientShouldDisconect());					
			tmpe.printStackTrace();
		}
	}

}
