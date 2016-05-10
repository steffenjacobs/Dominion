package com.tpps.test.application.game;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import com.tpps.application.game.CardName;
import com.tpps.application.game.GameBoard;
import com.tpps.application.game.GameConstant;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.network.game.SynchronisationException;
import com.tpps.technicalServices.network.game.WrongSyntaxException;

public class GameBoardTest {
	GameBoard gameBoard;

	@Before
	public void setUp() throws Exception {
		;
		this.gameBoard = new GameBoard(new String[] { CardName.MOAT.getName(), CardName.MILITIA.getName(), CardName.WITCH.getName(), CardName.THIEF.getName(), CardName.SPY.getName(),
				CardName.THRONEROOM.getName(), CardName.COUNCILROOM.getName(), CardName.ADVENTURER.getName(), CardName.CELLAR.getName(), CardName.CHAPEL.getName() });
	}

	@Test
	public void test() {

		LinkedList<Card> startSet = this.gameBoard.getStartSet();
		assertThat(startSet.size(), is(10));
		for (int i = 0; i < GameConstant.INIT_COPPER_CARDS_ON_HAND.getValue(); i++) {
			assertTrue(startSet.get(i).getTypes().contains(CardType.TREASURE));
			assertTrue(startSet.get(i).getCost() == GameConstant.COPPER_COST.getValue());
		}

		for (int i = GameConstant.INIT_COPPER_CARDS_ON_HAND.getValue(); i < GameConstant.INIT_COPPER_CARDS_ON_HAND.getValue() + GameConstant.INIT_ESTATE_CARDS_ON_HAND.getValue(); i++) {
			// assertTrue(startSet.get(i).getId().equals(CardName.ESTATE.getName()
			// + (i -
			// GameConstant.INIT_COPPER_CARDS_ON_HAND.getValue())));
			assertTrue(startSet.get(i).getTypes().contains(CardType.VICTORY));
			assertTrue(startSet.get(i).getCost() == GameConstant.ESTATE_COST.getValue());
		}
	}

	@Test
	public void initTreasureMapTest() {
		assertThat(this.gameBoard.getTableForTreasureCards(), is(notNullValue()));
	}

	@Test
	public void initVictoryMapTest() {
		LinkedHashMap<String, LinkedList<Card>> victoryMap = this.gameBoard.getTableForVictoryCards();
		LinkedList<Card> estateList = victoryMap.get(CardName.ESTATE.getName());
		LinkedList<Card> duchyList = victoryMap.get(CardName.DUCHY.getName());

		for (int i = 0; i < duchyList.size(); i++) {
			assertTrue(estateList.get(i).getId().equals(CardName.ESTATE.getName() + i));
		}

		for (int i = 0; i < duchyList.size(); i++) {
			assertTrue(duchyList.get(i).getId().equals(CardName.DUCHY.getName() + i));
		}
	}

	@Test
	public void amoutOfPilesEmptyTest() {
		this.gameBoard.getTableForVictoryCards().remove(CardName.PROVINCE.getName());
		this.gameBoard.getTableForVictoryCards().put(CardName.PROVINCE.getName(), new LinkedList<Card>());
		assertThat(this.gameBoard.amountOfPilesEmptyForTable(this.gameBoard.getTableForVictoryCards()), is(1));

		this.gameBoard.getTableForTreasureCards().remove(CardName.SILVER.getName());
		this.gameBoard.getTableForTreasureCards().put(CardName.SILVER.getName(), new LinkedList<>());
		assertThat(this.gameBoard.amountOfPilesEmptyForTable(this.gameBoard.getTableForTreasureCards()), is(1));

		assertThat(this.gameBoard.amountOfPilesEmptyForTable(this.gameBoard.getTableForActionCards()), is(0));

		assertTrue(!this.gameBoard.checkPilesEmpty(GameConstant.EMPTY_PILES.getValue()));

		this.gameBoard.getTableForActionCards().remove(CardName.MOAT.getName());
		this.gameBoard.getTableForActionCards().put(CardName.MOAT.getName(), new LinkedList<Card>());
		assertTrue(this.gameBoard.checkPilesEmpty(GameConstant.EMPTY_PILES.getValue()));

//		this.gameBoard.getTableForTreasureCards().remove(CardName.COPPER.getName());
//		this.gameBoard.getTableForActionCards().put(CardName.COPPER.getName(), new LinkedList<Card>());
		assertTrue(this.gameBoard.checkPilesEmpty(GameConstant.EMPTY_PILES.getValue()));

	}

