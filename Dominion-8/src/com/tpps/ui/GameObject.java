package com.tpps.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Comparator;

import com.tpps.technicalServices.util.MathUtil;
import com.tpps.technicalServices.util.PhysicsUtil;

/**
 * represents the visualization of a game-object
 * 
 * @author Steffen Jacobs
 */
public abstract class GameObject implements Cloneable, Serializable {
	private static final long serialVersionUID = 3954559836391148293L;

	private static int objectCounter = 0;

	private int id;
	private Image renderedImage;

	private Image bufferedImage;
	private RelativeGeom2D location;
	private RelativeGeom2D dimension;
	private int x, y, height, width;
	private GraphicFramework parent;
	private int layer;
	private boolean visible = true;

	/**
	 * Dummy **SORRY** =D
	 * 
	 * @author - Nico
	 */
	public GameObject() {

	}

	/**
	 * creates a game object, only used for cloning
	 * 
	 * @author Steffen Jacobs
	 **/
	protected GameObject(double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight,
			int absWidth, int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, int _id) {
		this.location = new RelativeGeom2D(relativeLocX, relativeLocY);
		this.dimension = new RelativeGeom2D(relativeWidth, relativeHeight);
		this.bufferedImage = sourceImage;
		this.parent = _parent;
		this.layer = _layer;
		this.id = _id;
		this.resizeObject(absWidth, absHeight);
	}

	/**
	 * creates a game object and automatically sets absolute values for width
	 * and height
	 * 
	 * @author Steffen Jacobs
	 */
	public GameObject(double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight, int _layer,
			Image sourceImage, GraphicFramework _parent) {
		this.location = new RelativeGeom2D(relativeLocX, relativeLocY);
		this.dimension = new RelativeGeom2D(relativeWidth, relativeHeight);
		this.bufferedImage = sourceImage;
		this.parent = _parent;
		this.layer = _layer;
		this.id = GameObject.objectCounter++;
		this.resizeObject(_parent.getWidth(), _parent.getHeight());
	}

	/**
	 * creates a game object
	 * 
	 * @author Steffen Jacobs
	 */
	public GameObject(double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight,
			int absWidth, int absHeight, int _layer, Image sourceImage, GraphicFramework _parent) {
		this.location = new RelativeGeom2D(relativeLocX, relativeLocY);
		this.dimension = new RelativeGeom2D(relativeWidth, relativeHeight);
		this.bufferedImage = sourceImage;
		this.parent = _parent;
		this.layer = _layer;
		this.id = GameObject.objectCounter++;
		this.resizeObject(absWidth, absHeight);
	}

	/**
	 * constructor for all objects that are not visible instantaneous
	 * 
	 * @author Steffen Jacobs
	 */
	public GameObject(GraphicFramework _parent) {
		this.location = new RelativeGeom2D(1, 1);
		this.dimension = new RelativeGeom2D(1, 1);
		this.parent = _parent;
		this.id = GameObject.objectCounter++;
		this.resizeObject(_parent.getWidth(), _parent.getHeight());
		this.bufferedImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		this.visible = false;
	}

	/**
	 * sets the layer the game object is on
	 * 
	 * @param layer
	 *            new layer
	 * @author Steffen Jacobs
	 */
	public void setLayer(int layer) {
		this.layer = layer;
		parent.repaint(this.getHitbox());
	}

	/**
	 * changes the visibility of the game object
	 * 
	 * @author Steffen Jacobs
	 */
	public void setVisible(boolean state) {
		this.visible = state;
		parent.repaint(this.getHitbox());
	}

	/**
	 * @return whether the game object is visible
	 * @author Steffen Jacobs
	 */
	public boolean isVisible() {
		return this.visible;
	}

	/**
	 * @return whether a point is inside the hitbox of the rectangle
	 * @author Steffen Jacobs
	 */
	public boolean isInside(int x, int y) {
		return this.getHitbox().contains(x, y);
	}

	/**
	 * @return whether two game objects overlap
	 * @author Steffen Jacobs
	 */
	public boolean overlap(GameObject go2) {
		return PhysicsUtil.collides(new Rectangle(this.x, this.y, this.width, this.height),
				new Rectangle(go2.x, go2.y, go2.width, go2.height));
	}

	/**
	 * @return whether the game-object overlaps with the rectangle
	 * @author Steffen Jacobs
	 */
	public boolean overlap(Rectangle area) {
		return PhysicsUtil.collides(area, new Rectangle(this.x, this.y, this.width, this.height));
	}

	/**
	 * updates the size
	 * 
	 * @param width
	 *            new width
	 * @param height
	 *            new height
	 * @author Steffen Jacobs
	 */
	public void updateRelativeSize(double width, double height) {
		this.dimension = new RelativeGeom2D(width, height);
		this.resizeObject(this.parent.getWidth(), this.parent.getHeight());
	}

	/**
	 * @return the location (x, y)
	 * @author Steffen Jacobs
	 */
	public Point getLocation() {
		return new Point(this.x, this.y);
	}

