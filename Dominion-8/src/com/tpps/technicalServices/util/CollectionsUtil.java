package com.tpps.technicalServices.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;

/**
 * 
 * @author nwipfler - Nicolas Christian Wipfler
 * @author ladler - Lukas Karl Adler
 */
public final class CollectionsUtil {

	/**
	 * @param
	 * @return creates a sorted hashmap (LinkedHashMap) from the given
	 *         parameters (single elements)
	 */
	public static LinkedHashMap<CardAction, Integer> linkedHashMapAction(CardAction action, Integer number) {
		LinkedHashMap<CardAction, Integer> hashMap = new LinkedHashMap<CardAction, Integer>();
		hashMap.put(action, number);
		return hashMap;
	}

	/**
	 * @param
	 * @return creates a sorted hashmap (LinkedHashMap) from the given
	 *         parameters (lists)
	 */
	public static LinkedHashMap<CardAction, Integer> linkedHashMapAction(LinkedList<CardAction> actions,
			LinkedList<Integer> numbers) {
		if (actions.size() == numbers.size()) {
			LinkedHashMap<CardAction, Integer> hashMap = new LinkedHashMap<CardAction, Integer>();
			for (int i = 0; i < actions.size(); i++) {
				hashMap.put(actions.get(i), numbers.get(i));
			}
			return hashMap;
		} else {
			System.err.println("arrayLists don't have the same size");
			return null;
		}
	}

	public static <T> LinkedList<T> linkedList(T[] objects) {
		LinkedList<T> resultList = new LinkedList<T>();
		for (T object : objects) {
			resultList.add(object);
		}
		return resultList;
	}

	public static <T> LinkedList<T> linkedList(T object) {
		LinkedList<T> resultList = new LinkedList<T>();
		resultList.add(object);
		return resultList;
	}

	/**
	 * returns 'amount' elements from the given list
	 * 
	 * @author nwipfler - Nicolas Wipfler
	 */
	public static <T> LinkedList<T> getNextElements(int amount, LinkedList<T> list) {
		LinkedList<T> resultList = new LinkedList<T>();
		for (int i = 0; i < amount; i++) {
			resultList.add(list.getLast());
			list.removeLast();
		}
		return resultList;
	}

	// public static <T> T getNextElement(LinkedList<T> list) {
	// return list.removeLast();
	// }

	public static void addCardToList(Card card, LinkedList<Card> destination) {
		destination.addLast(card);
	}

	/**
	 * clones the card and adds this card 'amount'-times to the list in
	 * parameters
	 */
	public static void cloneCardToList(Card card, int amount, LinkedList<Card> destination) {
		for (int i = 0; i < amount; i++) {
			destination.addLast(card.clone());
		}
	}

	/**
	 * 
	 * @return adds a list of cards to the (destination-)list from parameters,
	 *         but doesn't remove the cards from the original list
	 * @author Nicolas Wipfler
	 */
	public static void appendListToList(LinkedList<Card> cards, LinkedList<Card> destination) {
		for (Card card : cards) {
			destination.addLast(card);
		}
	}
}
