package com.tpps.technicalServices.util;

import java.util.ArrayList;

/**
 * @author Nicolas Wipfler
 */
public final class GameConstant {

	public static int PLAYERS = 4;

	/**
	 * INIT VALUES
	 */
	public static final int INIT_CARD_HAND_SIZE = 5;

	public static final int INIT_PILE_SIZE = 10;
	public static final int INIT_PILES = 10;

	public static final int INIT_ESTATE_PILE_SIZE = 12;
	public static final int INIT_DUCHY_PILE_SIZE = 12;
	public static final int INIT_PROVINCE_PILE_SIZE = 12;

	public static final int INIT_ACTIONS = 1;
	public static final int INIT_PURCHASES = 1;
	public static final int INIT_TREASURES = 0;

	/**
	 * VICTORY
	 */
	public static final int ESTATE_VALUE = 1;
	public static final int ESTATE_COST = 2;
	public static final int DUCHY_VALUE = 3;
	public static final int DUCHY_COST = 5;
	public static final int PROVINCE_VALUE = 6;
	public static final int PROVINCE_COST = 8;

	/**
	 * Curse
	 */
	public static final int CURSE_COST = 0;
	public static final int CURSE_VALUE = -1;

	/**
	 * TREASURE
	 */
	public static final int COPPER_VALUE = 1;
	public static final int COPPER_COST = 0;
	public static final int SILVER_VALUE = 2;
	public static final int SILVER_COST = 3;
	public static final int GOLD_VALUE = 3;
	public static final int GOLD_COST = 6;

	/**
	 * Init card amount for players
	 **/
	public static final int INIT_COPPER_CARDS = 5;
	public static final int INIT_ESTATE_CARDS = 3;

	public static final String ESTATE = "Estate";
	public static final String COPPER = "Copper";

	public static final String NIL = "NIL";

	public static final int EMPTY_PILES = 3;
}
