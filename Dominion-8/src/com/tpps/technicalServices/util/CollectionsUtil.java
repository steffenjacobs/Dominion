package com.tpps.technicalServices.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;

/**
 * 
 * @author Nicolas Christian Wipfler
 * @author Lukas Karl Adler
 */
public final class CollectionsUtil {

	/**
	 * @param
	 * @return creates a sorted hashmap (LinkedHashMap) from the given
	 *         parameters (single element)
	 */
	public static LinkedHashMap<CardAction, String> linkedHashMapAction(CardAction action, String number) {
		LinkedHashMap<CardAction, String> hashMap = new LinkedHashMap<CardAction, String>();
		hashMap.put(action, number);
		return hashMap;
	}

	/**
	 * @param
	 * @return creates a sorted hashmap (LinkedHashMap) from the given
	 *         parameters (list)
	 */
	public static LinkedHashMap<CardAction, String> linkedHashMapAction(LinkedList<CardAction> actions, LinkedList<String> numbers) {
		if (actions.size() == numbers.size()) {
			LinkedHashMap<CardAction, String> hashMap = new LinkedHashMap<CardAction, String>();
			for (int i = 0; i < actions.size(); i++) {
				hashMap.put(actions.get(i), numbers.get(i));
			}
			return hashMap;
		} else {
			System.err.println("LinkedLists don't have the same size.");
			return null;
		}
	}

	/**
	 * 
	 * */
	public static <T> LinkedList<T> linkedList(T[] objects) {
		LinkedList<T> resultList = new LinkedList<T>();
		for (T object : objects) {
			resultList.add(object);
		}
		return resultList;
	}
	
	/**
	 * 
	 * @param color
	 * @return
	 */
	public static LinkedList<Color> linkedColorList(Color... color) {
		LinkedList<Color> resultList = new LinkedList<Color>();
		for (Color c : color) {
			resultList.addLast(c);
		}
		return resultList;
	}

	/**
	 * 
	 * */
	public static <T> LinkedList<T> linkedList(T object) {
		LinkedList<T> resultList = new LinkedList<T>();
		resultList.add(object);
		return resultList;
	}

	/**
	 *
	 * @param amount
	 *            the amount of elements
	 * @param list
	 *            the list where the last elements will be returned from
	 * @return the 'amount' last elements from the given list
	 * @author nwipfler - Nicolas Wipfler
	 */
	public static <T> LinkedList<T> getLastElements(int amount, LinkedList<T> list) {
		if (list.size() >= amount) {
			LinkedList<T> resultList = new LinkedList<T>();
			for (int i = 0; i < amount; i++) {
				resultList.add(getLastElement(list));
			}
			return resultList;
		} else
			return null;
	}

	/**
	 * returns the last element (for example on top of the drawPile) from the
	 * given list
	 *
	 * @param list
	 *            the list where the last element will be returned from
	 * @return the last object of the list
	 * @author nwipfler - Nicolas Wipfler
	 */
	public static <T> T getLastElement(LinkedList<T> list) {
		return list.removeLast();
	}

	/**
	 * clones the card and adds this card 'amount - 1'-times, to the list in
	 * parameters, because one object of the card already exists
	 */
	public static void cloneCardToList(Card card, int amount, LinkedList<Card> destination) {
		destination.addLast(card);
		for (int i = 0; i < amount - 1; i++) {
			destination.addLast(card.clone());
		}
		// Card.resetClassID();
	}

	/**
	 * 
	 * */
	public static void addCardToList(Card card, LinkedList<Card> destination) {
		destination.addLast(card);
	}

	/**
	 * 
	 * @return adds a list of cards to the (destination-)list from parameters,
	 *         but doesn't remove the cards from the original list
	 */
	public static void appendListToList(LinkedList<Card> cards, LinkedList<Card> destination) {
		for (Card card : cards) {
			destination.addLast(card);
		}
	}

	/**
	 * removes one card by the given id
	 * 
	 * @param cards
	 * @param cardId
	 * @return the card or null if the card was not found
	 */
	public static Card removeCardById(LinkedList<Card> cards, String cardId) {
		for (Iterator<Card> iterator = cards.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getId().equals(cardId)) {
				cards.remove(card);
				return card;
			}
		}
		return null;
	}

	/**
	 * 
	 * */
	public static LinkedList<String> getCardIDs(LinkedList<Card> cards) {
		LinkedList<String> cardHandIDs = new LinkedList<String>();
		for (Iterator<Card> iterator = cards.iterator(); iterator.hasNext();) {
			cardHandIDs.add(((Card) iterator.next()).getId());
		}
		return cardHandIDs;
	}
	
	
	public static ArrayList<String> getArrayList(String... words) {
		ArrayList<String> returnList = new ArrayList<String>();
		for (String word : words) {
			returnList.add(word);
		}
		return returnList;
	}
	
	public static Map<String,Color> getTreeMap(String s, Color c) {
		Map<String,Color> map = new TreeMap<String,Color>();
		map.put(s, c);
		return map;
	}
}
