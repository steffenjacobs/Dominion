package com.tpps.technicalServices.logger;

public class DrawAndShuffle {

	private final boolean shuffled;
	private final int drawAmount;
	
	public DrawAndShuffle(boolean shuffled, int drawAmount) {
		this.shuffled = shuffled;
		this.drawAmount = drawAmount;
	}

	/**
	 * @return the shuffled
	 */
	public boolean wasShuffled() {
		return shuffled;
	}

	/**
	 * @return the drawAmount
	 */
	public int getDrawAmount() {
		return drawAmount;
	}	
	
}
