package com.tpps.ui.gameplay;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * Main GUI Window where all compenents are merged together and ready for GamePlay
 * 
 *  @author Nishit Agrawal - nagrawal, Lukas Adler - ladler
 *  
 */

public class GameWindow extends JFrame {
	private static final long serialVersionUID = -5389003835573453281L;
	public static GFButton closeButton, endActionPhase, playTreasures, endTurn, takeCards, putBack, 
	takeThiefCards, putBackThiefCards, takeDrawedCard, setAsideDrawedCard;
	private static GameWindow instance;

	private BufferedImage closeImage, backgroundImage, tableImage, buttonImage, displayImageBuys, displayImageActions,
			displayImageCoins, buttonGameImage;
	private GameBackground table;
	private GraphicFramework framework;
	private DisplayValue buy, coin, action;
	private LinkedList<Card> victoryCards, coinCards, handCards, tableCards, middleCards, extraTableCards;
	private LinkedList<GFButton> victoryButtons, coinButtons, tableButtons;
	private ButtonClass stopDiscard, stopTrash, discardDeck, endReactions;
	public static String coins, buys, actions;
	private static final double CORRECTION_16TO9 = 16 / (double) 9;

	public static GameWindow getInstance() {
		return instance;
	}

	/**
	 * 
	 * a constructor where the windowsize and functionality is set. Every Image
	 * is loaded here.
	 * 
	 * @throws IOException
	 */

