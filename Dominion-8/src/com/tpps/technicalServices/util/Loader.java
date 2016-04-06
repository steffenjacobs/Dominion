package com.tpps.technicalServices.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Loader {

	private static Loader instance = new Loader();
	private Font xenippa;
	
	public Loader() {
		try {
			xenippa = importFont();
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		};
	}
	
	public static Loader getInstance() {
		return Loader.instance;
	}
	
	public Font getXenipa(){
		return this.xenippa;
	}
	
	
	/**
	 * importing font from resources
	 * 
	 * @throws IOException
	 * @throws FontFormatException
	 */

	public Font importFont() throws FontFormatException, IOException {
		System.out.println("im gameWindow"+ ClassLoader.getSystemResource("resources/font/xenippa1.ttf"));
		
		Font customFont = Font.createFont(Font.TRUETYPE_FONT, 
				ClassLoader.getSystemResourceAsStream("resources/font/xenippa1.ttf"));
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
				ClassLoader.getSystemResourceAsStream("resources/font/xenippa1.ttf")));		
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
