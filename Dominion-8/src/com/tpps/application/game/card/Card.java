package com.tpps.application.game.card;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.JFrame;

import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

public class Card extends GameObject {

	private static final long serialVersionUID = -4157717625890678601L;
	private final LinkedHashMap<CardAction, Integer> actions; // if the card is VICTORY/TREASURE, Int is the value (not the cost)
	private final LinkedList<CardType> types;
	private final int cost; // cost of the card
	private final String name;
	private final String id;
	private static int classID = 0;

	public Card(LinkedHashMap<CardAction, Integer> actions, LinkedList<CardType> types, String name, int cost,
			GraphicFramework _parent) {
		super(_parent);
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.types = types;
		this.id = this.name + classID++;
	}

	/** Test Konstruktor ohne Graphic Framework */
	public Card(LinkedHashMap<CardAction, Integer> actions, LinkedList<CardType> types, String name, int cost) {
		super();
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.types = types;
		this.id = this.name + classID++;
		System.out.println(id);
	}
	
	// clone constructor
	//	private GraphicFramework gf_test = new GraphicFramework(new JFrame());
		
	// equals

	public LinkedHashMap<CardAction, Integer> getActions() {
		return actions;
	}

	public LinkedList<CardType> getTypes() {
		return types;
	}

	public int getCost() {
		return this.cost;
	}

	public String getName() {
		return this.name;
	}

	public String getId() {
		return id;
	}

	@Override
	public Card clone() {
		return new Card(this.getActions(), this.getTypes(), this.getName(), this.getCost()/*, this.getParent()*/);
	}

	@Override
	public void onMouseEnter() {
	}

	@Override
	public void onMouseExit() {
	}

	@Override
	public void onMouseClick() {
	}

	@Override
	public void onMouseDrag() {
	}

	@Override
	public void onResize(int absWidth, int absHeight) {
	}

	/**
	 * @override toString()
	 */
	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		sBuf.append("Card: " + "'" + this.name + "'\nActions: <");
		Iterator<CardAction> actionsIt = actions.keySet().iterator();
		Iterator<Integer> intsIt = actions.values().iterator();
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

	/**
	 * sets the classId to zero
	 */
	public static void resetClassID() {
		Card.classID = 0;
	}

	/**
	 * main method with test case for cardObject
	 */
	public static void main(String[] args) {
		LinkedList<CardAction> act = CollectionsUtil.linkedList(new CardAction[] { CardAction.ADD_ACTION_TO_PLAYER,
				CardAction.ADD_PURCHASE, CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, CardAction.DRAW_CARD });
		LinkedList<Integer> ints = CollectionsUtil.linkedList(new Integer[] { 1, 2, 4, 3 });
		LinkedList<CardType> type = CollectionsUtil.linkedList(CardType.ACTION);
		
		JFrame frame = new JFrame();
		GraphicFramework gf = new GraphicFramework(frame);
		gf.setSize(1,1);

		Card card = new Card(CollectionsUtil.linkedHashMapAction(act, ints), type, "Market", 5, gf);
		System.out.println(card.toString());
	}
}
