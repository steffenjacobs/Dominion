package com.tpps.ui.lobbyscreen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import com.tpps.application.game.DominionController;

public class Button extends JButton implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	
	public Button() {
		try {
			this.image = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/Back.png"));
			this.setOpaque(false);
			this.setContentAreaFilled(false);
			this.setBorderPainted(false);
		} catch (IOException e) {		
			e.printStackTrace();
		}		
		this.addActionListener(this);
	}

	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    g.drawImage(image, 0, 0, null);
	}

	@Override
	public Dimension getPreferredSize() {
	    return new Dimension(image.getWidth(), image.getHeight());
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		DominionController.getInstance().abortSearching();
		DominionController.getInstance().joinMainMenu();
		DominionController.getInstance().deletePlayerFromGUI(DominionController.getInstance().getUsername());
	}
}
