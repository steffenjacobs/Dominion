package com.tpps.technicalServices.network.matchmaking.server;

public class StatisticUnit {
	private Object obj;

	public StatisticUnit(Object stat) {
		this.obj = stat;
	}

	public int asInteger() {
		return (int) obj;
	}

	public long[] asLongArray() {
		return (long[]) obj;
	}

	public boolean[] asBooleanArray() {
		return (boolean[]) obj;
	}
}