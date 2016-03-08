package com.tpps.ui.gameplay;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;

public class TestCardLoader {
	String s="asdf";

	public void loading() throws IOException{
		 CardStorageController cs =  new CardStorageController();
		 LinkedHashMap<String,SerializedCard> hand = new LinkedHashMap<>();
		 LinkedHashMap<String,SerializedCard> table = new LinkedHashMap<String, SerializedCard>();
		 HashMap<String,SerializedCard> estate = new HashMap<>();
		 HashMap<String,SerializedCard> coins = new HashMap<>();
		 cs.loadCards();
		 for (int i = 0; i < 3; i++) {
			coins.put(s+="s",cs.getCard("Copper"));
			estate.put(s+="s",cs.getCard("Duchy"));
		}
		 
		 for (int i = 0; i < 10; i++) {
			table.put(s+=s,cs.getCard("Province"));
		}

		 for (int i = 0; i < 9; i++) {
			hand.put(s+=s,cs.getCard("Gold"));
		}
		 GameWindow gw = new GameWindow();
		 gw.tableActionCards(table);
		 gw.handCards(hand);

	}
	
	public static void main(String[] args) throws IOException {
		new TestCardLoader().loading();
	}
}
