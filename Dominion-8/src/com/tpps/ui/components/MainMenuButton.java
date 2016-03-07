package com.tpps.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.Loader;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class MainMenuButton {
	private int x, y;
	private BufferedImage originalImage;
	private BufferedImage actualImage;
	private final String name;
	private int letterSize;

	/**
	 * Concstructor for the MainMenu buttons 
	 * @param locX x coordinate of the button
	 * @param locY y coordinate of the button
	 * @param name of the button
	 * @throws IOException
	 */
	public MainMenuButton(int locX, int locY, String name) throws IOException {
		this.letterSize = 83;
		this.originalImage = ImageIO.read(ClassLoader.getSystemResource("resources/img/gameObjects/testButton.png"));
		this.originalImage = GraphicsUtil.resize(this.originalImage, this.originalImage.getWidth(), 
				(int)(this.originalImage.getHeight() * 0.9));
		int newWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().width / 2.35);
		int newHeight = (int) (Toolkit.getDefaultToolkit().getScreenSize().height / 6);
		this.name = name;
		labelImages();
		this.actualImage = GraphicsUtil.resize(this.originalImage, newWidth, newHeight);
		this.x = locX - (newWidth / 2);
		this.y = locY;
	}

	/**
	 * prints the names of the buttons on the images
	 */
	private void labelImages() {
		Graphics2D g = this.originalImage.createGraphics();

//		g.setFont(Loader.importFont(), Font.PLAIN, letterSize);
		try {
			g.setFont(Loader.importFont().deriveFont(Font.PLAIN, this.letterSize));
			g.setColor(Color.BLACK);
			g.drawString(this.name, (originalImage.getWidth() / 2) - this.name.length() * 22,
					(int) (originalImage.getHeight() / 1.5));
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param x value of the mouse
	 * @param y value of the mouse
	 * @return true if the mouse is on the button false else
	 */
	public boolean isOn(double x, double y) {
		if (this.x <= x && x <= this.actualImage.getWidth() + this.x && this.y <= y
				&& y <= this.actualImage.getHeight() + this.y) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * reacts on resize events of the gui and resizes the buttons
	 * @param x new x value
	 * @param y new y value
	 * @param sizeFactorWidth resize factor
	 * @param sizeFactorHeight resize factor
	 */
	public void onResize(int x, int y, double sizeFactorWidth, double sizeFactorHeight) {
		this.x = x;
		this.y = y;		
		this.actualImage = GraphicsUtil.resize((BufferedImage) this.originalImage,
				(int) (this.originalImage.getWidth() * sizeFactorWidth),
				(int) (this.originalImage.getHeight() * sizeFactorHeight));
	}

	/**
	 * 
	 * @return the actualButtonImage
	 */
	public BufferedImage getActualImage() {
		return this.actualImage;
	}

	/**
	 * 
	 * @return the x coordinate of the button
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * 
	 * @return the y coordinate of the button
	 */
	public int getY() {
		return this.y;
	}

}
