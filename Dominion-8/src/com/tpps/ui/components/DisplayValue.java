package com.tpps.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.FontLoader;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

/**
 * 
 * @author Nishit Agrawal - nagrawal
 *
 */

public final class DisplayValue extends GameObject {

	private static final long serialVersionUID = 4684563120751528743L;
	private String caption;
	private Font customFont;

	/**
	 * 
	 * calling the GraphicFramework and drawing the display value panel.
	 * 
	 * @param relativeLocX
	 * @param relativeLocY
	 * @param relativeWidth
	 * @param relativeHeight
	 * @param absWidth
	 * @param absHeight
	 * @param _layer
	 * @param sourceImage
	 * @param _parent
	 * @param caption
	 */

	public DisplayValue(double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight,
			int absWidth, int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, String caption) {
		super(relativeLocX, relativeLocY, relativeWidth, relativeHeight, absWidth, absHeight, _layer, sourceImage,
				_parent);
		this.onResize(absWidth, absHeight);
		this.caption = caption;
	}

	@Override
	public GameObject clone() {
		return null;
	}

	@Override
	public void onMouseEnter() {
	}

	@Override
	public void onMouseExit() {

	}

	@Override
	public void onMouseClick() {
	}

	@Override
	public void onMouseDrag() {
	}

	/**
	 * 
	 * setting a special font for the buttons and the size.
	 */
	public void onResize(int absWidth, int absHeight) {
		try {
			if (customFont == null) {
				customFont = FontLoader.getInstance().getXenipa();
				if (customFont == null) {
					customFont = new FontLoader().importFont();
				}
			}

		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (this.getCaption() != null)
			super.setRenderedImage(GraphicsUtil.drawStringCentered(
					GraphicsUtil.resize((BufferedImage) super.getBufferedImage(),
							super.dimension.getAbsoluteX(absWidth), super.dimension.getAbsoluteY(absHeight)),
					this.getCaption(), customFont.deriveFont(Font.PLAIN, 22), Color.WHITE));
	}

	public String getCaption() {
		return caption;
	}

	/**
	 * a method to renew the caption on the display value panel
	 * 
	 * @param caption
	 */

	public void renewCaption(String caption) {
		this.caption = caption;
		try {
			if (customFont == null) {
				customFont = FontLoader.getInstance().getXenipa();
				if (customFont == null) {
					customFont = new FontLoader().importFont();
				}
			}
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (this.getCaption() != null) {
			super.setRenderedImage(GraphicsUtil.drawStringCentered(
					GraphicsUtil.resize((BufferedImage) super.getBufferedImage(), 100, 100), caption,
					customFont.deriveFont(Font.PLAIN, 22), Color.WHITE));
		}
	}

}
