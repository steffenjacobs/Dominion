package com.tpps.ui;

import java.awt.Container;
import java.awt.Toolkit;
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
