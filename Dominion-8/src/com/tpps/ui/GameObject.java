package com.tpps.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Comparator;

import com.tpps.technicalServices.util.MathUtil;
import com.tpps.technicalServices.util.PhysicsUtil;

/** @author sjacobs - Steffen Jacobs */
public abstract class GameObject implements Cloneable, Serializable {
	private static final long serialVersionUID = 3954559836391148293L;

	private static int objectCounter = 0;

	private int id;
	private Image image;
	private Location2D location;
	private int height, width;
	private GraphicFramework parent;
	private int layer;
	private boolean visable = true;

	public void setVisable(boolean state) {
		this.visable = state;
	}

	public boolean isVisible() {
		return this.visable;
	}

	public boolean isInside(int x, int y) {
		return !((x < this.location.getX() || x > this.location.getX() + this.width)
				&& (y < this.location.getY() || y > this.location.getY() + this.height));
	}

	public boolean overlap(GameObject go2) {
		return PhysicsUtil.collides(new Rectangle(this.location.getX(), this.location.getY(), this.width, this.height),
				new Rectangle(go2.location.getX(), go2.location.getY(), go2.width, go2.height));
	}

	public boolean overlap(Rectangle area) {
		return PhysicsUtil.collides(area,
				new Rectangle(this.location.getX(), this.location.getY(), this.width, this.height));
	}

	public Location2D getLocation() {
		return this.location;
	}

	public Dimension getDimension() {
		return new Dimension(this.width, this.height);
	}

	public Rectangle getHitbox() {
		return new Rectangle(this.location.getPoint(), this.getDimension());
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	protected int getID() {
		return this.id;
	}

	public int getLayer() {
		return this.layer;
	}

	public Image getImage() {
		return this.image;
	}

	protected GraphicFramework getParent() {
		return this.parent;
	}

	@Override
	public String toString() {
		return this.location.toString() + " - " + this.getDimension().toString() + " - Layer: " + this.getLayer()
				+ " - " + this.getParent() + " - " + this.isVisible();
	}

	public GameObject(int locX, int locY, int _layer, Image sourceImage, GraphicFramework _parent, int _id) {
		this.location = new Location2D(locX, locY);
		this.image = sourceImage;
		this.parent = _parent;
		this.height = this.image.getHeight(null);
		this.width = this.image.getWidth(null);
		this.layer = _layer;
		this.id = _id;
	}

	public GameObject(int locX, int locY, int _layer, Image sourceImage, GraphicFramework _parent) {
		this.location = new Location2D(locX, locY);
		this.image = sourceImage;
		this.parent = _parent;
		this.height = this.image.getHeight(null);
		this.width = this.image.getWidth(null);
		this.layer = _layer;
		this.id = GameObject.objectCounter;
		GameObject.objectCounter++;
	}

	public void updateImage(Image newImage) {
		this.image = newImage;
		this.height = this.image.getHeight(null);
		this.width = this.image.getWidth(null);
		parent.redrawObjectsWithoutRaytrace(this);
	}

	public void setID(int _id) {
		this.id = _id;
	}

	public void moveTo(Location2D newLocation) {
		parent.moveObject(this, newLocation);
	}

	static class CompareByLayer implements Comparator<GameObject> {

		@Override
		public int compare(GameObject go1, GameObject go2) {

			return MathUtil.signumOfInteger(go1.getLayer() - go2.getLayer());
		}

	}

	@Override
	public abstract GameObject clone();

	public abstract void onMouseEnter();

	public abstract void onMouseExit();

	public abstract void onMouseClick();

	public abstract void onMouseDrag();
}
