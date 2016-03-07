package com.tpps.ui.gameplay;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GameConstant;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.RelativeGeom2D;
import com.tpps.ui.components.GFButton;
import com.tpps.ui.components.GameBackground;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = -5389003835573453281L;
	private GFButton closeButton;
	private static GameWindow instance;

	private BufferedImage closeImage, backgroundImage, tableImage;
	private BufferedImage[] actionCards;
	private GraphicFramework framework;
	private Card[] gfcAction;
	private LinkedList<Card> estate, coins, handCards, tableCards;

	public static GameWindow getInstance() {
		return instance;
	}

	public static void main(String[] args) throws IOException {
		instance = new GameWindow();
	}

	/**
	 * creates the GameWindow
	 * 
	 * @author Steffen Jacobs
	 */
	public GameWindow() throws IOException {
		final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
		actionCards = new BufferedImage[10];
		gfcAction = new Card[10];

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setMinimumSize(new Dimension(1280, 720));
		this.setVisible(true);
		framework = new GraphicFramework(this);
		this.add(framework);

		backgroundImage = this.loadingImage(backgroundImage, "resources/img/gamePlay/GameBackground.jpg");
		closeImage = this.loadingImage(closeImage, "resources/img/gameObjects/close.png");
		tableImage = this.loadingImage(tableImage, "resources/img/gameObjects/table.jpg");

		closeButton = new ButtonClass(0.97, 0.01, 0.015, 0.015, WIDTH, WIDTH, 1, closeImage, framework, "");

		framework.addComponent(closeButton);
		framework.addComponent(new GameBackground(0, 0, 1, 1, 0, backgroundImage, framework));
		framework.addComponent(new GameBackground(0.33, 0.01, 0.38, 0.38, 2, tableImage, framework));

		// testing
		tableCards = new LinkedList<Card>();
		
		for (int i = 0; i < actionCards.length; i++) {

			loadingImage(actionCards[i], "resources/img/gameObjects/baseCards/Copper.jpg");
			tableCards.add(
					new Card(CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE, GameConstant.COPPER_VALUE),
							CollectionsUtil.linkedList(CardType.TREASURE), "Copper", GameConstant.COPPER_COST));
		}

		tableActionCards(tableCards);
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

	public void tableActionCards(LinkedList<Card> tableCards) {
		
		 double shift = 0.315;
		 double shiftBottom = 0.315;
		 int k = 3;

		for (int i = 0; i < tableCards.size(); i++) {

			if (i < 5) {
				tableCards.get(i).forceSetImage(actionCards[i]);
//				tableCards.get(i).updateRelativeSize(0.05, 0.15);
				tableCards.get(i).moveTo(new RelativeGeom2D(shift+=0.06, 0.02));
				framework.addComponent(tableCards.get(i));
				tableCards.get(i).setVisible(true);
			} else {
				tableCards.get(i).forceSetImage(actionCards[i]);
				tableCards.get(i).updateRelativeSize(0.05, 0.15);
				tableCards.get(i).moveTo(new RelativeGeom2D(shiftBottom+=0.06, 0.2));
				framework.addComponent(tableCards.get(i));
				
			}

		}

		// double shift = 0.315;
		// double shiftBottom = 0.315;
		// int k = 3;
		// for (int i = 0; i < actionCards.length; i++) {
		//
		// loadingImage(actionCards[i],
		// "resources/img/gameObjects/baseCards/Copper.jpg");
		//
		// if(i<5){
		// gfcAction[i] = new Card(
		// CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE,
		// GameConstant.COPPER_VALUE),
		// CollectionsUtil.linkedList(CardType.TREASURE), "Copper",
		// GameConstant.COPPER_COST, shift += 0.06,
		// 0.02, 0.05, 0.15, k++, actionCards[i], framework);
		// framework.addComponent(gfcAction[i]);
		// }
		// else{
		// gfcAction[i] = new Card(
		// CollectionsUtil.linkedHashMapAction(CardAction.IS_TREASURE,
		// GameConstant.COPPER_VALUE),
		// CollectionsUtil.linkedList(CardType.TREASURE), "Copper",
		// GameConstant.COPPER_COST, shiftBottom += 0.06,
		// 0.2, 0.05, 0.15, k++, actionCards[i], framework);
		// framework.addComponent(gfcAction[i]);
		// }
		// }
	}

	public void coinCards(LinkedList<Card> coins) {

	}

	public void handCards(LinkedList<Card> handCards) {

	}

	public void estateCards(LinkedList<Card> estate) {

	}

	private class ButtonClass extends GFButton {
		private static final long serialVersionUID = 1520424079770080041L;

		public ButtonClass(double relativeX, double relativeY, double relativeWidth, double relativeHeight,
				int absWidth, int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, String caption) {
			super(relativeX, relativeY, relativeWidth, relativeHeight, absWidth, absHeight, _layer, sourceImage,
					_parent, caption);
		}

		@Override
		public GameObject clone() {
			return null;// return new TestButton(super.get);
		}

		@Override
		public void onMouseEnter() {
			System.out.println("entered " + this.toString());

		}

		@Override
		public void onMouseExit() {

		}

		@Override
		public void onMouseClick() {
			System.out.println("clicked " + this.toString());
			if (closeButton.getCaption().equals("")) {
				System.exit(0);
			}
		}

		@Override
		public void onMouseDrag() {
			System.out.println("dragged " + this.toString());

		}

		@Override
		public String toString() {
			return "@" + System.identityHashCode(this) + " - " + super.getLocation() + " , " + super.getDimension()
					+ " , " + super.getLayer() + " , " + super.getImage() + " , " + super.getParent() + " , "
					+ super.getCaption();
		}

		@Override
		public void onResize(int absWidth, int absHeight) {
			super.onResize(absWidth, absHeight);
		}

	}

}
