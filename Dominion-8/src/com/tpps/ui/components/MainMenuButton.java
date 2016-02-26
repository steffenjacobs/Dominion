package com.tpps.ui.components;

import java.awt.Image;

public class MainMenuButton {
	private int x, y;
	private Image sourceImage;
	


	public MainMenuButton(int locX, int locY, Image sourceImage) {
		this.x = locX;
		this.y = locY;
		this.sourceImage = sourceImage;
		
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
