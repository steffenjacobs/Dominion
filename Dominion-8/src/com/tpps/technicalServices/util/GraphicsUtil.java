package com.tpps.technicalServices.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public final class GraphicsUtil {
	public static Image setAlpha(Image img, float transparency) {		
		BufferedImage tmpImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) tmpImg.getGraphics();
		g2d.setComposite(AlphaComposite.SrcOver.derive(transparency));
		// set the transparency level in range 0.0f - 1.0f
		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
		// AlphaComposite ac = java.awt.AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency);
		g2d.drawImage(img, 0, 0, null);
		return tmpImg;
	}
}
