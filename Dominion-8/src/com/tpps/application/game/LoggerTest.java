package com.tpps.application.game;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerTest {
	
	public static void main(String[] args) {
		Logger log = Logger.getLogger(LoggerTest.class.getName());
		Handler handler = new ConsoleHandler();
		handler.setLevel(Level.FINEST);
		log.addHandler(handler);
		log.setLevel(Level.FINEST);
		log.fine("Alles Fein Yo");
		log.info("Random Info");
	}
}
