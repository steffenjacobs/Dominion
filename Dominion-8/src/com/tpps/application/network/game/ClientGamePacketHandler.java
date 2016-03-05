package com.tpps.application.network.game;

import java.io.IOException;
import java.util.LinkedList;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.gameSession.packets.PacketOpenGuiAndEnableOne;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.application.network.gameSession.packets.PacketSendClientId;
import com.tpps.application.network.gameSession.packets.PacketSendHandCards;
import com.tpps.ui.GameWindow;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class ClientGamePacketHandler extends PacketHandler {
	private GameClient gameClient;

	@Override
	public void handleReceivedPacket(int port, Packet packet) {

		if (packet == null) {
			super.output("<- Empty Packet from (" + port + ")");
			return;
		}
		switch (packet.getType()) {
		case CARD_PLAYED:
			System.out.println("packet received from Server of type " + packet.getType() + "id: "
					+ ((PacketPlayCard) packet).getCardID());
			break;
		case SEND_CLIENT_ID:
			this.gameClient.setClientId(((PacketSendClientId) packet).getClientId());
			break;
		case CLIENT_SHOULD_DISCONECT:
			System.out.println("Sorry there are already too many player connected to the server.");
			this.gameClient.disconnect();
			break;
		case OPEN_GUI_AND_ENABLE_ONE:
			openGuiAndEnableOne(packet);
			break;
		case SEND_BOARD:
//			gameGui.printBoard();
			break;
		case SEND_HAND_CARDS:
			LinkedList<String> cardIds = ((PacketSendHandCards)packet).getCardIds();
			System.out.println();
			try {
				this.gameClient.sendMessage(new PacketPlayCard(cardIds.get(0)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case UPDATE_VALUES:
			// gameGui.updateValues
			break;
		
		// case PLAY_TREASURES:
		// gameGui.disableActionCards();
		// gameGui.enalbeMoney();
		// break;
		default:
			System.out.println("unknown packed type");
			break;

		}

	}

	private void openGuiAndEnableOne(Packet packet) {
		try {
			GameWindow g = new GameWindow();
			if (((PacketOpenGuiAndEnableOne) packet).getClientId() == this.gameClient.getClientId()) {
				g.setEnabled(true);
				System.out.println("my gameWindow is enabled");
			} else {
				g.setEnabled(false);
				System.out.println("my gameWindo is disabled");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setGameClient(GameClient gameClient) {
		this.gameClient = gameClient;
	}

}
