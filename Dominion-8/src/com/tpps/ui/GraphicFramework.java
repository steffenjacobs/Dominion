package com.tpps.ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.util.PhysicsUtil;
import com.tpps.ui.GameObject.CompareByLayer;

/**
 * general framework handling all the graphic objects@author Steffen Jacobs
 */
public class GraphicFramework extends JPanel {
	private static final long serialVersionUID = 5135999956197786309L;
	private JFrame parent;

	private Mouse mouseListener;

	/**
	 * moves a game object to a new location
	 * 
	 * @param x
	 *            new x-location
	 * @param y
	 *            new y-location
	 * @param obj
	 *            the game-object to move
	 * @author Steffen Jacobs
	 */
	public void moveGameObjectTo(GameObject obj, double x, double y) {
		Rectangle area = obj.getHitbox();
		obj.moveTo(new RelativeGeom2D(x, y));
		this.repaintSpecificArea(area);
	}

	/**
	 * updates the relative-size of an gameobject
	 * 
	 * @param width
	 *            new width
	 * @param height
	 *            new height
	 * @param obj
	 *            the game-object to move
	 * @author Steffen Jacobs
	 */
	public void updateGameObjectSize(GameObject obj, double width, double height) {
		obj.updateRelativeSize(width, height);
	}

	// Integer represents ID
	private ConcurrentHashMap<Integer, GameObject> gameObjects = new ConcurrentHashMap<>();

	/**
	 * @return the top object (sorted by layers)
	 * @author Steffen Jacobs
	 * @param x the x-location
	 * @param y the y-location
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
	 * @author Steffen Jacobs
	 * @param area the area to check collision for
	 */
	private ArrayList<GameObject> getAllCollisions(Rectangle area) {
		ArrayList<GameObject> objects = new ArrayList<>();
		for (GameObject go : gameObjects.values()) {
			if (go.isVisible() && go.overlap(area)) {
				objects.add(go);
			}
		}
		return objects;
	}

	/**
	 * repaints the specific area and finds out which game-objects need to be
	 * redrawed
	 * 
	 * @author Steffen Jacobs
	 * @param area the area to repaint
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
	 * @author Steffen Jacobs
	 * @param obj the object to move
	 * @param location the location to move the object to
	 */
	public void moveObject(GameObject obj, RelativeGeom2D location) {
		Rectangle old = (Rectangle) obj.getHitbox().clone();
		boolean visible = obj.isVisible();
		obj.setVisible(!visible);
		obj.moveTo(location);
		obj.setVisible(visible);
		Rectangle area = PhysicsUtil.getBigBox(new Rectangle[] { old, obj.getHitbox() });
		this.repaint(area);
	}

	/**
	 * @return all GameObject which the location is on top of
	 * @author Steffen Jacobs
	 * @param location the location to raytrace
	 */
	protected ArrayList<GameObject> raytrace(RelativeGeom2D location) {
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
	 * @author Steffen Jacobs
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		GameObject[] objects = gameObjects.values().toArray(new GameObject[] {});
		Arrays.sort(objects, new CompareByLayer());

		for (GameObject obj : objects) {
			if (obj.isVisible()) {
				g.drawImage(obj.getRenderdImage(), (int) obj.getLocation().getX(), (int) obj.getLocation().getY(),
						null);
				
			}
		}
	}

	/**
	 * force-redraw the GameObject without raytrace
	 * 
	 * @author Steffen Jacobs
	 * @param obj the object to redraw
	 */
	private void redrawWithoutRaytrace(GameObject obj) {
		obj.resizeObject(parent.getWidth(), parent.getHeight());
		this.repaint(obj.getHitbox());

	}

	/**
	 * creates a new instance of the GraphicFramework-JPanel, adds
	 * mouse-listeners
	 * 
	 * @author Steffen Jacobs
	 * @param _parent the parent JFrame
	 */
	public GraphicFramework(JFrame _parent) {
		GameLog.log(MsgType.GUI ,"im framework");
		this.parent = _parent;
		this.mouseListener = new Mouse(this);
		_parent.getContentPane().addMouseListener(mouseListener);
		_parent.getContentPane().addMouseMotionListener(mouseListener);
		_parent.addComponentListener(new ComponentAdapter() {
			private ExecutorService threadPool = Executors.newCachedThreadPool();

			long last = 0;

			@Override
			public void componentResized(ComponentEvent e) {
				last = System.currentTimeMillis();

				threadPool.submit(() -> {
					try {
						final long l = last;
						Thread.sleep(300);

						if (l == last) {
							last = System.currentTimeMillis();
							onWindowResize();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				});
			}
		});
	}

	private void onWindowResize() {
		GameLog.log(MsgType.GUI ,"window resized to " + parent.getWidth() + "/" + parent.getHeight());
		for (GameObject go : gameObjects.values()) {
			go.resizeObject(parent.getWidth(), parent.getHeight());
		}
		repaint();
	}

	/**
	 * adds the game object to the framework and updates the visuals
	 * 
	 * @author Steffen Jacobs
	 * @param obj the object to add
	 */
	public void addComponent(GameObject obj) {
		gameObjects.put(obj.getID(), obj);
		this.redrawWithoutRaytrace(obj);
	}

	/**
	 * removes the game object from the framework and updates the visuals
	 * 
	 * @author Steffen Jacobs
	 * @param obj the object to remove
	 * @return the removed object
	 */
	public GameObject removeComponent(GameObject obj) {
		GameObject res = gameObjects.remove(obj.getID());
		this.repaint(obj.getHitbox());
		return res;
	}

	/**
	 * redraws all given GameObjects without checking for overlap
	 * 
	 * @author Steffen Jacobs
	 * @param gameObjects the objects to redraw
	 */
	public void redrawObjectsWithoutRaytrace(GameObject... gameObjects) {
		Arrays.sort(gameObjects, new GameObject.CompareByLayer());
		for (GameObject go : gameObjects) {
			this.redrawWithoutRaytrace(go);
		}
	}

	/**
	 * listens to the mouse-input and tunnels it to the underlying game-objects
	 * 
	 * @author Steffen Jacobs
	 */
	private class Mouse extends MouseAdapter {
		private GraphicFramework framework;
		private GameObject underCursor, bufferedCursor = null;

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
			underCursor = framework.getTopObject(arg0.getX(), arg0.getY());
			if (bufferedCursor == null && underCursor == null) {
				// outside
				return;
			}
			if (bufferedCursor == null && underCursor != null) {
				// enter
				underCursor.onMouseEnter();
				bufferedCursor = underCursor;
			} else if (underCursor == null && bufferedCursor != null) {
				// exit
				bufferedCursor.onMouseExit();
				bufferedCursor = underCursor;
			} else if (bufferedCursor == underCursor) {
				// inside
				return;
			} else {
				// changed
				bufferedCursor.onMouseExit();
				underCursor.onMouseEnter();
				bufferedCursor = underCursor;
			}

		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			GameObject obj = framework.getTopObject(arg0.getX(), arg0.getY());
			if (obj != null) {
				obj.onMouseDrag();
			}
		}
	}

	/**
	 * @author Steffen Jacobs @return the parent frame everything is drawn upon
	 * @return the parent JFrame
	 */
	public JFrame getDisplayFrame() {
		return this.parent;
	}
}
