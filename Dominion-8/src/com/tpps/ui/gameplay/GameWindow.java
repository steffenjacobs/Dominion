package com.tpps.ui.gameplay;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.tpps.application.game.card.BaseCardAction;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.components.*;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = -5389003835573453281L;
	private GFButton closeButton;
	private static GameWindow instance;
	private HashSet<GFCards> gfcAct;

	private BufferedImage closeImage, backgroundImage, tableImage;
	private BufferedImage[] actionCards;
	private GraphicFramework framework;
	private GFCards[] gfcAction;

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
		gfcAction = new GFCards[10];
		gfcAct = new HashSet<GFCards>();
		
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
		basicCardsImages();
		// buttonImage = this.loadingImage(buttonImage,
		// "resources/img/gameObjects/testButton.png");
		// framework.addComponent(new TestButton(.3, .3, .4, .4, WIDTH, HEIGHT,
		// 6, buttonImage, framework, "first"));
		// framework.addComponent(new TestButton(.2, .2, .4, .4, WIDTH, HEIGHT,
		// 4, buttonImage, framework, "second"));
		// GFButton gfb = new TestButton(.1, .1, .4, .4, WIDTH, HEIGHT, 5,
		// buttonImage, framework, "third");

		// framework.addComponent(gfb);
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
	
	private void basicCardsImages(){
		double shift = 0.315;
		double shiftBottom = 0.315;
		int k =3;
		for (int i = 0; i < actionCards.length; i++) {
			int random =(int) (Math.random()*25+1);
			actionCards[i] = loadingImage(actionCards[i], "resources/img/gameObjects/baseCards/"+random+".jpg");
			if(i <5){
			gfcAction[i] = new GFCards(shift+=0.06,0.02, 0.05, 0.15,k++ , actionCards[i], framework);
			}
			else{
				gfcAction[i] = new GFCards(shiftBottom+=0.06,0.2, 0.05, 0.15, k++, actionCards[i], framework);
			}
			switch (random) {
			case 1:
				gfcAction[i].setCardName(BaseCardAction.Adventurer.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 2:
				gfcAction[i].setCardName(BaseCardAction.Bureaucrat.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 3:
				gfcAction[i].setCardName(BaseCardAction.Cellar.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 4:
				gfcAction[i].setCardName(BaseCardAction.Chancellor.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 5:
				gfcAction[i].setCardName(BaseCardAction.Chapel.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 6:
				gfcAction[i].setCardName(BaseCardAction.CouncilRoom.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 7:
				gfcAction[i].setCardName(BaseCardAction.Feast.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 8:
				gfcAction[i].setCardName(BaseCardAction.Festival.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 9:
				gfcAction[i].setCardName(BaseCardAction.Gardens.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 10:
				gfcAction[i].setCardName(BaseCardAction.Laboratory.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 11:
				gfcAction[i].setCardName(BaseCardAction.Library.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 12:
				gfcAction[i].setCardName(BaseCardAction.Market.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 13:
				gfcAction[i].setCardName(BaseCardAction.Militia.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 14:
				gfcAction[i].setCardName(BaseCardAction.Mine.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 15:
				gfcAction[i].setCardName(BaseCardAction.Moat.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 16:
				gfcAction[i].setCardName(BaseCardAction.Moneylender.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 17:
				gfcAction[i].setCardName(BaseCardAction.Remodel.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 18:
				gfcAction[i].setCardName(BaseCardAction.Smithy.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 19:
				gfcAction[i].setCardName(BaseCardAction.Spy.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 20:
				gfcAction[i].setCardName(BaseCardAction.Thief.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 21:
				gfcAction[i].setCardName(BaseCardAction.ThroneRoom.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 22:
				gfcAction[i].setCardName(BaseCardAction.Village.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 23:
				gfcAction[i].setCardName(BaseCardAction.Witch.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 24:
				gfcAction[i].setCardName(BaseCardAction.Woodcutter.name());
				framework.addComponent(gfcAction[i]);
				break;
			case 25:
				gfcAction[i].setCardName(BaseCardAction.Workshop.name());
				framework.addComponent(gfcAction[i]);
				break;
				
			}
		
		}
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
