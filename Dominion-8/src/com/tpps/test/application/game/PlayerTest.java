package com.tpps.test.application.game;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import com.tpps.application.game.CardName;
import com.tpps.application.game.Deck;
import com.tpps.application.game.GameBoard;
import com.tpps.application.game.Player;
import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.game.card.Tuple;
import com.tpps.technicalServices.util.CollectionsUtil;

public class PlayerTest {
	Player player;

	@Before
	public void setUp() throws Exception {
		GameBoard gameBoard = new GameBoard(new String[] { CardName.MOAT.getName(), CardName.MILITIA.getName(), CardName.WITCH.getName(), CardName.THIEF.getName(), CardName.SPY.getName(),
				CardName.THRONEROOM.getName(), CardName.COUNCILROOM.getName(), CardName.ADVENTURER.getName(), CardName.CELLAR.getName(), CardName.CHAPEL.getName() });

		this.player = new Player(new Deck(gameBoard.getStartSet()), 0, 0, "Test0", null, null);
	}

	// @Test
	// public void testPlayerDeckIntIntStringGameServer() {
	// fail("Not yet implemented");
	// }

	// @Test
	// public void testPlayerIntIntLinkedListOfCardStringGameServer() {
	// fail("Not yet implemented");
	// }

	@Test
	public void testResetPlayerValues() {

	}

	@Test
	public void testSetDiscardMode() {
		assertTrue(!this.player.isDiscardMode());
		this.player.setDiscardMode();
		assertTrue(this.player.isDiscardMode());
		this.player.endDiscardAndDrawMode();
		assertTrue(!this.player.isDiscardMode());
	}

	@Test
	public void testSetThief() {
		assertTrue(!this.player.isThief());
		this.player.setThief();
		assertTrue(this.player.isThief());
		this.player.setThiefFalse();
		assertTrue(!this.player.isThief());
	}

	@Test
	public void testResetThiefMode() {
		this.player.resetThiefMode();
		assertTrue(!(this.player.isThief() || this.player.playsReactionCard() || this.player.isReactionMode() || this.player.isRevealMode()) && this.player.getRevealList().isEmpty());
	}

	@Test
	public void testSetWitch() {
		assertTrue(!this.player.isWitch());
		this.player.setWitch();
		assertTrue(this.player.isWitch());
		this.player.setWitchFalse();
		assertTrue(!this.player.isWitch());
	}

	@Test
	public void testSetBureaucrat() {
		assertTrue(!this.player.isBureaucrat());
		this.player.setBureaucrat();
		assertTrue(this.player.isBureaucrat());
		this.player.setBureaucratFalse();
		assertTrue(!this.player.isBureaucrat());
	}

