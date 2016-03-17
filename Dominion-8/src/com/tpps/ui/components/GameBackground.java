package com.tpps.ui.components;

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

public class GameBackground extends GameObject {

	private static final long serialVersionUID = -8029224710447313551L;
	
	public GameBackground(double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight, int _layer,
			Image sourceImage, GraphicFramework _parent){
		super(relativeLocX,  relativeLocY, relativeWidth, relativeHeight,  _layer,
				sourceImage, _parent);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseDrag() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResize(int absWidth, int absHeight) {
		super.setRenderedImage(GraphicsUtil.resize((BufferedImage) super.getBufferedImage(), absWidth, absHeight));
		// TODO Auto-generated method stub
		
	}
}
