package com.tpps.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

/**
 * an abstract class representing any button
 * 
 * @author sjacobs
 */
public abstract class GFButton extends GameObject {
	private static final long serialVersionUID = -5419554206946431577L;

	private String caption;

	/**
	 * clone-constructor
	 * 
	 * @author sjacobs
	 */
	private GFButton(double relativeX, double relativeY, double relativeWidth, double relativeHeight, int absWidth, int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, int _id,
			String caption) {
		super(relativeX, relativeY, relativeWidth, relativeHeight, absWidth, absHeight,  _layer, sourceImage, _parent, _id);
		this.caption = caption;
	}

	/**
	 * normal constructor
	 * 
	 * @author sjacobs
	 */
	public GFButton(double relativeX, double relativeY, double relativeWidth, double relativeHeight, int absWidth, int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, String caption) {
		super(relativeX, relativeY, relativeWidth, relativeHeight, absWidth, absHeight,  _layer, sourceImage, _parent);
		this.caption = caption;
		System.out.println("rendering text...");
		super.updateImage(GraphicsUtil.drawStringCentered(super.getImage(), this.caption,
				new Font("Blackadder ITC", Font.BOLD, 80), Color.BLACK), absWidth, absHeight);
	}

	/**
	 * @author sjacobs
	 * @return the object-caption
	 */
	public String getCaption() {
		return this.caption;
	}

	/**
	 * } is called to clone the entire object
	 * 
	 * @author sjacobs
	 */
	@Override
	public abstract GameObject clone();

	/**
	 * is called when the user enters the object with the mouse
	 * 
	 * @author sjacobs
	 */
	@Override
	public abstract void onMouseEnter();

	/**
	 * is called when the user exits the object with the mouse
	 * 
	 * @author sjacobs
	 */
	@Override
	public abstract void onMouseExit();

	/**
	 * is called when the user clicks on the object
	 * 
	 * @author sjacobs
	 */
	@Override
	public abstract void onMouseClick();

	/**
	 * is called when the users drags the mouse across the object
	 * 
	 * @author sjacobs
	 */
	@Override
	public abstract void onMouseDrag();

	/**
	 * @author sjacobs
	 * @return a readable representation of the object
	 */
	@Override
	public abstract String toString();
}
