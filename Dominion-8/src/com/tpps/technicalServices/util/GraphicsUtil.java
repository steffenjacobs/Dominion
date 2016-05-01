package com.tpps.technicalServices.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

/**
 * provides some useful methods for manipulating graphic objects
 * 
 * @author Steffen Jacobs
 */
public final class GraphicsUtil {

	/**
	 * sets the alpha-value for an image synchronously
	 * 
	 * @author Steffen Jacobs, Johannes Huhn
	 * 
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
	 * @param img
	 *            Image to draw on top of
	 * @param caption
	 *            text to draw
	 * @param font
	 *            font to draw text with
	 * @param fontColor
	 *            color of the font
	 * @param offsetX
	 *            offset left and right
	 * @param offsetY
	 *            offset up and down
	 * @return a new rendered image with the caption on top of the old image
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
	 * @param img
	 *            Image to draw on top of
	 * @param caption
	 *            text to draw
	 * @param font
	 *            font to draw text with
	 * @param fontColor
	 *            color of the font
	 * @return a new rendered image with the caption on top of the old image
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
	 */
	public static BufferedImage resize(BufferedImage buffImg, int newWidth, int newHeight) {
		if (newWidth <= 0)
			newWidth = 1;
		if (newHeight <= 0)
			newHeight = 1;
		BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		result.createGraphics();
		result.getGraphics().drawImage(buffImg, 0, 0, newWidth, newHeight, null);
		return result;
	}

	/**
	 * compares to image based on their pixel values (RGBA)
	 * 
	 * @param image1
	 *            first image to compare
	 * @param image2
	 *            second image to compare
	 * @param debug
	 *            whether to output debug-messages
	 * @return whether all RGBA-values for all pixel were identical in both
	 *         images
	 */
	public static boolean compareImages(BufferedImage image1, BufferedImage image2, boolean debug) {

		DataBuffer buff1 = image1.getAlphaRaster().getDataBuffer();
		DataBuffer buff2 = image2.getAlphaRaster().getDataBuffer();

		if (buff1.getSize() != buff2.getSize())
			return false;

		long now = System.currentTimeMillis();
		for (int cnt = 0; cnt < buff1.getSize(); cnt++) {
			if (buff1.getElem(cnt) != buff2.getElem(cnt)) {
				if (debug)
					System.err.println("Pixels do not match @ " + cnt);
				return false;
			}
		}
		if (debug)
			System.out.println(
					"time to compare " + buff1.getSize() + " values: " + (System.currentTimeMillis() - now) + "ms");

		return true;
	}

	/**
	 * rotates the image by an angle. Returns a copy of.
	 * 
	 * @param img
	 *            the image to rotate
	 * @param angle
	 *            angle of rotation (degree)
	 * @return a new image which is the old image rotated by the angle
	 * 
	 */
	public static BufferedImage rotate(BufferedImage img, double angle) {
		double sinAngle = Math.abs(Math.sin(Math.toRadians(angle))),
				cosAngle = Math.abs(Math.cos(Math.toRadians(angle)));

		int w = img.getWidth(null), h = img.getHeight(null);

		int newWidth = (int) Math.floor(w * cosAngle + h * sinAngle),
				newHeight = (int) Math.floor(h * cosAngle + w * sinAngle);

		BufferedImage bimg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bimg.createGraphics();

		g.translate((newWidth - w) / 2, (newHeight - h) / 2);
		g.rotate(Math.toRadians(angle), w / 2, h / 2);
		g.drawRenderedImage(img, null);
		g.dispose();

		return bimg;
	}

	/**
	 * compares to image based on their pixel values (RGBA)
	 * 
	 * @param image1
	 *            first image to compare
	 * @param image2
	 *            second image to compare
	 * @return wheter all RGBA-values for all pixel were identical in both
	 *         images
	 */
	public static boolean compareImages(BufferedImage image1, BufferedImage image2) {
		return compareImages(image1, image2, false);
	}

	/**
	 * colors the image in a specified color
	 * 
	 * @param color
	 *            the color to color the image to
	 * @param bim
	 *            the image to color
	 * @param scale
	 *            the scale of coloring
	 * @return the colored image
	 */
	public static BufferedImage colorScale(Color color, BufferedImage bim, float scale) {
		BufferedImage copy = new BufferedImage(bim.getWidth(), bim.getHeight(), bim.getType());
		Graphics2D g = copy.createGraphics();
		g.setPaint(color);
		g.fillRect(0, 0, bim.getWidth(), bim.getHeight());
		g.drawImage(setAlpha(bim, scale), 0, 0, null);
		return copy;
	}
}