package com.tpps.test.application.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Test;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.util.ImageLoader;

/**
 * JUnit-Test for SerializedCard-Class
 * 
 * tests whether a card can be serialized and deserialized via
 * SerializedCard.getBytes() and new SerializedCard(byte[])
 * 
 * @author Steffen Jacobs
 */
public class JUnitSerializedCard {
	private static final boolean DEBUG = false;

	/**
	 * main-entry-point
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void test() throws IOException, InterruptedException {

		// create test-card
		LinkedHashMap<CardAction, String> actions = new LinkedHashMap<>();
		actions.put(CardAction.ADD_ACTION_TO_PLAYER, "2");
		actions.put(CardAction.DRAW_CARD, "5");
		LinkedList<CardType> types = new LinkedList<>();
		types.add(CardType.CURSE);
		types.add(CardType.VICTORY);
		types.add(CardType.TREASURE);
		BufferedImage img = ImageLoader.getImage("resources/img/gameObjects/testButton.png");

		SerializedCard sc = new SerializedCard(actions, types, 5, "☢TestCardäöü☢", img);

		// write card to console before serializing
		if (DEBUG)
			System.out.println(sc.toString());

		// serialize card
		byte[] serializedCard = sc.getBytes();

		// deserialize card
		SerializedCard sc2 = new SerializedCard(serializedCard);

		// write card to console after deserializing
		if (DEBUG)
			System.out.println(sc2.toString());

		// check if names are equal
		assertEquals(sc, sc2);

		// check if all other properties (including image) are equal
		assertTrue(sc.equalsEntirely(sc2));

		// show JFrame so the tester can SEE whether the two images are
		// identical.
		if (DEBUG) {
			JFrame frame = new JFrame();
			frame.setSize(img.getWidth() * 2 + 22, img.getHeight() + 45);
			frame.setVisible(false);
			JPanel panel = new JPanel() {
				private static final long serialVersionUID = 1L;

				@Override
				public void paintComponent(Graphics g) {
					g.setColor(Color.MAGENTA);
					g.drawRect(0, 0, img.getWidth(), img.getHeight());
					g.drawImage(img, 0, 0, null);
					g.drawRect(img.getWidth(), 0, sc2.getImage().getWidth(), sc2.getImage().getHeight());
					g.drawImage(sc2.getImage(), img.getWidth(), 0, null);
				}
			};
			frame.setContentPane(panel);
			frame.setVisible(true);
			Thread.sleep(5000);
		}
	}
}