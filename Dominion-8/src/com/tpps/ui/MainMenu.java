package com.tpps.ui;

import java.awt.Container;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class MainMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private final Container c;
//	private int width, height;
	private MainMenuPanel panel;

	public MainMenu() {
		this.c = this.getContentPane();
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.panel = new MainMenuPanel(this);
		c.add(panel);
//		this.panel.repaint();
//		this.addComponentListener(new MyComponentAdapter());
	}





	public static void main(String[] args) {
		MainMenu menu = new MainMenu();
		menu.setVisible(true);
	}
}
