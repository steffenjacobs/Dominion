package com.tpps.application.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.ui.gameplay.GameWindow;

public class GameStorageInterface {
	GameWindow gameWindow;
	
	public GameStorageInterface(GameWindow gameWindow){
		this.gameWindow = gameWindow;
	}
	
	public void loadActionCardsAndPassToGameWindow(LinkedList<String> actionCardIds){
		CardStorageController cs =  new CardStorageController();
		LinkedHashMap<String, SerializedCard> serializedCardWithId = new LinkedHashMap<String, SerializedCard>();
		for (Iterator<String> iterator = actionCardIds.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println(string);
			
		}
		 cs.loadCards();
		 
		 for (Iterator<String> iterator = actionCardIds.iterator(); iterator.hasNext();) {
			String actionCardId = (String) iterator.next();
			SerializedCard serializedCard = cs.getCard(actionCardId.substring(0, actionCardId.length() - 1));
			System.out.println(serializedCard);
			System.out.println("actions: " +serializedCard.getActions());
			System.out.println("types: " +serializedCard.getTypes());
			System.out.println("costs: " + serializedCard.getCost());
			System.out.println("name: " + serializedCard.getName());
			System.out.println(serializedCard.getImage());
			serializedCard = new SerializedCard(serializedCard.getActions(), serializedCard.getTypes(), 
					serializedCard.getCost(), serializedCard.getName(), serializedCard.getImage());
			serializedCardWithId.put(actionCardId, serializedCard);			
		}
		 this.gameWindow.tableActionCards(serializedCardWithId);
	}
	
	public static void main(String[] args) {
		try {
			new GameStorageInterface(new GameWindow()).loadActionCardsAndPassToGameWindow(CollectionsUtil.linkedList(new String[]{"Cellar1"}));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
