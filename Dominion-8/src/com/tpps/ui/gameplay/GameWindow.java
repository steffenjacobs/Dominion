package com.tpps.ui.gameplay;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import com.tpps.application.game.DominionController;
import com.tpps.application.game.GameConstant;
import com.tpps.application.game.card.Card;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.GameLogTextPane;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.MyAudioPlayer;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.components.DisplayValue;
import com.tpps.ui.components.GFButton;
import com.tpps.ui.components.GameBackground;
import com.tpps.ui.lobbyscreen.ChatWindowForInGame;

/**
 * Main GUI Window where all components are merged together and ready for
 * GamePlay
 * 
 * @author Nishit Agrawal - nagrawal, Lukas Adler - ladler
 * 
 */
public class GameWindow extends JFrame {

	private static final long serialVersionUID = -5389003835573453281L;

	private final GraphicFramework framework;
	private static GameWindow instance;
	static GFButton closeButton, endActionPhase, playTreasures, endTurn, takeCards, putBack, takeThiefCards,
			putBackThiefCards, takeDrawedCard, setAsideDrawedCard;

	private GameBackground table;
	private BufferedImage closeImage, backgroundImage, tableImage, buttonImage, displayImageBuys, displayImageActions,
			displayImageTurnRed, displayImageCoins, buttonGameImage;

	private DisplayValue buy, coin, action, turn;
	private LinkedList<Card> victoryCards, coinCards, handCards, tableCards, middleCards, extraTableCards;
	private LinkedList<GFButton> victoryButtons, coinButtons, tableButtons;
	private ButtonClass stopDiscard, stopTrash, discardDeck, endReactions;

	private JTabbedPane jTabbedPane;
	private ChatWindowForInGame chatWindow;
	private GameLogTextPane loggerPane;
	private BufferedImage clickImage;
	private final int WIDTH, HEIGHT;
	private int reactionCounter, gameBackgroundCounter, topGap;

	private BufferedImage displayImageTurnGreen, muteImage, playImage;

	private static ButtonClass muteButton, playButton;
	
	private static final double CORRECTION_16TO9 = 16 / (double) 9;

	/**
	 * getter Method for GameWindow
	 * 
	 * @return the instance of the game-window
	 */

	public static GameWindow getInstance() {
		return instance;
	}

	/**
	 * getter Method for framework
	 * 
	 * @return the graphic-framework
	 */

	public GraphicFramework getGraphicFramework() {
		return this.framework;
	}

