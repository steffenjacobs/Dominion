package com.tpps.application.game.card;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import javax.swing.JFrame;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.gameSession.packets.PacketPlayCard;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.animations.MoveAnimation;
import com.tpps.ui.components.GameBackground;
import com.tpps.ui.gameplay.GameWindow;

/**
 * @author Nicolas Wipfler
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
	private Image sourceImage;
	private GameBackground gameBackground;
	private String handTrigger = "";
	private boolean animationEnabled = false;
	private boolean animationEnabledVictory = false;
	private boolean animationEnabledCoins = false;

	// private int mouseReaction = 0;
	// private ArrayList<GameBackground> cardReaction = new ArrayList<>();
	// private GameBackground tmp;
	private static int classID = 0;

	/**
	 * 
	 * @param actions
	 * @param types
	 * @param name
	 * @param cost
	 * @param relativeLocX
	 * @param relativeLocY
	 * @param relativeWidth
	 * @param relativeHeight
	 * @param _layer
	 * @param sourceImage
	 * @param _parent
	 */
	public Card(LinkedHashMap<CardAction, String> actions, LinkedList<CardType> types, String name, int cost,
			double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight, int _layer,
			Image sourceImage, GraphicFramework _parent) {
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
	 * @param types
	 * @param name
	 * @param cost
	 * @param cardId
	 * @param relativeLocX
	 * @param relativeLocY
	 * @param relativeWidth
	 * @param relativeHeight
	 * @param _layer
	 * @param sourceImage
	 * @param _parent
	 */
	public Card(LinkedHashMap<CardAction, String> actions, LinkedList<CardType> types, String name, int cost,
			String cardId, double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight,
			int _layer, Image sourceImage, GraphicFramework _parent) {
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
	 * constructor for Card, taking all required data
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
	 * Test Konstruktor
	 * 
	 * @param actions
	 * @param types
	 * @param name
	 * @param cost
	 */
	public Card(LinkedHashMap<CardAction, String> actions, LinkedList<CardType> types, String name, int cost) {
		super();
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.types = types;
		this.id = this.name + classID++;
		// System.out.println(id);
	}

	public Card(LinkedHashMap<CardAction, String> actions, LinkedList<CardType> types, String name, int cost,
			String cardId, double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight,
			int _layer, Image sourceImage, GraphicFramework _parent, String handTrigger) {
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
	 * @return
	 */
	public LinkedHashMap<CardAction, String> getActions() {
		return actions;
	}

	/**
	 * 
	 * @return
	 */
	public LinkedList<CardType> getTypes() {
		return types;
	}

	/**
	 * 
	 * @return
	 */
	public int getCost() {
		return this.cost;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @return
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
	 * 
	 */
	@Override
	public Card clone() {
		return new Card(this.getActions(), this.getTypes(), this.getName(),
				this.getCost()/* , this.getParent() */);
	}

	/**
	 * Hover animation
	 */
	@Override
	public void onMouseEnter() {
		if (!(handTrigger.equals("handCards") || name.equals("Copper") || name.equals("Silver") || name.equals("Gold")
				|| name.equals("Curse") || name.equals("Province") || name.equals("Duchy") || name.equals("Estate"))) {
			gameBackground = new GameBackground(0.12, 0.01, relativeWidth + 0.08, relativeHeight + 0.24, 110,
					sourceImage, parent);
			parent.addComponent(gameBackground);
		}
		if (handTrigger.equals("Victory")) {
			for (int i = 0; i < GameWindow.getInstance().getVictoryButtons().size(); i++) {
				parent.removeComponent(GameWindow.getInstance().getVictoryButtons().get(i));
			}
			// try {
			// Thread.sleep(50);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			if (!(animationEnabledVictory)) {
				MoveAnimation anim = new MoveAnimation(parent, this, 250, new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						animationEnabledVictory = true;
						return null;
					}
				}, relativeX + 0.05, relativeY);
				anim.play();
			}
		}
		if (handTrigger.equals("Coins")) {
			for (int i = 0; i < GameWindow.getInstance().getCoinButtons().size(); i++) {
				parent.removeComponent(GameWindow.getInstance().getCoinButtons().get(i));
			}
			// try {
			// Thread.sleep(50);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			if (!(animationEnabledCoins)) {
				MoveAnimation anim = new MoveAnimation(parent, this, 250, new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						animationEnabledCoins = true;
						return null;
					}
				}, relativeX - 0.05, relativeY);
				anim.play();
			}
		}

		if (handTrigger.equals("handCards")) {
			// try {
			// Thread.sleep(50);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			if (!(animationEnabled)) {
				MoveAnimation anim = new MoveAnimation(parent, this, 250, new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						animationEnabled = true;
						return null;
					}
				}, relativeX, relativeY - 0.05);
				anim.play();
			}
		}
	}

	/**
	 * Hover animation
	 */

	public void onMouseExit() {
		if (!(handTrigger.equals("handCards") || name.equals("Copper") || name.equals("Silver") || name.equals("Gold")
				|| name.equals("Curse") || name.equals("Province") || name.equals("Duchy") || name.equals("Estate"))) {
			parent.removeComponent(gameBackground);
		}
		if (handTrigger.equals("Victory")) {
			for (int i = 0; i < GameWindow.getInstance().getVictoryButtons().size(); i++) {
				parent.addComponent(GameWindow.getInstance().getVictoryButtons().get(i));
			}
			// try {
			// Thread.sleep(50);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			if (animationEnabledVictory) {
				MoveAnimation anim = new MoveAnimation(parent, this, 250, new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						animationEnabledVictory = false;
						return null;
					}
				}, relativeX, relativeY);
				anim.play();
			}
		}
		if (handTrigger.equals("Coins")) {
			for (int i = 0; i < GameWindow.getInstance().getCoinButtons().size(); i++) {
				parent.addComponent(GameWindow.getInstance().getCoinButtons().get(i));
			}
			// try {
			// Thread.sleep(50);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			if (animationEnabledCoins) {
				MoveAnimation anim = new MoveAnimation(parent, this, 250, new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						animationEnabledCoins = false;
						return null;
					}
				}, relativeX, relativeY);
				anim.play();
			}
		}
		if (handTrigger.equals("handCards")) {
			// try {
			// Thread.sleep(50);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			if (animationEnabled) {
				MoveAnimation anim = new MoveAnimation(parent, this, 250, new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						animationEnabled = false;
						return null;
					}
				}, relativeX, relativeY);
				anim.play();
			}
		}
	}

	public <T extends Client> void onAiClick(T client, int clientId) {
		System.out.println("AiClick on Card");
		try {
			client.sendMessage(new PacketPlayCard(this.id, clientId));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@Override
	public void onMouseClick() {
		if (DominionController.getInstance().isTurnFlag()) {
			System.out.println("MouseClick on Card");
			try {
				DominionController.getInstance().getGameClient().sendMessage(
						new PacketPlayCard(this.id, DominionController.getInstance().getGameClient().getClientId()));
				DominionController.getInstance().setTurnFlag(false);
				DominionController.getInstance().getGameClient().getGameWindow().setCaptionTurn("execute");
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
			System.out.println("MouseClick on Card");
			try {
				DominionController.getInstance().getGameClient().sendMessage(
						new PacketPlayCard(this.id, DominionController.getInstance().getGameClient().getClientId()));
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		// if (mouseReaction == 0 &&
		// !(handTrigger.equals("handCards"))&&DominionController.getInstance().isTurnFlag())
		// {
		// cardReaction = new GameBackground(this.relativeX, this.relativeY,
		// this.relativeWidth, this.relativeHeight,
		// 102, GameWindow.getInstance().getClickImage(), this.parent);
		// this.parent.addComponent(cardReaction);
		// mouseReaction++;
		// }
		// if (DominionController.getInstance().isTurnFlag()) {
		// System.out.println("MouseClick on Card");
		//
		// try {
		// DominionController.getInstance().getGameClient().sendMessage(
		// new PacketPlayCard(this.id,
		// DominionController.getInstance().getGameClient().getClientId()));
		// if (cardReaction != null) {
		// this.parent.removeComponent(cardReaction);
		// mouseReaction = 0;
		// }
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// }
		// }
	}

	/**
	 * 
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

	public void setRelativeY(int relativeY) {
		this.relativeY = relativeY;
	}

	/**
	 * main method with test case for cardObject
	 */
	public static void main(String[] args) {
		LinkedList<CardAction> act = CollectionsUtil.linkedList(new CardAction[] { CardAction.ADD_ACTION_TO_PLAYER,
				CardAction.ADD_PURCHASE, CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, CardAction.DRAW_CARD });
		LinkedList<String> ints = CollectionsUtil.linkedList(new String[] { "1", "2", "4", "3" });
		LinkedList<CardType> type = CollectionsUtil.linkedList(CardType.ACTION);

		JFrame frame = new JFrame();
		GraphicFramework gf = new GraphicFramework(frame);
		gf.setSize(1, 1);

		Card market = new Card(CollectionsUtil.linkedHashMapAction(act, ints), type, "Market", 5, gf);
		System.out.println(market.toString());
	}
}
