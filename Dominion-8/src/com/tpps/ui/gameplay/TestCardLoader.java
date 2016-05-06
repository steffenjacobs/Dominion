package com.tpps.ui.gameplay;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.tpps.application.game.CardName;
import com.tpps.application.storage.CardStorageController;
import com.tpps.application.storage.SerializedCard;

/**
 * GUI Testing class. All components are working
 * 
 * @author Nishit Agrawal - nagrawal
 *
 *
 */

public class TestCardLoader {
	String s="asdf";
	
	/**
	 * loading every variety of cards
	 * 
	 * @throws IOException
	 */

	public void loading() throws IOException{
		 CardStorageController cs =  new CardStorageController();
		 LinkedHashMap<String,SerializedCard> hand = new LinkedHashMap<>();
		 LinkedHashMap<String,SerializedCard> table = new LinkedHashMap<String, SerializedCard>();
		 LinkedHashMap<String,SerializedCard> middle = new LinkedHashMap<String, SerializedCard>();
		 HashMap<String,SerializedCard> estate = new HashMap<>();
		 HashMap<String,SerializedCard> coins = new HashMap<>();
		 cs.loadCards();
		 for (int i = 0; i < 3; i++) {
			coins.put(s+="s",cs.getCard(CardName.COPPER.getName()));
			estate.put(s+="s",cs.getCard(CardName.DUCHY.getName()));
		}
		 
		 for (int i = 0; i < 10; i++) {
			table.put(s+="s",cs.getCard(CardName.PROVINCE.getName()));
		}

		 for (int i = 0; i < 13; i++) {
			hand.put(s+="s",cs.getCard(CardName.GOLD.getName()));
		}
		 
		 for (int i = 0; i < 15; i++) {
			middle.put(s+="s",cs.getCard(CardName.GOLD.getName()));
		}
		 
		 GameWindow gw = new GameWindow();
		 gw.tableActionCards(table);
		 gw.handCards(hand);
		 gw.coinCards(coins);
		 gw.victoryCards(estate);
		 gw.middleCards(middle);
		 gw.extraTable(middle);
		 gw.removeTableComponents();
	}
	
	public static void main(String[] args) throws IOException {
		new TestCardLoader().loading();
	}
}
