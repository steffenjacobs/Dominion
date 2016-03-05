package com.tpps.test.application.storage;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;

/**
 * JUnit-Test for CardStorageController check if cards can be added to the
 * Storage-Controller check if cards can be removed from the Storage-Controller
 * check if cards can be saved by the Storage-Controller check if cards can be
 * cleared from the Storage-Controller check if cards can be loaded by the
 * Storage-Controller
 * 
 * @author Steffen Jacobs
 */
public class JUnitCardStorageController {

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws IOException {

		// create first test-card
		LinkedHashMap<CardAction, Integer> actions = new LinkedHashMap<>();
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, 2);
		actions.put(CardAction.DRAW_CARD, 5);
		LinkedList<CardType> types = new LinkedList<>();
		types.add(CardType.CURSE);
		types.add(CardType.VICTORY);
		types.add(CardType.TREASURE);
		BufferedImage img = ImageIO
				.read(getClass().getClassLoader().getResourceAsStream("resources/img/gameObjects/testButton.png"));

		SerializedCard sc = new SerializedCard(actions, types, 5, "☢TestCardäöü☢", img);

		// create second test-card
		LinkedList<CardType> types2 = (LinkedList<CardType>) types.clone();
		types2.remove(CardType.VICTORY);
		types2.add(CardType.KNIGHT);

		LinkedHashMap<CardAction, Integer> actions2 = (LinkedHashMap<CardAction, Integer>) actions.clone();
		actions2.remove(CardAction.DRAW_CARD);
		actions2.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, 3);

		SerializedCard sc2 = new SerializedCard(actions2, types2, 4, "♞ ♞ ♞ TestCardäöü♞ ", img);

		// add first card to Card-Storage-Controller
		CardStorageController.addCard(sc);

		// remove first card from Card-Storage-Controller to check if it can be
		// added and removed successfully
		assertEquals(sc, CardStorageController.removeCard(sc));

		// add both cards
		CardStorageController.addCard(sc);
		CardStorageController.addCard(sc2);

		// save all added cards to file
		CardStorageController.saveCards();

		// remove all cards from cache
		CardStorageController.clearCards();

		// load cards from file
		CardStorageController.loadCards();

		// check if both cards are still correct
		SerializedCard sc3 = CardStorageController.getCard(sc.getName());
		assertEquals(sc, sc3);

		SerializedCard sc4 = CardStorageController.getCard(sc2.getName());
		assertEquals(sc2, sc4);

		// print both cards
		System.out.println(sc.toString());
		System.out.println(sc2.toString());

	}

}
