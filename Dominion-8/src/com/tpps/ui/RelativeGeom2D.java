package com.tpps.ui;

import java.awt.Dimension;
import java.awt.Point;

/** @author Steffen Jacobs */
public class RelativeGeom2D implements Cloneable {
	private double x, y;

	/**
	 * @return relative x-coordinate
	 * @author Steffen Jacobs
	 */
	public double getRelativeX() {
		return this.x;
	}

	/**
	 * @return relative y-coordinate
	 * @author Steffen Jacobs
	 */
	public double getRelativeY() {
		return this.y;
	}

	/**
	 * @return absolute x-coordinate
	 * @author Steffen Jacobs
	 * @param width the absolute width
	 */
	public int getAbsoluteX(int width) {
		return (int) (this.x * width + .5);
	}
	
	/**
	 * @return absolute y-coordinate
	 * @author Steffen Jacobs
	 * @param height the absolute height
	 */
	public int getAbsoluteY(int height) {
		return (int) (this.y * height + .5);
	}

	/**
	 * creates something similar to java.awt.Point
	 * 
	 * @author Steffen Jacobs
	 * @param _x the relative x
	 * @param _y the relative y
	 */
	public RelativeGeom2D(double _x, double _y) {
		this.x = _x;
		this.y = _y;
	}

	/**
	 * adds a second location to the first location
	 * 
	 * @author Steffen Jacobs
	 * @param vector the geom to add
	 */
	public void addRelative(RelativeGeom2D vector) {
		this.x += vector.getRelativeX();
		this.y += vector.getRelativeY();
	}

	/**
	 * sets the x-coordinate to the desired value
	 * 
	 * @author Steffen Jacobs
	 * @param newX the new relative x
	 */
	public void setRelativeX(double newX) {
		this.x = newX;
	}

	/**
	 * sets the y-coordinate to the desired value
	 * 
	 * @author Steffen Jacobs
	 * @param newY the new relative y
	 */
	public void setRelativeY(double newY) {
		this.y = newY;
	}

	/**
	 * @return an  absolute java.awt.Point (for compatibility)
	 * @author Steffen Jacobs
	 * @param width the absolute width
	 * @param height the absolute height
	 */
	public Point getAbsolutePoint(int width, int height) {
		return new Point(this.getAbsoluteX(width), this.getAbsoluteY(height));
	}
	

	/**
	 * @return an  absolute java.awt.Dimension (for compatibility)
	 * @author Steffen Jacobs
	 * @param width the absolute width
	 * @param height the absolute height
	 */
	public Dimension getAbsoluteDimension(int width, int height) {
		return new Dimension(this.getAbsoluteX(width), this.getAbsoluteY(height));
	}

	/**
	 * @return a readable representation of the object
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return "Vector2D: " + this.getRelativeX() + "/" + this.getRelativeY();
	}

	/**
	 * @return an exact clone of the object
	 * @author Steffen Jacobs
	 */
	@Override
	public RelativeGeom2D clone() {
		return new RelativeGeom2D(this.x, this.y);
	}
}