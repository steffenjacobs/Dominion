package com.tpps.technicalServices.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

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
	 *         parameters
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
	 * @author nwipfler - Nicolas Wipfler
	 */
	public static <T> LinkedList<T> getNextElements(int amount, List<T> list) {
		LinkedList<T> resultList = new LinkedList<T>();
		for (int i = 0; i < amount; i++) {
			resultList.add(list.get(i));
			list.remove(i);
		}
		return resultList;
	}
}
