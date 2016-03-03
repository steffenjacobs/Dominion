package com.tpps.application.game.card;

/**
 * enumeration of all actions a card could possibly execute
 * 
 * @author ladler - Lukas Adler
 * @author nwipfler - Nicolas Wipfler
 */
public enum CardAction {

	ADD_ACTION_TO_PLAYER,
	ADD_TEMPORARY_MONEY_FOR_TURN,
	ADD_PURCHASE, 
	DRAW_CARD, 
	DRAW_CARD_UNTIL, 
	PUT_BACK, 
	GAIN_CARD,
	DISCARD_CARD, 
	TRASH_CARD, 
	REVEAL_CARD, 
	IS_TREASURE, 
	IS_VICTORY;

}
