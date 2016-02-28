package com.tpps.application.game.card;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

/**
 * 
 * @author ladler - Lukas Adler
 * @author nwipfler - Nicolas Wipfler
 */

public class Card extends GameObject {

	private static final long serialVersionUID = 1L;
	private final Map<CardAction, Integer> actions;
	private final List<CardType> types;
	private final int cost;
	private final String name;

	/**
	 * sets the actions array containing the actions which the cardObject will
	 * execute
	 */
	public Card(Map<CardAction, Integer> actions, List<CardType> types, String name, int cost, double relativeLocX,
			double relativeLocY, double relativeWidth, double relativeHeight, int absWidth, int absHeight, int _layer,
			Image sourceImage, GraphicFramework _parent, int _id) {
		super(relativeLocX, relativeLocY, relativeWidth, relativeHeight, absWidth, absHeight, _layer, sourceImage,
				_parent, _id);
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.types = types;
	}

	/** dummy constructor for testing of deck class */
	public Card(Map<CardAction, Integer> actions, List<CardType> types, String name, int cost) {
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

	public Map<CardAction, Integer> getActions() {
		return actions;
	}

	public List<CardType> getTypes() {
		return types;
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
		sBuf.append("CardObject: " + "'" + this.name
				+ "'" /* + "\nsuper.toString():\n-" + super.toString() */);
		sBuf.append("\nCost: " + this.cost);
		sBuf.append("\nAction(s):");
		for (CardAction act : actions.keySet()) {
			sBuf.append("\n- " + act.toString());
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
		Map<CardAction, Integer> act = new HashMap<CardAction, Integer>();
		act.put(CardAction.ADD_ACTION_TO_PLAYER, 1);
		act.put(CardAction.ADD_PURCHASE, 1);
		act.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, 1);
		act.put(CardAction.DRAW, 1);
		List<CardType> type = new ArrayList<CardType>();
		type.add(CardType.ACTION);
		Card card = new Card(act, type, "Market", 5);
		System.out.println(card.toString());
	}
}
