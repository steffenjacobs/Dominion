package com.tpps.application.game.card;

import java.awt.Image;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.JFrame;

import com.tpps.application.game.DominionController;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

/**
 * @author Nicolas Wipfler
 */
public class Card extends GameObject {

	private static final long serialVersionUID = -4157717625890678601L;
	/**
	 * if the card is VICTORY/TREASURE, String is the value (not the cost)
	 * */
	private final LinkedHashMap<CardAction, String> actions;
	private final LinkedList<CardType> types;
	private final int cost;
	private final String name;
	private final String id;
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
	public Card(LinkedHashMap<CardAction, String> actions,
			LinkedList<CardType> types, String name, int cost,
			double relativeLocX, double relativeLocY, double relativeWidth,
			double relativeHeight, int _layer, Image sourceImage,
			GraphicFramework _parent) {
		super(relativeLocX, relativeLocY, relativeWidth, relativeHeight,
				_layer, sourceImage, _parent);
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.types = types;
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
	public Card(LinkedHashMap<CardAction, String> actions,
			LinkedList<CardType> types, String name, int cost, String cardId,
			double relativeLocX, double relativeLocY, double relativeWidth,
			double relativeHeight, int _layer, Image sourceImage,
			GraphicFramework _parent) {
		super(relativeLocX, relativeLocY, relativeWidth, relativeHeight,
				_layer, sourceImage, _parent);
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.types = types;
		this.id = cardId;
	}

	/**
	 * constructor for Card, taking all required data
	 * 
	 * @param actions the list of CardActions mapped to their value
	 * @param types the list of CardTypes
	 * @param cost the card-cost
	 * @param name the name of the card 
	 * @param _parent graphic framework where the card will be drawn
	 */
	public Card(LinkedHashMap<CardAction, String> actions,
			LinkedList<CardType> types, String name, int cost,
			GraphicFramework _parent) {
		super(_parent);
		this.name = name;
		this.actions = actions;
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
	public Card(LinkedHashMap<CardAction, String> actions,
			LinkedList<CardType> types, String name, int cost) {
		super();
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.types = types;
		this.id = this.name + classID++;
//		System.out.println(id);
	}

	// clone constructor

	// equals

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
	 * 
	 */
	@Override
	public void onMouseEnter() {
		
	}

	/**
	 * 
	 */
	@Override
	public void onMouseExit() {
		
	}

	/**
	 * 
	 */
	@Override
	public void onMouseClick() {
		System.out.println("MouseClick on Card");
		try {
			DominionController.getInstance().getGameClient()
					.sendMessage(new PacketPlayCard(this.id, DominionController.getInstance().getGameClient().getClientId()));
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@Override
	public void onMouseDrag() {
	}

	/**
	 * 
	 */
	@Override
	public void onResize(int absWidth, int absHeight) {
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
			sBuf.append("<" + actionsIt.next().toString() + ": "
					+ intsIt.next() + ">");
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

	/**
	 * main method with test case for cardObject
	 */
	public static void main(String[] args) {
		LinkedList<CardAction> act = CollectionsUtil.linkedList(new CardAction[] { CardAction.ADD_ACTION_TO_PLAYER,
						CardAction.ADD_PURCHASE,
						CardAction.ADD_TEMPORARY_MONEY_FOR_TURN,
						CardAction.DRAW_CARD });
		LinkedList<String> ints = CollectionsUtil.linkedList(new String[] {"1", "2", "4", "3"});
		LinkedList<CardType> type = CollectionsUtil.linkedList(CardType.ACTION);
	
		JFrame frame = new JFrame();
		GraphicFramework gf = new GraphicFramework(frame);
		gf.setSize(1, 1);
	
		Card market = new Card(CollectionsUtil.linkedHashMapAction(act, ints), type, "Market", 5, gf);
		System.out.println(market.toString());
	}
}
