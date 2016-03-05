package com.tpps.technicalServices.util;

/**
 * @author Nicolas Wipfler
 * */
public class GameConstant {
	
	public static int HUMAN_PLAYERS = 4; // add setter
	
	// s. Deck: init()
	// CardAction.IS_VICTORY ; GameConstant.ESTATE_VALUE ; CardType.ESTATE ; "Estate"
	// redundant, create Constructor which only takes GameConstant.'CARDNAME' (e.g. GameConstant.COPPER) 
	// and add switch case to GameConstant class to compute the outcome/created card
	
	/**
	 * INIT VALUES
	 * */
	public static final int INIT_CARD_HAND_SIZE = 5;
	
	public static final int INIT_PILE_SIZE = 10;
	public static final int INIT_PILES = 10; 
	
	public static final int INIT_ESTATE_PILE_SIZE = 12;
	public static final int INIT_DUCHY_PILE_SIZE = 12;
	public static final int INIT_PROVINCE_PILE_SIZE = 12;
	
	public static final int INIT_ACTIONS = 1;
	public static final int INIT_PURCHASES = 1;
	public static final int INIT_MONEY = 0;
	
	/**
	 * VICTORY
	 * */
	public static final int ESTATE_VALUE = 1;
	public static final int ESTATE_COST = 2;
	public static final int DUCHY_VALUE = 3;
	public static final int DUCHY_COST = 5;
	public static final int PROVINCE_VALUE = 6;
	public static final int PROVINCE_COST = 8;	
	
	/**
	 * TREASURE
	 * */
	public static final int COPPER_VALUE = 1;
	public static final int COPPER_COST = 0;
	public static final int SILVER_VALUE = 2;
	public static final int SILVER_COST = 3;
	public static final int GOLD_VALUE = 3;
	public static final int GOLD_COST = 6;	
	
}
