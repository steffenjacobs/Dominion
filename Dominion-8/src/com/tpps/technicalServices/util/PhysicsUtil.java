package com.tpps.technicalServices.util;

import java.awt.Rectangle;

/**
 * provides some useful physic-calculations
 * 
 * @author sjacobs
 */
public final class PhysicsUtil {
	
	/**
	 * checks wheter two 2D rectangular areas overlap
	 * 
	 * @author sjacobs
	 */
	public static boolean collides(Rectangle area1, Rectangle area2) {
		return area1.x < area2.x + area2.width && area1.x + area1.width > area2.x && area1.y < area2.y + area2.height
				&& area1.height + area1.y > area2.y;
	}
}
