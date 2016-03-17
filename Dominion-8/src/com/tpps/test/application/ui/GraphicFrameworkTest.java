package com.tpps.test.application.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JFrame;

import org.junit.Test;

import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.RelativeGeom2D;
import com.tpps.ui.components.GFButton;

/**
 * class to test the graphic framework
 * 
 * tests whether the framework can be created
 * 
 * tests visability of game-objects
 * 
 * tests the raytracing
 * 
 * tests the hitboxes
 * 
 * moving gameObjects
 * 
 * tests the layers (click and see if the returned top-element is on top)
 * 
 * tests wheter the objects are repositioned correctly after resizing the window
 * 
 * tests hitbox & raytrace after resizing the window
 * 
 * tests check invisibility of gameObjects & redo layer-test
 * 
 * tests remove top element & redo layer-test
 * 
 * tests remove all components & redo layer-test
 * 
 * @author Steffen Jacobs
 */
public class GraphicFrameworkTest {
	private static final int F_WIDTH = 1280, F_HEIGHT = 720;

	@Test
	public void test() throws AWTException {

		// create a test-framework
		GraphicFramework framework = new GraphicFramework(new TestFrame());

		// check if framework was created without exceptions
		assertNotNull(framework);

		// add two overlapping buttons
		TestButton button_1 = new TestButton(0, 0, .5, .5, F_WIDTH, F_HEIGHT, 1, null, framework, "TEST");
		TestButton button_2 = new TestButton(0, 0, .25, .25, F_WIDTH, F_HEIGHT, 2, null, framework, "TEST2");
		framework.addComponent(button_1);
		framework.addComponent(button_2);

		// check visibility
		button_1.setVisible(true);
		assertTrue(button_1.isVisible());

		// check raytracing
		assertTrue(button_1.isInside(F_WIDTH / 2 - 1, F_HEIGHT / 2 - 1));

		// check hitbox
		Rectangle rect = new Rectangle(0, 0, F_WIDTH / 2, F_HEIGHT / 2);
		assertEquals(button_1.getHitbox(), rect);

		// check click
		GameObject top1 = framework.getTopObject(0, 0);
		assertSame(top1, button_2);

		// check resize
		final int newWidth = 1920, newHeight = 1080;
		button_1.resizeObject(newWidth, newHeight);
		rect = new Rectangle(0, 0, newWidth / 2, newHeight / 2);

		// hitbox after resize
		assertEquals(button_1.getHitbox(), rect);

		// raytrace after resize
		assertTrue(button_1.isInside(newWidth / 4 - 1, newHeight / 4 - 1));

		// remove top element at 0, 0
		GameObject toRemove = framework.getTopObject(0, 0);
		GameObject go = framework.removeComponent(toRemove);
		assertNotNull(go);

		// check if that was button_2
		assertSame(button_2, go);

		// check if button_1 is now top object
		GameObject top = framework.getTopObject(0, 0);
		assertSame(top, button_1);

		// check visibility again: invisible objects should not be clickable
		button_1.setVisible(false);
		top = framework.getTopObject(0, 0);
		assertNull(top);

		// check if elements are correctly moved
		button_1.setVisible(true);
		framework.moveObject(button_1, new RelativeGeom2D(.1, .1));
		assertNotSame(framework.getTopObject(0, 0), button_1);
		assertSame(framework.getTopObject((int) (.1 * F_WIDTH), (int) (.1 * F_HEIGHT)),button_1);

		// check if second object was removed successful, true
		go = framework.removeComponent(button_1);
		assertSame(go,  button_1);

		// check if framework is now empty
		assertNull(framework.getTopObject(0, 0));

	}

	private class TestFrame extends JFrame {
		private static final long serialVersionUID = 1L;

		public TestFrame() {
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.setSize(F_WIDTH, F_HEIGHT);
			this.setMinimumSize(new Dimension(1280, 720));
			this.setVisible(true);
			this.repaint();
			this.revalidate();
		}
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
			return null;
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
					+ " , " + super.getLayer() + " , " + super.getRenderdImage() + " , " + super.getParent() + " , "
					+ super.getCaption();
		}

		@Override
		public void onResize(int absWidth, int absHeight) {
			super.onResize(absWidth, absHeight);
		}

	}
}
