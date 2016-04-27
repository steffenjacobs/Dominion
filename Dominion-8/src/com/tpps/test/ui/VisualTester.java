package com.tpps.test.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.animations.FadeAnimation;
import com.tpps.ui.animations.MoveAnimation;
import com.tpps.ui.components.GFButton;

/**
 * Second-Playground for the UI-stuff
 * 
 * @author Steffen Jacobs
 *
 */
public class VisualTester extends JFrame {
	private static final long serialVersionUID = -5389003835573453281L;

	private static VisualTester instance;

	/**
	 * @return the instance
	 */
	public static VisualTester getInstance() {
		return instance;
	}

	/**
	 * updates the instance
	 * 
	 * @param gw
	 *            new instance
	 * @return the new instance
	 */
	public static VisualTester setInstance(VisualTester gw) {
		instance = gw;
		return gw;
	}

	/**
	 * main-entry-point
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		instance = new VisualTester();
	}

	private GraphicFramework framework;

	/**
	 * creates the GameWindow
	 * 
	 * @throws IOException
	 */
	public VisualTester() throws IOException {
		instance = this;
		// JFrame frame = new JFrame();
		// frame.setSize(100, 100);
		// GraphicFramework gf = new GraphicFramework(frame);
		// gf.setSize(100, 100);
		// gf.addComponent(new Card(null, null, "Lachs", 100,gf));
		// frame.add(gf);
		final int WIDTH = 1280, HEIGHT = 720;

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(WIDTH, HEIGHT);
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setVisible(true);
		framework = new GraphicFramework(this);
		this.setContentPane(framework);

		BufferedImage im = ImageIO
				.read(getClass().getClassLoader().getResourceAsStream("resources/img/gameObjects/testButton.png"));
		im = GraphicsUtil.resize(im, (int) (im.getWidth() * .4), (int) (im.getHeight() * 0.8));

		framework.addComponent(new TestButton(.3, .3, .4, .4, WIDTH, HEIGHT, 6, im, framework, "first"));
		framework.addComponent(new TestButton(.2, .2, .4, .4, WIDTH, HEIGHT, 4, im, framework, "second"));

		GFButton gfb = new TestButton(.1, .1, .4, .4, WIDTH, HEIGHT, 5, im, framework, "third");

		this.revalidate();
		framework.addComponent(gfb);
		Card.resetClassID();
		framework.addComponent(new Card(
				CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(CardAction.ADD_ACTION_TO_PLAYER),
						CollectionsUtil.linkedList("2")),
				CollectionsUtil.linkedList(CardType.ACTION), "Copper", 4, 0.8, 0.9, 0.9, 0.9, 1,
				ImageIO.read(ClassLoader.getSystemResource("resources/img/mainMenu/Dominion.jpg")), this.framework));

		// new Thread(() -> {
		// try {
		// Thread.sleep(2000);
		// framework.moveObject(gfb, new RelativeGeom2D(.4, .4));
		// Thread.sleep(2000);
		// gfb.updateImage(GraphicsUtil.setAlpha(gfb.getOriginalImage(), .5f));
		// Thread.sleep(5000);
		// framework.removeComponent(gfb);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }).start();

		this.revalidate();
		this.repaint();

		FadeAnimation anim = new FadeAnimation(gfb, 1000, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				System.out.println("finished fade!");
				return null;
			}
		}, 255, 0, false);
		MoveAnimation anim2 = new MoveAnimation(framework, gfb, 2000, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				System.out.println("finished move!");
				return null;
			}
		}, .7, .7);
		anim.play();
		anim2.play();
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
					+ " , " + super.getLayer() + " , " + super.getRenderdImage() + " , " + super.getFramework() + " , "
					+ super.getCaption();
		}

		@Override
		public void onResize(int absWidth, int absHeight) {
			super.onResize(absWidth, absHeight);
		}

	}

}
