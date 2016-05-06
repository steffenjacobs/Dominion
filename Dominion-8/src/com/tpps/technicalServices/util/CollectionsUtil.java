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
import com.tpps.technicalServices.logger.GameLog;

import javafx.util.Pair;

/**
 * Basic Collection Utils to make the handling easier
 * 
 * @author Nicolas Christian Wipfler
 * @author Lukas Karl Adler
 */
public final class CollectionsUtil {

	/**
	 * @param number the number of the LinkedHashMap 
	 * @param action the action of the LinkedHashMap
	 * 
	 * @return creates a sorted hashmap (LinkedHashMap) from the given
	 *         parameters (single element)
	 */
	public static LinkedHashMap<CardAction, String> linkedHashMapAction(CardAction action, String number) {
		LinkedHashMap<CardAction, String> hashMap = new LinkedHashMap<CardAction, String>();
		hashMap.put(action, number);
		return hashMap;
	}

	/**
	 * @param actions list of actions
	 * @param numbers list of numbers
	 *
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
	 * @param objects the objects to add to a LinkedList<T>
	 * @return a T-generic list with all objects
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
	 * @param color the colors to add in a LinkedList
	 * @return a linked list of all Colors
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
	 * @param object the object to add to a LinkedList
	 * @return the LinkedList with one object
	 */
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
	 * @param card the card to clone
	 * @param amount the amount of cards to clone
	 * @param destination the destination list
	 */
	public static void cloneCardToList(Card card, int amount, LinkedList<Card> destination) {
		destination.addLast(card);
		for (int i = 0; i < amount - 1; i++) {
			destination.addLast(card.clone());
		}
		// Card.resetClassID();
	}

	/**
	 * add a card to a list (method exists so there is a consistent way of adding
	 * cards to a list at the last position)
	 * 
	 * @param card the card to add
	 * @param destination the destination list
	 */
	public static void addCardToList(Card card, LinkedList<Card> destination) {
		destination.addLast(card);
	}

	/**
	 * adds a list of cards to the (destination-)list from parameters,
	 * but doesn't remove the cards from the original list
	 * 
	 * @param cards the cards to append
	 * @param destination the destination
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
	 * @param cards the cards to get the IDs from
	 * @return a linked list with all cardIDs of the parameter list cards
	 */
	public static LinkedList<String> getCardIDs(LinkedList<Card> cards) {
		LinkedList<String> cardHandIDs = new LinkedList<String>();
		for (Iterator<Card> iterator = cards.iterator(); iterator.hasNext();) {
			cardHandIDs.add(((Card) iterator.next()).getId());
		}
		return cardHandIDs;
	}

	/**
	 * 
	 * @param words the words to add to an array list
	 * @return an arraylist with all given string parameters of words
	 */
	public static ArrayList<String> getArrayList(String... words) {
		ArrayList<String> returnList = new ArrayList<String>();
		for (String word : words) {
			returnList.add(word);
		}
		return returnList;
	}

	/**
	 * 
	 * @param s a String
	 * @param c a Color
	 * @return a TreeMap with the given String and Color
	 */
	public static Map<String, Color> getTreeMap(String s, Color c) {
		Map<String, Color> map = new TreeMap<String, Color>();
		map.put(s, c);
		return map;
	}

	/**
	 * 
	 * @param s a String
	 * @param c a Color
	 * @return a generic Pair with the given String and Color
	 */
	public static Pair<String, Color> getPair(String s, Color c) {
		return new Pair<String, Color>(s, c);
	}
	
	/**
	 * 
	 * @param s a String
	 * @return a generic Pair with the given String and default Color GameLog.getMsgColor()
	 */
	public static Pair<String, Color> getPair(String s) {
		return new Pair<String, Color>(s, GameLog.getMsgColor());
	}
}