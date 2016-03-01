package com.tpps.technicalServices.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
	public static LinkedHashMap<CardAction, Integer> linkedHashMapAction(ArrayList<CardAction> actions,
			ArrayList<Integer> numbers) {
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

	public static <T> ArrayList<T> arrayList(T[] objects) {
		ArrayList<T> resultList = new ArrayList<T>();
		for (T object : objects) {
			resultList.add(object);
		}
		return resultList;
	}

	public static <T> ArrayList<T> arrayList(T object) {
		ArrayList<T> resultList = new ArrayList<T>();
		resultList.add(object);
		return resultList;
	}

	/**
	 * returns 'amount' elements from the given list
	 * @author nwipfler - Nicolas Wipfler
	 */
	public static <T> ArrayList<T> getNextElements(int amount, List<T> list) {
		ArrayList<T> resultList = new ArrayList<T>();
		for (int i = 0; i < amount; i++) {
			resultList.add(list.get(i));
		}
		return resultList;
	}
}