	public GameWindow() throws IOException {
		instance = this;
		final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
		final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.handCards = new LinkedList<Card>();
		this.tableCards = new LinkedList<Card>();
		this.victoryCards = new LinkedList<Card>();
		this.extraTableCards = new LinkedList<Card>();
		this.coinCards = new LinkedList<Card>();
		this.middleCards = new LinkedList<Card>();
		this.tableButtons = new LinkedList<GFButton>();
		this.victoryButtons = new LinkedList<GFButton>();
		this.coinButtons = new LinkedList<GFButton>();

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setIconImage((ImageIO.read(ClassLoader.getSystemResource("resources/img/loginScreen/Icon.png"))));
		// this.setExtendedState(Frame.MAXIMIZED_BOTH);
		// this.setUndecorated(true);
		coins = "Coins: ";
		buys = "Buys: ";
		actions = "Actions: ";
		this.setMinimumSize(new Dimension(1280, 720));
//		this.setVisible(true);
		framework = new GraphicFramework(this);
		this.add(framework);

		backgroundImage = this.loadingImage(backgroundImage, "resources/img/gamePlay/GameBackground.jpg");
		closeImage = this.loadingImage(closeImage, "resources/img/gameObjects/close.png");
		tableImage = this.loadingImage(tableImage, "resources/img/gameObjects/table.jpg");
		buttonImage = this.loadingImage(buttonImage, "resources/img/gameObjects/testButton.png");
		displayImageBuys = this.loadingImage(displayImageBuys, "resources/img/gameObjects/Buys.png");
		displayImageCoins = this.loadingImage(displayImageCoins, "resources/img/gameObjects/Coins.png");
		displayImageActions = this.loadingImage(displayImageActions, "resources/img/gameObjects/Actions.png");
		buttonGameImage = this.loadingImage(buttonGameImage, "resources/img/gameObjects/ButtonsGame.png");

		closeButton = new ButtonClass(0.97, 0.01, 0.015, 0.015 * CORRECTION_16TO9, WIDTH, WIDTH, 1, closeImage,
				framework, "");

		endActionPhase = new ButtonClass(0.75, 0.05, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework,
				"End ActionPhase");
		playTreasures = new ButtonClass(0.75, 0.15, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework,
				"Play Treasures");
		stopDiscard = new ButtonClass(0.75, 0.25, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Stop Discard");
		stopTrash = new ButtonClass(0.75, 0.25, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Stop Trash");
		takeCards = new ButtonClass(0.75, 0.25, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Take Cards");
		putBack = new ButtonClass(0.75, 0.75, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Put Back");
		takeThiefCards = new ButtonClass(0.75, 0.25, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Take Thief Cards");
		putBackThiefCards = new ButtonClass(0.75, 0.75, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Put Back Thief Cards");
		takeDrawedCard = new ButtonClass(0.75, 0.25, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Take Drawed Card");
		setAsideDrawedCard = new ButtonClass(0.75, 0.75, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Set Aside Drawed Card");
//		takeCards = new ButtonClass(0.75, 0.75, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Temporary Trash");
		discardDeck = new ButtonClass(0.75, 0.25, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "Discard Deck");
		endTurn = new ButtonClass(0.75, 0.35, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework, "End Turn");
		endReactions = new ButtonClass(0.75, 0.25, 0.12, 0.05, WIDTH, HEIGHT, 1, buttonImage, framework,
				"End Reactions");

		action = new DisplayValue(0.1, 0.3, 0.12, 0.12, 1, 1, 1, displayImageActions, framework,
				String.valueOf(GameConstant.INIT_ACTIONS));
		coin = new DisplayValue(0.1, 0.4, 0.12, 0.12, 1, 1, 1, displayImageCoins, framework,
				String.valueOf(GameConstant.INIT_TREASURES));
		buy = new DisplayValue(0.1, 0.5, 0.12, 0.12, 1, 1, 1, displayImageBuys, framework,
				String.valueOf(GameConstant.INIT_PURCHASES));

		framework.addComponent(new GameBackground(0, 0, 1, 1, 0, backgroundImage, framework));
		framework.addComponent(new GameBackground(0.31, 0.01, 0.38, 0.38, 2, tableImage, framework));
		framework.addComponent(closeButton);
		framework.addComponent(endActionPhase);

		framework.addComponent(endTurn);

		framework.addComponent(action);
		framework.addComponent(coin);
		framework.addComponent(buy);

		this.revalidate();
		this.repaint();

	}

	/**
	 * a method to load Image
	 * 
	 * @param im
	 * @param resource
	 * @return
	 */

	private BufferedImage loadingImage(BufferedImage im, String resource) {
		try {
			im = ImageIO.read(getClass().getClassLoader().getResourceAsStream(resource));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return im;

	}

	/**
	 * resetting method for the value button to its initial values
	 */

	public void reset() {

		this.framework.removeComponent(action);
		this.framework.removeComponent(coin);
		this.framework.removeComponent(buy);

		action = new DisplayValue(0.1, 0.3, 0.12, 0.12, 1, WIDTH, HEIGHT, displayImageActions, framework,
				String.valueOf(GameConstant.INIT_ACTIONS));
		coin = new DisplayValue(0.1, 0.4, 0.12, 0.12, 1, WIDTH, HEIGHT, displayImageCoins, framework,
				String.valueOf(GameConstant.INIT_TREASURES));
		buy = new DisplayValue(0.1, 0.5, 0.12, 0.12, 1, WIDTH, HEIGHT, displayImageBuys, framework,
				String.valueOf(GameConstant.INIT_PURCHASES));

		this.framework.addComponent(action);
		this.framework.addComponent(coin);
		this.framework.addComponent(buy);

		this.framework.addComponent(endActionPhase);
		this.framework.removeComponent(playTreasures);

		for (Iterator<Card> iterator = this.middleCards.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			this.framework.removeComponent(card);
		}
		this.middleCards = new LinkedList<Card>();

		this.repaint();
	}

	/**
	 * action cards which are on the table. The basic limit 10 cards
	 * 
	 * @param table
	 */

	public void tableActionCards(LinkedHashMap<String, SerializedCard> table) {
		LinkedList<String> actionCardlds = new LinkedList<>(table.keySet());
		double shift = 0.295;
		double shiftBottom = 0.295;
		double shiftCard = 0.29;
		double shiftCardBottom = 0.29;
		int k = 3;
		int l = 4;

		for (int i = 0; i < tableCards.size(); i++) {
			Card card = tableCards.get(i);
			GFButton button = tableButtons.get(i);
			this.framework.removeComponent(card);
			this.framework.removeComponent(button);

		}
		this.tableButtons = new LinkedList<GFButton>();
		this.tableCards = new LinkedList<Card>();

		for (int i = 0; i < table.size(); i++) {
		if (actionCardlds.get(i).equals(GameConstant.NIL)) {
			System.out.println("nil");
			if (i < 5) {
				shift += 0.06;
				shiftCard += 0.06;
			}else{
				shiftBottom += 0.06;
				shiftCardBottom += 0.06;
			}
		}else{
			SerializedCard serializedCard = table.get(actionCardlds.get(i));

			// // Example For nishit
			Matcher matcher = Pattern.compile("\\d+").matcher(actionCardlds.get(i));
			matcher.find();
			String number = actionCardlds.get(i).substring(matcher.start(), matcher.end());

			if (i < 5) {
				Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(), serializedCard.getName(),
						serializedCard.getCost(), actionCardlds.get(i), shift += 0.06, 0.02, 0.05, 0.15, k++,
						serializedCard.getImage(), framework);

				GFButton button = new ButtonClass(shiftCard += 0.06, 0.02, 0.015, 0.015 * CORRECTION_16TO9, WIDTH,
						HEIGHT, l++, buttonGameImage, framework, number);

				framework.addComponent(button);
				framework.addComponent(card);
				this.tableCards.add(card);
				this.tableButtons.add(button);
			} else {
				Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(), serializedCard.getName(),
						serializedCard.getCost(), actionCardlds.get(i), shiftBottom += 0.06, 0.2, 0.05, 0.15, k++,
						serializedCard.getImage(), framework);
				GFButton button = new ButtonClass(shiftCardBottom += 0.06, 0.2, 0.015, 0.015 * CORRECTION_16TO9, WIDTH,
						HEIGHT, l++, buttonGameImage, framework, number);

				framework.addComponent(button);
				framework.addComponent(card);
				this.tableCards.add(card);
				this.tableButtons.add(button);
			}
		}
		}
	}

	/**
	 * for special action cards created. Example Spy card... New Table where the
	 * player/opponent pick cards.
	 * 
	 * @param cards
	 */

	public void extraTable(HashMap<String, SerializedCard> cards) {
		table = new GameBackground(0.31, 0.01, 0.38, 0.38, 15, tableImage, framework);
		framework.addComponent(table);

		int k = 16;
		double sub = cards.size();
		double shift = ((0.35 - (sub * 0.05))) / sub;
		double shiftsmall = ((0.2 - (sub * 0.05))) / sub;
		double startsmall = 0.4 - shiftsmall;
		double start = 0.31 - shift;

		LinkedList<String> actionCardlds = new LinkedList<>(cards.keySet());
		for (Iterator<Card> iterator = this.extraTableCards.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			this.framework.removeComponent(card);
		}
		this.extraTableCards = new LinkedList<Card>();
		for (int i = 0; i < cards.size(); i++) {
			if (sub > 4) {
				if (i == 0) {
					SerializedCard serializedCard = cards.get(actionCardlds.get(i));

					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), start += shift,
							0.13, 0.05, 0.15, k++, serializedCard.getImage(), framework);
					framework.addComponent(card);
					this.extraTableCards.add(card);
				} else {
					SerializedCard serializedCard = cards.get(actionCardlds.get(i));
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i),
							start += (shift + 0.05), 0.13, 0.05, 0.15, k++, serializedCard.getImage(), framework);
					framework.addComponent(card);
					this.extraTableCards.add(card);
				}
			} else {
				if (i == 0) {
					SerializedCard serializedCard = cards.get(actionCardlds.get(i));
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i),
							startsmall += shiftsmall, 0.13, 0.05, 0.15, k++, serializedCard.getImage(), framework);
					framework.addComponent(card);
					this.extraTableCards.add(card);
				} else {
					SerializedCard serializedCard = cards.get(actionCardlds.get(i));
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i),
							startsmall += (shiftsmall + 0.05), 0.13, 0.05, 0.15, k++, serializedCard.getImage(),
							framework);
					framework.addComponent(card);
					this.extraTableCards.add(card);
				}
			}
		}
	}

	/**
	 * 
	 * creating coin cards on the right hand side with 90 degree rotation.
	 * 
	 * @param coins
	 */

	public void coinCards(HashMap<String, SerializedCard> coins) {
		LinkedList<String> actionCardlds = new LinkedList<>(coins.keySet());
		double shift = -0.05;
		double shiftCard = -0.055;
		int k = 3;
		int l = 4;

		for (int i = 0; i < coinCards.size(); i++) {
			Card card = coinCards.get(i);
			GFButton button = coinButtons.get(i);
			this.framework.removeComponent(card);
			this.framework.removeComponent(button);

		}
		this.coinButtons = new LinkedList<GFButton>();
		this.coinCards = new LinkedList<Card>();

		for (int i = 0; i < coins.size(); i++) {			
			Matcher matcher = Pattern.compile("\\d+").matcher(actionCardlds.get(i));
			matcher.find();
			String number = actionCardlds.get(i).substring(matcher.start(), matcher.end());

			SerializedCard serializedCard = coins.get(actionCardlds.get(i));
			Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(), serializedCard.getName(),
					serializedCard.getCost(), actionCardlds.get(i), 0.95, shift += 0.12, 0.1, 0.1, k++,
					GraphicsUtil.rotate(serializedCard.getImage(), 270), framework);

			GFButton button = new ButtonClass(0.945, shiftCard += 0.12, 0.015, 0.015 * CORRECTION_16TO9, WIDTH, HEIGHT,
					l++, buttonGameImage, framework, number);
			framework.addComponent(button);
			framework.addComponent(card);
			this.coinCards.add(card);
			this.coinButtons.add(button);

		}

	}

	/**
	 * played cards by the user. They will appear in the middle of the board
	 * 
	 * @param middleCards
	 */

	public void middleCards(LinkedHashMap<String, SerializedCard> middleCards) {
		LinkedList<String> actionCardlds = new LinkedList<>(middleCards.keySet());
		int k = 14;
		double sub = middleCards.size();
		double shift = ((0.4 - (sub * 0.05))) / sub;
		double shiftsmall = ((0.2 - (sub * 0.05))) / sub;
		double startsmall = 0.4 - shiftsmall;
		double start = 0.3 - shift;

		for (Iterator<Card> iterator = this.middleCards.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			this.framework.removeComponent(card);
		}
		this.middleCards = new LinkedList<Card>();

		for (int i = 0; i < middleCards.size(); i++) {
			if (sub > 4) {
				if (i == 0) {
					SerializedCard serializedCard = middleCards.get(actionCardlds.get(i));

					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), start += shift,
							0.45, 0.05, 0.15, k++, serializedCard.getImage(), framework);
					framework.addComponent(card);
					this.middleCards.add(card);
				} else {
					SerializedCard serializedCard = middleCards.get(actionCardlds.get(i));
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i),
							start += (shift + 0.05), 0.45, 0.05, 0.15, k++, serializedCard.getImage(), framework);
					framework.addComponent(card);
					this.middleCards.add(card);
				}
			} else {
				if (i == 0) {
					SerializedCard serializedCard = middleCards.get(actionCardlds.get(i));
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i),
							startsmall += shiftsmall, 0.45, 0.05, 0.15, k++, serializedCard.getImage(), framework);
					framework.addComponent(card);
					this.middleCards.add(card);
				} else {
					SerializedCard serializedCard = middleCards.get(actionCardlds.get(i));
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i),
							startsmall += (shiftsmall + 0.05), 0.45, 0.05, 0.15, k++, serializedCard.getImage(),
							framework);
					framework.addComponent(card);
					this.middleCards.add(card);
				}
			}
		}
	}

	/**
	 * all hand cards from the player. (coins, victory, actions)
	 * 
	 * @param handCards
	 */

	public void handCards(LinkedHashMap<String, SerializedCard> handCards) {
		LinkedList<String> actionCardIds = new LinkedList<>(handCards.keySet());

		int k = 14;
		double sub = handCards.size();
		double shift = ((0.8 - (sub * 0.1))) / sub;
		double shiftsmall = ((0.4 - (sub * 0.1))) / sub;
		double start = 0.1 - shift;
		double startsmall = 0.2 - shiftsmall;

		// double shiftSmall = shift - 0.03;
		// double shiftOne = shiftSmall - 0.03;

		for (Iterator<Card> iterator = this.handCards.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			this.framework.removeComponent(card);
		}
		this.handCards = new LinkedList<Card>();

		for (int i = 0; i < handCards.size(); i++) {

			SerializedCard serializedCard = handCards.get(actionCardIds.get(i));

			// Example For nishit
			// Matcher matcher =
			// Pattern.compile("\\d+").matcher(actionCardIds.get(i));
			// matcher.find();
			// String number = actionCardIds.get(i).substring(matcher.start(),
			// matcher.end());

			if (sub > 7) {
				if (i == 0) {
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardIds.get(i), start += shift,
							0.70, 0.1, 0.3, k++, serializedCard.getImage(), framework);
					framework.addComponent(card);
					this.handCards.add(card);
				} else {
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardIds.get(i),
							start += (shift + 0.1), 0.70, 0.1, 0.3, k++, serializedCard.getImage(), framework);
					framework.addComponent(card);
					this.handCards.add(card);
				}
			} else {
				if (i == 0) {
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardIds.get(i),
							startsmall += shift, 0.70, 0.1, 0.3, k++, serializedCard.getImage(), framework);
					framework.addComponent(card);
					this.handCards.add(card);
				} else {
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardIds.get(i),
							startsmall += shiftsmall + 0.1, 0.70, 0.1, 0.3, k++, serializedCard.getImage(), framework);
					framework.addComponent(card);
					this.handCards.add(card);
				}
			}
		}
	}

	/**
	 * all victory cards are created here on the top left corner. 90° Rotation
	 * as well..
	 * 
	 * @param victory
	 */

	public void victoryCards(HashMap<String, SerializedCard> victory) {
		LinkedList<String> actionCardlds = new LinkedList<>(victory.keySet());
		double shift = -0.05;
		double shiftCard = -0.055;
		int k = 3;
		int l = 4;

		// Example For nishit

		for (int i = 0; i < victoryCards.size(); i++) {
			Card card = victoryCards.get(i);
			GFButton button = victoryButtons.get(i);
			this.framework.removeComponent(card);
			this.framework.removeComponent(button);

		}
		this.victoryButtons = new LinkedList<GFButton>();
		this.victoryCards = new LinkedList<Card>();

		for (int i = 0; i < victory.size(); i++) {

			// TODO Lukas commented section. Please verify first this class.
			Matcher matcher = Pattern.compile("\\d+").matcher(actionCardlds.get(i));
			matcher.find();
			String number = actionCardlds.get(i).substring(matcher.start(), matcher.end());

			SerializedCard serializedCard = victory.get(actionCardlds.get(i));
			Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(), serializedCard.getName(),
					serializedCard.getCost(), actionCardlds.get(i), -0.05, shift += 0.12, 0.1, 0.1, k++,
					GraphicsUtil.rotate(serializedCard.getImage(), 90), framework);
			GFButton button = new ButtonClass(0.04, shiftCard += 0.12, 0.015, 0.015 * CORRECTION_16TO9, WIDTH, WIDTH,
					l++, buttonGameImage, framework, number);
			framework.addComponent(button);
			framework.addComponent(card);

			this.victoryCards.add(card);
			this.victoryButtons.add(button);

		}
	}

	/**
	 * setting the caption on coin value bar.
	 * 
	 * @param caption
	 */

	public void setCaptionCoins(String caption) {
		// framework.removeComponent(coin);
		// coin = new DisplayValue(0.1, 0.4, 0.12, 0.12, 1, 1, 1,
		// displayImageCoins, framework, caption);
		// framework.addComponent(coin);
		coin.renewCaption(caption);
	}

	/**
	 * setting the caption on action value bar.
	 * 
	 * @param caption
	 */

	public void setCaptionActions(String caption) {
		action.renewCaption(caption);		
		// action.renewCaption(caption);
	}

	/**
	 * setting the caption on buy value bar.
	 * 
	 * @param caption
	 */

	public void setCaptionBuys(String caption) {
		buy.renewCaption(caption);
	}
	
	/**
	 *clearing the extra table if needed. 
	 * 
	 */
	
	public void removeTableComponents() {
		framework.removeComponent(table);
		for (int i = 0; i < extraTableCards.size(); i++) {
			framework.removeComponent(extraTableCards.get(i));
		}
		framework.removeComponent(table);
	}
	
	/**
	 * a simple button handler to end the action phase
	 * 
	 */

	public void endActionPhase() {
		framework.removeComponent(endActionPhase);
		framework.addComponent(playTreasures);
		this.repaint();
	}

	/**
	 * removing the endActionPhase Button
	 */
	
	public void removeEndActionPhaseButton() {
		framework.removeComponent(endActionPhase);
	}
	
	/**
	 * adding actionPhase Button
	 */

	public void addEndActionPhaseButton() {
		framework.addComponent(endActionPhase);
	}
	

	/**
	 * adding PlayTreasures Button
	 */

	public void addPlayTreasuresButton() {
		framework.addComponent(playTreasures);
	}
	
	/**
	 * removing the PlayTreasure Button
	 */

	public void removePlayTreasuresButton() {
		framework.removeComponent(playTreasures);
	}
	
	/**
	 * adding EndTurn Button
	 */

	public void addEndTurnButton() {
		framework.addComponent(endTurn);
	}
	
	/**
	 * removing EndTurn Button
	 */

	public void removeEndTurnButton() {
		framework.removeComponent(endTurn);
	}
	
	/**
	 * adding StopDiscard Button
	 */

	public void addStopDiscardButton() {
		framework.addComponent(stopDiscard);
		this.repaint();
	}
	
	/**
	 * removing StopDiscard Button
	 */

	public void removeStopDiscardButton() {
		framework.removeComponent(stopDiscard);
		this.repaint();
	}

	/**
	 * adding StopTrash Button
	 */
	
	public void addStopTrashButton() {
		framework.addComponent(this.stopTrash);
	}
	
	/**
	 * removing StopTrash Button
	 */

	public void removeStopTrashButton() {
		framework.removeComponent(this.stopTrash);
	}

	/**
	 * adding PutBack Button
	 */
	
	public void addPutBackButton() {
		framework.addComponent(putBack);
	}

	/**
	 * adding TakeCards Button
	 */
	
	public void addTakeCardsButton() {
		framework.addComponent(takeCards);
	}
	
	/**
	 * adding EndReactionMode Button
	 */

	public void addEndReactionModeButton() {
		framework.addComponent(this.endReactions);
	}
	
	public void removeEndReactionModeButton() {
		framework.removeComponent(this.endReactions);
	}
	
	/**
	 * adding DiscardDeck Button
	 */

	public void addDiscardDeckButton() {
		framework.addComponent(this.discardDeck);
	}
	

	/**
	 * adding ActionPhase Button
	 */
	
	public void addActionPhaseButton() {
		framework.addComponent(endActionPhase);
	}
	
	public void addTakeThiefCardsButtonRemoveOtherButtons() {
		framework.addComponent(takeThiefCards);
		framework.removeComponent(playTreasures);
		framework.removeComponent(endTurn);
	}
	
	public void addPutBackThiefCardsButton() {
		framework.addComponent(putBackThiefCards);
	}
	
	public void addTakeDrawedCard() {
		framework.addComponent(takeDrawedCard);
	}
	
	public void addSetAsideDrawedCard() {
		framework.addComponent(setAsideDrawedCard);
	}
	

}