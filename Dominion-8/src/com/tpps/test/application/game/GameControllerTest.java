package com.tpps.test.application.game;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;

import com.tpps.application.game.GameController;
import com.tpps.application.game.Player;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.technicalServices.util.GameConstant;

public class GameControllerTest {
	GameController gameControlller;
	GameServer gameServer;

	@Before
	public void setUp() throws Exception {
		this.gameServer = new GameServer();
		this.gameControlller = new GameController();
		for (int i = 0; i < GameConstant.HUMAN_PLAYERS; i++){
		this.gameControlller.addPlayer(new Player(GameServer.getCLIENT_ID(), 80 + i, gameServer.getGameController().getGameBoard().getStartSet(), "test" + i));
		}
	}

	@Test
	public void testEndGame() {		
		assertTrue(gameControlller.getGameNotFinished());
		gameControlller.endGame();
		assertTrue(!this.gameControlller.getGameNotFinished());
		
	}

}
