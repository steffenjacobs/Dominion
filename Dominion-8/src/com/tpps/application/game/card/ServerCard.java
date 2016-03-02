package com.tpps.application.game.card;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.ui.GraphicFramework;

/**
 * 
 * @author ladler - Lukas Adler
 * @author nwipfler - Nicolas Wipfler
 */

public class ServerCard extends Card {

	private static final long serialVersionUID = 9028795210979126827L;

	public ServerCard(LinkedHashMap<CardAction, Integer> actions, LinkedList<CardType> types, String name, int cost,
			GraphicFramework _parent) {
		super(actions, types, name, cost, _parent);
	}

	/**
	 * @override toString()
	 */
	public String toString() {
		// super.toString();
		// Iterator<CardAction> actionsIt = actions.keySet().iterator();
		// Iterator<Integer> intsIt = actions.values().iterator();
		// while (actionsIt.hasNext() && intsIt.hasNext()) {
		// sBuf.append("<" + actionsIt.next().toString() + ": "
		// + intsIt.next() + ">");
		// if (actionsIt.hasNext() && intsIt.hasNext())
		// sBuf.append(" ");
		// }

		// REMOVE
		// StringBuffer sBuf = new StringBuffer();
		// sBuf.append("Card: " + "'" + this.name + "'\nActions: <");
		// Iterator<CardAction> actionsIt = actions.keySet().iterator();
		// Iterator<Integer> intsIt = actions.values().iterator();
		// while (actionsIt.hasNext() && intsIt.hasNext()) {
		// sBuf.append("<" + actionsIt.next().toString() + ": " + intsIt.next()
		// + ">");
		// if (actionsIt.hasNext() && intsIt.hasNext())
		// sBuf.append(" ");
		// }
		// Iterator<CardType> typesIt = types.iterator();
		// sBuf.append(">\nTypes: <");
		// while (typesIt.hasNext()) {
		// sBuf.append("<" + typesIt.next().toString() + ">");
		// if (typesIt.hasNext())
		// sBuf.append(" ");
		// }
		// return sBuf.append(">\nCost: " + this.cost).toString();
		return null;
	}

	/**
	 * main method with test case for cardObject
	 */
	public static void main(String[] args) {
		LinkedList<CardAction> act = CollectionsUtil.linkedList(new CardAction[] { CardAction.ADD_ACTION_TO_PLAYER,
				CardAction.ADD_PURCHASE, CardAction.ADD_TEMPORARY_MONEY_FOR_TURN, CardAction.DRAW });
		LinkedList<Integer> ints = CollectionsUtil.linkedList(new Integer[] { 1, 2, 4, 3 });
		LinkedList<CardType> type = CollectionsUtil.linkedList(new CardType[] { CardType.ACTION });

		ServerCard card = new ServerCard(CollectionsUtil.linkedHashMapAction(act, ints), type, "Market", 5, null);
		System.out.println(card.toString());
	}
}