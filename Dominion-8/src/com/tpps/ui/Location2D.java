package com.tpps.ui;

import java.awt.Point;

/** @author sjacobs - Steffen Jacobs */
public class Location2D implements Cloneable {
	private int x, y;

	/**
	 * @return x-coordinate
	 * @author sjacobs - Steffen Jacobs
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * @return y-coordinate
	 * @author sjacobs - Steffen Jacobs
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * creates something similar to java.awt.Point but with integers
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public Location2D(int locX, int locY) {
		this.x = locX;
		this.y = locY;
	}

	/**
	 * adds a second location to the first location
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void add(Location2D vector2) {
		this.x += vector2.getX();
		this.y += vector2.getY();
	}

	/**
	 * sets the x-coordinate to the desired value
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void setX(int newX) {
		this.x = newX;
	}

	/**
	 * sets the y-coordinate to the desired value
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void setY(int newY) {
		this.y = newY;
	}

	/**
	 * @return a java.awt.Point (for compatibilty)
	 * @author sjacobs - Steffen Jacobs
	 */
	public Point getPoint() {
		return new Point(this.x, this.y);
	}

	/**
	 * @return a readable representation of the object
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public String toString() {
		return "Vector2D: " + this.getX() + "/" + this.getY();
	}

	/**
	 * @return an exact clone of the object
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public Location2D clone() {
		return new Location2D(this.x, this.y);
	}
}
