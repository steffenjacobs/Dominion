package com.tpps.application.network.game;

public class ToMuchPlayerException extends Exception{

	private static final long serialVersionUID = 3504144103985981050L;
	
	public ToMuchPlayerException() {
		super("Already four player are connected to the server");
	}

}
