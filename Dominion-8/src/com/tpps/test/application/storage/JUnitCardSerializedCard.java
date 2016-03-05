package com.tpps.test.application.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Test;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.storage.SerializedCard;

public class JUnitCardSerializedCard {
	private static final boolean SHOW_COMAPARE_WINDOW_AFTERWARDS = false;

	@Test
	public void test() throws IOException, InterruptedException {

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
		System.out.println(sc.toString());

		byte[] serializedCard = sc.getBytes();
		SerializedCard sc2 = new SerializedCard(serializedCard);
		System.out.println(sc2.toString());

		assertEquals(sc, sc2);
		assertTrue(sc.equalsEntirely(sc2));

		if (SHOW_COMAPARE_WINDOW_AFTERWARDS) {
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