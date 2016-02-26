package com.tpps.ui.animations;

import java.awt.image.BufferedImage;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;

public final class AnimationUtil {

	public static void fadeOut(GameObject go, boolean block) {
		if (block) {
		}
		Thread renderAnimation = new Thread(new Runnable() {
			float transparency = 1f;
			private BufferedImage bim;

			@Override
			public void run() {
				this.bim = new BufferedImage(go.getImage().getWidth(null), go.getImage().getHeight(null),
						BufferedImage.TYPE_INT_ARGB);
				bim.getGraphics().drawImage(go.getImage(), 0, 0, null);
				while (true) {

					if (transparency < .01f) {
						go.setVisible(false);
						System.out.println("lu");
						return;
					}
					transparency *= .95f;
					go.updateImage(GraphicsUtil.setAlpha(bim, transparency));
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		});
		renderAnimation.start();
		if (block) {
			try {
				renderAnimation.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
