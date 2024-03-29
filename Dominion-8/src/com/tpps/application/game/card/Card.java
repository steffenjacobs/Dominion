package com.tpps.application.game.card;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import com.tpps.application.game.CardName;
import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayCard;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.MyAudioPlayer;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.animations.AnimationDirection;
import com.tpps.ui.animations.MoveAnimation;
import com.tpps.ui.components.GameBackground;

/**
 * Card class represents a GameObject on the board as a card.
 * 
 * @author Nicolas Wipfler, Steffen Jacobs
 */
public class Card extends GameObject {

	private static final long serialVersionUID = -4157717625890678601L;
	/**
	 * if the card is VICTORY/TREASURE, String is the value (not the cost)
	 */
	private final LinkedHashMap<CardAction, String> actions;
	private final LinkedList<CardType> types;
	private final int cost;
	private final String name;
	private final String id;
	private GraphicFramework parent;
	private double relativeX, relativeY, relativeWidth, relativeHeight;
	private BufferedImage sourceImage;
	private GameBackground gameBackground;
	private String handTrigger = "";

	private MoveAnimation moveAnimation = null;

	private AnimationDirection animDir = AnimationDirection.STATIC;

	private boolean mouseOver = false;

	private static int classID = 0;

	/**
	 * 
	 * @param actions
	 *            the actions of the card
	 * @param types
	 *            the cardTypes of the card (e.g. VICTORY and ACTION, see
	 *            http://www.brettspiele-report.de/images/dominion/die_intrige/
	 *            dominion_die_intrige_spielkarte_adelige.jpg)
	 * @param name
	 *            the name of the card
	 * @param cost
	 *            the cost of the card
	 * @param cardId
	 *            the cardID of the card
	 * @param relativeLocX
	 *            the relative X-location on the screen
	 * @param relativeLocY
	 *            the relative Y-location on the screen
	 * @param relativeWidth
	 *            the relative width
	 * @param relativeHeight
	 *            the relative height
	 * @param _layer
	 *            the layer the game-object is on
	 * @param sourceImage
	 *            the image-source for the image
	 * @param _parent
	 *            the parent framework
	 */
	public Card(LinkedHashMap<CardAction, String> actions, LinkedList<CardType> types, String name, int cost,
			double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight, int _layer,
			BufferedImage sourceImage, GraphicFramework _parent) {
		super(relativeLocX, relativeLocY, relativeWidth, relativeHeight, _layer, sourceImage, _parent);
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.types = types;
		this.parent = _parent;
		this.relativeX = relativeLocX;
		this.relativeY = relativeLocY;
		this.relativeWidth = relativeWidth;
		this.relativeHeight = relativeHeight;
		this.sourceImage = sourceImage;
		this.id = this.name + classID++;
	}

	/**
	 * 
	 * @param actions
	 *            the actions of the card
	 * @param types
	 *            the cardTypes of the card (e.g. VICTORY and ACTION, see
	 *            http://www.brettspiele-report.de/images/dominion/die_intrige/
	 *            dominion_die_intrige_spielkarte_adelige.jpg)
	 * @param name
	 *            the name of the card
	 * @param cost
	 *            the cost of the card
	 * @param cardId
	 *            the cardID of the card
	 * @param relativeLocX
	 *            the relative X-location on the screen
	 * @param relativeLocY
	 *            the relative Y-location on the screen
	 * @param relativeWidth
	 *            the relative width
	 * @param relativeHeight
	 *            the relative height
	 * @param _layer
	 *            the layer the game-object is on
	 * @param sourceImage
	 *            the image-source for the image
	 * @param _parent
	 *            the parent framework
	 */
	public Card(LinkedHashMap<CardAction, String> actions, LinkedList<CardType> types, String name, int cost,
			String cardId, double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight,
			int _layer, BufferedImage sourceImage, GraphicFramework _parent) {
		super(relativeLocX, relativeLocY, relativeWidth, relativeHeight, _layer, sourceImage, _parent);
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.sourceImage = sourceImage;
		this.parent = _parent;
		this.relativeX = relativeLocX;
		this.relativeY = relativeLocY;
		this.relativeWidth = relativeWidth;
		this.relativeHeight = relativeHeight;
		this.types = types;
		this.id = cardId;
	}

	/**
	 *
	 * @param actions
	 *            the list of CardActions mapped to their value
	 * @param types
	 *            the list of CardTypes
	 * @param cost
	 *            the card-cost
	 * @param name
	 *            the name of the card
	 * @param _parent
	 *            graphic framework where the card will be drawn
	 */
	public Card(LinkedHashMap<CardAction, String> actions, LinkedList<CardType> types, String name, int cost,
			GraphicFramework _parent) {
		super(_parent);
		this.name = name;
		this.actions = actions;
		this.parent = _parent;
		this.cost = cost;
		this.types = types;

		this.id = this.name + classID++;
	}

