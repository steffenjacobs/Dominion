package com.tpps.ui.lobbyscreen;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.tpps.technicalServices.util.ImageLoader;

/**
 * This class is created mainly for gui testing purposes
 * 
 * @author jhuhn
 *
 */
public class LobbyScreen extends JFrame{

	private static final long serialVersionUID = 1L;
	private int width = 1920;
	private int height = 1080;
	private Container c;
	
	private PlayerSettingsPanel right;
	
	/**
	 * initializes this test class
	 * 
	 * @author jhuhn
	 */
	LobbyScreen(){
		
		this.setVisible(true);
		this.c = new JLabel(new ImageIcon(ImageLoader.getImage("resources/img/loginScreen/LoginBackground.jpg")));
		this.setContentPane(c);
		c.setLayout(new GridLayout(1,2));
		
		
		this.setMinimumSize(new Dimension(1280, 720));
		this.setSize(width, height);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	
		right = new PlayerSettingsPanel().updateCards();
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
