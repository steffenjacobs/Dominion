package com.tpps.application.game.card;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

public abstract class Card extends GameObject {
	
	private static final long serialVersionUID = -4157717625890678601L;
	private final LinkedHashMap<CardAction, Integer> actions;
	private final LinkedList<CardType> types;
	private final int cost;
	private final String name;
	private final String id;
	private static int classID;
	
	public Card(LinkedHashMap<CardAction, Integer> actions,
			LinkedList<CardType> types, String name, int cost,
			GraphicFramework _parent) {
		super(_parent);
		this.name = name;
		this.actions = actions;
		this.cost = cost;
		this.types = types;
		this.id = this.name + classID++;
	}

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
		LinkedList<CardAction> act = CollectionsUtil
				.linkedList(new CardAction[] { CardAction.ADD_ACTION_TO_PLAYER,
						CardAction.ADD_PURCHASE,
						CardAction.ADD_TEMPORARY_MONEY_FOR_TURN,
						CardAction.DRAW });
		LinkedList<Integer> ints = CollectionsUtil.linkedList(new Integer[] {
				1, 2, 4, 3 });
		LinkedList<CardType> type = CollectionsUtil
				.linkedList(new CardType[] { CardType.ACTION });

		ServerCard card = new ServerCard(CollectionsUtil.linkedHashMapAction(
				act, ints), type, "Market", 5, null);
		System.out.println(card.toString());
	}
}