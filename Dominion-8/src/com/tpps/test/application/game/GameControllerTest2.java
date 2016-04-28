package com.tpps.test.application.game;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.Before;
import org.junit.Test;

import com.tpps.application.game.Deck;
import com.tpps.application.game.GameBoard;
import com.tpps.application.game.GameController;
import com.tpps.application.game.Player;
import com.tpps.technicalServices.network.game.GameServer;
import com.tpps.technicalServices.network.game.SynchronisationException;
import com.tpps.technicalServices.network.game.WrongSyntaxException;
import com.tpps.technicalServices.util.GameConstant;

public class GameControllerTest2 {
	GameController gameController;


	@Before
	public void setUp() throws Exception {
		
		this.gameController = new GameController(null);
		for (int i = 0; i < GameConstant.PLAYERS; i++){
			this.gameController.getPlayers().add((new Player(new Deck(this.gameController.getGameBoard().getStartSet()), i, 80 + i, "test" + i, null, null)));
		}
		this.gameController.setActivePlayer(this.gameController.getPlayers().get(0));
		
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
	public void testSetGameBoard() {
		GameBoard gameBoard = this.gameController.getGameBoard();
		this.gameController.setGameBoard(new GameBoard());
		assertTrue(!gameBoard.equals(this.gameController.getGameBoard()));
	}

	@Test
	public void testSetGamePhase() {
		this.gameController.setGamePhase("actionPhase");
		assertThat(this.gameController.getGamePhase(), is("actionPhase"));
		this.gameController.setGamePhase("buyPhase");
		assertThat(this.gameController.getGamePhase(), is("buyPhase"));
	}

	@Test
	public void testSetThiefList() {
		CopyOnWriteArrayList<Player> cardList = this.gameController.getThiefList();
		cardList.add(this.gameController.getActivePlayer());
		this.gameController.setThiefList(new CopyOnWriteArrayList<Player>());
		assertTrue(!cardList.equals(this.gameController.getThiefList()));
		this.gameController.resetThiefList();
		assertTrue(this.gameController.getThiefList().isEmpty());
	}

	@Test
	public void testSetSpyList() {
		CopyOnWriteArrayList<Player> cardList =  this.gameController.getSpyList();
		cardList.add(this.gameController.getActivePlayer());
		this.gameController.setSpyList(new CopyOnWriteArrayList<Player>());
		
		assertTrue(!cardList.equals(this.gameController.getSpyList()));
		
	}

	@Test
	public void testSetNextActivePlayer() {
		this.gameController.setActivePlayer(this.gameController.getPlayers().get(0));
		for (int i = 0; i < 20; i++) {
			
			assertTrue(this.gameController.getActivePlayer().equals(this.gameController.getPlayers().get(i % 4)));
			this.gameController.setNextActivePlayer();
		}
	}

	@Test
	public void testUpdateTrashPile() {
		int size = this.gameController.getGameBoard().getTrashPile().size();
		this.gameController.getActivePlayer().getTemporaryTrashPile().add(
				this.gameController.getGameBoard().getTableForTreasureCards().get("Copper").get(0));
		this.gameController.updateTrashPile(this.gameController.getActivePlayer().getTemporaryTrashPile());
		assertThat(size, is(this.gameController.getGameBoard().getTrashPile().size()));
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
//		LinkedList<Player> players = this.gameController.getPlayers();
//		players.remove(this.gameController.get)
	}


	@Test
	public void testEndTurn() {
		Player player = this.gameController.getActivePlayer();
		System.out.println(player);
		this.gameController.endTurn();
		assertThat(player.getActions(), is(1));
		assertThat(player.getCoins(), is(0));
		assertThat(player.getBuys(), is(1));
		assertTrue(!player.equals(this.gameController.getActivePlayer()));
	}

	@Test
	public void testGetPlayers() {
		assertThat(this.gameController.getPlayers().size(), is(4));
	}

	@Test
	public void testGetClientById() {
		Player player = this.gameController.getClientById(this.gameController.getPlayers().get(0).getClientID());
		assertThat(player, is(this.gameController.getPlayers().get(0)));
	}

	@Test
	public void testIsGameNotFinished() {
		assertTrue(this.gameController.isGameNotFinished());
		this.gameController.setGameNotFinished(false);
		assertTrue(this.gameController.isGameNotFinished());
	}

	@Test
	public void testBuyOneCard() {
		try {
//			this.gameController.setActivePlayer(this.gameController.getPlayers().get(0));
			int size = this.gameController.getActivePlayer().getDeck().getDiscardPile().size();
			this.gameController.buyOneCard(this.gameController.getGameBoard().getActionCardIDs().get(0));
			assertThat(this.gameController.getActivePlayer().getDeck().getDiscardPile().size(), is(size + 1));
			
		} catch (SynchronisationException e) {
			e.printStackTrace();
		} catch (WrongSyntaxException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetGameBoard() {
		assertThat(this.gameController.getGameBoard(), is(notNullValue()));
	}

	@Test
	public void testSetActionPhase() {
		this.gameController.setBuyPhase();
		assertThat(this.gameController.getGamePhase(), is("buyPhase"));
		this.gameController.setActionPhase();
		assertThat(this.gameController.getGamePhase(), is("actionPhase"));
		
	}



	@Test
	public void testStartGame() {
		this.gameController.startGame();
		assertThat(this.gameController.getGamePhase(), is("actionPhase"));
	}


	@Test
	public void testGetPlayerNames() {
		assertThat(this.gameController.getPlayerNames(), is(new String[]{"test0", "test1", "test2", "test3"}));
	}
	
	@Test
	public void testGetSpyList() {
		assertThat(this.gameController.getSpyList().size(), is(0));
		
	}

	@Test
	public void testResetThiefList() {
		this.gameController.getThiefList().add(new Player(null, 0, 0, "a", null, null));
		assertThat(this.gameController.getThiefList().size(), is(1));
		this.gameController.resetThiefList();
		assertThat(this.gameController.getThiefList().size(), is(0));
	}

}
