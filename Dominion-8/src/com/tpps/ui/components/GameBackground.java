package com.tpps.ui.components;

import java.awt.Image;

import java.awt.image.BufferedImage;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.gameplay.GameWindow;

/**
 * 
 * @author Nishit Agrawal - nagrawal
 *
 */

public class GameBackground extends GameObject {

	private static final long serialVersionUID = -8029224710447313551L;
	private int layer;

	/**
	 * 
	 * calling the GraphicFramework to draw the background. Mostly on the first
	 * layer
	 * 
	 * @param relativeLocX
	 * @param relativeLocY
	 * @param relativeWidth
	 * @param relativeHeight
	 * @param _layer
	 * @param sourceImage
	 * @param _parent
	 */

	public GameBackground(double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight,
			int _layer, Image sourceImage, GraphicFramework _parent) {
		super(relativeLocX, relativeLocY, relativeWidth, relativeHeight, _layer, sourceImage, _parent);
		this.layer = _layer;

	}

	@Override
	public GameObject clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onMouseEnter() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseExit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseClick() {
		GameWindow.getInstance().requestFocus();
	}

	@Override
	public void onMouseDrag() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * setting the updated image.
	 */
	public void onResize(int absWidth, int absHeight) {
		super.setRenderedImage(GraphicsUtil.resize((BufferedImage) super.getBufferedImage(),
				super.dimension.getAbsoluteX(absWidth), super.dimension.getAbsoluteY(absHeight)));
		// TODO Auto-generated method stub

	}
}
