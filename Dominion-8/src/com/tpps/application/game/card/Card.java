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
	public Card(HashMap<CardAction, Integer> actions, List<CardType> types,
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
	public Card(HashMap<CardAction, Integer> actions, List<CardType> types,
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
				System.out.println("Add_purchase: "
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
		sBuf.append("CardObject: " + "'" + this.name + "'" /*
															 * +
															 * "\nsuper.toString():\n-"
															 * +
															 * super.toString()
															 */);
		sBuf.append("\nCost: " + this.cost);
		sBuf.append("\nAction(s):");
		ArrayList<CardAction> actionsList = new ArrayList<CardAction>(
				actions.keySet());
		ArrayList<Integer> ints = new ArrayList<Integer>(actions.values());
		for (int i = 0; i < actions.size(); i++) {

			sBuf.append("\n- " + actionsList.get(i).toString() + " amount: "
					+ ints.get(i));

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
		ArrayList<Integer> ints = CollectionsUtil.arrayListInteger(1, 2, 3, 4);
		ArrayList<CardType> type = CollectionsUtil.arrayListType(CardType.ACTION);

		Card card = new Card(CollectionsUtil.hashMapAction(act, ints), type, "Market", 5);
		System.out.println(card.toString());
		System.out.println("do_Action");
		card.doAction();
	}
}