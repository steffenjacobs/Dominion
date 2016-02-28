package com.tpps.technicalServices.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


/**
 * provides some useful methods for manipulating graphic objects
 * 
 * @author sjacobs - Steffen Jacobs
 */
public final class GraphicsUtil {

	/**
	 * @author sjacobs, jhuhn sets the alpha-value for an image synchronously
	 * @param transparency
	 *            new Alpha-Value for desired image
	 * @param img
	 *            image which will be used
	 * @return modified image
	 */
	public static Image setAlpha(Image img, float transparency) {
		BufferedImage tmpImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) tmpImg.getGraphics();
		g2d.setComposite(AlphaComposite.SrcOver.derive(transparency));
		g2d.drawImage(img, 0, 0, null);
		return tmpImg;
	}

	/**
	 * draws a String centered on top of an image
	 * 
	 * @param img:
	 *            Image to draw on top of
	 * @param caption:
	 *            text to draw
	 * @param font:
	 *            font to draw text with
	 * @param fontColor:
	 *            color of the font
	 * @param offsetX:
	 *            offset left and right
	 * @param offsetY:
	 *            offset up and down
	 * @return a new rendered image with the caption on top of the old image
	 * @author sjacobs - Steffen Jacobs
	 */
	public static Image drawStringCentered(Image img, String caption, Font font, Color fontColor, int offsetX,
			int offsetY) {
		BufferedImage rendered = new BufferedImage(img.getWidth(null), img.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) rendered.getGraphics();
		graphics.drawImage(img, 0, 0, null);
		graphics.setFont(font);
		graphics.setColor(fontColor);

		FontMetrics fm = graphics.getFontMetrics();
		Rectangle2D r = fm.getStringBounds(caption, graphics);
		int x = (img.getWidth(null) - (int) r.getWidth()) / 2 + offsetX;
		int y = (img.getHeight(null) - (int) r.getHeight()) / 2 + fm.getAscent() + offsetY;
		graphics.drawString(caption, x, y);
		return rendered;
	}

	/**
	 * draws a String centered on top of an image
	 * 
	 * @param img:
	 *            Image to draw on top of
	 * @param caption:
	 *            text to draw
	 * @param font:
	 *            font to draw text with
	 * @param fontColor:
	 *            color of the font
	 * @return a new rendered image with the caption on top of the old image
	 * @author sjacobs - Steffen Jacobs
	 */
	public static Image drawStringCentered(Image img, String caption, Font font, Color fontColor) {
		return drawStringCentered(img, caption, font, fontColor, 0, 0);
	}

	/**
	 * resizes the image
	 * 
	 * @param buffImg
	 *            image to resize
	 * @param newWidth
	 *            new width to resize to
	 * @param newHeight
	 *            new height to resize to
	 * @return the resized image
	 * @author sjacobs - Steffen Jacobs
	 */
	public static BufferedImage resize(BufferedImage buffImg, int newWidth, int newHeight) {
		BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		result.createGraphics();
		result.getGraphics().drawImage(buffImg, 0, 0, newWidth, newHeight, null);
		return result;
	}
}