	/**
	 * 
	 * @param actions
	 *            the actions of the card
	 * @param types
	 *            the cardTypes of the card (e.g. VICTORY and ACTION, see
	 *            http://www.brettspiele-report.de/images/dominion/die_intrige/
	 *            dominion_die_intrige_spielkarte_adelige.jpg)
	 * @param name
	 *            the name of the card
	 * @param cost
	 *            the cost of the card
	 */
	public Card(LinkedHashMap<CardAction, String> actions, LinkedList<CardType> types, String name, int cost) {
		super();
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.types = types;
		this.id = this.name + classID++;
	}

	/**
	 * 
	 * @param actions
	 *            the actions of the card
	 * @param types
	 *            the cardTypes of the card (e.g. VICTORY and ACTION, see
	 *            http://www.brettspiele-report.de/images/dominion/die_intrige/
	 *            dominion_die_intrige_spielkarte_adelige.jpg)
	 * @param name
	 *            the name of the card
	 * @param cost
	 *            the cost of the card
	 * @param cardId
	 *            the cardID of the card
	 * @param relativeLocX
	 *            the relative X-location on the screen
	 * @param relativeLocY
	 *            the relative Y-location on the screen
	 * @param relativeWidth
	 *            the relative width
	 * @param relativeHeight
	 *            the relative height
	 * @param _layer
	 *            the layer the game-object is on
	 * @param sourceImage
	 *            the image-source for the image
	 * @param _parent
	 *            the parent framework
	 * @param handTrigger
	 *            a String value representing the players hand position on hover
	 *            action
	 */
	public Card(LinkedHashMap<CardAction, String> actions, LinkedList<CardType> types, String name, int cost,
			String cardId, double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight,
			int _layer, BufferedImage sourceImage, GraphicFramework _parent, String handTrigger) {
		super(relativeLocX, relativeLocY, relativeWidth, relativeHeight, _layer, sourceImage, _parent);
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.sourceImage = sourceImage;
		this.parent = _parent;
		this.relativeX = relativeLocX;
		this.relativeY = relativeLocY;
		this.relativeWidth = relativeWidth;
		this.relativeHeight = relativeHeight;
		this.types = types;
		this.id = cardId;
		this.handTrigger = handTrigger;
	}

	/**
	 * 
	 * @return the actions
	 */
	public LinkedHashMap<CardAction, String> getActions() {
		return actions;
	}

	/**
	 * 
	 * @return the types
	 */
	public LinkedList<CardType> getTypes() {
		return types;
	}

	/**
	 * 
	 * @return the cost
	 */
	public int getCost() {
		return this.cost;
	}

	/**
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * sets the classID to zero
	 */
	public static void resetClassID() {
		Card.classID = 0;
	}

	/**
	 * return a cloned Card Object
	 */
	@Override
	public Card clone() {
		return new Card(this.getActions(), this.getTypes(), this.getName(), this.getCost());
	}

	/**
	 * 
	 * @param relativeY
	 *            the relativeY to set
	 */
	public void setRelativeY(int relativeY) {
		this.relativeY = relativeY;
	}

	/**
	 * Hover animation
	 */
	@Override
	public void onMouseEnter() {
		mouseOver = true;
		if (!(handTrigger.equals("handCards") || name.equals(CardName.COPPER.getName())
				|| name.equals(CardName.SILVER.getName()) || name.equals(CardName.GOLD.getName())
				|| name.equals(CardName.CURSE.getName()) || name.equals(CardName.PROVINCE.getName())
				|| name.equals(CardName.DUCHY.getName()) || name.equals(CardName.ESTATE.getName()))) {
			gameBackground = new GameBackground(0.12, 0.01, relativeWidth + 0.08, relativeHeight + 0.24, 110,
					sourceImage, parent);
			parent.addComponent(gameBackground);
		}
		if (handTrigger.equals("Victory")) {
			checkAndPlayRightAnimation();
		}
		if (handTrigger.equals("Coins")) {
			checkAndPlayLeftAnimation();
		}
		if (handTrigger.equals("handCards")) {
			checkAndPlayUpAnimation();
		}
	}

	/**
	 * Hover animation
	 */

	public void onMouseExit() {
		mouseOver = false;
		if (!(handTrigger.equals("handCards") || name.equals(CardName.COPPER.getName())
				|| name.equals(CardName.SILVER.getName()) || name.equals(CardName.GOLD.getName())
				|| name.equals(CardName.CURSE.getName()) || name.equals(CardName.PROVINCE.getName())
				|| name.equals(CardName.DUCHY.getName()) || name.equals(CardName.ESTATE.getName()))) {
			parent.removeComponent(gameBackground);
		}
		if (handTrigger.equals("Victory")) {
			checkAndPlayRightBackAnimation();
		}
		if (handTrigger.equals("Coins")) {
			checkAndPlayLeftBackAnimation();
		}
		if (handTrigger.equals("handCards")) {
			checkAndPlayDownAnimation();
		}
	}

