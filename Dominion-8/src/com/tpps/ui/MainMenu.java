package com.tpps.ui;

import java.awt.AlphaComposite;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import com.tpps.ui.components.MainMenuButton;

import java.awt.Image;

public class MainMenu extends JFrame{

	private static final long serialVersionUID = 1L;
	private final Container c;
	private Image background;
	private MainMenuButton singlePlayer, multiPlayer, settings, community;
	private int width, height;

	public MainMenu(){
		c = this.getContentPane();
		this.width = Toolkit.getDefaultToolkit().getScreenSize().width;
		this.height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		loadImage();
		registrateMouseListener();
		
		try {			
			Image singlePlayerImg = ImageIO.read(ClassLoader.getSystemResource("resources/img/gameObjects/testButton.png"));
			Image multiPlayerImg = ImageIO.read(ClassLoader.getSystemResource("resources/img/gameObjects/testButton.png"));
			
			singlePlayer = new MainMenuButton((width/2) - (singlePlayerImg.getWidth(null) / 2), 0 + height/5, singlePlayerImg);
			multiPlayer = new MainMenuButton((width/2) - (singlePlayerImg.getWidth(null) / 2), 0 + (height/5)*2, multiPlayerImg);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		c.repaint();
	}

	private void loadImage() {
		try {			
			this.background = ImageIO.read(ClassLoader.getSystemResource("resources/img/mainMenu/Dominion.jpg"));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	private void registrateMouseListener() {
		Mouse m = new Mouse();		
		c.addMouseListener(m);
		c.addMouseMotionListener(m);
	}	
	
	public void paint(Graphics g) {		
		g.drawImage(background, 0, 0, null);
		
		AlphaComposite ac = java.awt.AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6F);
		Graphics2D g2 = (Graphics2D)g;
		g2.setComposite(ac);
	    
		
		g2.drawImage(singlePlayer.getSourceImage(), singlePlayer.getX(), singlePlayer.getY(), null);
		g2.drawImage(multiPlayer.getSourceImage(), multiPlayer.getX(), multiPlayer.getY(), null);
		
	}
	
	
	private class Mouse extends MouseAdapter{

		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		int counter = 0;
		@Override
		public void mouseMoved(MouseEvent e) {			
			System.out.println(counter++);			
		}
		
	}

	
	
	public static void main(String[] args) {
		MainMenu menu = new MainMenu();
		menu.setVisible(true);
	}
	
	
}