	@Test
	public void findCardListFromBoardTest() {

		this.gameBoard = new GameBoard(new String[] { CardName.MOAT.getName(), CardName.MILITIA.getName(), CardName.WITCH.getName(), CardName.THIEF.getName(), CardName.SPY.getName(),
				CardName.THRONEROOM.getName(), CardName.COUNCILROOM.getName(), CardName.ADVENTURER.getName(), CardName.CELLAR.getName(), CardName.CHAPEL.getName() });
		LinkedList<Card> testList;
		boolean exceptionFlag = false;
		try {
			testList = this.gameBoard.findCardListFromBoard(CardName.COPPER.getName());
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
			testList = this.gameBoard.findCardListFromBoard(CardName.COPPER.getName() + (this.gameBoard.getTableForTreasureCards().get(CardName.COPPER.getName()).size() - 1));
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

		testList = this.gameBoard.getTableForTreasureCards().get(CardName.COPPER.getName());
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
		this.gameBoard = new GameBoard(new String[] { CardName.MOAT.getName(), CardName.MILITIA.getName(), CardName.WITCH.getName(), CardName.THIEF.getName(), CardName.SPY.getName(),
				CardName.THRONEROOM.getName(), CardName.COUNCILROOM.getName(), CardName.ADVENTURER.getName(), CardName.CELLAR.getName(), CardName.CHAPEL.getName() });
		Class<? extends GameBoard> gameBoardClass = this.gameBoard.getClass();
		boolean exceptionFlag = false;
		try {

			Method findAndRemoveCardFromBoard = gameBoardClass.getDeclaredMethod("findAndRemoveCardFromBoard", String.class);
			if (!findAndRemoveCardFromBoard.isAccessible()) {
				findAndRemoveCardFromBoard.setAccessible(true);
			}
			findAndRemoveCardFromBoard.invoke(this.gameBoard, CardName.COPPER.getName() + (this.gameBoard.getTableForTreasureCards().get(CardName.COPPER.getName()).size() - 1));
			exceptionFlag = true;
			assertTrue(exceptionFlag);

			exceptionFlag = false;
			try {
				findAndRemoveCardFromBoard.invoke(this.gameBoard, "Copper#");
				fail("should not be in this linse");
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof WrongSyntaxException) {
					exceptionFlag = true;

				}
			}
			assertTrue(exceptionFlag);

			exceptionFlag = false;
			try {
				findAndRemoveCardFromBoard.invoke(this.gameBoard, CardName.COPPER.getName() + (this.gameBoard.getTableForTreasureCards().get(CardName.COPPER.getName()).size()));
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

	@Test
	public void setRandomSetTest() {
		Field field;
		try {
			field = this.gameBoard.getClass().getDeclaredField("tableForAllActionCards");
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}

			@SuppressWarnings("unchecked")
			LinkedHashMap<String, LinkedList<Card>> tableForAllActionCards = (LinkedHashMap<String, LinkedList<Card>>) field.get(this.gameBoard);
			int size = tableForAllActionCards.size();

			this.gameBoard.setRandomSet();

			assertThat(tableForAllActionCards.size(), is(size));
			assertThat(this.gameBoard.getTableForActionCards().size(), is(10));

			String key = new LinkedList<String>(this.gameBoard.getTableForActionCards().keySet()).get(0);
			this.gameBoard.getTableForActionCards().get(key).removeFirst();
			assertThat(this.gameBoard.getTableForActionCards().get(key).size(), is(tableForAllActionCards.get(key).size() - 1));

			this.gameBoard.getTableForActionCards().remove(key);

			assertThat(tableForAllActionCards.get(key), is(notNullValue()));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void setStandardSetTest() {
		this.gameBoard.setStandardSet();
		assertThat(this.gameBoard.getTableForActionCards().size(), is(GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue()));
	}

	@Test
	public void setAttackSetTest() {
		this.gameBoard.setAttackSet();
		assertThat(this.gameBoard.getTableForActionCards().size(), is(GameConstant.INIT_ACTIONCARD_PILE_SIZE.getValue()));
	}

}
