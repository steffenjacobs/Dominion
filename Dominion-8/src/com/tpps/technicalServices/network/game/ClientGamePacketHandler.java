package com.tpps.technicalServices.network.game;

import java.util.Arrays;
import java.util.LinkedList;

import com.tpps.application.game.GameStorageInterface;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnableDisable;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnableOthers;
import com.tpps.technicalServices.network.gameSession.packets.PacketOpenGuiAndEnableOne;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayCard;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendActiveButtons;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendBoard;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendClientId;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendHandCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendPlayedCardsToAllClients;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendRevealCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketUpdateTreasures;
import com.tpps.technicalServices.network.gameSession.packets.PacketUpdateValues;
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
		case ENABLE:
			this.gameWindow.setEnabled(true);
			break;
		case ENABLE_OTHERS:
			if (((PacketEnableOthers)packet).getClientID() == this.gameClient.getClientId()){
				this.gameWindow.setEnabled(false);
			}else{
				this.gameWindow.setEnabled(true);
			}
			break;
		case DISABLE:
			this.gameWindow.setEnabled(false);
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
		case SEND_REVEAL_CARDS:
			System.out.println(Arrays.toString(((PacketSendRevealCards)packet).getCardIds().toArray()));
			this.gameStorageInterface.loadRevealCardsAndPassToGameWindow(((PacketSendRevealCards)packet).getCardIds());
			break;
		case UPDATE_VALUES:
			PacketUpdateValues puv = ((PacketUpdateValues)packet);
			System.out.println("clientGameHandler actions: " + puv.getActions() +
					"buys: " + puv.getBuys() + "coins: " + puv.getCoins());
			this.gameWindow.setCaptionActions(Integer.toString(puv.getActions()));
			this.gameWindow.setCaptionBuys(Integer.toString(puv.getBuys()));
			this.gameWindow.setCaptionCoins(Integer.toString(puv.getCoins()));			
			break;
		case UPDATE_TREASURES:
			PacketUpdateTreasures put = (PacketUpdateTreasures)(packet);
			
			this.gameWindow.setCaptionCoins(Integer.toString(put.getCoins()));
			this.gameWindow.repaint();
			break;
		case END_ACTION_PHASE:
			this.gameWindow.endActionPhase();
			break;
		case START_DISCARD_MODE:
			this.gameWindow.addStopDiscardButton();
			
			break;
		case END_DISCARD_MODE:
			this.gameWindow.removeStopDiscardButton();
			break;
		case START_TRASH_MODE:
			this.gameWindow.addStopTrashButton();
			break;
		case TAKE_CARDS:
			this.gameWindow.addTakeCardsButton();
			this.gameWindow.removeEndTurnButton();
			this.gameWindow.removeEndActionPhaseButton();
			this.gameWindow.removePlayTreasuresButton();
			break;
		case TAKE_THIEF_CARDS:
			this.gameWindow.addTakeThiefCardsButtonRemoveOtherButtons();
			break;
		case PUT_BACK_THIEF_CARDS:
			this.gameWindow.addPutBackThiefCardsButton();
			break;
		case TAKE_DRAWED_CARD:
			this.gameWindow.addTakeDrawedCard();
			break;
		case SET_ASIDE_DRAWED_CARD:
			this.gameWindow.addSetAsideDrawedCard();
			break;
		case PUT_BACK_CARDS:
			this.gameWindow.addPutBackButton();
			break;
		case SEND_ACTIVE_BUTTONS:
			PacketSendActiveButtons p = (PacketSendActiveButtons)packet;
			if (p.isEndTurn()){
				this.gameWindow.addEndTurnButton();
			}else{
				this.gameWindow.removeEndTurnButton();
			}
			if (p.isPlayTreasures()){
				this.gameWindow.addPlayTreasuresButton();
			}else{
				this.gameWindow.removePlayTreasuresButton();
			}
			if (p.isEndActionPhase()){
				this.gameWindow.addEndActionPhaseButton();
			}else{
				this.gameWindow.removeEndActionPhaseButton();
			}
			break;
		case END_TRASH_MODE:
			this.gameWindow.removeStopTrashButton();
			break;
		case REMOVE_EXTRA_TABLE:
			this.gameWindow.removeTableComponents();
			break;
		case SHOW_END_REACTION_MODE:
			this.gameWindow.addEndReactionModeButton();
			this.gameWindow.removeEndActionPhaseButton();
			this.gameWindow.removeEndTurnButton();
			break;
		case DONT_SHOW_END_REACTION_MODE:
			this.gameWindow.removeEndReactionModeButton();
			break;
		case DISCARD_DECK:
			this.gameWindow.addDiscardDeckButton();
			break;
		case SEND_PLAYED_CARDS_TO_ALL_CLIENTS:
			
			this.gameStorageInterface.loadPlayedCardsAndPassToGameWindow(((PacketSendPlayedCardsToAllClients)packet).getCardIds());
			break;
		// case PLAY_TREASURES:
		// gameGui.disableActionCards();
		// gameGui.enalbeMoney();
		// break;
		default:
			System.out.println("unknown packet type: " + packet.getType());
			break;
		}
	}

	/**
	 * 
	 * @param packet
	 */
	private void enableDisable(Packet packet) {
//		if (this.gameClient.getClientId() == -1){
//			System.out.println();
//		}
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
			while(this.gameClient.getClientId() == -1){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("clientId not set. please wait a moment");
			}
		
			if (((PacketOpenGuiAndEnableOne) packet).getClientId() == this.gameClient.getClientId()) {
				this.gameWindow.setEnabled(true);
				System.out.println("my gameWindow is enabled");
			} else {
				this.gameWindow.setEnabled(false);
				System.out.println("my gameWindo is disabled");
			}
			this.gameWindow.setVisible(true);
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
