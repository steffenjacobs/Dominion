package com.tpps.ui.lobbyscreen;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.tpps.ui.statisticsscreen.StatisticsBoard;

/**
 * This class is created mainly for gui testing purposes
 * 
 * @author jhuhn
 *
 */
public class LobbyScreen extends JFrame{

	private static final long serialVersionUID = 1L;
	private int width = 1280;
	private int height = 720;
	private Container c;
	private BufferedImage originalBackground;
	
	private PlayerSettingsPanel right;
	
	/**
	 * initializes this test class
	 * 
	 * @author jhuhn
	 */
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
		
		
		this.setMinimumSize(new Dimension(1280, 720));
		this.setSize(width, height);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	
		right = new PlayerSettingsPanel();
		c.add(new GlobalChatPanel());
		c.add(right);
		
		right.insertPlayer("kevinS");
		right.insertPlayer("Gamingfish");
		right.insertPlayer("nishit");		
		
		this.revalidate();
		this.repaint();
	}

	/**
	 * main method for testing
	 * 
	 * @author jhuhn
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		new LobbyScreen();
	}
}
