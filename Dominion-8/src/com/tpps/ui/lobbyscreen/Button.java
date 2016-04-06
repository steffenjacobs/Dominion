package com.tpps.ui.lobbyscreen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import com.tpps.application.game.DominionController;

public class Button extends JButton implements MouseListener{
	
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	
	public Button() {
		try {
			this.image = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/arrow_left.png"));
		} catch (IOException e) {		
			e.printStackTrace();
		}		
		this.addMouseListener(this);
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
	public void mouseClicked(MouseEvent e) {
		DominionController.getInstance().joinMainMenu();
	}

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@Override
	public void mousePressed(MouseEvent e) { }

	@Override
	public void mouseReleased(MouseEvent e) { }
}
