package com.tpps.technicalServices.util;

import java.awt.Rectangle;

/**
 * provides some useful physic-calculations
 * 
 * @author Steffen Jacobs
 */
public final class PhysicsUtil {

	/**
	 * checks wheter two 2D rectangular areas overlap
	 * 
	 * @author Steffen Jacobs
	 */
	public static boolean collides(Rectangle area1, Rectangle area2) {
		return area1.x < area2.x + area2.width && area1.x + area1.width > area2.x && area1.y < area2.y + area2.height
				&& area1.height + area1.y > area2.y;
	}

	public static Rectangle getBigBox(Rectangle... areas) {
		double smallestX = Integer.MAX_VALUE;
		double biggestX = -1;
		double smallestY = Integer.MAX_VALUE;
		double biggestY = -1;
		for (Rectangle rect : areas) {
			smallestX = rect.getX() < smallestX ? rect.getX() : smallestX;
			biggestX = rect.getX() + rect.getWidth() > biggestX ? rect.getX() + rect.getWidth() : biggestX;

			smallestY = rect.getY() < smallestY ? rect.getY() : smallestY;
			biggestY = rect.getY() + rect.getWidth() > biggestY ? rect.getY() + rect.getWidth() : biggestY;
		}

		return new Rectangle((int) smallestX, (int) smallestY, (int) (biggestX - smallestX),
				(int) (biggestY - smallestY));
	}
}