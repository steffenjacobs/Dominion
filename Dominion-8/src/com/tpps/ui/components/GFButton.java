package com.tpps.ui.components;

import java.awt.Image;

import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;

public class GFButton extends GameObject {
	private boolean isOverButton = false;

	public GFButton(int locX, int locY, int _layer, Image sourceImage, GraphicFramework _parent, int _id,
			boolean overButtonFlag) {
		super(locX, locY, _layer, sourceImage, _parent, _id);
		this.isOverButton = overButtonFlag;
	}

	public GFButton(int locX, int locY, int _layer, Image sourceImage, GraphicFramework _parent) {
		super(locX, locY, _layer, sourceImage, _parent);
	}

	private static final long serialVersionUID = -5419554206946431577L;

	@Override
	public GameObject clone() {
		return new GFButton(super.getLocation().getX(), super.getLocation().getY(), super.getLayer(), super.getImage(),
				super.getParent(), super.getID(), this.isOverButton);
	}

	@Override
	public void onMouseEnter() {
		this.isOverButton = true;
		System.out.println("enter " + toString());
	}

	@Override
	public void onMouseExit() {
		this.isOverButton = false;
		System.out.println("exit " + toString());
	}

	@Override
	public void onMouseClick() {
		System.out.println("Clicked @" + this.toString());

	}

	@Override
	public void onMouseDrag() {
		System.out.println("drag");
		// do nothing
	}

	@Override
	public String toString() {
		return "GFButton: " + super.toString();
	}

}
