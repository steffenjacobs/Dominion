package com.tpps.technicalServices.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;

/**
 * 
 * @author nwipfler - Nicolas Christian Wipfler
 * @author ladler - Lukas Karl Adler
 */
public final class CollectionsUtil {

	public static HashMap<CardAction, Integer> hashMapAction(ArrayList<CardAction> actions,
			ArrayList<Integer> numbers) {
		if (actions.size() == numbers.size()) {
			HashMap<CardAction, Integer> hashMap = new HashMap<CardAction, Integer>();
			for (int i = 0; i < actions.size(); i++) {
				hashMap.put(actions.get(i), numbers.get(i));
			}
			return hashMap;
		} else {
			System.err.println("arrayLists don't have the same size");
			return null;
		}
	}

	// remove?
	// public static ArrayList<Integer> arrayListInteger(Integer... actions) {
	// ArrayList<Integer> resultList = new ArrayList<Integer>();
	// for (Integer action : actions) {
	// resultList.add(action);
	// }
	// return resultList;
	// }

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

	// maybe remove?

	// public static ArrayList<CardAction> arrayListAction(CardAction...
	// actions) {
	// ArrayList<CardAction> resultList = new ArrayList<CardAction>();
	// for (CardAction action : actions) {
	// resultList.add(action);
	// }
	// return resultList;
	// }

	// public static ArrayList<CardType> arrayListType(CardType... types) {
	// ArrayList<CardType> resultList = new ArrayList<CardType>();
	// for (CardType type : types) {
	// resultList.add(type);
	// }
	// return resultList;
	// }

}
