package com.tpps.ui.lobbyscreen;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.tpps.technicalServices.util.GraphicsUtil;

public class LobbyScreenPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private BufferedImage originalBackground, actualBackground;
	private LobbyScreen parent;
	private JTextArea chatwindow;
	
	
	public LobbyScreenPanel(LobbyScreen parent) {
		this.parent =  parent;
		this.loadBackgroundImage();
	}
	
	private void loadBackgroundImage() {
		try {
			this.originalBackground = ImageIO.read(ClassLoader.getSystemResource("resources/img/mainMenu/Dominion.jpg"));			
			int newWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
			int newHeight = Toolkit.getDefaultToolkit().getScreenSize().height;			
			this.actualBackground = GraphicsUtil.resize(this.originalBackground, newWidth, newHeight);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createChatWindow(){
		this.chatwindow = new JTextArea();
	}
	
	public void paint(Graphics g) {
		g.drawImage(actualBackground, 0, 0, null);
	}
	
}
