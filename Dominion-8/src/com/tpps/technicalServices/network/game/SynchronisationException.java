package com.tpps.technicalServices.network.game;

public class SynchronisationException extends Exception{

	private static final long serialVersionUID = -1379983755091068880L;
	
	public SynchronisationException() {
		super("sorry something went wrong the required card was not found on the gameBoard");
	}
	
	
	

}
