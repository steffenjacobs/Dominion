package com.tpps.technicalServices.network.game;

public class TooMuchPlayerException extends Exception{

	private static final long serialVersionUID = 3504144103985981050L;
	
	public TooMuchPlayerException() {
		super("Already four player are connected to the server");
	}

}
