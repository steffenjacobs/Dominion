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

	public GameStorageInterface(GameWindow gameWindow) {
		this.gameWindow = gameWindow;
	}

	public void loadHandCardsAndPassToGameWindow(LinkedList<String> handCardIds) {
		LinkedHashMap<String, SerializedCard> serializedCardWithId = loadCards(handCardIds);
		this.gameWindow.handCards(serializedCardWithId);
	}

	private LinkedHashMap<String, SerializedCard> loadCards(LinkedList<String> handCardIds) {
		CardStorageController cs = new CardStorageController();
		LinkedHashMap<String, SerializedCard> serializedCardWithId = new LinkedHashMap<String, SerializedCard>();
		cs.loadCards();

		for (Iterator<String> iterator = handCardIds.iterator(); iterator.hasNext();) {

			String handCardId = (String) iterator.next();

			SerializedCard serializedCard = cs.getCard(handCardId.substring(0, handCardId.length() - 1));

			if (serializedCard != null) {
				serializedCard = new SerializedCard(serializedCard.getActions(), serializedCard.getTypes(),
						serializedCard.getCost(), serializedCard.getName(), serializedCard.getImage());
			}
			serializedCardWithId.put(handCardId, serializedCard);

		}
		return serializedCardWithId;
	}

	public void loadActionCardsAndPassToGameWindow(LinkedList<String> actionCardIds) {
		CardStorageController cs = new CardStorageController();
		LinkedHashMap<String, SerializedCard> serializedCardWithId = new LinkedHashMap<String, SerializedCard>();

		cs.loadCards();
		cs.listCards();

		for (Iterator<String> iterator = actionCardIds.iterator(); iterator.hasNext();) {
			String actionCardId = (String) iterator.next();
			SerializedCard serializedCard = cs.getCard(actionCardId.substring(0, actionCardId.length() - 1));
			// System.out.println(serializedCard);
			// System.out.println("actions: " +serializedCard.getActions());
			// System.out.println("types: " +serializedCard.getTypes());
			// System.out.println("costs: " + serializedCard.getCost());
			// System.out.println("name: " + serializedCard.getName());
			//
			serializedCard = new SerializedCard(serializedCard.getActions(), serializedCard.getTypes(),
					serializedCard.getCost(), serializedCard.getName(), serializedCard.getImage());
			serializedCardWithId.put(actionCardId, serializedCard);
		}
		this.gameWindow.tableActionCards(serializedCardWithId);
	}

	public static void main(String[] args) {
		try {
			new GameStorageInterface(new GameWindow())
					.loadActionCardsAndPassToGameWindow(CollectionsUtil.linkedList(new String[] { "Cellar2" }));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
