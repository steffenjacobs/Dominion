package com.tpps.application.game.card;

public class Tuple<A> {

	private A a;
	private int val;

	public Tuple(A a, int val) {
		this.a = a;
		this.val = val;
	}

	/**
	 * 
	 * @return
	 */
	public A getFirstEntry() {
		return a;
	}

	public int getSecondEntry() {
		return val;
	}

	public void decrementSecondEntry() {
		this.val--;
	}

	public String toString() {
		return "First entry: " + a + "second entry: " + val;

	}
}
