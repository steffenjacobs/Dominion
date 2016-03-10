package com.tpps.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.IOException;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.Loader;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

/**
 * an abstract class representing any button
 * 
 * @author Steffen Jacobs
 */
public abstract class GFButton extends GameObject {
	private static final long serialVersionUID = -5419554206946431577L;

	private String caption;

	private Font customFont;

	/**
	 * clone-constructor
	 * 
	 * @author Steffen Jacobs
	 */
	protected GFButton(double relativeX, double relativeY, double relativeWidth, double relativeHeight, int absWidth,
			int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, int _id, String caption) {
		super(relativeX, relativeY, relativeWidth, relativeHeight, absWidth, absHeight, _layer, sourceImage, _parent,
				_id);
		this.caption = caption;
		this.onResize(absWidth, absHeight);
	}

	/**
	 * normal constructor
	 * 
	 * @author Steffen Jacobs
	 */
	public GFButton(double relativeX, double relativeY, double relativeWidth, double relativeHeight, int absWidth,
			int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, String caption) {
		super(relativeX, relativeY, relativeWidth, relativeHeight, absWidth, absHeight, _layer, sourceImage, _parent);
		this.caption = caption;
		this.onResize(absWidth, absHeight);
	}
	
	/**
	 * different constructor
	 * 
	 * @param relativeX
	 * @param relativeY
	 * @param relativeWidth
	 * @param relativeHeight
	 * @param _layer
	 * @param sourceImage
	 * @param _parent
	 * @param caption
	 * 
	 * @ Nishit Agrawal
	 */

	public GFButton(double relativeX, double relativeY, double relativeWidth, double relativeHeight, int _layer,
			Image sourceImage, GraphicFramework _parent, String caption) {
		super(relativeX, relativeY, relativeWidth, relativeHeight, _layer, sourceImage, _parent);
		this.caption = caption;
	}

	@Override
	public void onResize(int absWidth, int absHeight) {
		try {
			customFont = Loader.importFont();
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (this.caption != null)
			super.forceSetImage(GraphicsUtil.drawStringCentered(super.getImage(), this.caption,
					customFont.deriveFont(Font.PLAIN, 22), Color.BLACK));
	}
	/**
	 * @author Steffen Jacobs
	 * @return the object-caption
	 */
	public String getCaption() {
		return this.caption;
	}

	/**
	 * } is called to clone the entire object
	 * 
	 * @author Steffen Jacobs
	 */
	@Override
	public abstract GameObject clone();

	/**
	 * is called when the user enters the object with the mouse
	 * 
	 * @author Steffen Jacobs
	 */
	@Override
	public abstract void onMouseEnter();

	/**
	 * is called when the user exits the object with the mouse
	 * 
	 * @author Steffen Jacobs
	 */
	@Override
	public abstract void onMouseExit();

	/**
	 * is called when the user clicks on the object
	 * 
	 * @author Steffen Jacobs
	 */
	@Override
	public abstract void onMouseClick();

	/**
	 * is called when the users drags the mouse across the object
	 * 
	 * @author Steffen Jacobs
	 */
	@Override
	public abstract void onMouseDrag();

	/**
	 * @author Steffen Jacobs
	 * @return a readable representation of the object
	 */
	@Override
	public abstract String toString();
}
