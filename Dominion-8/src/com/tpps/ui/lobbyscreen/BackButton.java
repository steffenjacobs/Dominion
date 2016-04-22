package com.tpps.ui.lobbyscreen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import com.tpps.application.game.DominionController;

/**
 * This class represents the button which is used to go back to the main menu
 * @author jhuhn
 */
public class BackButton extends JButton implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	
	/**
	 * initializes the object
	 * @author jhuhn
	 */
	public BackButton() {
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

	@Override
	/**
	 * This method overrides the paintComponent method to paint a nice picture
	 * instead of the ugly default picture
	 * 
	 * @author jhuhn
	 */
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    g.drawImage(image, 0, 0, null);
	}

	@Override
	/**
	 * This method overrides the getPrefferedSize method to resize the JButton to the size of the used image
	 * @author jhuhn
	 */
	public Dimension getPreferredSize() {
	    return new Dimension(image.getWidth(), image.getHeight());
	}
	
	@Override
	/**
	 * implements the logic to go back to the main menu
	 * @author jhuhn
	 */
	public void actionPerformed(ActionEvent arg0) {
		DominionController.getInstance().abortSearching();
		DominionController.getInstance().joinMainMenu();
		DominionController.getInstance().clearAllPlayersFromGUI();
	}
}
