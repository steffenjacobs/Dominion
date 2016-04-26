package com.tpps.test.application.game;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;

import com.tpps.application.game.Deck;
import com.tpps.application.game.GameController;
import com.tpps.application.game.Player;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.technicalServices.util.GameConstant;

public class GameControllerTest2 {
	GameController gameController;


	@Before
	public void setUp() throws Exception {
		
		this.gameController = new GameController(null);
		for (int i = 0; i < GameConstant.PLAYERS; i++){
			this.gameController.addPlayer(new Player(new Deck(this.gameController.getGameBoard().getStartSet()), i, 80 + i, "test" + i, null, null));
		}
		
	}
	
	@Test
	public void testSetActivePlayer() {
		Player activePlayer = this.gameController.getPlayers().get(0);
		this.gameController.setActivePlayer(this.gameController.getPlayers().get(0));
		assertThat(activePlayer, is(this.gameController.getActivePlayer()));
		assertThat(activePlayer.getPlayerName(), is(this.gameController.getActivePlayerName()));
	}
	
	@Test
	public void testSetCardsDisabled() {
		assertTrue(this.gameController.isCardsEnabled());
		this.gameController.setCardsDisabled();
		assertTrue(!this.gameController.isCardsEnabled());
		this.gameController.setCardsEnabled();
		assertTrue(this.gameController.isCardsEnabled());
	}



	

	@Test
	public void testGetGameNotFinished() {
		fail("Not yet implemented");
	}



	@Test
	public void testSetGameBoard() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetGamePhase() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetThiefList() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetSpyList() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetNextActivePlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateTrashPile() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckCardExistsAndDiscardOrTrash() {
		fail("Not yet implemented");
	}

	@Test
	public void testValidateTurnAndExecute() {
		fail("Not yet implemented");
	}

	@Test
	public void testGain() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckBoardCardExistsAppendToDiscardPile() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsVictoryCardOnHand() {
		fail("Not yet implemented");
	}

	@Test
	public void testPlayTreasures() {
		fail("Not yet implemented");
	}

	@Test
	public void testOrganizePilesAndrefreshCardHand() {
		fail("Not yet implemented");
	}

	@Test
	public void testDiscardOtherDownto() {
		fail("Not yet implemented");
	}

	@Test
	public void testRevealAndTakeCardsDiscardOthers() {
		fail("Not yet implemented");
	}

	@Test
	public void testRevealCardAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testGainCurseOthers() {
		fail("Not yet implemented");
	}

	@Test
	public void testRevealCardOthersPutItOnTopOfDeck() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckWitchFinish() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckBureaucratFinish() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckThiefFinish() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckSpyFinish() {
		fail("Not yet implemented");
	}

	@Test
	public void testReactOnThief() {
		fail("Not yet implemented");
	}

	@Test
	public void testReactOnSpy() {
		fail("Not yet implemented");
	}

	@Test
	public void testDrawOthers() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckReactionModeFinishedAndEnableGuis() {
		fail("Not yet implemented");
	}

	@Test
	public void testEndTurn() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPlayers() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClientById() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPlayers() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetActivePlayer() {
		fail("Not yet implemented");
	}

	

	@Test
	public void testIsGameNotFinished() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetGameNotFinished() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddPlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testBuyOneCard() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGameBoard() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetDiscardPhase() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetActionPhase() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetBuyPhase() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGamePhase() {
		fail("Not yet implemented");
	}

	@Test
	public void testStartGame() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsGameFinished() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPlayerNames() {
		fail("Not yet implemented");
	}

	@Test
	public void testEndGame() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetThiefList() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSpyList() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetThiefList() {
		fail("Not yet implemented");
	}

}
