package com.tpps.ui.components;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class MainMenuButton {
	private int x, y;
	private final BufferedImage sourceImage;
	private final String name;
	


	public MainMenuButton(int locX, int locY, BufferedImage sourceImage, String name) {
		this.x = locX;
		this.y = locY;
		this.name = name;
		this.sourceImage = sourceImage;
		Graphics2D g = this.sourceImage.createGraphics();
		
		g.setFont(new Font("Comic Sans MS", Font.PLAIN, 100));
		
		g.setColor(Color.BLACK);
		g.drawString(name, (sourceImage.getWidth(null) / 2) - name.length() * 22, (int)(sourceImage.getHeight(null) / 1.4));
		
	}
	
	public boolean isIn(double x, double y){
		
		if (this.x <= x && x <= this.sourceImage.getWidth() + this.x
				&& this.y <= y && y <= this.sourceImage.getHeight() + this.y){		
			return true;
		}else{			
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
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public Image getSourceImage(){
		return this.sourceImage;
		
	}




}
