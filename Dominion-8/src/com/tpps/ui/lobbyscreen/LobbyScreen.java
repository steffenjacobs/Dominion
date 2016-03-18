package com.tpps.ui.lobbyscreen;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LobbyScreen extends JFrame{

	private int width = 1280;
	private int height = 720;
	private Container c;
	private BufferedImage originalBackground;
	
	LobbyScreen(){
		try {
			this.originalBackground = ImageIO.read(ClassLoader.getSystemResource("resources/img/loginScreen/LoginBackground.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setVisible(true);
		this.c = new JLabel(new ImageIcon(originalBackground));
		this.setContentPane(c);
		c.setLayout(new GridLayout(1,2));
	//	width = Toolkit.getDefaultToolkit().getScreenSize().width;
	//	height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setMinimumSize(new Dimension(1280, 720));
		this.setSize(width, height);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	
		c.add(new GlobalChatPanel(this));
		c.add(new RightPanel(this));
		
		this.revalidate();
		this.repaint();
	}
	
//	@Override
//	public void paint(Graphics g) {
//		g.drawImage(originalBackground, 0, 0, null);
//		super.paint(g);		
//	}

	
	public static void main(String[] args) {
		new LobbyScreen();
	}

}
