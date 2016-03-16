package com.tpps.technicalServices.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

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
	
	public static BufferedImage loadingImage(BufferedImage im, String resource) {
		try {
			im = ImageIO.read(ClassLoader.getSystemResourceAsStream(resource));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return im;

	}
}
