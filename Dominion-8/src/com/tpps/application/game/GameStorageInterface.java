package com.tpps.application.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.ui.gameplay.GameWindow;

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
		for (Iterator<String> iterator = handCardIds.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println("HandCards: " + string);			
		}
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(handCardIds);
		
		this.gameWindow.handCards(serializedCardWithId);
	}
	
	/**
	 * 
	 * @param victoryCardIds
	 */
	public void loadVictoryCardsAndPassToGameWindow(LinkedList<String> victoryCardIds){
	
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(victoryCardIds);
		this.gameWindow.victoryCards(serializedCardWithId);
	}
	
	/**
	 * 
	 * @param coinCardIds
	 */
	public void loadCoinCardsAndPassToGameWindow(LinkedList<String> coinCardIds){
	
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(coinCardIds);
		this.gameWindow.coinCards(serializedCardWithId);
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
			matcher.find();		
			
			SerializedCard serializedCard = cs.getCard(handCardId.substring(0, matcher.start()));
			if (serializedCard != null) {				
				serializedCard = new SerializedCard(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getCost(), serializedCard.getName(), serializedCard.getImage());
			}
			serializedCardWithId.put(handCardId, serializedCard);
		}
		return serializedCardWithId;
	}

	/**
	 * 
	 * @param actionCardIds
	 */
	public void loadActionCardsAndPassToGameWindow(LinkedList<String> actionCardIds) {
		for (Iterator<String> iterator = actionCardIds.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println("ActionCards: " + string);			
		}
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(actionCardIds);
		this.gameWindow.tableActionCards(serializedCardWithId);
	}
	
	public void loadPlayedCardsAndPassToGameWindow(LinkedList<String> playedCardIds) {
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(playedCardIds);
		this.gameWindow.middleCards(serializedCardWithId);
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GameWindow gameWindow = new GameWindow();
			gameWindow.setVisible(true);
			gameWindow.addStopDiscardButton();
			new GameStorageInterface(gameWindow)
					.loadActionCardsAndPassToGameWindow(CollectionsUtil.linkedList(new String[] { "Cellar2" }));			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Matcher matcher = Pattern.compile("\\d+").matcher("TESt243");
//		matcher.find();		
//		System.out.println("Start: " + matcher.start() + "Ende: " + matcher.end());
//		System.out.println("TESt243".substring(matcher.start(), matcher.end()));
		

	}
}
