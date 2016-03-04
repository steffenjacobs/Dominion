package com.tpps.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;

import com.tpps.technicalServices.util.GraphicsUtil;
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
		onResize(absWidth, absHeight);
	}

	@Override
	public void onResize(int absWidth, int absHeight) {
		if (this.caption != null)
		super.forceSetImage(GraphicsUtil.drawStringCentered(super.getImage(), this.caption,
				new Font("Blackadder ITC", Font.BOLD, 80), Color.BLACK));
	}
	
	private void importFont() {

		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT,
					ClassLoader.getSystemResourceAsStream("resources/font/xenippa1.TTF"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
					ClassLoader.getSystemResourceAsStream("resources/font/xenippa1.TTF")));
		} catch (Exception e) {
			System.err.println(e);
		}
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
