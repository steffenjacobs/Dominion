package com.tpps.ui.lobbyscreen;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.tpps.technicalServices.util.GraphicsUtil;

public class LobbyScreenPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private BufferedImage originalBackground, actualBackground;
	private LobbyScreen parent;
	
	public LobbyScreenPanel(LobbyScreen parent) {
		this.setLayout(new GridLayout(1,2,0,0));
		this.parent =  parent;
		this.loadBackgroundImage();
		this.addComponentListener(new MyComponentAdapter());
		this.add(new ChatPanel(this));
		this.add(new SearchPanel());
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
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(actualBackground, 0, 0, null);
	}
	
	private class MyComponentAdapter extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			super.componentResized(e);

			int width = LobbyScreenPanel.this.parent.getContentPane().getWidth();
			int height = LobbyScreenPanel.this.parent.getContentPane().getHeight();
			
			LobbyScreenPanel.this.actualBackground = GraphicsUtil.resize(LobbyScreenPanel.this.originalBackground, width , height);			
			LobbyScreenPanel.this.repaint();
		}
	}
	
	@Override
	public Container getParent() {
		return super.getParent();
	}
}
