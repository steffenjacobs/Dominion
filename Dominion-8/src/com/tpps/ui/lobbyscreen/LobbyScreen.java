package com.tpps.ui.lobbyscreen;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class LobbyScreen extends JFrame{

	private static final long serialVersionUID = 1L;
	private final Container c;
	private LobbyScreenPanel lobbyPanel;
	private int width;
	private int height;

	public LobbyScreen() {
		this.setVisible(true);
		this.c = this.getContentPane();
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setMinimumSize(new Dimension(1280, 720));
		this.setSize(width, height);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.lobbyPanel = new LobbyScreenPanel(this);
		this.c.add(lobbyPanel);
		this.revalidate();
		this.repaint();
	}
	
	public static void main(String[] args) {
		new LobbyScreen();
	}
}
