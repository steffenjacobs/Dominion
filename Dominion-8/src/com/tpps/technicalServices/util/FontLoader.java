package com.tpps.technicalServices.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;

public class FontLoader {

	private static FontLoader instance = new FontLoader();
	private Font xenippa;

	public FontLoader() {
		try {
			xenippa = importFont();
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		;
	}

	public static FontLoader getInstance() {
		return FontLoader.instance;
	}

	public Font getXenipa() {
		return this.xenippa;
	}

	/**
	 * importing font from resources
	 * 
	 * @throws IOException
	 * @throws FontFormatException
	 */

	public Font importFont() throws FontFormatException, IOException {
		GameLog.log(MsgType.INFO,"in gameWindow: " + ClassLoader.getSystemResource("resources/font/xenippa1.ttf"));

		Font customFont = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("resources/font/xenippa1.ttf"));
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("resources/font/xenippa1.ttf")));
		return customFont;
	}
}
