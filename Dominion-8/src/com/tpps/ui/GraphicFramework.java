package com.tpps.ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;

import com.tpps.ui.GameObject.CompareByLayer;

public class GraphicFramework extends JPanel {
	private static final long serialVersionUID = 5135999956197786309L;

	private JPanel canvas;
	private Mouse mouseListener;

	//Integer represents ID
	private ConcurrentHashMap<Integer, GameObject> gameObjects = new ConcurrentHashMap<>();

	public GameObject getTopObject(int x, int y) {
		LinkedList<GameObject> objects = new LinkedList<>();
		for (GameObject obj : gameObjects.values()) {
			objects.add(obj);
		}
		GameObject highest = null;
		for (GameObject obj : objects) {
			if (highest == null)
				highest = obj;
			else {
				highest = highest.getLayer() > obj.getLayer() ? highest : obj;
			}
		}
		return highest;
	}

	public void moveObject(GameObject obj, Location2D location) {
		Location2D old = obj.getLocation().clone();
		obj.setVisable(false);
		this.repaint();
		obj.moveTo(location);
		obj.setVisable(true);
		this.repaint();
	}

	protected ArrayList<GameObject> raytrace(Location2D location) {
		GameObject[] objects = (GameObject[]) gameObjects.values().toArray();

		ArrayList<GameObject> hits = new ArrayList<>();
		for (GameObject obj : objects) {
			hits.add(obj);
		}
		return hits;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		GameObject[] objects = (GameObject[]) gameObjects.values().toArray();
		Arrays.sort(objects, new CompareByLayer());

		for (GameObject obj : objects) {
			g.drawImage(obj.getImage(), obj.getLocation().getX(), obj.getLocation().getY(), null);
		}
	}

	public void redrawWithoutRaytrace(GameObject obj) {
		this.repaint(obj.getLocation().getX(), obj.getLocation().getY(), obj.getWidth(), obj.getHeight());
	}

	public GraphicFramework(JPanel _canvas) {
		this.canvas = _canvas;
		this.mouseListener = new Mouse(this);
		this.canvas.addMouseListener(mouseListener);
	}

	public void addComponent(GameObject obj) {
		gameObjects.put(obj.getID(), obj);
		this.repaint();
	}
	
	public void removeComponent(GameObject obj) {
		gameObjects.remove(obj.getID());
		this.repaint();
	}

	public void redrawObjectsWithoutRaytrace(GameObject[] gameObjects) {
		Arrays.sort(gameObjects, new GameObject.CompareByLayer());
		for (GameObject go : gameObjects) {
			this.redrawWithoutRaytrace(go);
		}
	}

	class Mouse implements MouseListener, MouseMotionListener {
		private GraphicFramework framework;

		public Mouse(GraphicFramework fw) {
			framework = fw;
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			GameObject obj = framework.getTopObject(arg0.getX(), arg0.getY());
			if (obj != null)
				obj.onMouseClick();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			GameObject obj = framework.getTopObject(arg0.getX(), arg0.getY());
			if (obj != null)
				obj.onMouseEnter();
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			GameObject obj = framework.getTopObject(arg0.getX(), arg0.getY());
			if (obj != null)
				obj.onMouseExit();
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			GameObject obj = framework.getTopObject(arg0.getX(), arg0.getY());
			if (obj != null)
				obj.onMouseDrag();
		}
	}
}
