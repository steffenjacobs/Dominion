package com.tpps.application.game.card;

public class Tuple<A, B> {
	private A a;
	private B b;
	
	public Tuple(A a, B b){
		
	}

	public A getFirstEntry() {
		return a;
	}

	public B getB() {
		return b;
	}
	
	public String toString(){
		return "First entry: " + a + "second entry: " + b;
	}
	
	

}