	/**
	 * @return the dimension (width and height)
	 * @author Steffen Jacobs
	 */
	public Dimension getDimension() {
		return new Dimension(this.width, this.height);
	}

	/**
	 * @return the hitbox of the game-object
	 * @author Steffen Jacobs
	 */
	public Rectangle getHitbox() {
		return new Rectangle(this.x, this.y, this.width, this.height);
	}

	/**
	 * @return game object height
	 * @author Steffen Jacobs
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * @return game object width
	 * @author Steffen Jacobs
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * @return game object id
	 * @author Steffen Jacobs
	 */
	protected int getID() {
		return this.id;
	}

	/**
	 * @return the layer the game object is on
	 * @author Steffen Jacobs
	 */
	public int getLayer() {
		return this.layer;
	}

	/**
	 * @return the original image of the game object
	 * @author Steffen Jacobs
	 */
	public Image getBufferedImage() {
		return this.bufferedImage;
	}

	/**
	 * @return the current image of the game object
	 * @author Steffen Jacobs
	 */
	public Image getRenderdImage() {
		return this.renderedImage;
	}

	/**
	 * @return parent framework
	 * @author Steffen Jacobs
	 */
	protected GraphicFramework getParent() {
		return this.parent;
	}

	/**
	 * @return a readable representation of the game object
	 * @author Steffen Jacobs
	 */
	@Override
	public String toString() {
		return this.location.toString() + " - " + this.dimension.toString() + " - Layer: " + this.getLayer() + " - "
				+ this.getParent() + " - " + this.isVisible();
	}

	/**
	 * is called when the window has been resized and the object have to be
	 * resized and repositioned, too
	 * 
	 * @author Steffen Jacobs
	 */
	public void resizeObject(int absWidth, int absHeight) {
		this.x = this.location.getAbsoluteX(absWidth);
		this.y = this.location.getAbsoluteY(absHeight);
		this.width = this.dimension.getAbsoluteX(absWidth);
		this.height = this.dimension.getAbsoluteY(absHeight);
		this.onResize(absWidth, absHeight);
	}

	/**
	 * resizes the new image to the relative layout, replaces the image with the
	 * newImage and udpates the framework
	 * 
	 * @param newImage
	 *            the new image
	 * @param absWidth
	 *            absolute width of the framework-frame
	 * @param absHeight
	 *            absolute height of the framework-frame
	 * 
	 * @author Steffen Jacobs
	 */
	private void updateBufferedImage(Image newImage, int absWidth, int absHeight) {
		this.bufferedImage = newImage;
		if (this.isVisible())
			parent.repaintSpecificArea(this.getHitbox());
	}

	/**
	 * resizes the new image to the relative layout, replaces the image with the
	 * newImage and udpates the framework
	 * 
	 * @param newImage
	 *            the new image
	 * 
	 * @author Steffen Jacobs
	 */
	public void updatedBufferedImage(Image newImage) {
		updateBufferedImage(newImage, parent.getWidth(), parent.getHeight());
	}

	/**
	 * replaces the image which is actually drawn on the screen with the newImage and udpates the framework without
	 * recalculating the size
	 * 
	 * @author Steffen Jacobs
	 */
	public void setRenderedImage(Image newImage) {
		this.renderedImage = newImage;
	}

	/**
	 * sets the id
	 * 
	 * @author Steffen Jacobs
	 */
	public void setID(int _id) {
		this.id = _id;
	}

	public boolean equals(GameObject go) {
		return this.id == go.id;
	}

	/**
	 * ONLY USED BY FRAMEWORK - DO NOT CALL THIS METHOD
	 * 
	 * moves the object to the newLocation and redraws it on the framework
	 * 
	 * @author Steffen Jacobs
	 */
	public void moveTo(RelativeGeom2D newLocation) {
		this.location = newLocation;
		this.x = newLocation.getAbsoluteX(parent.getDisplayFrame().getWidth());
		this.y = newLocation.getAbsoluteY(parent.getDisplayFrame().getHeight());
	}

	/**
	 * a comparator for the GameObject which compares by the Layer
	 * 
	 * @author Steffen Jacobs
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
	 * @author Steffen Jacobs
	 */
	@Override
	public abstract GameObject clone();

	/**
	 * is called by the framework when the mouse enters the visual
	 * representation of the game object
	 * 
	 * @author Steffen Jacobs
	 */
	public abstract void onMouseEnter();

	/**
	 * is called by the framework when the mouse exits the visual representation
	 * of the game object
	 * 
	 * @author Steffen Jacobs
	 */
	public abstract void onMouseExit();

	/**
	 * is called by the framework when the visual representation of the game
	 * object is clicked
	 * 
	 * @author Steffen Jacobs
	 */
	public abstract void onMouseClick();

	/**
	 * is called by the framework when the visual representation of the game
	 * object is dragged
	 * 
	 * @author Steffen Jacobs
	 */
	public abstract void onMouseDrag();

	/**
	 * is called directly when the game window is resized
	 * 
	 * @author Steffen Jacobs
	 */
	public abstract void onResize(int absWidth, int absHeight);

}