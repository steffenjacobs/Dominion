package com.tpps.application.game;

/**
 * CardName is an enum with all card names so there won't be any typos crashing the game
 * 
 * @author Nicolas
 *
 */
public enum CardName {

	ESTATE("Estate"),
	DUCHY("Duchy"),
	PROVINCE("Province"),
	CURSE("Curse"),
	COPPER("Copper"),
	SILVER("Silver"),
	GOLD("Gold"),
	
	ADVENTURER("Adventurer"),
	BUREAUCRAT("Bureaucrat"),
	CELLAR("Cellar"),
	CHANCELLOR("Chancellor"),
	CHAPEL("Chapel"),
	COUNCILROOM("CouncilRoom"),
	FEAST("Feast"),
	FESTIVAL("Festival"),
	LABORATORY("Laboratory"),
	LIBRARY("Library"),
	MARKET("Market"),
	MILITIA("Militia"),
	MINE("Mine"),
	MOAT("Moat"),
	MONEYLENDER("Moneylender"),
	REMODEL("Remodel"),
	SMITHY("Smithy"),
	SPY("Spy"),
	THIEF("Thief"),
	THRONEROOM("ThroneRoom"),
	VILLAGE("Village"),
	WITCH("Witch"),
	WOODCUTTER("Woodcutter"),
	WORKSHOP("Workshop");
	
	private String name;
	
	private CardName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
