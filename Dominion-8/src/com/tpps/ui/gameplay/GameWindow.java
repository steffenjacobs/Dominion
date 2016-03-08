package com.tpps.ui.gameplay;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.tpps.application.game.DominionController;
import com.tpps.application.game.card.Card;
import com.tpps.application.network.gameSession.packets.PacketEndActionPhase;
import com.tpps.application.network.gameSession.packets.PacketPlayTreasures;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.components.GFButton;
import com.tpps.ui.components.GameBackground;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = -5389003835573453281L;
	private GFButton closeButton;
	private GFButton endActionPhase;
	private GFButton playTreasures;
	private GFButton endTurn;
	private static GameWindow instance;

	private BufferedImage closeImage, backgroundImage, tableImage, buttonImage;
	private BufferedImage[] actionCards;
	private GraphicFramework framework;
	private Card[] gfcAction;
	private LinkedList<Card> estate, coins, handCards, tableCards;

	public static GameWindow getInstance() {
		return instance;
	}

	/**
	 * creates the GameWindow
	 * 
	 * @author Steffen Jacobs
	 */
	public GameWindow() throws IOException {
		final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
		final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
		actionCards = new BufferedImage[10];
		gfcAction = new Card[10];

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
//		 this.setExtendedState(Frame.MAXIMIZED_BOTH);
//		 this.setUndecorated(true);
		this.setMinimumSize(new Dimension(1280, 720));
		this.setVisible(true);
		framework = new GraphicFramework(this);
		this.add(framework);

		backgroundImage = this.loadingImage(backgroundImage, "resources/img/gamePlay/GameBackground.jpg");
		closeImage = this.loadingImage(closeImage, "resources/img/gameObjects/close.png");
		tableImage = this.loadingImage(tableImage, "resources/img/gameObjects/table.jpg");
		buttonImage = this.loadingImage(buttonImage, "resources/img/gameObjects/testButton.png");

		closeButton = new ButtonClass(0.98, 0.01, 0.015, 0.015, WIDTH, WIDTH, 1, closeImage, framework, "");

		endActionPhase = new ButtonClass(0.75, 0.1, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "End ActionPhase");
		playTreasures = new ButtonClass(0.75, 0.2, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Play Treasures");

		framework.addComponent(closeButton);
		framework.addComponent(endActionPhase);
		framework.addComponent(playTreasures);
		framework.addComponent(new GameBackground(0, 0, 1, 1, 0, backgroundImage, framework));
		framework.addComponent(new GameBackground(0.31, 0.01, 0.38, 0.38, 2, tableImage, framework));

//		this.setSize(1280, 720);
		this.revalidate();
		this.repaint();

	}

	private BufferedImage loadingImage(BufferedImage im, String resource) {
		try {
			im = ImageIO.read(getClass().getClassLoader().getResourceAsStream(resource));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return im;

	}

	public void tableActionCards(LinkedHashMap<String, SerializedCard> table) {

		double shift = 0.295;
		double shiftBottom = 0.295;
		int k = 3;
		LinkedList<String> actionCardlds = new LinkedList<>(table.keySet());

		for (int i = 0; i < table.size(); i++) {
			SerializedCard serializedCard = table.get(actionCardlds.get(i));

			if (i < 5) {
				framework.addComponent(new Card(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), shift += 0.06, 0.02,
						0.05, 0.15, k++, serializedCard.getImage(), framework));
			} else {
				framework.addComponent(new Card(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), shiftBottom += 0.06,
						0.2, 0.05, 0.15, k++, serializedCard.getImage(), framework));
			}

		}
	}

	public void coinCards(HashMap<String, SerializedCard> coins) {
		LinkedList<String> actionCardlds = new LinkedList<>(coins.keySet());
		double shift = -0.05;
		int k = 3;
		for (int i = 0; i < coins.size(); i++) {
			SerializedCard serializedCard = coins.get(actionCardlds.get(i));
			framework.addComponent(new Card(serializedCard.getActions(), serializedCard.getTypes(),
					serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), 0.95, shift += 0.12, 0.1,
					0.1, k++, GraphicsUtil.rotate(serializedCard.getImage(), 270), framework));

		}

	}

	public void handCards(LinkedHashMap<String, SerializedCard> handCards) {
		LinkedList<String> actionCardlds = new LinkedList<>(handCards.keySet());
		int k = 14;
		double sub = handCards.size();
		double shift = (1 - (sub / 10)) / 2;
		double shiftSmall = shift - 0.03;
		double shiftOne = shiftSmall - 0.03;
		System.out.println(shift);
		for (int i = 0; i < handCards.size(); i++) {

			SerializedCard serializedCard = handCards.get(actionCardlds.get(i));

			if (handCards.size() <= 5 && handCards.size() > 1) {
				framework.addComponent(new Card(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), shiftSmall += 0.075,
						0.70, 0.1, 0.3, k++, serializedCard.getImage(), framework));
			} else if (handCards.size() == 1) {
				framework.addComponent(new Card(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), shiftOne += 0.075,
						0.70, 0.1, 0.3, k++, serializedCard.getImage(), framework));
			} else {
				framework.addComponent(new Card(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), shift += 0.075, 0.70,
						0.1, 0.3, k++, serializedCard.getImage(), framework));
			}
		}
	}

	public void estateCards(HashMap<String, SerializedCard> estate) {
		LinkedList<String> actionCardlds = new LinkedList<>(estate.keySet());
		double shift = -0.05;
		int k = 3;
		for (int i = 0; i < estate.size(); i++) {
			SerializedCard serializedCard = estate.get(actionCardlds.get(i));
			framework.addComponent(new Card(serializedCard.getActions(), serializedCard.getTypes(),
					serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), -0.05, shift += 0.12, 0.1,
					0.1, k++, GraphicsUtil.rotate(serializedCard.getImage(), 90), framework));

		}
	}
}