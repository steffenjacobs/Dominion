package com.tpps.technicalServices.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

public class Loader {

	/**
	 * importing font from resources
	 * 
	 * @throws IOException
	 * @throws FontFormatException
	 */

	public static Font importFont() throws FontFormatException, IOException {

		Font customFont = Font.createFont(Font.TRUETYPE_FONT,
				ClassLoader.getSystemResourceAsStream("resources/font/xenippa1.TTF"));
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
				ClassLoader.getSystemResourceAsStream("resources/font/xenippa1.TTF")));
		return customFont;
	}
}
