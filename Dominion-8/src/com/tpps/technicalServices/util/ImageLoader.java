package com.tpps.technicalServices.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

public class ImageLoader {	

	private static final ConcurrentHashMap<String, BufferedImage> loadedImages;

	static {
		loadedImages = new ConcurrentHashMap<>();
		
		addImage("black_0.6", GraphicsUtil.setAlpha(getImage("resources/img/lobbyScreen/blackbeauty.png"), 0.6f));
	}

	private ImageLoader() {
		throw new AssertionError();
	}

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
	
	public static void addImage(String name, BufferedImage bim){
		loadedImages.put(name, bim);
	}
}