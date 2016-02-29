package com.tpps.application.game.card;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

/**
 * 
 * @author ladler - Lukas Adler
 * @author nwipfler - Nicolas Wipfler
 */

public class Card extends GameObject {

	private static final long serialVersionUID = 1L;
	private final LinkedHashMap<CardAction, Integer> actions;
	private final List<CardType> types;
	private final int cost;
	private final String name;

	/**
	 * sets the actions array containing the actions which the cardObject will
	 * execute
	 */
	public Card(LinkedHashMap<CardAction, Integer> actions, List<CardType> types,
			String name, int cost, double relativeLocX, double relativeLocY,
			double relativeWidth, double relativeHeight, int absWidth,
			int absHeight, int _layer, Image sourceImage,
			GraphicFramework _parent, int _id) {
		
		super(relativeLocX, relativeLocY, relativeWidth, relativeHeight,
				absWidth, absHeight, _layer, sourceImage, _parent, _id);
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.types = types;
	}

	/** dummy constructor for testing of deck class */
	public Card(LinkedHashMap<CardAction, Integer> actions, List<CardType> types,
			String name, int cost) {
		this.actions = actions;
		this.types = types;
		this.name = name;
		this.cost = cost;
	}

	public String getName() {
		return this.name;
	}

	public int getCost() {
		return this.cost;
	}

	public HashMap<CardAction, Integer> getActions() {
		return actions;
	}

	public List<CardType> getTypes() {
		return types;
	}

	/**
	 * calls the static method which executes the actions
	 * 
	 * @author ladler - Lukas Adler
	 */
	public void doAction() {
		ArrayList<CardAction> actionsList = new ArrayList<CardAction>(
				actions.keySet());
		for (int i = 0; i < actionsList.size(); i++) {
			switch (actionsList.get(i)) {
			case ADD_ACTION_TO_PLAYER:
				System.out.println("ADD_ACTION_TO_PLAYER: "
						+ actions.get(CardAction.ADD_ACTION_TO_PLAYER));
				break;
			case ADD_PURCHASE:
				System.out.println("ADD_PURCHASE: "
						+ actions.get(CardAction.ADD_PURCHASE));
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				System.out.println("ADD_TEMPORARY_MONEY_FOR_TURN: "
						+ actions.get(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN));
				break;
			case DRAW:
				System.out.println("DRAW: " + actions.get(CardAction.DRAW));
				break;
			case GAIN:
				System.out.println("GAIN: " + actions.get(CardAction.GAIN));
				break;
			case DISCARD:
				System.out.println("DISCARD: "
						+ actions.get(CardAction.DISCARD));
				break;
			case TRASH:
				System.out.println("TRASH: " + actions.get(CardAction.TRASH));
				break;
			case PUT_BACK:
				System.out.println("PUT_BACK: "
						+ actions.get(CardAction.PUT_BACK));
				break;
			case REVEAL:
				System.out.println("REVEAL: " + actions.get(CardAction.REVEAL));
				break;
			case NONE:
				System.out.println("NONE: " + actions.get(CardAction.NONE));
				break;
			case COUNT_FOR_VICTORY:
				System.out.println("COUNT_VOR_VICTORY: "
						+ actions.get(CardAction.COUNT_FOR_VICTORY));
				break;
			default:
				// call
				break;
			}
		}
	}

	@Override
	public GameObject clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onMouseEnter() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseExit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseDrag() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResize(int absWidth, int absHeight) {
		// TODO Auto-generated method stub
	}

	/**
	 * @override toString()
	 */
	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		sBuf.append("Card: " + "'" + this.name + "'\nActions: <");
		Iterator<CardAction> actionsIt = actions.keySet().iterator();
		Iterator<Integer> intsIt = actions.values().iterator();
		while(actionsIt.hasNext() && intsIt.hasNext()) {
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
	 * main method with test case for cardObject
	 */
	public static void main(String[] args) {
		ArrayList<CardAction> act = CollectionsUtil.arrayList(new CardAction[] {CardAction.ADD_ACTION_TO_PLAYER, CardAction.ADD_PURCHASE, CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, CardAction.DRAW });
		ArrayList<Integer> ints = CollectionsUtil.arrayList(new Integer[] { 1, 2, 4, 3 });
		ArrayList<CardType> type = CollectionsUtil.arrayList(new CardType[] { CardType.ACTION });

		Card card = new Card(CollectionsUtil.linkedHashMapAction(act, ints), type, "Market", 5);
		System.out.println(card.toString());
	}
}