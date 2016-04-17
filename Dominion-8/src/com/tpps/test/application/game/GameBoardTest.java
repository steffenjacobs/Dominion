package com.tpps.test.application.game;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import com.tpps.application.game.GameBoard;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.network.game.SynchronisationException;
import com.tpps.technicalServices.network.game.WrongSyntaxException;
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
		assertThat(this.gameBoard.getTableForTreasureCards(), is(notNullValue()));
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
	
	@Test
	public void findCardListFromBoardTest() {
		
		
			this.gameBoard = new GameBoard();
			LinkedList<Card> testList;
			boolean exceptionFlag = false;
			try {
				testList = this.gameBoard.findCardListFromBoard("Copper");
				fail("should not be in this line");
			} catch (WrongSyntaxException e) {			
				exceptionFlag = true;
			} catch (NoSuchElementException e) {
				fail("should not be thrown");
			} catch (SynchronisationException e) {
				fail("should not be thrown");
			}
			assertTrue(exceptionFlag);
			
			
			
			
			try {
				testList = this.gameBoard.findCardListFromBoard("Copper" + 
			(this.gameBoard.getTableForTreasureCards().get("Copper").size() - 1));
				assertThat(testList, is(notNullValue()));
			} catch (WrongSyntaxException e) {
				fail("no exception should be thrown");
			} catch (NoSuchElementException e) {
				fail("should not be thrown");
			} catch (SynchronisationException e) {
				fail("should not be thrown");
			}
			
			exceptionFlag = false;
			try {
				this.gameBoard.findCardListFromBoard("Copper#");
				fail("should not be in this line");
			} catch (WrongSyntaxException e) {
				exceptionFlag = true;
			} catch (NoSuchElementException e) {
				fail("should not be thronw");
			} catch (SynchronisationException e) {
				fail("should not be thronw");
			}
			assertTrue(exceptionFlag);
			
			testList = this.gameBoard.getTableForTreasureCards().get("Copper");
			for (Iterator<Card> iterator = testList.iterator(); iterator.hasNext();) {
				testList.removeLast();
			}
			
			
			exceptionFlag = false;
			try {
				this.gameBoard.findCardListFromBoard("Copper0");
			} catch (WrongSyntaxException e) {
				fail("should not be thrown");
			} catch (NoSuchElementException e) {
				fail("should not be thrown");
			} catch (SynchronisationException e) {
				exceptionFlag = true;
			}
			assertTrue(exceptionFlag);		
	}
	
	@Test
	public void findAndRemoveCardFromBoard() {
		this.gameBoard = new GameBoard();
		Class<? extends GameBoard> gameBoardClass = this.gameBoard.getClass();
		boolean exceptionFlag = false;
		try {
			
			Method findAndRemoveCardFromBoard = gameBoardClass.getDeclaredMethod("findAndRemoveCardFromBoard", String.class);
			if(!findAndRemoveCardFromBoard.isAccessible()) {
			      findAndRemoveCardFromBoard.setAccessible(true);
			 }
			findAndRemoveCardFromBoard.invoke(this.gameBoard, "Copper" + (this.gameBoard.getTableForTreasureCards().get("Copper").size() - 1));
			exceptionFlag = true;
			assertTrue(exceptionFlag);
			
			exceptionFlag = false;
			try{
				findAndRemoveCardFromBoard.invoke(this.gameBoard, "Copper#");
				fail("should not be in this linse");
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof WrongSyntaxException) {
					exceptionFlag = true;
					
				}				
			}
			assertTrue(exceptionFlag);
			
			exceptionFlag = false;
			try{
				findAndRemoveCardFromBoard.invoke(this.gameBoard, "Copper" + (this.gameBoard.getTableForTreasureCards().get("Copper").size()));
				fail("should not be in this line");
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof SynchronisationException) {
					exceptionFlag = true;
				}
			}
			assertTrue(exceptionFlag);
			
		} catch (NoSuchMethodException | SecurityException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void getCardIdsTest() {
		LinkedList<String> testList = this.gameBoard.getCardIDs(this.gameBoard.getTableForTreasureCards());
		for (Iterator<String> iterator = testList.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			assertTrue(string.matches(".*(0|[1-9][0-9]*|#)"));
		}
		
		
	}
	
	

}
