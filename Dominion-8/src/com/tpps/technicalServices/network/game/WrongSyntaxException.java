package com.tpps.technicalServices.network.game;

public class WrongSyntaxException extends Exception{

	private static final long serialVersionUID = -7255506180862012051L;
	
	public WrongSyntaxException(){
		super("internal error: wrong syntax for cardId");
	}
}
