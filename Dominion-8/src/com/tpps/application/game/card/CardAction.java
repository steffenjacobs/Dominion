package com.tpps.application.game.card;

/**
 * enumeration of all actions a card can possibly cause
 * 
 * @author Lukas Adler
 * @author Nicolas Wipfler
 */
public enum CardAction {

	/* Comments for Nishit */
	
	/* +1 Aktion */
	ADD_ACTION_TO_PLAYER,
	
	/* +1 (Geld) */
	ADD_TEMPORARY_MONEY_FOR_TURN,
	
	/* +1 Kauf */
	ADD_PURCHASE, 
	
	/* +1 Karte */
	DRAW_CARD,
	
	DRAW_CARD_OTHERS,
	
	/* z.B. für Abenteurer: ziehe Karten solange, bis... */
	DRAW_CARD_UNTIL, 
	
	/* z.B. "jeder Spieler legt eine Punktekarte zurueck auf den Nachziehstapel" */
	PUT_BACK, 
	
	/* z.B. "jeder Spieler nimmt sich einen Fluch" */
	GAIN_CARD,
	
	GAIN_CARD_OTHERS,
	
	GAIN_CARD_DRAW_PILE,
	
	/* z.B. bei Miliz */
	DISCARD_CARD, 
	
	DISCARD_AND_DRAW,
	
	/* z.B. bei Kapelle */
	TRASH_CARD, 
	
	/* z.B. bei Spion */
	REVEAL_CARD,
	
	REVEAL_UNTIL_TREASURES, 
	
	/* z.B. wenn eine Karte keine Aktion hat und eine Geldkarte ist */
	IS_TREASURE, 
	
	/* z.B. wenn eine Karte keine Aktion hat und eine Punktekarte ist */
	IS_VICTORY,
	
	DISCARD_OTHER_DOWNTO,
	
	DEFEND,
	
	SEPERATOR,
	
	TRASH_AND_ADD_TEMPORARY_MONEY,
	
	TRASH_AND_GAIN,
	
	TRASH_AND_GAIN_MORE_THAN,
	
	TRASH_TREASURE_GAIN_MORE_THAN_ON_HAND,
	
	CHOOSE_CARD_PLAY_TWICE,
	
	REVEAL_CARD_ALL,
	
	ALL_REVEAL_CARDS_TRASH_COINS_I_CAN_TAKE_DISCARD_OTHERS;
	
}
