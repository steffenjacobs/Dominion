package com.tpps.test.application.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JFrame;

import org.junit.Test;

import com.tpps.ui.GameObject;
import com.tpps.ui.GraphicFramework;
import com.tpps.ui.components.GFButton;

/**
 * class to test the graphic framework
 * 
 * @author sjacobs - Steffen Jacobs
 */
public class GraphicFrameworkTest {

	@Test
	public void test() throws AWTException {
		int width = 1280, height = 720;

		// create a test-framework
		GraphicFramework framework = new GraphicFramework(new JFrame());

		// check if framework was created without exceptions
		assertThat(framework, is(notNullValue()));

		// add two overlapping buttons
		TestButton button_1 = new TestButton(0, 0, .5, .5, width, height, 1, null, framework, "TEST");
		TestButton button_2 = new TestButton(0, 0, .25, .25, width, height, 2, null, framework, "TEST2");
		framework.addComponent(button_1);
		framework.addComponent(button_2);

		// check visibility
		button_1.setVisible(true);
		assertTrue(button_1.isVisible());

		// check raytracing
		assertTrue(button_1.isInside(width / 2 - 1, height / 2 - 1));

		// check hitbox
		Rectangle rect = new Rectangle(0, 0, width / 2, height / 2);
		assertTrue(button_1.getHitbox().equals(rect));

		// check click
		GameObject top1 = framework.getTopObject(0, 0);
		assertTrue(top1 == button_2);

		// check resize
		final int newWidth = 1920, newHeight = 1080;
		button_1.resizeObject(newWidth, newHeight);
		rect = new Rectangle(0, 0, newWidth / 2, newHeight / 2);

		// hitbox after resize
		assertTrue(button_1.getHitbox().equals(rect));

		// raytrace after resize
		assertTrue(button_1.isInside(newWidth / 4 - 1, newHeight / 4 - 1));

		// remove top element at 0, 0
		GameObject toRemove = framework.getTopObject(0, 0);
		GameObject go = framework.removeComponent(toRemove);
		assertThat(go, is(notNullValue()));

		// check if that was button_2
		assertTrue(button_2 == go);

		// check if button_1 is now top object
		GameObject top = framework.getTopObject(0, 0);
		assertTrue(top == button_1);

		// check visibility again: invisible objects should not be clickable
		button_1.setVisible(false);
		top = framework.getTopObject(0, 0);
		assertTrue(top == null);

		// check if second object was removed successful, true
		go = framework.removeComponent(button_1);
		assertTrue(go == button_1);

		// check if framework is now empty
		assertTrue(framework.getTopObject(0, 0) == null);
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
					+ " , " + super.getLayer() + " , " + super.getImage() + " , " + super.getParent() + " , "
					+ super.getCaption();
		}

		@Override
		public void onResize(int absWidth, int absHeight) {
			super.onResize(absWidth, absHeight);
		}

	}
}
