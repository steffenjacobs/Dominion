package com.tpps.technicalServices.util;

public class ANSIUtil {

	public static final String ANSI_RESET = "\u001B[0m";

	public static final String ANSI_WHITE = "\u001B[37m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_RED = "\u001B[31m";

	public static String getWhiteText(String text) {
		return ANSI_WHITE + text + ANSI_RESET;
	}

	public static String getCyanText(String text) {
		return ANSI_CYAN + text + ANSI_RESET;
	}

	public static String getRedText(String text) {
		return ANSI_RED + text + ANSI_RESET;
	}
}
