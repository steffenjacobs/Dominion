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

	public ServerCard(LinkedHashMap<CardAction, Integer> actions,
			LinkedList<CardType> types, String name, int cost,
			GraphicFramework _parent) {
		super(actions, types, name, cost, _parent);
	}

	/**
	 * calls the static method which executes the actions
	 * 
	 * @author ladler - Lukas Adler
	 */
	public void doAction() {
		ArrayList<CardAction> actionsList = new ArrayList<CardAction>(super
				.getActions().keySet());
		// Player player = GameController.getActivePlayer();
		for (int i = 0; i < actionsList.size(); i++) {
			switch (actionsList.get(i)) {
			case ADD_ACTION_TO_PLAYER:
				// player.addAction(this.actions.get(ADD_ACTION_TO_PLAYER));
				// sentPackage(player, numbAction);
				System.out.println("ADD_ACTION_TO_PLAYER: "
						+ super.getActions().get(
								CardAction.ADD_ACTION_TO_PLAYER));
				break;
			case ADD_PURCHASE:
				System.out.println("ADD_PURCHASE: "
						+ super.getActions().get(CardAction.ADD_PURCHASE));
				break;
			case ADD_TEMPORARY_MONEY_FOR_TURN:
				System.out.println("ADD_TEMPORARY_MONEY_FOR_TURN: "
						+ super.getActions().get(
								CardAction.ADD_TEMPORARY_MONEY_FOR_TURN));
				break;
			case DRAW:
				System.out.println("DRAW: "
						+ super.getActions().get(CardAction.DRAW));
				break;
			case GAIN:
				System.out.println("GAIN: "
						+ super.getActions().get(CardAction.GAIN));
				break;
			case DISCARD:
				System.out.println("DISCARD: "
						+ super.getActions().get(CardAction.DISCARD));
				break;
			case TRASH:
				System.out.println("TRASH: "
						+ super.getActions().get(CardAction.TRASH));
				break;
			case PUT_BACK:
				System.out.println("PUT_BACK: "
						+ super.getActions().get(CardAction.PUT_BACK));
				break;
			case REVEAL:
				System.out.println("REVEAL: "
						+ super.getActions().get(CardAction.REVEAL));
				break;
			case NONE:
				System.out.println("NONE: "
						+ super.getActions().get(CardAction.NONE));
				break;
			case COUNT_FOR_VICTORY:
				System.out.println("COUNT_VOR_VICTORY: "
						+ super.getActions().get(CardAction.COUNT_FOR_VICTORY));
				break;
			default:
				// call
				break;
			}
		}
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