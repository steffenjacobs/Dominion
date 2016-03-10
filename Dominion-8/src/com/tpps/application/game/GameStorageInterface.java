package com.tpps.application.game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.ui.gameplay.GameWindow;

public class GameStorageInterface {
	GameWindow gameWindow;

	public GameStorageInterface(GameWindow gameWindow) {
		this.gameWindow = gameWindow;
	}

	public void loadHandCardsAndPassToGameWindow(LinkedList<String> handCardIds) {
		for (Iterator<String> iterator = handCardIds.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println("HandCards: " + string);			
		}
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(handCardIds);
		
		this.gameWindow.handCards(serializedCardWithId);
	}
	
	
	public void loadVictoryCardsAndPassToGameWindow(LinkedList<String> victoryCardIds){
	
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(victoryCardIds);
		this.gameWindow.victoryCards(serializedCardWithId);
	}
	
	public void loadCoinCardsAndPassToGameWindow(LinkedList<String> coinCardIds){
	
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(coinCardIds);
		this.gameWindow.coinCards(serializedCardWithId);
	}

	private LinkedHashMap<String, SerializedCard> loadCards(LinkedList<String> handCardIds) {
		CardStorageController cs = new CardStorageController();
		LinkedHashMap<String, SerializedCard> serializedCardWithId = new LinkedHashMap<String, SerializedCard>();
		cs.loadCards();

		for (Iterator<String> iterator = handCardIds.iterator(); iterator.hasNext();) {

			String handCardId = (String) iterator.next();
			
			SerializedCard serializedCard = cs.getCard(handCardId.substring(0, handCardId.length() - 1));

			if (serializedCard != null) {
				System.out.println("hier");
				serializedCard = new SerializedCard(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getCost(), serializedCard.getName(), serializedCard.getImage());
			}
			serializedCardWithId.put(handCardId, serializedCard);

		}
		return serializedCardWithId;
	}

	public void loadActionCardsAndPassToGameWindow(LinkedList<String> actionCardIds) {
		for (Iterator<String> iterator = actionCardIds.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println("ActionCards: " + string);			
		}

		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(actionCardIds);
		this.gameWindow.tableActionCards(serializedCardWithId);
	}

	public static void main(String[] args) {
//		try {
//			new GameStorageInterface(new GameWindow())
//					.loadActionCardsAndPassToGameWindow(CollectionsUtil.linkedList(new String[] { "Cellar2" }));
//			System.out.println("Cellar2".substring("Cellar2".length()-1, "Cellar2".length()));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		

	}

}
