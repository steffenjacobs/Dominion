package com.tpps.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.concurrent.Callable;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.animations.FadeOutAnimation;

public class GFButton extends GameObject {
	private boolean isOverButton = false;
	private String caption;

	private GFButton(int locX, int locY, int _layer, Image sourceImage, GraphicFramework _parent, int _id,
			boolean overButtonFlag, String caption) {
		super(locX, locY, _layer, sourceImage, _parent, _id);
		this.isOverButton = overButtonFlag;
		this.caption = caption;
	}

	public GFButton(int locX, int locY, int _layer, Image sourceImage, GraphicFramework _parent, String caption) {
		super(locX, locY, _layer, sourceImage, _parent);
		this.caption = caption;
		System.out.println("rendering text...");        
		super.updateImage(GraphicsUtil.drawStringCentered(super.getImage(), this.caption, new Font("Blackadder ITC", Font.BOLD, 80), Color.BLACK));
	}

	private static final long serialVersionUID = -5419554206946431577L;

	@Override
	public GameObject clone() {
		return new GFButton(super.getLocation().getX(), super.getLocation().getY(), super.getLayer(), super.getImage(),
				super.getParent(), super.getID(), this.isOverButton, this.caption);
	}

	@Override
	public void onMouseEnter() {
		this.isOverButton = true;
		System.out.println("enter " + toString());

		// super.updateImage(GraphicsUtil.setAlpha(super.getImage(), 0.6f));

	}

	@Override
	public void onMouseExit() {
		this.isOverButton = false;
		System.out.println("exit " + toString());
	}

	@Override
	public void onMouseClick() {
		System.out.println("Clicked " + this.toString());
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
