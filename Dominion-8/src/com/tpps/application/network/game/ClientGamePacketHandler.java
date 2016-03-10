package com.tpps.application.network.game;

import java.util.LinkedList;

import com.tpps.application.game.GameStorageInterface;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.gameSession.packets.PacketEnableDisable;
import com.tpps.application.network.gameSession.packets.PacketOpenGuiAndEnableOne;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.application.network.gameSession.packets.PacketSendBoard;
import com.tpps.application.network.gameSession.packets.PacketSendClientId;
import com.tpps.application.network.gameSession.packets.PacketSendHandCards;
import com.tpps.application.network.gameSession.packets.PacketUpdateTreasures;
import com.tpps.application.network.gameSession.packets.PacketUpdateValues;
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
			System.out.println("packet received from Server of type " + packet.getType() + "id: " + ((PacketPlayCard) packet).getCardID());
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
		case ENABLE_DISABLE:
			enableDisable(packet);
			
			this.gameWindow.reset();
			
			break;
		case SEND_BOARD:
			PacketSendBoard packetSendBoard = (PacketSendBoard)packet;
			this.gameStorageInterface.loadActionCardsAndPassToGameWindow(packetSendBoard.getActionCardIds());
			this.gameStorageInterface.loadCoinCardsAndPassToGameWindow(packetSendBoard.getCoinCardIds());
			this.gameStorageInterface.loadVictoryCardsAndPassToGameWindow(packetSendBoard.getVictoryCardIds());
			break;
		case SEND_HAND_CARDS:
			LinkedList<String> handCardIds = ((PacketSendHandCards)packet).getCardIds();
			this.gameStorageInterface.loadHandCardsAndPassToGameWindow(handCardIds);			
			break;
		case UPDATE_VALUES:
			PacketUpdateValues puv = ((PacketUpdateValues)packet);
			
			GameWindow.actions = "Actions " + puv.getActions();
			GameWindow.coins = "Coins " + puv.getCoins();
			GameWindow.buys = "Buys " + puv.getBuys();
			this.gameWindow.repaint();
			break;
		case UPDATE_TREASURES:
			PacketUpdateTreasures put = (PacketUpdateTreasures)(packet);
			System.out.println("update Treasures client");
			GameWindow.coins = "Coins " + put.getCoins();
			this.gameWindow.repaint();
			break;
		case END_ACTION_PHASE:
			this.gameWindow.endActionPhase();
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

	/**
	 * 
	 * @param packet
	 */
	private void enableDisable(Packet packet) {
		if (this.gameClient.getClientId() == -1){
			System.out.println();
		}
		if (((PacketEnableDisable) packet).getClientId() == this.gameClient.getClientId()) {
			this.gameWindow.setEnabled(true);
			System.out.println("my gameWindow is enabled");
		} else {
			this.gameWindow.setEnabled(false);
			System.out.println("my gameWindo is disabled");
		}
	}

	/**
	 * 
	 * @param packet
	 */
	private void openGuiAndEnableOne(Packet packet) {
			
		while(this.gameClient.getClientId() == -1)
			
			this.gameWindow.setVisible(true);
			if (((PacketOpenGuiAndEnableOne) packet).getClientId() == this.gameClient.getClientId()) {
				this.gameWindow.setEnabled(true);
				System.out.println("my gameWindow is enabled");
			} else {
				this.gameWindow.setEnabled(false);
				System.out.println("my gameWindo is disabled");
			}	
	}

	/**
	 * 
	 */
	public void setGameWindow(GameWindow gameWindow) {
		this.gameWindow = gameWindow;
	}

	/**
	 * 
	 * @param gameStorageInterface
	 */
	public void setGameStorageInterface(GameStorageInterface gameStorageInterface) {
		this.gameStorageInterface = gameStorageInterface;
	}

	/**
	 * 
	 * @param gameClient
	 */
	public void setGameClient(GameClient gameClient) {
		this.gameClient = gameClient;
	}

}
