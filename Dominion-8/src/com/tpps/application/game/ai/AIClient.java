package com.tpps.application.game.ai;

import java.io.IOException;
import java.net.SocketAddress;

import com.tpps.technicalServices.network.core.Client;

/**
 * The GameHandler class provides methods for the AI to interact with the actual
 * game such as ending a turn, buying a card or playing all treasure cards from
 * hand
 * 
 * @author Nicolas Wipfler
 */
public class AIClient extends Client {

	private int clientId;
	
	/**
	 * 
	 * @param gameServer
	 *            the GameServer which contains all relevant game information
	 *            for the AI
	 * @throws IOException 
	 */
	public AIClient(SocketAddress _address, AIPacketHandler aiPacketHandler) throws IOException {
		super(_address, aiPacketHandler, false);
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
	public int getClientId() {
		return this.clientId;
	}
}	





	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// /**
	// *
	// * @return if the game is NOT finished (so when the method returns true,
	// the
	// * game is still running)
	// */
	// protected boolean notFinished() {
	// return this.gameServer.getGameController().isGameNotFinished();
	// }
	//
	// /**
	// *
	// * @return if its the AIs turn
	// */
	// protected boolean myTurn(Player player) {
	// return
	// this.gameServer.getGameController().getActivePlayer().equals(player);
	// }
	//
	// /**
	// * end the turn of AI
	// */
	// protected void endTurn() {
	// GameWindow.endTurn.onMouseClick();
	// // DominionController.getInstance().getGameClient().sendMessage(new
	// // PacketEndTurn());
	// }
	//
	// /**
	// * play all treasures of AI
	// */
	// protected void playTreasures() {
	// GameWindow.playTreasures.onMouseClick();
	// }
	//
	// protected void buyCard(String cardname) {
	//
	// }
	//
	// protected void playCard(String cardname) {
	//
	// }
//}
