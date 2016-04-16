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
}