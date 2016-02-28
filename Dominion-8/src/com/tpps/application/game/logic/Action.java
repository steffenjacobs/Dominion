package com.tpps.application.game.logic;

/**
 * 
 * @author ladler - Lukas Adler
 * @author nwipfler - Nicolas Wipfler
 *
 */
public enum Action {
	// @Lukas, keine Ahnung ob der auskommentierte Code so viel Sinn macht, habs mal vorläufig geändert
	
	COUNT_FOR_VICTORY, ADD_ACTION_TO_PLAYER, ADD_TEMPORARY_MONEY_FOR_TURN, ADD_PURCHASE, DRAW, PUT_BACK, GAIN, DISCARD, TRASH, REVEAL, NONE;

	// COUNT_FOR_VICTORY(-1), ADD_ACTION_TO_PLAYER(-1),
	// ADD_TEMPORARY_MONEY_FOR_TURN(-1), ADD_PURCHASE(-1), DRAW(-1),
	// PUT_BACK(null),
	// GAIN(-1), DISCARD(-1), TRASH(null), REVEAL(-1), NONE(0) /* ab hier DLC:
	// CHOOSE, PASS, NAME, COST_LESS */;
	//
	// private int amount;
	// private Type type;
	//
	// private Action(int amount) {
	// this.amount = amount;
	// }
	//
	// private Action(Type type) {
	// this.type = type;
	// }
	//
	// private Action(int amount, Type type) {
	// this.amount = amount;
	// this.type = type;
	// }
	//
	// public Type getType() {
	// return type;
	// }
	//
	// public void setType(Type type) {
	// this.type = type;
	// }
	//
	// public int getAmount() {
	// return this.amount;
	// }
	//
	// public void setAmount(int amount) {
	// this.amount = amount;
	// }
}
