package com.tpps.technicalServices.util;

/**
 * ANSIUtil provides basic ANSI color codes with which the console output can be colored.
 * Better Look and Feel of the log messages.
 * 
 * @author Nicolas
 *
 */
public class ANSIUtil {

	public static final String ANSI_RESET = "\u001B[0m";

	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_MAGENTA = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
	/**
	 * 
	 * @param text the text to color
	 * @return the white-colored text
	 */
	public static String getWhiteText(String text) {
		return ANSI_WHITE + text + ANSI_RESET;
	}

	/**
	 * 
	 * @param text the text to color
	 * @return the cyan-colored text
	 */
	public static String getCyanText(String text) {
		return ANSI_CYAN + text + ANSI_RESET;
	}

	/**
	 * 
	 * @param text the text to color
	 * @return the red-colored text
	 */
	public static String getRedText(String text) {
		return ANSI_RED + text + ANSI_RESET;
	}
	
	/**
	 * 
	 * @param text the text to color
	 * @param ansicolor the ansicolor to color the text with
	 * @return the ansicolored text
	 */
	public static String getAnsiColoredText(String text, String ansicolor) {
		return ansicolor + text + ANSI_RESET;
	}
}
