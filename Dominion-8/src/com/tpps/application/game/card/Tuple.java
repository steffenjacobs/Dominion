package com.tpps.application.game.card;

/**
 * 
 *
 * @param <A>
 * @param <B>
 */
public class Tuple<A, B> {
	private A a;
	private B b;

	/**
	 * 
	 * @param a
	 * @param b
	 */
	public Tuple(A a, B b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * 
	 * @return
	 */
	public A getFirstEntry() {
		return a;
	}

	/**
	 * 
	 * @return
	 */
	public B getSecondEntry() {
		return b;
	}

	/**
	 * 
	 */
	public String toString() {
		return "First entry: " + a + "second entry: " + b;
	}
}
