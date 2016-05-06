package com.tpps.application.game;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;
import com.tpps.ui.gameplay.GameWindow;

/**
 * 
 * @author Lukas Adler
 *
 */
public class GameStorageInterface {

	GameWindow gameWindow;

	/**
	 * 
	 * @param gameWindow
	 */
	public GameStorageInterface(GameWindow gameWindow) {
		this.gameWindow = gameWindow;
	}

	/**
	 * 
	 * @param handCardIds
	 */
	public void loadHandCardsAndPassToGameWindow(LinkedList<String> handCardIds) {
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(handCardIds);
		this.gameWindow.handCards(serializedCardWithId);
	}

	/**
	 * 
	 * @param victoryCardIds
	 */
	public void loadVictoryCardsAndPassToGameWindow(LinkedList<String> victoryCardIds) {
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(victoryCardIds);
		this.gameWindow.victoryCards(serializedCardWithId);
	}

	/**
	 * 
	 * @param coinCardIds
	 */
	public void loadCoinCardsAndPassToGameWindow(LinkedList<String> coinCardIds) {
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(coinCardIds);
		this.gameWindow.coinCards(serializedCardWithId);
	}

	public void loadRevealCardsAndPassToGameWindow(LinkedList<String> revealCardIds) {
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(revealCardIds);
		this.gameWindow.extraTable(serializedCardWithId);
	}

	/**
	 * 
	 * @param handCardIds
	 * @return
	 */
	private LinkedHashMap<String, SerializedCard> loadCards(LinkedList<String> handCardIds) {
		CardStorageController cs = new CardStorageController();
		LinkedHashMap<String, SerializedCard> serializedCardWithId = new LinkedHashMap<String, SerializedCard>();
		cs.loadCards();
		for (Iterator<String> iterator = handCardIds.iterator(); iterator.hasNext();) {
			String handCardId = (String) iterator.next();
			Matcher matcher = Pattern.compile("\\d+").matcher(handCardId);
			if (matcher.find()) {
				SerializedCard serializedCard = cs.getCard(handCardId.substring(0, matcher.start()));
				if (serializedCard != null) {
					serializedCard = new SerializedCard(serializedCard.getActions(), serializedCard.getTypes(), serializedCard.getCost(), serializedCard.getName(), serializedCard.getImage());
					serializedCardWithId.put(handCardId, serializedCard);
				}
			} else {
				serializedCardWithId.put(handCardId, null);
			}

		}
		return serializedCardWithId;
	}

	/**
	 * 
	 * @param actionCardIds
	 */
	public void loadActionCardsAndPassToGameWindow(LinkedList<String> actionCardIds) {

		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(actionCardIds);
		this.gameWindow.tableActionCards(serializedCardWithId);
	}

	/**
	 * 
	 * @param playedCardIds
	 */
	public void loadPlayedCardsAndPassToGameWindow(LinkedList<String> playedCardIds) {
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(playedCardIds);
		this.gameWindow.middleCards(serializedCardWithId);
	}
}
