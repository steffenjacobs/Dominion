package com.tpps.ui.gameplay;

import java.io.IOException;
import java.util.ArrayList;

import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;

public class TestCardLoader {

	public void loading() throws IOException{
		 CardStorageController cs =  new CardStorageController();
		 ArrayList<SerializedCard> hand = new ArrayList<>();
		 ArrayList<SerializedCard> table = new ArrayList<>();
		 ArrayList<SerializedCard> estate = new ArrayList<>();
		 ArrayList<SerializedCard> coins = new ArrayList<>();
		 cs.loadCards();
		 for (int i = 0; i < 3; i++) {
			coins.add(cs.getCard("Copper"));
			estate.add(cs.getCard("Duchy"));
		}
		 
		 for (int i = 0; i < 10; i++) {
			table.add(cs.getCard("Province"));
		}
		 
//		 new GameWindow().tableActionCards(table);
	}
	
	public static void main(String[] args) throws IOException {
		new TestCardLoader().loading();
	}
}
