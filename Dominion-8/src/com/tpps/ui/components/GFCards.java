package com.tpps.ui.components;

import java.awt.Image;

import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

public class GFCards extends GameObject {

	private static final long serialVersionUID = -1901356244675357118L;
	private String cardName;
	
	
	public GFCards(double relativeLocX, double relativeLocY, double relativeWidth, double relativeHeight, int _layer,
			Image sourceImage, GraphicFramework _parent){
		super(relativeLocX,relativeLocY,relativeWidth,relativeHeight,_layer,sourceImage,_parent);
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
		// TODO Auto-generated method stub

	}



	public String getCardName() {
		return cardName;
	}
	
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}


}
