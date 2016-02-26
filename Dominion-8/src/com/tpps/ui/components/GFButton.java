package com.tpps.ui.components;

import java.awt.Image;

import com.tpps.technicalServices.util.GraphicsUtil;
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

		// super.updateImage(GraphicsUtil.setAlpha(super.getImage(), 0.6f));

	}

	@Override
	public void onMouseExit() {
		this.isOverButton = false;
		System.out.println("exit " + toString());
	}

	@Override
	public void onMouseClick() {
		System.out.println("Clicked @" + this.toString());
		//TODO: fix here
		new Thread(new Runnable() {
			float transparency = 1f;

			@Override
			public void run() {
				while (true) {
					// System.out.println("tick " + transparency);

					if (transparency <.6f){
						System.out.println("lu");
						return;
					}
					transparency *= .99f;
					updateImage(GraphicsUtil.setAlpha(getImage(), transparency));
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}).start();
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
