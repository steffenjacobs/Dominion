package com.tpps.technicalServices.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.google.common.base.Throwables;

public class DominionLog {
	
	public static final Logger GAME_LOGGER = Logger.getLogger("GameLogger");
	public static final Logger DEBUG_LOGGER = Logger.getLogger("DebugLogger");
	public static final Logger SETUP_LOGGER = Logger.getLogger("SetupLogger");
	
	public DominionLog() {
		try {
			FileHandler gameHandler = new FileHandler("/resources/logfiles/MyLog.txt");
			GAME_LOGGER.addHandler(gameHandler);
//			gameHandler.setLevel(Level.FINEST);
//			GAME_LOGGER.setLevel(Level.FINEST);
			gameHandler.setFormatter(new SimpleFormatter());
			// SETUP LOGGER
			// DEBUG LOGGER
		} catch(SecurityException se) {
			DominionLog.DEBUG_LOGGER.log(Level.WARNING, Throwables.getStackTraceAsString(se));
		} catch(IOException e) {
			DominionLog.DEBUG_LOGGER.log(Level.WARNING, Throwables.getStackTraceAsString(e));
		}
	}
	
	public static void main(String[] args) {
//		DominionLog dl = new DominionLog();
		GAME_LOGGER.info("Test");
	}
}
