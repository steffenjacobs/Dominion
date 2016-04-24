package com.tpps.application.game.ai;

import com.tpps.application.game.Player;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.ui.gameplay.GameWindow;

/**
 * The GameHandler class provides methods for the AI to interact with the actual
 * game such as ending a turn, buying a card or playing all treasure cards from
 * hand
 * 
 * @author Nicolas Wipfler
 */
public class GameHandler {

	private GameServer gameServer;

	/**
	 * 
	 * @param gameServer
	 *            the GameServer which contains all relevant game information
	 *            for the AI
	 */
	public GameHandler(GameServer gameServer) {
		this.gameServer = gameServer;
	}

	/**
	 * 
	 * @return if the game is NOT finished (so when the method returns true, the
	 *         game is still running)
	 */
	protected boolean notFinished() {
		return this.gameServer.getGameController().isGameNotFinished();
	}

	/**
	 * 
	 * @return if its the AIs turn
	 */
	protected boolean myTurn(Player player) {
		return this.gameServer.getGameController().getActivePlayer().equals(player);
	}

	/**
	 * end the turn of AI
	 */
	protected void endTurn() {
		GameWindow.endTurn.onMouseClick();
		// DominionController.getInstance().getGameClient().sendMessage(new
		// PacketEndTurn());
	}

	/**
	 * play all treasures of AI
	 */
	protected void playTreasures() {
		GameWindow.playTreasures.onMouseClick();
	}
	
	protected void buyCard() {
		
	}
	
	protected void playCard() {
		
	}
}
