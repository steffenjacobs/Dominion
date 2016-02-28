package com.tpps.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tpps.technicalServices.util.GraphicsUtil;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class MainMenuButton {
	private int x, y;
	private final BufferedImage originalImage;
	private BufferedImage actualImage;
	private final String name;
	private int letterSize;

	public MainMenuButton(int locX, int locY, String name) throws IOException{
		this.letterSize = 100;
		this.originalImage = ImageIO
				.read(ClassLoader
						.getSystemResource("resources/img/gameObjects/testButton.png"));
	
		
		
		int newWidth = (int)(Toolkit.getDefaultToolkit().getScreenSize().width / 2.35);
		int newHeight = (int)(Toolkit.getDefaultToolkit().getScreenSize().height / 6);
		this.name = name;	
		labelImages();
		this.actualImage = GraphicsUtil.resize(this.originalImage, newWidth, newHeight);
		this.x = locX - (actualImage.getWidth() / 2);
		this.y = locY;
		
		
		
	}

	private void labelImages() {
		Graphics2D g = this.originalImage.createGraphics();

		g.setFont(new Font("Comic Sans MS", Font.PLAIN, letterSize));

		g.setColor(Color.BLACK);
		g.drawString(this.name,
				(originalImage.getWidth() / 2) - this.name.length() * 22,
				(int) (originalImage.getHeight() / 1.4));

	}

	/**
	 * @param x value of the mouse
	 * @param y value of the mouse
	 * @return true if the mouse is on the button false else
	 */
	public boolean isOn(double x, double y) {
		if (this.x <= x && x <= this.actualImage.getWidth() + this.x
				&& this.y <= y && y <= this.actualImage.getHeight() + this.y) {
			return true;
		} else {
			return false;
		}
	}

	public void onMouseEnter() {
		System.out.println("enter " + toString());
	}

	public void onMouseExit() {
		System.out.println("exit " + toString());
	}

	public void onMouseClick() {
		System.out.println("Clicked @" + this.toString());
	}
	
	public void onResize(int x, int y, double sizeFactorWidth, double sizeFactorHeight){
		this.x = x;
		this.y = y;
		System.out.println(sizeFactorHeight);
		System.out.println(sizeFactorWidth);
		this.actualImage = GraphicsUtil.resize((BufferedImage) this.originalImage,
				(int)(this.actualImage.getWidth() * sizeFactorWidth), 
				(int)(this.actualImage.getHeight() * sizeFactorHeight));
		
	}
	
	public BufferedImage getActualImage(){
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
