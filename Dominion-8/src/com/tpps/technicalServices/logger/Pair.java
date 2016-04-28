package com.tpps.technicalServices.logger;

import java.awt.Color;

public class Pair<S, C> {

	public final S s;
	public final C c;

	public Pair(S s, C c) {
		this.s = s;
		this.c = c;
	}
	
	public S getS() {
		return this.s;
	}
	
	public C getC() {
		return this.c;
	}
	
	public static void main(String[] args) {
		Pair<String, Color> pair = new Pair<String, Color>("Hey, die Farbe ist: ", Color.BLACK);
	}

}
