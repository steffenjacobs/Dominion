package com.tpps.test.application.game;

import static org.junit.Assert.assertTrue;

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
			System.out.println(startSet.get(i).getId());
			
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
	
	

}
