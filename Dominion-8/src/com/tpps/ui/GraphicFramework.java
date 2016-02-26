package com.tpps.ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.tpps.technicalServices.util.PhysicsUtil;
import com.tpps.ui.GameObject.CompareByLayer;

/**
 * general framework handling all the graphic objects@author sjacobs - Steffen
 * Jacobs
 */
public class GraphicFramework extends JPanel {
	private static final long serialVersionUID = 5135999956197786309L;

	private Mouse mouseListener;
	// Integer represents ID
	private ConcurrentHashMap<Integer, GameObject> gameObjects = new ConcurrentHashMap<>();

	/**
	 * @return the top object (sorted by layers)
	 * @author sjacobs - Steffen Jacobs
	 */
	public GameObject getTopObject(int x, int y) {
		GameObject highest = null;
		for (GameObject obj : gameObjects.values()) {
			if (!obj.isVisible() || !obj.isInside(x, y))
				continue;
			if (highest == null)
				highest = obj;
			else {
				highest = highest.getLayer() > obj.getLayer() ? highest : obj;
			}
		}
		return highest;
	}

	/**
	 * @return all game-objects which collide with the given area
	 * @author sjacobs - Steffen Jacobs
	 */
	public ArrayList<GameObject> getAllCollisions(Rectangle area) {
		ArrayList<GameObject> objects = new ArrayList<>();
		for (GameObject go : gameObjects.values()) {
			if (go.overlap(area)) {
				objects.add(go);
			}
		}
		return objects;
	}

	/**
	 * repaints the specific area and finds out which game-objects need to be
	 * redrawed
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void repaintSpecificArea(Rectangle area) {
		for (GameObject go : getAllCollisions(area)) {
			this.redrawWithoutRaytrace(go);
		}
		this.repaint(area);
	}

	/**
	 * moves the game-object to the desired location and updates the area
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void moveObject(GameObject obj, Location2D location) {
		Rectangle old = (Rectangle) obj.getHitbox().clone();
		obj.setVisable(false);
		obj.moveTo(location);
		obj.setVisable(true);
		Rectangle area = PhysicsUtil.getBigBox(new Rectangle[] { old, obj.getHitbox() });
		this.repaint(area);
	}

	/**
	 * @return all GameObject which the location is on top of
	 * @author sjacobs - Steffen Jacobs
	 */
	protected ArrayList<GameObject> raytrace(Location2D location) {
		GameObject[] objects = gameObjects.values().toArray(new GameObject[] {});

		ArrayList<GameObject> hits = new ArrayList<>();
		for (GameObject obj : objects) {
			hits.add(obj);
		}
		return hits;
	}

	/**
	 * paints the component and layers the GameObjects
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		GameObject[] objects = gameObjects.values().toArray(new GameObject[] {});
		Arrays.sort(objects, new CompareByLayer());

		for (GameObject obj : objects) {
			g.drawImage(obj.getImage(), obj.getLocation().getX(), obj.getLocation().getY(), null);
		}
	}

	/**
	 * force-redraw the GameObject without raytrace
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	private void redrawWithoutRaytrace(GameObject obj) {
		this.repaint(obj.getLocation().getX(), obj.getLocation().getY(), obj.getWidth(), obj.getHeight());
	}

	/**
	 * creates a new instance of the GraphicFramework-JPanel, adds
	 * mouse-listeners
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public GraphicFramework(JFrame parent) {
		this.mouseListener = new Mouse(this);
		parent.addMouseListener(mouseListener);
	}

	/**
	 * adds the game object to the framework and updates the visuals
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void addComponent(GameObject obj) {
		gameObjects.put(obj.getID(), obj);
		this.redrawWithoutRaytrace(obj);
		this.repaint(obj.getHitbox());
	}

	/**
	 * removes the game object from the framework and updates the visuals
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void removeComponent(GameObject obj) {
		gameObjects.remove(obj.getID());
		this.repaint(obj.getHitbox());
	}

	/**
	 * redraws all given GameObjects
	 * 
	 * @author sjacobs - Steffen Jacobs
	 */
	public void redrawObjectsWithoutRaytrace(GameObject... gameObjects) {
		Arrays.sort(gameObjects, new GameObject.CompareByLayer());
		for (GameObject go : gameObjects) {
			this.redrawWithoutRaytrace(go);
		}
	}

	/**
	 * listenes to the mouse-input and tunnels it to the underlying game-objects
	 */
	private class Mouse extends MouseAdapter {
		private GraphicFramework framework;
		private GameObject underCursor, tmpCursor = null;

		public Mouse(GraphicFramework fw) {
			framework = fw;
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			GameObject obj = framework.getTopObject(arg0.getX(), arg0.getY());
			if (obj != null)
				obj.onMouseClick();
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			System.out.println("moved!");
			tmpCursor = framework.getTopObject(arg0.getX(), arg0.getY());
			if (tmpCursor != underCursor) {
				tmpCursor.onMouseExit();
				underCursor.onMouseEnter();
				tmpCursor = underCursor;
			}

		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			GameObject obj = framework.getTopObject(arg0.getX(), arg0.getY());
			if (obj != null)
				System.out.println(arg0.getPoint() + " " + arg0.getX() + "/" + arg0.getY());
			obj.onMouseDrag();
		}
	}
}
