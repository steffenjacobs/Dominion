package com.tpps.technicalServices.network.game;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import com.tpps.application.game.DominionController;
import com.tpps.application.game.GameStorageInterface;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLog;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLogMultiColor;
import com.tpps.technicalServices.network.gameSession.packets.PacketDisable;
import com.tpps.technicalServices.network.gameSession.packets.PacketEnable;
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
import com.tpps.technicalServices.network.gameSession.packets.PacketUpdateValuesChangeButtons;
import com.tpps.ui.gameplay.GameWindow;

import javafx.util.Pair;

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
	public synchronized void handleReceivedPacket(int port, Packet packet) {
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
			new Thread(() -> {openGuiAndEnableOne(packet);}).start();			
			break;
		case ENABLE_DISABLE:
			enableDisable(packet);
			if (((PacketEnableDisable)packet).resetGameWindow()){
				this.gameWindow.reset();
			}
			break;
		case ENABLE:
			// this.gameWindow.setEnabled(true);
			DominionController.getInstance().setTurnFlag(true);
			this.gameWindow.setCaptionTurn(((PacketEnable)packet).getCaption());
			break;
		case ENABLE_OTHERS:
			if (((PacketEnableOthers) packet).getClientID() == this.gameClient.getClientId()) {
				// this.gameWindow.setEnabled(false);
				DominionController.getInstance().setTurnFlag(false);
				this.gameWindow.setCaptionTurn("wait on reaction");
			} else {
				// this.gameWindow.setEnabled(true);
				DominionController.getInstance().setTurnFlag(true);
				this.gameWindow.setCaptionTurn("react");
			}
			break;
		case DISABLE:
			// this.gameWindow.setEnabled(false);
			DominionController.getInstance().setTurnFlag(false);
			this.gameWindow.setCaptionTurn(((PacketDisable)packet).getCaption());
			
			break;
		case SEND_BOARD:
			PacketSendBoard packetSendBoard = (PacketSendBoard) packet;
			this.gameStorageInterface.loadActionCardsAndPassToGameWindow(packetSendBoard.getActionCardIds());
			this.gameStorageInterface.loadCoinCardsAndPassToGameWindow(packetSendBoard.getCoinCardIds());
			this.gameStorageInterface.loadVictoryCardsAndPassToGameWindow(packetSendBoard.getVictoryCardIds());
			break;
		case SEND_HAND_CARDS:
			LinkedList<String> handCardIds = ((PacketSendHandCards) packet).getCardIds();
			this.gameStorageInterface.loadHandCardsAndPassToGameWindow(handCardIds);
			if (((PacketSendHandCards)packet).getChangeButtons() != null) {
				switch (((PacketSendHandCards)packet).getChangeButtons()) {
				case "action":
					this.gameWindow.addEndActionPhaseButton();
					this.gameWindow.removeStopTrashButton();
					this.gameWindow.addEndTurnButton();
					break;
				case "playTreasures":
					this.gameWindow.addEndTurnButton();
					this.gameWindow.removeStopTrashButton();
					this.gameWindow.removeStopDiscardButton();
					this.gameWindow.addPlayTreasuresButton();
					break;

				default:
					break;
				}
			}
			break;
		case SEND_REVEAL_CARDS:
			System.out.println(Arrays.toString(((PacketSendRevealCards) packet).getCardIds().toArray()));
			this.gameStorageInterface.loadRevealCardsAndPassToGameWindow(((PacketSendRevealCards) packet).getCardIds());
			break;
		case UPDATE_VALUES:
			PacketUpdateValuesChangeButtons puv = ((PacketUpdateValuesChangeButtons) packet);
			System.out.println("clientGameHandler actions: " + puv.getActions() + "buys: " + puv.getBuys() + "coins: " + puv.getCoins());
			this.gameWindow.setCaptionActions(Integer.toString(puv.getActions()));
			this.gameWindow.setCaptionBuys(Integer.toString(puv.getBuys()));
			this.gameWindow.setCaptionCoins(Integer.toString(puv.getCoins()));
			switch (puv.getChangeButtons()) {
			case "remove":
				System.out.println("remove");
				this.gameWindow.removeEndActionPhaseButton();
				this.gameWindow.removeEndTurnButton();
				this.gameWindow.removePlayTreasuresButton();
				
				break;
			case "actions":
				System.out.println("actions");
				this.gameWindow.addEndActionPhaseButton();
				this.gameWindow.addEndTurnButton();
				break;
			case "playTreasures":
				System.out.println("play treasures");
				this.gameWindow.addPlayTreasuresButton();
				this.gameWindow.addEndTurnButton();
				break;
			default:
				break;
			}
			break;
		case UPDATE_TREASURES:
			PacketUpdateTreasures put = (PacketUpdateTreasures) (packet);

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
			PacketSendActiveButtons p = (PacketSendActiveButtons) packet;
			if (p.isEndTurn()) {
				this.gameWindow.addEndTurnButton();
			} else {
				this.gameWindow.removeEndTurnButton();
			}
			if (p.isPlayTreasures()) {
				this.gameWindow.addPlayTreasuresButton();
			} else {
				this.gameWindow.removePlayTreasuresButton();
			}
			if (p.isEndActionPhase()) {
				this.gameWindow.addEndActionPhaseButton();
			} else {
				this.gameWindow.removeEndActionPhaseButton();
			}
			System.out.println("active button");
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
			this.gameStorageInterface.loadPlayedCardsAndPassToGameWindow(((PacketSendPlayedCardsToAllClients) packet).getCardIds());
			break;
		// case PLAY_TREASURES:
		// gameGui.disableActionCards();
		// gameGui.enalbeMoney();
		// break;
		case BROADCAST_LOG:
			PacketBroadcastLog pck = (PacketBroadcastLog) packet;
			
			String left = pck.getLeft();
			String username = pck.getUsername();
			String right = pck.getRight();
			Color usercolor = pck.getColor();
			MsgType type = pck.getMsgType();

			long timestamp = pck.getTimestamp();
			System.out.println("> Time in CGPH is > " + timestamp + " and its the same as pck.getTimestamp(): " + (timestamp == pck.getTimestamp()));

			GameLog.log(type, left, /*timestamp,*/ GameLog.getMsgColor());
			GameLog.log(type, username, /*timestamp,*/ usercolor);
			GameLog.log(type, right + "\n",/* timestamp,*/ GameLog.getMsgColor());
			break;
		case BROADCAST_LOG_MULTI_COLOR:
			for (Pair<String, Color> pair : ((PacketBroadcastLogMultiColor) packet).getPair()) {
				GameLog.log(((PacketBroadcastLogMultiColor) packet).getMsgType(), pair.getKey(),/*((PacketBroadcastLogMultiColor) packet).getLogNr(),*/ pair.getValue());
			}
			break;
		case SHOW_END_SCREEN:
			JOptionPane.showMessageDialog(null, "end game");
			DominionController.getInstance().setTurnFlag(false);
			this.gameClient.disconnect();
			DominionController.getInstance().finishMatch();
			break;
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
		// if (this.gameClient.getClientId() == -1){
		// System.out.println();
		// }
		PacketEnableDisable packetEnableDisable = (PacketEnableDisable)packet;
		if (packetEnableDisable.getClientId() == this.gameClient.getClientId()) {
			// this.gameWindow.setEnabled(true);
			DominionController.getInstance().setTurnFlag(true);
			this.gameWindow.setCaptionTurn("my turn");
			System.out.println("my gameWindow is enabled");
		} else {
			// this.gameWindow.setEnabled(false);
			DominionController.getInstance().setTurnFlag(false);
			this.gameWindow.setCaptionTurn(packetEnableDisable.getUserName() + "'s turn");
			System.out.println("my gameWindo is disabled");
		}
	}

	/**
	 * 
	 * @param packet
	 */
	private void openGuiAndEnableOne(Packet packet) {
		while (this.gameClient.getClientId() == -1) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("clientId not set. please wait a moment");
		}
		PacketOpenGuiAndEnableOne packetOpenGuiAndEnableOne = (PacketOpenGuiAndEnableOne)packet;
		if (packetOpenGuiAndEnableOne.getClientId() == this.gameClient.getClientId()) {
			// this.gameWindow.setEnabled(true);
			DominionController.getInstance().setTurnFlag(true);
			this.gameWindow.setCaptionTurn("my turn");
			System.out.println("my gameWindow is enabled");
		} else {
			// this.gameWindow.setEnabled(false);
			DominionController.getInstance().setTurnFlag(false);
			this.gameWindow.setCaptionTurn(packetOpenGuiAndEnableOne.getUserName() + "'s turn");
			System.out.println("my gameWindo is disabled");
		}
		System.out.println("open gui");
		this.gameWindow.setVisible(true);

System.out.println("opened gui"+ this.gameWindow.isVisible());

		System.out.println("open gui2");
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
