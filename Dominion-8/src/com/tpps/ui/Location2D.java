package com.tpps.ui;

public class Location2D implements Cloneable{
	private int x, y;	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public Location2D(int locX, int locY){
		this.x = locX;
		this.y = locY;
	}
	
	public void addLoc(Location2D vector2){
		this.x += vector2.getX();
		this.y += vector2.getY();		
	}
	
	public void add(Location2D vector2){
		this.x += vector2.getX();
		this.y += vector2.getY();
	}
	
	public void setX(int newX){
		this.x = newX;
	}
	
	public void setY(int newY){
		this.y = newY;
	}
	
	@Override
	public String toString(){
		return "Vector2D: " + this.getX() + "/" + this.getY();
	}
	
	@Override
	public Location2D clone(){
		return new Location2D(this.x, this.y);				
	}
}
