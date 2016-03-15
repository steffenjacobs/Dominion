package com.tpps.technicalServices.util;

import java.awt.Color;

/**
 * 
 * 
 * class converts JAVAFX colors to JAVA AWT colors, but does not work correctly so far not sure if
 * it is supposed to work anyways because we shouldnt use fxColors in swing
 */
public class ColorUtil {

	public static final Color DARKSEAGREEN = new Color(143, 188, 143);
	public static final Color MEDIUMGRAY = new Color(215, 215, 215);
	public static final Color SNOW = new Color(255, 250, 250);

	public static javafx.scene.paint.Color getFxColor(java.awt.Color color) {
		return javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 255.0);
	}

	public static java.awt.Color getAwtColor(javafx.scene.paint.Color color) {
		String colorStr = color.toString().substring(2, color.toString().length());
		return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr.substring(5, 7), 16));
	}

	// public static java.awt.Color getAwtColor(javafx.scene.paint.Color color) {
	// return new java.awt.Color((int) color.getRed(),(int) color.getGreen(), (int)
	// color.getBlue());
	// }
}
