package com.tpps.application.network.game;

import java.util.LinkedList;

import com.tpps.application.game.GameStorageInterface;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.gameSession.packets.PacketOpenGuiAndEnableOne;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.application.network.gameSession.packets.PacketSendBoard;
import com.tpps.application.network.gameSession.packets.PacketSendClientId;
import com.tpps.application.network.gameSession.packets.PacketSendHandCards;
import com.tpps.ui.gameplay.GameWindow;


/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class ClientGamePacketHandler extends PacketHandler {
	private GameClient gameClient;
	private GameWindow gameWindow;
	private GameStorageInterface gameStorageInterface;

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
			this.gameStorageInterface.loadActionCardsAndPassToGameWindow(((PacketSendBoard)packet).getActionCardIds());
			break;
		case SEND_HAND_CARDS:
			LinkedList<String> handCardIds = ((PacketSendHandCards)packet).getCardIds();
//			this.gameStorageInterface.loadHandCardsAndPassToGameWindow(handCardIds);
			
			break;
		case UPDATE_VALUES:
			// gameGui.updateValues();
			break;
		case UPDATE_COINS:
//			gameGui.updateCoins();
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
		
			this.gameWindow.setVisible(true);
			if (((PacketOpenGuiAndEnableOne) packet).getClientId() == this.gameClient.getClientId()) {
				this.gameWindow.setEnabled(true);
				System.out.println("my gameWindow is enabled");
			} else {
				this.gameWindow.setEnabled(false);
				System.out.println("my gameWindo is disabled");
			}	
	}
	
	

	public void setGameWindow(GameWindow gameWindow) {
		this.gameWindow = gameWindow;
	}

	public void setGameStorageInterface(GameStorageInterface gameStorageInterface) {
		this.gameStorageInterface = gameStorageInterface;
	}

	public void setGameClient(GameClient gameClient) {
		this.gameClient = gameClient;
	}

}
