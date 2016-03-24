package com.tpps.ui.statisticsscreen;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.tpps.ui.lobbyscreen.GlobalChatPanel;

public class StatisticsScreen extends JFrame{

	private static final long serialVersionUID = 1L;
	private int width = 1280;
	private int height = 720;
	private Container c;
	private BufferedImage originalBackground;
	
	private StatisticsBoard statBoard;
	
	public StatisticsScreen() {
		try {
			this.originalBackground = ImageIO.read(ClassLoader.getSystemResource("resources/img/loginScreen/LoginBackground.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setVisible(true);
		this.c = new JLabel(new ImageIcon(originalBackground));
		this.setContentPane(c);
		c.setLayout(new GridLayout(1,2));
		
		
		this.setMinimumSize(new Dimension(1280, 720));
		this.setSize(width, height);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	
		statBoard = new StatisticsBoard();
		c.add(new GlobalChatPanel(this));
		c.add(statBoard);
			
		
		this.revalidate();
		this.repaint();
	}
	
	public static void main(String[] args) {
		new StatisticsScreen();
	}

}
