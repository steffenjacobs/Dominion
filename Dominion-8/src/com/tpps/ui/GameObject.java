package com.tpps.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Comparator;

import com.tpps.technicalServices.util.MathUtil;
import com.tpps.technicalServices.util.PhysicsUtil;

/**
 * represents the visualization of a game-object
 * 
 * @author sjacobs - Steffen Jacobs
 */
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

	/**
	 * changes the visability of the game object
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void setVisible(boolean state) {
		this.visable = state;
		parent.repaint(this.getHitbox());
	}

	/**
	 * @return whether the game object is visible
	 * @author sjacobs - Steffen Jacobs
	 */
	public boolean isVisible() {
		return this.visable;
	}

	/**
	 * @return whether a point is inside the hitbox of the rectangle
	 * @author sjacobs - Steffen Jacobs
	 */
	public boolean isInside(int x, int y) {
		return this.getHitbox().contains(x, y);
	}

	/**
	 * @return whether two game objects overlap
	 * @author sjacobs - Steffen Jacobs
	 */
	public boolean overlap(GameObject go2) {
		return PhysicsUtil.collides(new Rectangle(this.location.getX(), this.location.getY(), this.width, this.height),
				new Rectangle(go2.location.getX(), go2.location.getY(), go2.width, go2.height));
	}

	/**
	 * @return whether the game-object overlaps with the rectangle
	 * @author sjacobs - Steffen Jacobs
	 */
	public boolean overlap(Rectangle area) {
		return PhysicsUtil.collides(area,
				new Rectangle(this.location.getX(), this.location.getY(), this.width, this.height));
	}

	/**
	 * @return the location (x, y)
	 * @author sjacobs - Steffen Jacobs
	 */
	public Location2D getLocation() {
		return this.location;
	}

	/**
	 * @return the dimension (width and height)
	 * @author sjacobs - Steffen Jacobs
	 */
	public Dimension getDimension() {
		return new Dimension(this.width, this.height);
	}

	/**
	 * @return the hitbox of the game-object
	 * @author sjacobs - Steffen Jacobs
	 */
	public Rectangle getHitbox() {
		return new Rectangle(this.location.getPoint(), this.getDimension());
	}

	/**
	 * @return game object height
	 * @author sjacobs - Steffen Jacobs
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * @return game object width
	 * @author sjacobs - Steffen Jacobs
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * @return game object id
	 * @author sjacobs - Steffen Jacobs
	 */
	protected int getID() {
		return this.id;
	}

	/**
	 * @return the layer the game object is on
	 * @author sjacobs - Steffen Jacobs
	 */
	public int getLayer() {
		return this.layer;
	}

	/**
	 * @return the current image of the game object
	 * @author sjacobs - Steffen Jacobs
	 */
	public Image getImage() {
		return this.image;
	}

	/**
	 * @return parent framework
	 * @author sjacobs - Steffen Jacobs
	 */
	protected GraphicFramework getParent() {
		return this.parent;
	}

	/**
	 * @return a readable representation of the game object
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.location.toString() + " - " + this.getDimension().toString() + " - Layer: " + this.getLayer()
				+ " - " + this.getParent() + " - " + this.isVisible();
	}

	/**
	 * creates a game object, only used for cloning
	 * 
	 * @author sjacobs - Steffen Jacobs
	 **/
	protected GameObject(int locX, int locY, int _layer, Image sourceImage, GraphicFramework _parent, int _id) {
		this.location = new Location2D(locX, locY);
		this.image = sourceImage;
		this.parent = _parent;
		this.height = this.image.getHeight(null);
		this.width = this.image.getWidth(null);
		this.layer = _layer;
		this.id = _id;
	}

	/**
	 * creates a game object
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
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

	/**
	 * replaces the image with the newImage and udpates the framework
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void updateImage(Image newImage) {
		this.image = newImage;
		this.height = this.image.getHeight(null);
		this.width = this.image.getWidth(null);
		if(this.isVisible())
			parent.repaintSpecificArea(this.getHitbox());
	}

	/**
	 * sets the id
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void setID(int _id) {
		this.id = _id;
	}

	public boolean equals(GameObject go) {
		return this.id == go.id;
	}

	/**
	 * moves the object to the newLocation and redraws it on the framework
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void moveTo(Location2D newLocation) {
		parent.moveObject(this, newLocation);
	}

	/**
	 * a comparator for the GameObject which compares by the Layer
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	static class CompareByLayer implements Comparator<GameObject> {

		@Override
		public int compare(GameObject go1, GameObject go2) {

			return MathUtil.signumOfInteger(go1.getLayer() - go2.getLayer());
		}

	}

	/**
	 * should be implemented for compatibilty. Should clone the object
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	public abstract GameObject clone();

	/**
	 * is called by the framework when the mouse enters the visual
	 * representation of the game object
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public abstract void onMouseEnter();

	/**
	 * is called by the framework when the mouse exits the visual representation
	 * of the game object
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public abstract void onMouseExit();

	/**
	 * is called by the framework when the visual representation of the game
	 * object is clicked
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public abstract void onMouseClick();

	/**
	 * is called by the framework when the visual representation of the game
	 * object is dragged
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public abstract void onMouseDrag();
}
