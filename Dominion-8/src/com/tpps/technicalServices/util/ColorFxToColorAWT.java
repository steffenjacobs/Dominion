package com.tpps.technicalServices.util;

import java.awt.Color;

public class ColorFxToColorAWT {

	public static void main(String[] args) {
		javafx.scene.paint.Color cfx = new javafx.scene.paint.Color(1, 0.5, 0.2, 0);
	//	System.out.println(cfx.getRed());
	//	System.out.println(cfx.toString());
		
		//Color c = new Color(cfx.toString());
		//System.out.println(cfx.toString().substring(2, cfx.toString().length()));
		Color color = hex2Rgb(cfx.toString().substring(2, cfx.toString().length()));
	}
	
	
	public static Color hex2Rgb(String colorStr) {
	    return new Color(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}
}
