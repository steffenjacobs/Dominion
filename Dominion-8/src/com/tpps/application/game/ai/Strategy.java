package com.tpps.application.game.ai;

/**
 * enum with all possible Strategys the AI can choose to play
 * 
 * @author Nicolas
 *
 */
public enum Strategy {

	/**
	 * try to build the longest chains possible
	 */
	DRAW_ADD_ACTION,
	/**
	 * pure big money
	 */
	BIG_MONEY,
	/**
	 * big money with witches
	 */
	BIG_MONEY_WITCH,
	/**
	 * big money with chapel
	 */
	BIG_MONEY_CHAPEL,
	/**
	 * big money with chapels and witches
	 */
	BIG_MONEY_CHAPEL_WITCH,
	/**
	 * big money with chapels and militias
	 */
	BIG_MONEY_CHAPEL_MILITIA;

}
