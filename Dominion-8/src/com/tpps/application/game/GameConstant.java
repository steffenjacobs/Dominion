package com.tpps.application.game;

/**
 * GameConstant lists values according to the rules of the game
 * 
 * @author Nicolas
 *
 */
public enum GameConstant {

	PLAYERS(4),
	
	INIT_CARD_HAND_SIZE(5),
	
	INIT_ACTIONCARD_PILE_SIZE(10),
	INIT_ACTION_PILES(10),
	
	INIT_ESTATE_PILE_SIZE(12),
	INIT_DUCHY_PILE_SIZE(12),
	INIT_PROVINCE_PILE_SIZE(12),
	INIT_GARDEN_PILE_SIZE(12),
	INIT_CURSE_PILE_SIZE(30),	
	
	INIT_COPPER_PILE_SIZE(32),
	INIT_SILVER_PILE_SIZE(40),
	INIT_GOLD_PILE_SIZE(30),

	INIT_COPPER_CARDS_ON_HAND(7),
	INIT_ESTATE_CARDS_ON_HAND(3),
	
	INIT_ACTIONS(1),
	INIT_PURCHASES(1),
	INIT_COINS(0),

	CURSE_VALUE(-1),
	GARDEN_VALUE(-1),
	ESTATE_VALUE(1),
	DUCHY_VALUE(3),
	PROVINCE_VALUE(6),
	COPPER_VALUE(1),
	SILVER_VALUE(2),
	GOLD_VALUE(3),
	
	CURSE_COST(0),
	ESTATE_COST(2),
	GARDEN_COST(4),
	DUCHY_COST(5),
	PROVINCE_COST(8),
	COPPER_COST(0),
	SILVER_COST(3),
	GOLD_COST(6),
	
	EMPTY_PILES(3);
	
	private int value;
	
	/**
	 * 
	 * @param value the value of the constant
	 */
	private GameConstant(int value) {
		this.value = value;
	}
	
	/**
	 * 
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * 
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
}
