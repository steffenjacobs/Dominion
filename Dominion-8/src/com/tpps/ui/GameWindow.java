package com.tpps.ui;

import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.sun.javafx.iio.ImageStorage.ImageType;
import com.tpps.ui.components.GFButton;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = -5389003835573453281L;

	public static void main(String[] args) throws IOException {
		new GameWindow();
	}

	Container c;
	JButton button;
	private GraphicFramework framework;

	/**
	 * creates the GameWindow
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public GameWindow() throws IOException {
		c = this.getContentPane();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(500, 300);
		this.setVisible(true);
		framework = new GraphicFramework();
		this.add(framework);
		BufferedImage im = ImageIO
				.read(getClass().getClassLoader().getResourceAsStream("ressources/img/gameObjects/testButton.png"));
		im = resize(im, (int) (im.getWidth() * .4), (int) (im.getHeight() * 0.8));
		framework.addComponent(new GFButton(50, 50, 6, im, framework));
		framework.addComponent(new GFButton(80, 80, 4, im, framework));
		GFButton gfb = new GFButton(110, 110, 5, im, framework);

		framework.addComponent(gfb);

		new Thread(() -> {
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			framework.removeComponent(gfb);
		}).start();

		this.revalidate();
		this.repaint();
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
	public BufferedImage resize(BufferedImage buffImg, int newWidth, int newHeight) {
		BufferedImage result = new BufferedImage(newWidth, newHeight, ImageType.RGBA.ordinal());
		result.createGraphics();
		result.getGraphics().drawImage(buffImg, 0, 0, newWidth, newHeight, null);
		return result;
	}

}
