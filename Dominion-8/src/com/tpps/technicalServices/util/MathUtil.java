package com.tpps.technicalServices.util;

/** provides some useful mathematical-calculations @author Steffen Jacobs */
public final class MathUtil {
	
	/**
	 * @return the signum
	 * @author Steffen Jacobs
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
