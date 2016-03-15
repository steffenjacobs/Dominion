package com.tpps.technicalServices.util;

/**
 * class converts JAVAFX colors to JAVA AWT colors, but not sure
 * 
 */
public class ColorUtil {

	public static java.awt.Color getAwtColor(javafx.scene.paint.Color color) {
		return new java.awt.Color((int) color.getRed(),(int) color.getGreen(), (int) color.getBlue());
	}
	
	public static javafx.scene.paint.Color getFxColor(java.awt.Color color) {
		return javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()/255.0);
	}
}
