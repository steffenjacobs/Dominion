package com.tpps.ui;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;

public class MainMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private final Container c;
	private int width, height;
	private MainMenuPanel panel;

	public MainMenu() {
		this.c = this.getContentPane();
		this.width = Toolkit.getDefaultToolkit().getScreenSize().width;
		this.height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.panel = new MainMenuPanel(this);
		c.add(panel);
		this.panel.repaint();
		this.addComponentListener(new MyComponentAdapter());
	}

	private class MyComponentAdapter extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			super.componentResized(e);

			MainMenu.this.width = MainMenu.this.panel.getWidth();
			MainMenu.this.height = MainMenu.this.panel.getHeight();

			int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
			int maxHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

			panel.onResize(width / Double.parseDouble(Integer.toString(maxWidth)),
					height / Double.parseDouble(Integer.toString(maxHeight)));
		}
	}

	/**
	 * @return the width of the JFrame
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height of the JFrame
	 */
	public int getHeight() {
		return height;
	}

	public static void main(String[] args) {
		MainMenu menu = new MainMenu();
		menu.setVisible(true);
	}
}
