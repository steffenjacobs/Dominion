package com.tpps.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.components.GFButton;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = -5389003835573453281L;

	private static GameWindow instance;

	public static GameWindow getInstance() {
		return instance;
	}

	public static void main(String[] args) throws IOException {
		instance = new GameWindow();
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
		final int WIDTH = 1280, HEIGHT = 720;

		c = this.getContentPane();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(WIDTH, HEIGHT);
		this.setMinimumSize(new Dimension(1280, 720));
		this.setVisible(true);
		framework = new GraphicFramework(this);
		this.add(framework);

		BufferedImage im = ImageIO
				.read(getClass().getClassLoader().getResourceAsStream("resources/img/gameObjects/testButton.png"));
		im = GraphicsUtil.resize(im, (int) (im.getWidth() * .4), (int) (im.getHeight() * 0.8));

		framework.addComponent(new TestButton(.3, .3, .4, .4, WIDTH, HEIGHT, 6, im, framework, "first"));
		framework.addComponent(new TestButton(.2, .2, .4, .4, WIDTH, HEIGHT, 4, im, framework, "second"));
		GFButton gfb = new TestButton(.1, .1, .4, .4, WIDTH, HEIGHT, 5, im, framework, "third");

		framework.addComponent(gfb);

		new Thread(() -> {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			framework.removeComponent(gfb);
		}).start();

		this.revalidate();
		this.repaint();
	}

	private class TestButton extends GFButton {
		private static final long serialVersionUID = 1520424079770080041L;

		public TestButton(double relativeX, double relativeY, double relativeWidth, double relativeHeight, int absWidth,
				int absHeight, int _layer, Image sourceImage, GraphicFramework _parent, String caption) {
			super(relativeX, relativeY, relativeWidth, relativeHeight, absWidth, absHeight, _layer, sourceImage,
					_parent, caption);
		}

		@Override
		public GameObject clone() {
			return null;// return new TestButton(super.get);
		}

		@Override
		public void onMouseEnter() {
			System.out.println("entered " + this.toString());

		}

		@Override
		public void onMouseExit() {
			System.out.println("exited " + this.toString());

		}

		@Override
		public void onMouseClick() {
			System.out.println("clicked " + this.toString());

		}

		@Override
		public void onMouseDrag() {
			System.out.println("dragged " + this.toString());

		}

		@Override
		public String toString() {
			return "@" + System.identityHashCode(this) + " - " + super.getLocation() + " , " + super.getDimension()
					+ " , " + super.getLayer() + " , " + super.getImage() + " , " + super.getParent() + " , "
					+ super.getCaption();
		}

		@Override
		public void onResize(int absWidth, int absHeight) {
			super.onResize(absWidth, absHeight);
		}

	}

}
