package com.tpps.ui.lobbyscreen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.util.ImageLoader;
import com.tpps.technicalServices.util.MyAudioPlayer;

/**
 * This class represents the button which is used to go back to the main menu
 * 
 * @author jhuhn
 */
public class BackButton extends JButton implements ActionListener {

	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private boolean isLobby;

	/**
	 * initializes the object
	 * 
	 * @author jhuhn
	 */
	public BackButton() {
		this.image = ImageLoader.getImage("resources/img/lobbyScreen/Back.png");
		this.setOpaque(false);
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
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
	 * This method overrides the getPrefferedSize method to resize the JButton
	 * to the size of the used image
	 * 
	 * @author jhuhn
	 */
	public Dimension getPreferredSize() {
		return new Dimension(image.getWidth(), image.getHeight());
	}

	@Override
	/**
	 * implements the logic to go back to the main menu
	 * 
	 * @author jhuhn
	 */
	public void actionPerformed(ActionEvent arg0) {
		MyAudioPlayer.doClick();
		if (isLobby) {
			DominionController.getInstance().abortSearching();
			DominionController.getInstance().joinMainMenu();
			DominionController.getInstance().clearAllPlayersFromGUI();
			DominionController.getInstance().setLobbyID(null);
		} else {
			DominionController.getInstance().joinMainMenu();
		}
	}

	/**
	 * @author jhuhn
	 * @param isLobby
	 *            boolean representation if the instance is used in the lobby or
	 *            the communityscreen
	 */
	public void setLobby(boolean isLobby) {
		this.isLobby = isLobby;
	}
}
