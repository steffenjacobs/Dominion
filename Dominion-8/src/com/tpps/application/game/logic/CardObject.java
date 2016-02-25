package com.tpps.application.game.logic;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */

public class CardObject extends GameObject {
	private final Actions[] actions;

	/**
	 * sets the actions array containing the actions which the cardObject will execute
	 */
	public CardObject(Actions[] actions) {
		this.actions = actions;
	}

	/**
	 * calls the static method which executes the actions*W
	 * 
	 * @author ladler - Lukas Adler
	 */
	public void doAction() {
		for (int i = 0; i < actions.length; i++) {

			switch (actions[i]) {
			case ADDACTIONTOPLAYER:
				// call
				break;
			case ADDPURCHASE:
				// call
				break;
			case DRAWCARDS:
				// call
				break;
			case DISCARD:
				// call
				break;
			case ADDTEMPORARYMONEYFORTURN:
				// call
				break;
			case TRASH:
				// call
				break;

			}
		}
	}
}
