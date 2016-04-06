package com.tpps.test.application.game;


import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;


import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import com.tpps.application.game.GameBoard;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.util.GameConstant;

public class GameBoardTest {
	GameBoard gameBoard;
	@Before
	public void setUp() throws Exception {;
		this.gameBoard = new GameBoard();
	}

	@Test
	public void test() {
		
		
		LinkedList<Card> startSet = this.gameBoard.getStartSet();
		
		assertTrue(startSet.size() == 8);
		
		for (int i = 0; i < GameConstant.INIT_COPPER_CARDS; i++){
			
			assertTrue(startSet.get(i).getTypes().contains(CardType.TREASURE));
			assertTrue(startSet.get(i).getCost() == GameConstant.COPPER_COST);
		}
		
		for (int i = GameConstant.INIT_COPPER_CARDS; i < 
				GameConstant.INIT_COPPER_CARDS + GameConstant.INIT_ESTATE_CARDS; i++){
			
//			assertTrue(startSet.get(i).getId().equals("Estate" + (i - GameConstant.INIT_COPPER_CARDS)));
			assertTrue(startSet.get(i).getTypes().contains(CardType.VICTORY));
			assertTrue(startSet.get(i).getCost() == GameConstant.ESTATE_COST);
			
		}
		
		
		
	}
	@Test
	public void initTreasureMapTest(){
		this.gameBoard.getTableForTreasureCards();
	}
	
	@Test
	public void initVictoryMapTest(){
		LinkedHashMap<String, LinkedList<Card>> victoryMap = this.gameBoard.getTableForVictoryCards();
		LinkedList<Card> estateList = victoryMap.get("Estate");
		LinkedList<Card> duchyList = victoryMap.get("Duchy");
		
		for (int i = 0; i < duchyList.size(); i++){
			assertTrue(estateList.get(i).getId().equals("Estate" + i));
		}
		
		for (int i = 0; i < duchyList.size(); i++){
			assertTrue(duchyList.get(i).getId().equals("Duchy" + i));
		}		
	}
	
	
	@Test
	public void amoutOfPilesEmptyTest() {
		this.gameBoard.getTableForVictoryCards().remove("Province");
		this.gameBoard.getTableForVictoryCards().put("Province", new LinkedList<Card>());
		assertThat(this.gameBoard.amountOfPilesEmpty(this.gameBoard.getTableForVictoryCards()), is(1));
		
		this.gameBoard.getTableForTreasureCards().remove("Silver");
		this.gameBoard.getTableForTreasureCards().put("Silver", new LinkedList<>());
		assertThat(this.gameBoard.amountOfPilesEmpty(this.gameBoard.getTableForTreasureCards()), is(1));
		
		assertThat(this.gameBoard.amountOfPilesEmpty(this.gameBoard.getTableForActionCards()), is(0));		
		
		assertTrue(!this.gameBoard.checkThreePilesEmpty());
		
		this.gameBoard.getTableForActionCards().remove("Moat");
		this.gameBoard.getTableForActionCards().put("Moat", new LinkedList<Card>());
		assertTrue(this.gameBoard.checkThreePilesEmpty());
		
		this.gameBoard.getTableForTreasureCards().remove("Copper");
		this.gameBoard.getTableForActionCards().put("Copper", new LinkedList<Card>());
		assertTrue(this.gameBoard.checkThreePilesEmpty());
		
	}
	
	

}
