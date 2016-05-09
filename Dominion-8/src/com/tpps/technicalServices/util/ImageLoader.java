package com.tpps.technicalServices.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

/**
 * provides a simple interface to load images
 * 
 * @author Steffen Jacobs, Johannes Huhn
 */
public final class ImageLoader {

	private static final ConcurrentHashMap<String, BufferedImage> loadedImages;

	static {
		loadedImages = new ConcurrentHashMap<>();

		addImage("black_0.6", GraphicsUtil.setAlpha(getImage("resources/img/lobbyScreen/blackbeauty.png"), 0.6f));
		addImage("black_0.4", GraphicsUtil.setAlpha(getImage("resources/img/lobbyScreen/blackbeauty.png"), 0.4f));
		addImage("white_0.4", GraphicsUtil.setAlpha(getImage("resources/img/lobbyScreen/walterWhite.jpg"), 0.4f));
		
		getImage("resources/img/loginScreen/LoginBackground.jpg");
	}

	/** never call this (Singleton) */
	private ImageLoader() {
		throw new AssertionError();
	}

	/**
	 * loads the requested image to cache and returns it
	 * 
	 * @param name
	 *            the name of the requested image
	 * @return the BufferedImage, loaded from cache or disk
	 */
	public static BufferedImage getImage(String name) {
		if (loadedImages.get(name) == null) {
			try {
				loadedImages.put(name, ImageIO.read(ClassLoader.getSystemResource(name)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return loadedImages.get(name);
	}

	/**
	 * adds a loaded Image to the cache
	 * 
	 * @param name
	 *            the name
	 * @param bim
	 *            the image to cache
	 */
	public static void addImage(String name, BufferedImage bim) {
		loadedImages.put(name, bim);
	}
}