	/**
	 * 
	 * a constructor where the windowsize and functionality is set. Every Image
	 * is loaded here.
	 * 
	 * @throws IOException
	 */
	public GameWindow() throws IOException {
		GameWindow.instance = this;
		this.WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
		this.HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

		GameLog.log(MsgType.INIT, "GameWindow");
		this.loggerPane = GameLog.getTextPane();
		GameLog.log(MsgType.INIT, "GameLogTextPane");
		this.chatWindow = new ChatWindowForInGame();

		this.tabbedComponent();

		this.setTopGap(Toolkit.getDefaultToolkit().getScreenSize().height / 4);
		// this.leftGap = Toolkit.getDefaultToolkit().getScreenSize().width / 7;

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
		this.setMinimumSize(new Dimension(1280, 720));
		// this.setVisible(true);
		this.framework = new GraphicFramework(this);
		this.framework.setLayout(null);

		this.add(this.framework);

		// this.backgroundImage = this.loadingImage(backgroundImage,
		// "resources/img/gamePlay/GameBackground.jpg");
		this.backgroundImage = DominionController.selectedGameImage;
		GameLog.log(MsgType.GUI, "THIRD: " + this.backgroundImage);
		this.closeImage = this.loadingImage(this.closeImage, "resources/img/gameObjects/close.png");
		this.tableImage = this.loadingImage(this.tableImage, "resources/img/gameObjects/table.jpg");
		this.buttonImage = this.loadingImage(this.buttonImage, "resources/img/gameObjects/testButtonGame.png");
		this.displayImageBuys = this.loadingImage(this.displayImageBuys, "resources/img/gameObjects/Buys.png");
		this.displayImageCoins = this.loadingImage(this.displayImageCoins, "resources/img/gameObjects/Coins.png");
		this.displayImageActions = this.loadingImage(this.displayImageActions, "resources/img/gameObjects/Actions.png");
		this.displayImageTurnRed = this.loadingImage(this.displayImageTurnRed,
				"resources/img/gameObjects/ValueButtonRed.png");
		this.displayImageTurnGreen = this.loadingImage(this.displayImageTurnGreen,
				"resources/img/gameObjects/ValueButtonGreen.png");
		this.buttonGameImage = this.loadingImage(this.buttonGameImage, "resources/img/gameObjects/ButtonsGame.png");
		this.clickImage = this.loadingImage(this.clickImage, "resources/img/gameObjects/CardGreen.png");
		this.muteImage = this.loadingImage(this.muteImage, "resources/img/gameObjects/Mute.png");
		this.playImage = this.loadingImage(this.playImage, "resources/img/gameObjects/Play.png");

		GameWindow.closeButton = new ButtonClass(0.97, 0.01, 0.015, 0.015 * CORRECTION_16TO9, getWIDTH(), getWIDTH(), 1,
				this.closeImage, this.framework, "", "exit");
		GameWindow.muteButton = new ButtonClass(0.03, 0.01, 0.02, 0.02 * CORRECTION_16TO9, getWIDTH(), getWIDTH(), 1,
				this.muteImage, this.framework, "", "mute");
		GameWindow.playButton = new ButtonClass(0.03, 0.01, 0.02, 0.02 * CORRECTION_16TO9, getWIDTH(), getWIDTH(), 1,
				this.playImage, this.framework, "", "play");
		GameWindow.endActionPhase = new ButtonClass(0.75, 0.05, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1,
				this.buttonImage, this.framework, "End ActionPhase");
		GameWindow.playTreasures = new ButtonClass(0.75, 0.15, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1, this.buttonImage,
				this.framework, "Play Treasures");
		GameWindow.takeCards = new ButtonClass(0.75, 0.25, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1, this.buttonImage,
				this.framework, "Take Cards");
		GameWindow.putBack = new ButtonClass(0.75, 0.5, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1, this.buttonImage,
				this.framework, "Put Back");
		GameWindow.takeThiefCards = new ButtonClass(0.75, 0.25, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1,
				this.buttonImage, this.framework, "Take Thief Cards");
		GameWindow.putBackThiefCards = new ButtonClass(0.75, 0.5, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1,
				this.buttonImage, this.framework, "Put Back Thief Cards");
		GameWindow.takeDrawedCard = new ButtonClass(0.75, 0.25, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1,
				this.buttonImage, this.framework, "Take Drawed Card");
		GameWindow.setAsideDrawedCard = new ButtonClass(0.75, 0.5, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1,
				this.buttonImage, this.framework, "Set Aside Drawed Card");
		GameWindow.endTurn = new ButtonClass(0.75, 0.35, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1, buttonImage, framework,
				"End Turn");

		// this. takeCards = new ButtonClass(0.75, 0.75, 0.12, 0.05, WIDTH,
		// HEIGHT, 1, buttonImage, framework, "Temporary Trash");
		this.discardDeck = new ButtonClass(0.75, 0.25, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1, buttonImage, framework,
				"Discard Deck");
		this.endReactions = new ButtonClass(0.75, 0.25, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1, buttonImage, framework,
				"End Reactions");
		this.stopDiscard = new ButtonClass(0.75, 0.25, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1, this.buttonImage,
				this.framework, "Stop Discard");
		this.stopTrash = new ButtonClass(0.75, 0.25, 0.12, 0.05, getWIDTH(), getHEIGHT(), 1, this.buttonImage,
				this.framework, "Stop Trash");
		this.action = new DisplayValue(0.1, 0.38, 0.12, 0.12, 1, 1, 20, this.displayImageActions, this.framework,
				String.valueOf(GameConstant.INIT_ACTIONS.getValue()));
		this.coin = new DisplayValue(0.1, 0.46, 0.12, 0.12, 1, 1, 20, this.displayImageCoins, this.framework,
				String.valueOf(GameConstant.INIT_COINS.getValue()));
		this.buy = new DisplayValue(0.1, 0.54, 0.12, 0.12, 1, 1, 20, this.displayImageBuys, this.framework,
				String.valueOf(GameConstant.INIT_PURCHASES.getValue()));
		this.turn = new DisplayValue(0.31, 0, 0.38, 0.05, 1, 1, 20, this.displayImageTurnRed, this.framework, "#");

		this.framework.addComponent(new GameBackground(0, 0, 1, 1, 0, this.backgroundImage, this.framework));
		this.framework.addComponent(new GameBackground(0.31, 0.05, 0.38, 0.38, 2, this.tableImage, this.framework));
		this.framework.addComponent(GameWindow.playButton);
		this.framework.addComponent(GameWindow.closeButton);
		this.framework.addComponent(GameWindow.endActionPhase);
		this.framework.addComponent(GameWindow.endTurn);
		this.framework.addComponent(this.action);
		this.framework.addComponent(this.coin);
		this.framework.addComponent(this.buy);
		this.framework.addComponent(this.turn);

		// this.framework.add(this.chatWindow);
		// this.framework.add(this.loggerPane);
		this.framework.add(this.jTabbedPane);

		this.addComponentListener(new MyComponentAdapter());
		this.setFocusable(true);
		this.addKeyListener(new KeyListener() {

			private boolean trigger = true;

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			/**
			 * 
			 * chatWindow pop up animation added
			 * 
			 * @param e
			 */
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_1) {
					jTabbedPane.setSelectedIndex(0);
				}
				if (e.getKeyCode() == KeyEvent.VK_2) {
					jTabbedPane.setSelectedIndex(1);
				}
				if (Character.isSpaceChar(e.getKeyChar())) {
					if (trigger) {
						jTabbedPane.setVisible(false);
						trigger = false;
					} else {
						jTabbedPane.setVisible(true);
						trigger = true;
					}
				}
			}
		});
		MyAudioPlayer.handleGameMusic(true);
		this.revalidate();
		this.repaint();
	}

	private void tabbedComponent() {
		UIManager.put("TabbedPane.opaque", Boolean.FALSE);
		UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
		this.jTabbedPane = new JTabbedPane(JTabbedPane.TOP);

		this.jTabbedPane.setOpaque(false);
		this.jTabbedPane.add("Chat", this.chatWindow);
		this.jTabbedPane.add("Game Log", this.loggerPane);
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
		this.framework.removeComponent(discardDeck);

		action = new DisplayValue(0.1, 0.38, 0.12, 0.12, 1, getWIDTH(), getHEIGHT(), displayImageActions, framework,
				String.valueOf(GameConstant.INIT_ACTIONS.getValue()));
		coin = new DisplayValue(0.1, 0.46, 0.12, 0.12, 1, getWIDTH(), getHEIGHT(), displayImageCoins, framework,
				String.valueOf(GameConstant.INIT_COINS.getValue()));
		buy = new DisplayValue(0.1, 0.54, 0.12, 0.12, 1, getWIDTH(), getHEIGHT(), displayImageBuys, framework,
				String.valueOf(GameConstant.INIT_PURCHASES.getValue()));

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

	public synchronized void tableActionCards(LinkedHashMap<String, SerializedCard> table) {
		LinkedList<String> actionCardlds = new LinkedList<>(table.keySet());
		double shift = 0.295;
		double shiftBottom = 0.295;
		double shiftCard = 0.29;
		double shiftCardBottom = 0.29;
		int k = 3;
		int l = 103;

		for (int i = 0; i < tableCards.size(); i++) {
			Card card = tableCards.get(i);
			GFButton button = tableButtons.get(i);
			this.framework.removeComponent(card);
			this.framework.removeComponent(button);

		}
		this.tableButtons = new LinkedList<GFButton>();
		this.tableCards = new LinkedList<Card>();
		GameLog.log(MsgType.GUI, Arrays.toString(actionCardlds.toArray()));
		for (int i = 0; i < table.size(); i++) {
			GameLog.log(MsgType.GUI, actionCardlds.get(i));
			if (actionCardlds.get(i).matches("[A-Z][a-z]+#")) {
				if (i < 5) {
					shift += 0.06;
					shiftCard += 0.06;
				} else {
					shiftBottom += 0.06;
					shiftCardBottom += 0.06;
				}
			} else {
				SerializedCard serializedCard = table.get(actionCardlds.get(i));

				// // Example For nishit
				Matcher matcher = Pattern.compile("\\d+").matcher(actionCardlds.get(i));
				matcher.find();
				String number = actionCardlds.get(i).substring(matcher.start(), matcher.end());
				number = String.valueOf((Integer.parseInt(number) + 1));

				if (i < 5) {
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i), shift += 0.06,
							0.07, 0.05, 0.15, k++, serializedCard.getImage(), framework);

					GFButton button = new ButtonClass(shiftCard += 0.06, 0.07, 0.015, 0.015 * CORRECTION_16TO9,
							getWIDTH(), getHEIGHT(), l, buttonGameImage, framework, number,"NumberButton");

					framework.addComponent(button);
					framework.addComponent(card);
					this.tableCards.add(card);
					this.tableButtons.add(button);
				} else {
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i),
							shiftBottom += 0.06, 0.25, 0.05, 0.15, k++, serializedCard.getImage(), framework);
					GFButton button = new ButtonClass(shiftCardBottom += 0.06, 0.25, 0.015, 0.015 * CORRECTION_16TO9,
							getWIDTH(), getHEIGHT(), l, buttonGameImage, framework, number,"NumberButton");

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
		for (int i = 0; i < extraTableCards.size(); i++) {
			framework.removeComponent(extraTableCards.get(i));
			// framework.removeComponent(tableButtons.get(i));
		}
		if (table != null) {
			GameLog.log(MsgType.GUI, "remove table");
			framework.removeComponent(table);
		}
		extraTableCards = new LinkedList<Card>();

		table = new GameBackground(0.31, 0.05, 0.38, 0.38, 15, tableImage, framework);
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
		for (int i = 0; i < tableButtons.size(); i++) {
			this.framework.removeComponent(tableButtons.get(i));
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
		int l = 103;

		for (int i = 0; i < coinCards.size(); i++) {
			Card card = coinCards.get(i);
			GFButton button = coinButtons.get(i);
			this.framework.removeComponent(card);
			this.framework.removeComponent(button);

		}
		this.coinButtons = new LinkedList<GFButton>();
		this.coinCards = new LinkedList<Card>();
		GameLog.log(MsgType.GUI, Arrays.toString(actionCardlds.toArray()));
		for (int i = 0; i < coins.size(); i++) {
			Matcher matcher = Pattern.compile("\\d+").matcher(actionCardlds.get(i));
			matcher.find();
			String number = actionCardlds.get(i).substring(matcher.start(), matcher.end());
			number = String.valueOf((Integer.parseInt(number) + 1));

			SerializedCard serializedCard = coins.get(actionCardlds.get(i));
			Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(), serializedCard.getName(),
					serializedCard.getCost(), actionCardlds.get(i), 0.94, shift += 0.12, 0.1, 0.1, k++,
					GraphicsUtil.rotate(serializedCard.getImage(), 270), framework, "Coins");

			GFButton button = new ButtonClass(0.935, shiftCard += 0.12, 0.015, 0.015 * CORRECTION_16TO9, getWIDTH(),
					getHEIGHT(), l, buttonGameImage, framework, number,"NumberButton");
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
							0.45, 0.05, 0.15, k++, serializedCard.getImage(), framework, "middleCards");
					framework.addComponent(card);
					this.middleCards.add(card);
				} else {
					SerializedCard serializedCard = middleCards.get(actionCardlds.get(i));
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i),
							start += (shift + 0.05), 0.45, 0.05, 0.15, k++, serializedCard.getImage(), framework,
							"middleCards");
					framework.addComponent(card);
					this.middleCards.add(card);
				}
			} else {
				if (i == 0) {
					SerializedCard serializedCard = middleCards.get(actionCardlds.get(i));
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i),
							startsmall += shiftsmall, 0.45, 0.05, 0.15, k++, serializedCard.getImage(), framework,
							"middleCards");
					framework.addComponent(card);
					this.middleCards.add(card);
				} else {
					SerializedCard serializedCard = middleCards.get(actionCardlds.get(i));
					Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
							serializedCard.getName(), serializedCard.getCost(), actionCardlds.get(i),
							startsmall += (shiftsmall + 0.05), 0.45, 0.05, 0.15, k++, serializedCard.getImage(),
							framework, "middleCards");
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

	public synchronized void handCards(LinkedHashMap<String, SerializedCard> handCards) {
		synchronized (this) {
			LinkedList<String> actionCardIds = new LinkedList<>(handCards.keySet());

			int k = 14;
			String handTrigger = "handCards";
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
				// String number =
				// actionCardIds.get(i).substring(matcher.start(),
				// matcher.end());

				if (sub > 7) {
					if (i == 0) {
						Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
								serializedCard.getName(), serializedCard.getCost(), actionCardIds.get(i),
								start += shift, 0.65, 0.1, 0.3, k++, serializedCard.getImage(), framework, handTrigger);
						framework.addComponent(card);
						this.handCards.add(card);
					} else {
						Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
								serializedCard.getName(), serializedCard.getCost(), actionCardIds.get(i),
								start += (shift + 0.1), 0.65, 0.1, 0.3, k++, serializedCard.getImage(), framework,
								handTrigger);
						framework.addComponent(card);
						this.handCards.add(card);
					}
				} else {
					// GameLog.log(MsgType.GUI, "Wie viel handkarten: " +
					// handCards.size() + "serialized Card "
					// + serializedCard.getImage());
					if (i == 0) {
						Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
								serializedCard.getName(), serializedCard.getCost(), actionCardIds.get(i),
								startsmall += shift, 0.65, 0.1, 0.3, k++, serializedCard.getImage(), framework,
								handTrigger);
						framework.addComponent(card);
						this.handCards.add(card);
					} else {
						Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(),
								serializedCard.getName(), serializedCard.getCost(), actionCardIds.get(i),
								startsmall += shiftsmall + 0.1, 0.65, 0.1, 0.3, k++, serializedCard.getImage(),
								framework, handTrigger);
						framework.addComponent(card);
						this.handCards.add(card);
					}
				}
			}
		}
	}

	/**
	 * all victory cards are created here on the top left corner. 90� Rotation
	 * as well..
	 * 
	 * @param victory
	 */

	public void victoryCards(HashMap<String, SerializedCard> victory) {
		LinkedList<String> actionCardlds = new LinkedList<>(victory.keySet());
		double shift = -0.05;
		double shiftCard = -0.055;
		int k = 3;
		int l = 103;

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
			number = String.valueOf((Integer.parseInt(number) + 1));

			SerializedCard serializedCard = victory.get(actionCardlds.get(i));
			Card card = new Card(serializedCard.getActions(), serializedCard.getTypes(), serializedCard.getName(),
					serializedCard.getCost(), actionCardlds.get(i), -0.05, shift += 0.12, 0.1, 0.1, k++,
					GraphicsUtil.rotate(serializedCard.getImage(), 90), framework, "Victory");
			GFButton button = new ButtonClass(0.04, shiftCard += 0.12, 0.015, 0.015 * CORRECTION_16TO9, getWIDTH(),
					getWIDTH(), l, buttonGameImage, framework, number,"NumberButton");
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
		framework.removeComponent(coin);
		coin = new DisplayValue(0.1, 0.46, 0.12, 0.12, 1, 1, 1, displayImageCoins, framework, caption);
		framework.addComponent(coin);
		// coin.renewCaption(caption);
	}

	/**
	 * setting the caption on action value bar.
	 * 
	 * @param caption
	 */

	public void setCaptionActions(String caption) {
		// action.renewCaption(caption);
		framework.removeComponent(action);
		action = new DisplayValue(0.1, 0.38, 0.12, 0.12, 1, 1, 1, displayImageActions, framework, caption);
		framework.addComponent(action);
		// action.renewCaption(caption);
	}

	/**
	 * 
	 * a method to set the caption on the TurnButton.
	 * 
	 * @param caption
	 */

	public void setCaptionTurn(String caption) {
		// turn.renewCaption(caption);
		framework.removeComponent(turn);
		if (caption.equals("my turn") || caption.equals("react")) {
			turn = new DisplayValue(0.31, 0, 0.38, 0.05, 1, 1, 20, displayImageTurnGreen, framework, caption);

		} else {
			turn = new DisplayValue(0.31, 0, 0.38, 0.05, 1, 1, 20, displayImageTurnRed, framework, caption);
		}
		framework.addComponent(turn);
	}

	/**
	 * setting the caption on buy value bar.
	 * 
	 * @param caption
	 */

	public void setCaptionBuys(String caption) {
		framework.removeComponent(buy);
		buy = new DisplayValue(0.1, 0.54, 0.12, 0.12, 1, 1, 1, displayImageBuys, framework, caption);
		framework.addComponent(buy);
		// buy.renewCaption(caption);
	}

	/**
	 * clearing the extra table if needed.
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

	/**
	 * 
	 * removing EndReactionMode Button
	 */
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

	/**
	 * 
	 * adding TakeThiefCardsButton and removing all other Buttons from the
	 * GameBoard
	 * 
	 */

	public void addTakeThiefCardsButtonRemoveOtherButtons() {
		framework.addComponent(takeThiefCards);
		framework.removeComponent(playTreasures);
		framework.removeComponent(endTurn);
	}

	/**
	 * 
	 * adding PutBackThiefCards Button to the GameBoard
	 * 
	 */

	public void addPutBackThiefCardsButton() {
		framework.addComponent(putBackThiefCards);
	}

	/**
	 * 
	 * adding TakeDrawedCard Button
	 * 
	 */

	public void addTakeDrawedCard() {
		framework.addComponent(takeDrawedCard);
	}

	/**
	 * 
	 * adding setasideDrawedCard Button
	 * 
	 */

	public void addSetAsideDrawedCard() {
		framework.addComponent(setAsideDrawedCard);
	}

	private class MyComponentAdapter extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			super.componentResized(e);

			int width = GameWindow.this.getContentPane().getWidth();
			int height = GameWindow.this.getContentPane().getHeight();

			int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
			int maxHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

			onResize(width / Double.parseDouble(Integer.toString(maxWidth)),
					height / Double.parseDouble(Integer.toString(maxHeight)));
			repaint();
		}

	}

	private void onResize(double relativeWidth, double relativeHeight) {
		double width = (relativeWidth * WIDTH) / 4;
		double height = (relativeHeight * HEIGHT) / 4;
		int y = this.getHeight();
		int x = this.getWidth();
		// GameLog.log(MsgType.GUI, "width: "+width + "height "+height +
		// "relativeWidth "+relativeWidth + "relativeHeight "+relativeHeight
		// +"WIDTH "+ WIDTH + "HEIGHT "+ HEIGHT);
		this.jTabbedPane.setBounds(x - (int) ((WIDTH / 3.5) * relativeWidth),
				y - (int) ((HEIGHT * (1 - 0.65)) * relativeHeight), (int) (width), (int) height);
		repaint();
		revalidate();
	}

	/**
	 * 
	 * getter Method for background Image
	 * 
	 * @return hte background image
	 */

	public BufferedImage getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * 
	 * setting method for backgroundImage
	 * 
	 * @param backgroundImage
	 */

	public void setBackgroundImage(BufferedImage backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	/**
	 * 
	 * get Screen resolution Width
	 * 
	 * @return the width
	 */

	public int getWIDTH() {
		return WIDTH;
	}

	/**
	 * get Screen resolution Height
	 * 
	 * @return the height
	 */

	public int getHEIGHT() {
		return HEIGHT;
	}

	/**
	 * 
	 * reactionCounter for the Card Class. How many times it was clicked
	 * 
	 */

	public void reactionCounterInkrement() {
		reactionCounter++;
	}

	/**
	 * 
	 * setting method
	 * 
	 * @param reactionCounter
	 */

	public void setReactionCounter(int reactionCounter) {
		this.reactionCounter = reactionCounter;
	}

	/**
	 * 
	 * getter of ReactionCounter
	 * 
	 * @return the reaction counter
	 */

	public int getReactionCounter() {
		return reactionCounter;
	}

	/**
	 * 
	 * getter Method for GameBackgroundCounter
	 * 
	 * @return the backgroundcounter
	 */

	public int getGameBackgroundCounter() {
		return gameBackgroundCounter;
	}

	/**
	 * 
	 * gameBackgroundCoutnerInkrement. How many times the background was
	 * clicked.
	 */

	public void gameBackgroundCounterInkrement() {
		gameBackgroundCounter++;
	}

	/**
	 * 
	 * setter Method
	 * 
	 * @param gamebackgroundCounter
	 */

	public void setGamebackgroundCounter(int gamebackgroundCounter) {
		this.gameBackgroundCounter = gamebackgroundCounter;
	}

	/**
	 * getter method
	 * 
	 * @return the click image
	 */

	public BufferedImage getClickImage() {
		return clickImage;
	}

	/**
	 * getter method for ChatWindow
	 * 
	 * @return the chat window
	 */

	public ChatWindowForInGame getChatWindow() {
		return chatWindow;
	}

	/**
	 * @return the topGap
	 */
	public int getTopGap() {
		return topGap;
	}

	/**
	 * getter Method
	 * 
	 * @return the coin buttons
	 */

	public LinkedList<GFButton> getCoinButtons() {
		return coinButtons;
	}

	/**
	 * getter Method
	 * 
	 * @return the mute button
	 */

	public ButtonClass getMuteButton() {
		return muteButton;
	}

	/**
	 * 
	 * getter Method
	 * 
	 * @return the play button
	 */

	public ButtonClass getPlayButton() {
		return playButton;
	}

	/**
	 * 
	 * getter Method
	 * 
	 * @return the victory buttons
	 */

	public LinkedList<GFButton> getVictoryButtons() {
		return victoryButtons;
	}

	/**
	 * @param topGap
	 *            the topGap to set
	 */
	public void setTopGap(int topGap) {
		this.topGap = topGap;
	}
}