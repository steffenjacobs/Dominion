package com.tpps.ui.gameplay;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.tpps.application.game.card.Card;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.util.GameConstant;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.components.DisplayValue;
import com.tpps.ui.components.GFButton;
import com.tpps.ui.components.GameBackground;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = -5389003835573453281L;
	static GFButton closeButton, endActionPhase, playTreasures, endTurn;
	private static GameWindow instance;

	private BufferedImage closeImage, backgroundImage, tableImage, buttonImage,displayImageBuys,displayImageActions,displayImageCoins;
	private GraphicFramework framework;
	private DisplayValue buy,coin,action;
	private LinkedList<Card> victoryCards, coinCards, handCards, tableCards;
	private ButtonClass stopDiscard;
	public static String coins, buys, actions;
	private static final double CORRECTION_16TO9 = 16/ (double) 9;
	

	public static GameWindow getInstance() {
		return instance;
	}

	/**
	 * creates the GameWindow
	 * 
	 * @author Steffen Jacobs
	 */
	public GameWindow() throws IOException {
		instance = this;
		final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
		final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.handCards = new LinkedList<Card>();
		this.tableCards = new LinkedList<Card>();
		this.victoryCards = new LinkedList<Card>();
		this.coinCards = new LinkedList<Card>();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		 this.setExtendedState(Frame.MAXIMIZED_BOTH);
		 this.setUndecorated(true);
		coins = "Coins: ";
		buys = "Buys: ";
		actions = "Actions: ";
		this.setMinimumSize(new Dimension(1280, 720));
		this.setVisible(true);
		framework = new GraphicFramework(this);
		this.add(framework);

		backgroundImage = this.loadingImage(backgroundImage, "resources/img/gamePlay/GameBackground.jpg");
		closeImage = this.loadingImage(closeImage, "resources/img/gameObjects/close.png");
		tableImage = this.loadingImage(tableImage, "resources/img/gameObjects/table.jpg");
		buttonImage = this.loadingImage(buttonImage, "resources/img/gameObjects/testButton.png");
		displayImageBuys= this.loadingImage(displayImageBuys, "resources/img/gameObjects/Buys.png");
		displayImageCoins= this.loadingImage(displayImageCoins, "resources/img/gameObjects/Coins.png");
		displayImageActions= this.loadingImage(displayImageActions, "resources/img/gameObjects/Actions.png");

		closeButton = new ButtonClass(0.98, 0.01, 0.015, 0.015*CORRECTION_16TO9, WIDTH, WIDTH, 1, closeImage, framework, "");

		endActionPhase = new ButtonClass(0.75, 0.05, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "End ActionPhase");
		playTreasures = new ButtonClass(0.75, 0.15, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Play Treasures");
		stopDiscard = new ButtonClass(0.75, 0.25, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Stop Discard");
		endTurn = new ButtonClass(0.75, 0.35, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "End Turn");
		
		action = new DisplayValue(0.1, 0.3, 0.12, 0.12, 1, 1, 1, displayImageActions, framework,String.valueOf(GameConstant.INIT_ACTIONS));
		coin = new DisplayValue(0.1, 0.4, 0.12, 0.12, 1, 1, 1, displayImageCoins, framework,String.valueOf(GameConstant.INIT_TREASURES));
		buy = new DisplayValue(0.1, 0.5, 0.12, 0.12, 1, 1, 1, displayImageBuys, framework,String.valueOf(GameConstant.INIT_PURCHASES));
		
		framework.addComponent(new GameBackground(0, 0, 1, 1, 0, backgroundImage, framework));
		framework.addComponent(new GameBackground(0.31, 0.01, 0.38, 0.38, 2, tableImage, framework));
		framework.addComponent(closeButton);
		framework.addComponent(endActionPhase);		
		framework.addComponent(stopDiscard);
		framework.addComponent(endTurn);
		
		framework.addComponent(action);
		framework.addComponent(coin);
		framework.addComponent(buy);
		
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
	
	public void reset() {
//		GameWindow.actions = "Actions " + GameConstant.INIT_ACTIONS;
//		GameWindow.coins = "Coins " + GameConstant.INIT_TREASURES;
//		GameWindow.buys = "Purchases " + GameConstant.INIT_PURCHASES;
		buy.renewCaption(String.valueOf(GameConstant.INIT_PURCHASES));
		coin.renewCaption(String.valueOf(GameConstant.INIT_TREASURES));
		action.renewCaption(String.valueOf(GameConstant.INIT_ACTIONS));
		this.framework.addComponent(endActionPhase);
		this.repaint();
	}

	public void tableActionCards(LinkedHashMap<String, SerializedCard> table) {
		LinkedList<String> actionCardlds = new LinkedList<>(table.keySet());
		double shift = 0.295;
		double shiftBottom = 0.295;
		int k = 3;
		
		for (Iterator<Card> iterator = this.tableCards.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			this.framework.removeComponent(card);		
		}
		this.tableCards = new LinkedList<Card>();
		
		
		

		for (int i = 0; i < table.size(); i++) {
			SerializedCard serializedCard = table.get(actionCardlds.get(i));

			if (i < 5) {
				Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), shift += 0.06, 0.02,
						0.05, 0.15, k++, serializedCard.getImage(), framework);
				framework.addComponent(card);
				this.tableCards.add(card);
			} else {
				Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), shiftBottom += 0.06,
						0.2, 0.05, 0.15, k++, serializedCard.getImage(), framework);
				framework.addComponent(card);
				this.tableCards.add(card);
			}

		}
	}

	public void coinCards(HashMap<String, SerializedCard> coins) {
		LinkedList<String> actionCardlds = new LinkedList<>(coins.keySet());
		double shift = -0.05;
		int k = 3;
		
		for (Iterator<Card> iterator = this.coinCards.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			this.framework.removeComponent(card);		
		}
		this.coinCards = new LinkedList<Card>();
		
		for (int i = 0; i < coins.size(); i++) {
			SerializedCard serializedCard = coins.get(actionCardlds.get(i));
			Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
					serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), 0.95, shift += 0.12, 0.1,
					0.1, k++, GraphicsUtil.rotate(serializedCard.getImage(), 270), framework);
			framework.addComponent(card);
			this.coinCards.add(card);

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
		
		
		for (Iterator<Card> iterator = this.handCards.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			this.framework.removeComponent(card);		
		}
		this.handCards = new LinkedList<Card>();
		
		for (int i = 0; i < handCards.size(); i++) {

			SerializedCard serializedCard = handCards.get(actionCardlds.get(i));
			//Example For nishit
//			actionCardlds.get(i).substring(actionCardlds.get(i).length()-1,actionCardlds.get(i).length());

			if (handCards.size() <= 5 && handCards.size() > 1) {
				Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), shiftSmall += 0.075,
						0.70, 0.1, 0.3, k++, serializedCard.getImage(), framework);
				framework.addComponent(card);
				this.handCards.add(card);
			} else if (handCards.size() == 1) {
				Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), shiftOne += 0.075,
						0.70, 0.1, 0.3, k++, serializedCard.getImage(), framework);
				framework.addComponent(card);
				this.handCards.add(card);
			} else {
				Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), shift += 0.075, 0.70,
						0.1, 0.3, k++, serializedCard.getImage(), framework);
				framework.addComponent(card);
				this.handCards.add(card);
			}
		}
	}

	public void victoryCards(HashMap<String, SerializedCard> victory) {
		LinkedList<String> actionCardlds = new LinkedList<>(victory.keySet());
		double shift = -0.05;
		int k = 3;
		
		for (Iterator<Card> iterator = this.victoryCards.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			this.framework.removeComponent(card);		
		}
		this.victoryCards = new LinkedList<Card>();
		
		
		
		for (int i = 0; i < victory.size(); i++) {
			SerializedCard serializedCard = victory.get(actionCardlds.get(i));
			Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
					serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), -0.05, shift += 0.12, 0.1,
					0.1, k++, GraphicsUtil.rotate(serializedCard.getImage(), 90), framework);
			framework.addComponent(card);
			this.victoryCards.add(card);

		}
	}
	
	public void setCaptionCoins(String caption){
		coin.renewCaption(caption);
	}
	public void setCaptionActions(String caption){
		action.renewCaption(caption);
	}
	public void setCaptionBuys(String caption){
		buy.renewCaption(caption);
	}
	
	public void endActionPhase(){
		framework.removeComponent(endActionPhase);
		framework.addComponent(playTreasures);
	}
	public void playTreasures(){
		framework.removeComponent(playTreasures);
	}
	
	public void endTurn(){
		framework.addComponent(endActionPhase);
	}
	
	
}