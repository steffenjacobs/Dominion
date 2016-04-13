package com.tpps.technicalServices.network.matchmaking.server;

/**
 * represents a Container-Object for all stats
 * 
 * @author Steffen Jacobs
 */
public class StatisticUnit {
	private Object obj;

	/**
	 * initializes the container with a stat
	 * 
	 * @param stat
	 *            the statistics
	 */
	public StatisticUnit(Object stat) {
		this.obj = stat;
	}

	/** @return container-interpretation as an integer */
	public int asInteger() {
		return (int) obj;
	}

	/** @return container-interpretation as an arry of longs */
	public long[] asLongArray() {
		return (long[]) obj;
	}

	/** @return container-interpretation as an arry of booleans */
	public boolean[] asBooleanArray() {
		return (boolean[]) obj;
	}
}