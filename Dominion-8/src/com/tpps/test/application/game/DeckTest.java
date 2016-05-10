package com.tpps.test.application.game;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.tpps.application.game.CardName;
import com.tpps.application.game.Deck;
import com.tpps.application.game.GameBoard;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardType;

public class DeckTest {
	Deck deck;
	GameBoard gameBoard;

	@Before
	public void setUp() throws Exception {
		gameBoard = new GameBoard(new String[] { CardName.MOAT.getName(), CardName.MILITIA.getName(), CardName.WITCH.getName(), CardName.THIEF.getName(), CardName.SPY.getName(),
				CardName.THRONEROOM.getName(), CardName.COUNCILROOM.getName(), CardName.ADVENTURER.getName(), CardName.CELLAR.getName(), CardName.CHAPEL.getName() });

		deck = new Deck(gameBoard.getStartSet());
	}
	

	@Test
	public void testCardWithHighestCost() {
		Card card = gameBoard.getTableForActionCards().get("Militia").removeLast();
		deck.getCardHand().add(card);
		assertThat(deck.cardWithHighestCost(deck.getCardHand()).getName(), is("Militia"));		
	}
	
	
	public void testCardHandAmount() {
		assertThat(deck.cardHandAmount(CardType.TREASURE), is(7));
		assertThat(deck.cardHandAmount(CardType.VICTORY), is(3));
		assertThat(deck.cardHandAmount(CardType.ACTION), is(1));
	}
}
