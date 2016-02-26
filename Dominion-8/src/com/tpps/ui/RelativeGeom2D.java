package com.tpps.ui;

import java.awt.Dimension;
import java.awt.Point;

/** @author sjacobs - Steffen Jacobs */
public class RelativeGeom2D implements Cloneable {
	private double x, y;

	/**
	 * @return relative x-coordinate
	 * @author sjacobs - Steffen Jacobs
	 */
	public double getRelativeX() {
		return this.x;
	}

	/**
	 * @return relative y-coordinate
	 * @author sjacobs - Steffen Jacobs
	 */
	public double getRelativeY() {
		return this.y;
	}

	/**
	 * @return absolute x-coordinate
	 * @author sjacobs - Steffen Jacobs
	 */
	public int getAbsoluteX(int width) {
		return (int) (this.x * width + .5);
	}
	
	/**
	 * @return absolute y-coordinate
	 * @author sjacobs - Steffen Jacobs
	 */
	public int getAbsoluteY(int height) {
		return (int) (this.y * height + .5);
	}

	/**
	 * creates something similar to java.awt.Point
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public RelativeGeom2D(double locX, double locY) {
		this.x = locX;
		this.y = locY;
	}

	/**
	 * adds a second location to the first location
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void addRelative(RelativeGeom2D vector2) {
		this.x += vector2.getRelativeX();
		this.y += vector2.getRelativeY();
	}

	/**
	 * sets the x-coordinate to the desired value
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void setRelativeX(double newX) {
		this.x = newX;
	}

	/**
	 * sets the y-coordinate to the desired value
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void setRelativeY(double newY) {
		this.y = newY;
	}

	/**
	 * @return an  absolute java.awt.Point (for compatibilty)
	 * @author sjacobs - Steffen Jacobs
	 */
	public Point getAbsolutePoint(int width, int height) {
		return new Point(this.getAbsoluteX(width), this.getAbsoluteY(height));
	}
	

	/**
	 * @return an  absolute java.awt.Dimension (for compatibilty)
	 * @author sjacobs - Steffen Jacobs
	 */
	public Dimension getAbsoluteDimension(int width, int height) {
		return new Dimension(this.getAbsoluteX(width), this.getAbsoluteY(height));
	}

	/**
	 * @return a readable representation of the object
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public String toString() {
		return "Vector2D: " + this.getRelativeX() + "/" + this.getRelativeY();
	}

	/**
	 * @return an exact clone of the object
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public RelativeGeom2D clone() {
		return new RelativeGeom2D(this.x, this.y);
	}
}