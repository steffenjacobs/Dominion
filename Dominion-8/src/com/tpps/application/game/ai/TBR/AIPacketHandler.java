package com.tpps.application.game.ai.TBR;

import java.util.Arrays;
import java.util.LinkedList;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLogSingleColor;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnableOthers;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendActiveButtons;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendBoard;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendClientId;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendHandCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketSendRevealCards;
import com.tpps.technicalServices.network.gameSession.packets.PacketUpdateTreasures;
import com.tpps.technicalServices.network.gameSession.packets.PacketUpdateValues;

/**
 * ClientGamePacketHandler
 * 
 * @author Nicolas
 *
 */
public class AIPacketHandler extends PacketHandler {

	private AIClient aiClient;

	/**
	 * @return the aiClient
	 */
	public AIClient getAiClient() {
		return aiClient;
	}

	/**
	 * @param aiClient
	 *            the aiClient to set
	 */
	public void setAiClient(AIClient aiClient) {
		this.aiClient = aiClient;
	}

	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		if (packet == null) {
			super.output("<- Empty Packet from (" + port + ")");
			return;
		}
		switch (packet.getType()) {
		case CARD_PLAYED:
			break;
		case SEND_CLIENT_ID:
			this.aiClient.setClientId(((PacketSendClientId) packet).getClientId());
			break;
		case CLIENT_SHOULD_DISCONECT:
			this.aiClient.disconnect();
			break;
		case OPEN_GUI_AND_ENABLE_ONE:
			// openGuiAndEnableOne(packet);
			break;
		case ENABLE_DISABLE:
			// enableDisable(packet);
			// this.gameWindow.reset();
			break;
		case ENABLE:
			// this.gameWindow.setEnabled(true);
			// DominionController.getInstance().setTurnFlag(true);
			// this.gameWindow.setCaptionTurn("E");
			break;
		case ENABLE_OTHERS:
			if (((PacketEnableOthers) packet).getClientID() == this.aiClient.getClientId()) {
				// this.gameWindow.setEnabled(false);
				DominionController.getInstance().setTurnFlag(false);
				// this.gameWindow.setCaptionTurn("D");
			} else {
				// this.gameWindow.setEnabled(true);
				DominionController.getInstance().setTurnFlag(true);
				// this.gameWindow.setCaptionTurn("E");
			}
			break;
		case DISABLE:
			// this.gameWindow.setEnabled(false);
			DominionController.getInstance().setTurnFlag(false);
			// this.gameWindow.setCaptionTurn("D");
			break;
		case SEND_BOARD:
			PacketSendBoard packetSendBoard = (PacketSendBoard) packet;
			// this.gameStorageInterface.loadActionCardsAndPassToGameWindow(packetSendBoard.getActionCardIds());
			// this.gameStorageInterface.loadCoinCardsAndPassToGameWindow(packetSendBoard.getCoinCardIds());
			// this.gameStorageInterface.loadVictoryCardsAndPassToGameWindow(packetSendBoard.getVictoryCardIds());
			break;
		case SEND_HAND_CARDS:
			LinkedList<String> handCardIds = ((PacketSendHandCards) packet).getCardIds();
			// this.gameStorageInterface.loadHandCardsAndPassToGameWindow(handCardIds);
			break;
		case SEND_REVEAL_CARDS:
			System.out.println(Arrays.toString(((PacketSendRevealCards) packet).getCardIds().toArray()));
			// this.gameStorageInterface.loadRevealCardsAndPassToGameWindow(((PacketSendRevealCards)
			// packet).getCardIds());
			break;
		case UPDATE_VALUES:
			PacketUpdateValues puv = ((PacketUpdateValues) packet);
			System.out.println("clientGameHandler actions: " + puv.getActions() + "buys: " + puv.getBuys() + "coins: " + puv.getCoins());
			// this.gameWindow.setCaptionActions(Integer.toString(puv.getActions()));
			// this.gameWindow.setCaptionBuys(Integer.toString(puv.getBuys()));
			// this.gameWindow.setCaptionCoins(Integer.toString(puv.getCoins()));
			break;
		case UPDATE_TREASURES:
			PacketUpdateTreasures put = (PacketUpdateTreasures) (packet);

			// this.gameWindow.setCaptionCoins(Integer.toString(put.getCoins()));
			// this.gameWindow.repaint();
			break;
		case END_ACTION_PHASE:
			// this.gameWindow.endActionPhase();
			break;
		case START_DISCARD_MODE:
			// this.gameWindow.addStopDiscardButton();
			break;
		case END_DISCARD_MODE:
			// this.gameWindow.removeStopDiscardButton();
			break;
		case START_TRASH_MODE:
			// this.gameWindow.addStopTrashButton();
			break;
		case TAKE_CARDS:
			// this.gameWindow.addTakeCardsButton();
			// this.gameWindow.removeEndTurnButton();
			// this.gameWindow.removeEndActionPhaseButton();
			// this.gameWindow.removePlayTreasuresButton();
			break;
		case TAKE_THIEF_CARDS:
			// this.gameWindow.addTakeThiefCardsButtonRemoveOtherButtons();
			break;
		case PUT_BACK_THIEF_CARDS:
			// this.gameWindow.addPutBackThiefCardsButton();
			break;
		case TAKE_DRAWED_CARD:
			// this.gameWindow.addTakeDrawedCard();
			break;
		case SET_ASIDE_DRAWED_CARD:
			// this.gameWindow.addSetAsideDrawedCard();
			break;
		case PUT_BACK_CARDS:
			// this.gameWindow.addPutBackButton();
			break;
		case SEND_ACTIVE_BUTTONS:
			PacketSendActiveButtons p = (PacketSendActiveButtons) packet;
			if (p.isEndTurn()) {
				// this.gameWindow.addEndTurnButton();
			} else {
				// this.gameWindow.removeEndTurnButton();
			}
			if (p.isPlayTreasures()) {
				// this.gameWindow.addPlayTreasuresButton();
			} else {
				// this.gameWindow.removePlayTreasuresButton();
			}
			if (p.isEndActionPhase()) {
				// this.gameWindow.addEndActionPhaseButton();
			} else {
				// this.gameWindow.removeEndActionPhaseButton();
			}
			break;
		case END_TRASH_MODE:
			// this.gameWindow.removeStopTrashButton();
			break;
		case REMOVE_EXTRA_TABLE:
			// this.gameWindow.removeTableComponents();
			break;
		case SHOW_END_REACTION_MODE:
			// this.gameWindow.addEndReactionModeButton();
			// this.gameWindow.removeEndActionPhaseButton();
			// this.gameWindow.removeEndTurnButton();
			break;
		case DONT_SHOW_END_REACTION_MODE:
			// this.gameWindow.removeEndReactionModeButton();
			break;
		case DISCARD_DECK:
			// this.gameWindow.addDiscardDeckButton();
			break;
		case SEND_PLAYED_CARDS_TO_ALL_CLIENTS:
			// this.gameStorageInterface.loadPlayedCardsAndPassToGameWindow(((PacketSendPlayedCardsToAllClients)
			// packet).getCardIds());
			break;
		// case PLAY_TREASURES:
		// gameGui.disableActionCards();
		// gameGui.enalbeMoney();
		// break;
		case BROADCAST_LOG_SINGLE_COLOR:
//			GameLog.log(((PacketBroadcastLogSingleColor) packet).getMsgType(), ((PacketBroadcastLogSingleColor) packet).getMessage());
			break;
		default:
			System.out.println("Unknown packet type: " + packet.getType());
			break;
		}
	}
}