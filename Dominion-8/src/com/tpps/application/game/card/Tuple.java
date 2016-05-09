package com.tpps.application.game.card;

import java.io.Serializable;

/**
 *
 * @param <A>
 */
public class Tuple<A> implements Serializable{

	private static final long serialVersionUID = -804258763406909062L;
	private A a;
	private int val;

	/**
	 * 
	 * @param a
	 * @param val
	 */
	public Tuple(A a, int val) {
		this.a = a;
		this.val = val;
	}

	/**
	 * 
	 * @return the first entry
	 */
	public A getFirstEntry() {
		return a;
	}

	/**
	 * 
	 * @return the second entry
	 */
	public int getSecondEntry() {
		return val;
	}

	/**
	 * decrement the second entry
	 */
	public void decrementSecondEntry() {
		this.val--;
	}
	
	/**
	 * String representation
	 */
	@Override
	public String toString() {
		return "First entry: " + a + "second entry: " + val;
	}
}
