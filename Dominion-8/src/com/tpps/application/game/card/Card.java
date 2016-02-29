package com.tpps.application.game.card;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
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
	private final HashMap<CardAction, Integer> actions;
	private final List<CardType> types;
	private final int cost;
	private final String name;

	/**
	 * sets the actions array containing the actions which the cardObject will
	 * execute
	 */
	public Card(HashMap<CardAction, Integer> actions, List<CardType> types, String name,
			int cost, double relativeLocX, double relativeLocY,
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
	public Card(HashMap<CardAction, Integer> actions, List<CardType> types, String name,
			int cost) {
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
		ArrayList<CardAction> actionsList = new ArrayList<CardAction>(actions.keySet());
		for (int i = 0; i < actionsList.size(); i++) {
			switch (actionsList.get(i)) {
			case ADD_ACTION_TO_PLAYER:
				// call
				break;
			case ADD_PURCHASE:
				// call
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				// call
				break;
			case DRAW:
				// call
				break;
			case GAIN:
				// call
				break;
			case DISCARD:
				// call
				break;
			case TRASH:
				// call
				break;
			case PUT_BACK:
				// call
				break;
			case REVEAL:
				// call
				break;
			case NONE:
				// call
				break;
			case COUNT_FOR_VICTORY:
				// call
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
		sBuf.append("CardObject: " + "'" + this.name + "'" /* + "\nsuper.toString():\n-" + super.toString()*/);
		sBuf.append("\nCost: " + this.cost);
		sBuf.append("\nAction(s):");
		ArrayList<CardAction> actionsList = new ArrayList<CardAction>(actions.keySet()); 
		ArrayList<Integer> ints = new ArrayList<Integer>(actions.values());
		for (int i = 0; i < actions.size(); i++) {

			sBuf.append("\n- " + actionsList.get(i).toString() + " amount: " + ints.get(i));

		}
		sBuf.append("\nType(s):");
		for (CardType type : types) {
			sBuf.append("\n- " + type.toString());
		}
		return sBuf.toString();
	}

	/**
	 * main method with test case for cardObject
	 */
	public static void main(String[] args) {
		ArrayList<CardAction> act = new ArrayList<CardAction>();
		act.add(CardAction.ADD_ACTION_TO_PLAYER);
		act.add(CardAction.ADD_PURCHASE);
		act.add(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN);
		act.add(CardAction.DRAW);
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(1); i.add(2); i.add(3); i.add(4);
		List<CardType> type = new ArrayList<CardType>();
		type.add(CardType.ACTION);
		
		
		Card card = new Card(CollectionsUtil.hashMapAction(act, i), type, "Market", 5);
		System.out.println(card.toString());
	}
}
