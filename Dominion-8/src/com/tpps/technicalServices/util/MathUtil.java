package com.tpps.technicalServices.util;

/** a class containing simple calculations */
public final class MathUtil {

	/**
	 * @return the signum
	 * @author Steffen Jacobs
	 * @param x the value which should be checked for signum
	 */
	public static int signumOfInteger(int x) {
		if (x < 0)
			return -1;
		if (x > 0)
			return 1;
		else
			return 0;
	}
	
	/**
	 * @return a time-sting hh:mm:ss
	 * @param milliseconds
	 *            delta-time
	 */
	public static String getTimeString(long milliseconds) {
		long second = (milliseconds / 1000) % 60;
		long minute = (milliseconds / (1000 * 60)) % 60;
		long hour = (milliseconds / (1000 * 60 * 60));

		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
}