	@Test
	public void testSetSpy() {
		assertTrue(!this.player.isSpy());
		this.player.setSpy();
		assertTrue(this.player.isSpy());
		this.player.setSpyFalse();
		assertTrue(!this.player.isSpy());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetDiscardOrTrashAction() {
		Class<? extends Player> playerClass = this.player.getClass();
		Field discardOrTrashActionField;
		try {
			discardOrTrashActionField = playerClass.getDeclaredField("discardOrTrashAction");
			if (!discardOrTrashActionField.isAccessible()) {
				discardOrTrashActionField.setAccessible(true);
			}
			Tuple<CardAction> discardOrTrashAction = (Tuple<CardAction>) (discardOrTrashActionField.get(this.player));
			assertTrue(!(discardOrTrashAction instanceof Tuple<?>));
			assertTrue(discardOrTrashAction == null);
			discardOrTrashAction = new Tuple<CardAction>(CardAction.ADD_ACTION_TO_PLAYER, 2);
			assertTrue(discardOrTrashAction instanceof Tuple<?>);

			Method setDiscardOrTrahsActionMethod = playerClass.getDeclaredMethod("setDiscardOrTrashAction", CardAction.class, int.class);
			if (!setDiscardOrTrahsActionMethod.isAccessible()) {
				setDiscardOrTrahsActionMethod.setAccessible(true);
			}
			setDiscardOrTrahsActionMethod.invoke(this.player, CardAction.ADD_ACTION_TO_PLAYER, 2);
			discardOrTrashActionField = playerClass.getDeclaredField("discardOrTrashAction");
			if (!discardOrTrashActionField.isAccessible()) {
				discardOrTrashActionField.setAccessible(true);
			}
			discardOrTrashAction = (Tuple<CardAction>) (discardOrTrashActionField.get(this.player));
			assertTrue(discardOrTrashAction != null);
			assertThat(discardOrTrashAction.getFirstEntry(), is(CardAction.ADD_ACTION_TO_PLAYER));
			assertThat(discardOrTrashAction.getSecondEntry(), is(2));

		} catch (NoSuchFieldException | SecurityException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testSetReactionCard() {
		assertTrue(!this.player.playsReactionCard());
		this.player.setReactionCard(true);
		assertTrue(this.player.playsReactionCard());
		this.player.setReactionCard(false);
		assertTrue(!this.player.playsReactionCard());
	}

	@Test
	public void testSetReactionMode() {
		assertTrue(!this.player.isReactionMode());
		this.player.setReactionMode();
		assertTrue(this.player.isReactionMode());
		this.player.setReactionModeFalse();
		assertTrue(!this.player.isReactionMode());
	}

	@Test
	public void testSetRevealMode() {
		assertTrue(!this.player.isRevealMode());
		this.player.setRevealMode();
		assertTrue(this.player.isRevealMode());
		this.player.setModesFalse();
		assertTrue(!this.player.isRevealMode());
	}

	@Test
	public void testGetRevealList() {
		//
	}

	@Test
	public void testSetGainModeFalse() {
		assertTrue(!this.player.isGainMode());
	}

	@Test
	public void testGetGainValue() {
		assertThat(this.player.getGainValue(), is(0));
		Class<? extends Player> playerClass = this.player.getClass();
		Method setGainModeFalse;

		try {
			setGainModeFalse = playerClass.getDeclaredMethod("setGainModeFalse");
			if (!setGainModeFalse.isAccessible()) {
				setGainModeFalse.setAccessible(true);

			}
			setGainModeFalse.invoke(this.player, (Object[]) null);
			assertThat(this.player.getGainValue(), is(-1));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
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
	public void testSetBuys() {
		assertThat(this.player.getBuys(), is(1));
		this.player.setBuys(4);
		assertThat(this.player.getBuys(), is(4));
	}

	@Test
	public void testSetPort() {
		assertThat(this.player.getPort(), is(0));
		this.player.setPort(2333);
		assertThat(this.player.getPort(), is(2333));
	}

	@Test
	public void testSetDeck() {
		LinkedList<Card> deck = new LinkedList<>(this.player.getDeck().getDrawPile());
		deck.addAll(this.player.getDeck().getCardHand());
		assertThat(deck.size(), is(8));
		deck.addAll(this.player.getDeck().getDiscardPile());
		assertThat(deck.size(), is(8));
	}

	@Test
	public void testSetCoins() {
		assertThat(this.player.getCoins(), is(0));
		this.player.setCoins(4);
		assertThat(this.player.getCoins(), is(4));
	}

	@Test
	public void testSetOnHandFalse() {
		assertThat(this.player.isOnHand(), is(false));
		this.player.setOnHandFalse();
		assertTrue(!this.player.isOnHand());
	}

	@Test
	public void testSetModesFalse() {
		assertTrue(!(this.player.isDiscardMode() || this.player.isTrashMode() || this.player.isReactionMode() || this.player.playsReactionCard() || this.player.isGainMode()
				|| this.player.isRevealMode() || this.player.isThief() || this.player.isSpy() || this.player.isWitch() || this.player.isBureaucrat()));
	}

	@Test
	public void testGetID() {

		assertThat(this.player.getClientID(), is(0));
	}

	@Test
	public void testGetClientID() {
		assertThat(this.player.getClientID(), is(0));
	}

	@Test
	public void testGetActions() {
		assertThat(this.player.getActions(), is(1));
	}

	@Test
	public void testGetPlayerName() {
		assertThat(this.player.getPlayerName(), is("Test0"));
	}

	@Test
	public void testGetPlayedCards() {
		try {
			Card card = this.player.getDeck().getCardHand().get(0);
			this.player.playCard(this.player.getDeck().getCardHand().get(0).getId());
			LinkedList<Card> deck = new LinkedList<>(this.player.getDeck().getDrawPile());
			deck.addAll(this.player.getDeck().getCardHand());
			assertThat(deck.size(), is(7));
			deck.addAll(this.player.getDeck().getDiscardPile());
			assertThat(deck.size(), is(7));
			assertThat(this.player.getPlayedCards().size(), is(1));
			assertTrue(card.getId().equals(this.player.getPlayedCards().get(0).getId()));
			assertTrue(card.equals(this.player.getPlayedCards().get(0)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRefreshPlayedCardsList() {
		CollectionsUtil.appendListToList(this.player.getPlayedCards(), this.player.getDeck().getDiscardPile());
		this.player.getDeck().refreshCardHand();
		this.player.refreshPlayedCardsList();
		for (int i = 0; i < 10; i++) {
			int constant = (int) (Math.random() * 6);
			for (int i1 = 0; i1 < constant; i1++) {
				Card card = this.player.getDeck().getCardHand().getFirst();
				try {
					this.player.playCard(card.getId());
					assertThat(this.player.getDeck().getCardHand().size(), is(5 - i1 - 1));
					assertThat(this.player.getPlayedCards().size(), is(1 + i1));
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			CollectionsUtil.appendListToList(this.player.getPlayedCards(), this.player.getDeck().getDiscardPile());
			this.player.getDeck().refreshCardHand();
			this.player.refreshPlayedCardsList();
			assertThat(this.player.getDeck().getCardHand().size(), is(5));
			LinkedList<Card> deck = new LinkedList<>(this.player.getDeck().getDrawPile());
			deck.addAll(this.player.getDeck().getCardHand());
			deck.addAll(this.player.getDeck().getDiscardPile());
			assertThat(deck.size(), is(8));

		}

	}

	@Test
	public void testTakeRevealedCardsSetRevealModeFalse() {
		assertThat(this.player.getRevealList().size(), is(0));
		assertTrue(!this.player.isRevealMode());
	}

	@Test
	public void testPutBackRevealedCardsSetRevealModeFalse() {
		assertThat(this.player.getRevealList().size(), is(0));
		assertTrue(!this.player.isRevealMode());
	}

	@Test
	public void testEndDiscardAndDrawMode() {
		this.player.endDiscardAndDrawMode();
		assertTrue(!this.player.isDiscardMode());
		Class<? extends Player> playerClass = this.player.getClass();
		Field drawListField;
		try {
			drawListField = playerClass.getDeclaredField("drawList");
			if (!drawListField.isAccessible()) {
				drawListField.setAccessible(true);
			}
			@SuppressWarnings("unchecked")
			LinkedList<Card> drawList = (LinkedList<Card>) drawListField.get(this.player);
			assertTrue(drawList.isEmpty());
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
	public void testEndTrashMode() {
		this.player.endTrashMode();
		assertTrue(!this.player.isTrashMode());
	}

	@Test
	public void testPlayCard() {
		Card card = this.player.getDeck().getCardHand().getFirst();
		try {
			this.player.playCard(card.getId());
			assertThat(this.player.getDeck().getCardHand().size(), is(4));
			assertThat(this.player.getPlayedCards().size(), is(1));
			CollectionsUtil.appendListToList(this.player.getPlayedCards(), this.player.getDeck().getDiscardPile());
			this.player.getDeck().refreshCardHand();
			this.player.refreshPlayedCardsList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPlayTreasures() {
		try {
			this.player.playTreasures();
			for (Iterator<Card> iterator = this.player.getDeck().getCardHand().iterator(); iterator.hasNext();) {
				Card card = (Card) iterator.next();
				assertTrue(!card.getTypes().contains(CardType.TREASURE));
				CollectionsUtil.appendListToList(this.player.getPlayedCards(), this.player.getDeck().getDiscardPile());
				this.player.getDeck().refreshCardHand();
				this.player.refreshPlayedCardsList();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetRelevantCardActions() {
		assertTrue(!this.player.isReactionMode());
		LinkedList<CardAction> cardAction = new LinkedList<CardAction>();
		cardAction.add(CardAction.ADD_ACTION_TO_PLAYER);
		cardAction.add(CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND);
		cardAction.add(CardAction.SEPERATOR);
		cardAction.add(CardAction.DEFEND);
		cardAction.add(CardAction.DRAW_CARD_UNTIL);
		cardAction.add(CardAction.REVEAL_UNTIL_TREASURES);

		LinkedList<CardAction> firstActions = new LinkedList<CardAction>();
		firstActions.add(CardAction.ADD_ACTION_TO_PLAYER);
		firstActions.add(CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND);

		LinkedList<CardAction> secondActions = new LinkedList<CardAction>();

		secondActions.add(CardAction.REVEAL_UNTIL_TREASURES);
		secondActions.add(CardAction.DRAW_CARD_UNTIL);
		secondActions.add(CardAction.DEFEND);

		assertThat(firstActions, is(this.player.getRelevantCardActions(cardAction)));
		assertTrue(!secondActions.equals(this.player.getRelevantCardActions(cardAction)));

		this.player.setReactionMode();
		assertTrue(this.player.isReactionMode());
		assertThat(secondActions, is(this.player.getRelevantCardActions(cardAction)));
		assertTrue(!firstActions.equals(this.player.getRelevantCardActions(cardAction)));

	}

	@Test
	public void testDoAction() {
		Card card = this.player.getDeck().getCardByTypeFromHand(CardType.TREASURE);
		assertThat(this.player.getCoins(), is(0));
		Card cardRef;
		try {
			cardRef = this.player.doAction(card.getId());
			assertThat(card, is(cardRef));
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertThat(this.player.getCoins(), is(Integer.parseInt(new LinkedList<String>(card.getActions().values()).get(0))));

	}

	@Test
	public void testDiscardOrTrashCard() {

		Card card = this.player.getDeck().getCardHand().getFirst();
		try {

			Class<? extends Player> playerClass = this.player.getClass();

			Method setDiscardOrTrahsActionMethod = playerClass.getDeclaredMethod("setDiscardOrTrashAction", CardAction.class, int.class);
			if (!setDiscardOrTrahsActionMethod.isAccessible()) {
				setDiscardOrTrahsActionMethod.setAccessible(true);
			}
			setDiscardOrTrahsActionMethod.invoke(this.player, CardAction.TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND, 2);
			Field discardOrTrashActionField;

			discardOrTrashActionField = playerClass.getDeclaredField("discardOrTrashAction");
			if (!discardOrTrashActionField.isAccessible()) {
				discardOrTrashActionField.setAccessible(true);
			}
			@SuppressWarnings("unchecked")
			Tuple<CardAction> discardOrTrashAction = (Tuple<CardAction>) (discardOrTrashActionField.get(this.player));

			assertTrue(!this.player.isGainMode());
			assertThat(discardOrTrashAction.getSecondEntry(), is(2));

			this.player.discardOrTrash(card);

			assertTrue(!this.player.isGainMode());
			card = this.player.getDeck().getCardHand().getFirst();
			assertThat(discardOrTrashAction.getSecondEntry(), is(1));
			assertThat(this.player.getGainValue(), is(0));

			this.player.discardOrTrash(card);
			assertThat(discardOrTrashAction.getSecondEntry(), is(0));
			assertTrue(!this.player.isTrashMode());
			assertTrue(this.player.isGainMode());
			assertThat(this.player.getGainValue(), is(card.getCost()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testResetTemporaryTrashPile() {
		this.player.resetTemporaryTrashPile();
		assertThat(this.player.getTemporaryTrashPile().size(), is(0));
	}

}