	private void checkAndPlayDownAnimation() {
		if (!mouseOver && animDir == AnimationDirection.STATIC) {
			animDir = AnimationDirection.DOWN;
			moveAnimation = new MoveAnimation(parent, this, 250, new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					animDir = AnimationDirection.STATIC;
					checkAndPlayUpAnimation();
					return null;
				}
			}, relativeX, relativeY);
			moveAnimation.play();
		}
	}

	private void checkAndPlayUpAnimation() {
		if (mouseOver && animDir == AnimationDirection.STATIC) {
			animDir = AnimationDirection.UP;
			moveAnimation = new MoveAnimation(parent, this, 250, new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					animDir = AnimationDirection.STATIC;
					checkAndPlayDownAnimation();
					return null;
				}
			}, relativeX, relativeY - .05);
			moveAnimation.play();
		}
	}

	private void checkAndPlayLeftAnimation() {
		if (mouseOver && animDir == AnimationDirection.STATIC) {
			animDir = AnimationDirection.LEFT;
			moveAnimation = new MoveAnimation(parent, this, 250, new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					animDir = AnimationDirection.STATIC;
					checkAndPlayLeftBackAnimation();
					return null;
				}
			}, relativeX - .05, relativeY);
			moveAnimation.play();
		}
	}

	private void checkAndPlayLeftBackAnimation() {
		if (!mouseOver && animDir == AnimationDirection.STATIC) {
			animDir = AnimationDirection.RIGHT;
			moveAnimation = new MoveAnimation(parent, this, 250, new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					animDir = AnimationDirection.STATIC;
					checkAndPlayLeftAnimation();
					return null;
				}
			}, relativeX, relativeY);
			moveAnimation.play();
		}
	}

	private void checkAndPlayRightAnimation() {
		if (mouseOver && animDir == AnimationDirection.STATIC) {
			animDir = AnimationDirection.LEFT;
			moveAnimation = new MoveAnimation(parent, this, 250, new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					animDir = AnimationDirection.STATIC;
					checkAndPlayRightBackAnimation();
					return null;
				}
			}, relativeX + 0.05, relativeY);
			moveAnimation.play();
		}
	}

	private void checkAndPlayRightBackAnimation() {
		if (!mouseOver && animDir == AnimationDirection.STATIC) {
			animDir = AnimationDirection.RIGHT;
			moveAnimation = new MoveAnimation(parent, this, 250, new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					animDir = AnimationDirection.STATIC;
					checkAndPlayRightAnimation();
					return null;
				}
			}, relativeX, relativeY);
			moveAnimation.play();
		}
	}

	/**
	 * 
	 */
	@Override
	public void onMouseClick() {
		if (DominionController.getInstance().isTurnFlag()) {
			GameLog.log(MsgType.GUI, "MouseClick on Card");
			try {
				DominionController.getInstance().getGameClient().sendMessage(
						new PacketPlayCard(this.id, DominionController.getInstance().getGameClient().getClientId()));
				DominionController.getInstance().setTurnFlag(false);
				DominionController.getInstance().getGameClient().getGameWindow().setCaptionTurn("execute");
				if (handTrigger.equals("Coins")) {
					MyAudioPlayer.doCashSound();
				}
				if (handTrigger.equals("Victory")) {
					MyAudioPlayer.doVictorySound();
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public void onMouseDrag() {
		if (DominionController.getInstance().isTurnFlag()) {
			GameLog.log(MsgType.GUI, "MouseClick on Card");
			try {
				DominionController.getInstance().getGameClient().sendMessage(
						new PacketPlayCard(this.id, DominionController.getInstance().getGameClient().getClientId()));
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * is called when resizing
	 */
	@Override
	public void onResize(int absWidth, int absHeight) {
		super.setRenderedImage(GraphicsUtil.resize((BufferedImage) super.getBufferedImage(),
				super.dimension.getAbsoluteX(absWidth), super.dimension.getAbsoluteY(absHeight)));
	}

	/**
	 * @override toString()
	 */
	@Override
	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		sBuf.append("Card: " + "'" + this.name + "'\nActions: <");
		Iterator<CardAction> actionsIt = actions.keySet().iterator();
		Iterator<String> intsIt = actions.values().iterator();
		while (actionsIt.hasNext() && intsIt.hasNext()) {
			sBuf.append("<" + actionsIt.next().toString() + ": " + intsIt.next() + ">");
			if (actionsIt.hasNext() && intsIt.hasNext())
				sBuf.append(" ");
		}
		Iterator<CardType> typesIt = types.iterator();
		sBuf.append(">\nTypes: <");
		while (typesIt.hasNext()) {
			sBuf.append("<" + typesIt.next().toString() + ">");
			if (typesIt.hasNext())
				sBuf.append(" ");
		}
		return sBuf.append(">\nCost: " + this.cost).toString();
	}
}
