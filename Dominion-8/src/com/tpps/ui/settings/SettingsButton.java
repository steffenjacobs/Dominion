package com.tpps.ui.settings;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.ImageLoader;
import com.tpps.technicalServices.util.MyAudioPlayer;

/**
 * this class represents the settings-button - Singleton
 * 
 * @author Steffen Jacobs
 */
public final class SettingsButton extends JPanel {
	private static final long serialVersionUID = -2786310942457889015L;

	private final BufferedImage normal, entered;
	private BufferedImage cache;

	private static SettingsButton INSTANCE;

	/** @return the only instance of the settings-button */
	static SettingsButton getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SettingsButton();
		}
		return INSTANCE;
	}

	/** initializes the settings-button */
	private SettingsButton() {
		normal = GraphicsUtil.resize(ImageLoader.getImage("resources/img/mainMenu/gear.png"),
				SettingsController.SETTINGS_BUTTON_SIZE.width, SettingsController.SETTINGS_BUTTON_SIZE.height);
		entered = GraphicsUtil.colorScale(Color.BLACK, normal, .5f);

		cache = normal;

		this.setOpaque(false);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MyAudioPlayer.doClick();
				SettingsController.showSettingsWindow(SettingsController.getCenter());
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				SettingsButton.this.cache = entered;
				SettingsButton.this.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				SettingsButton.this.cache = normal;
				SettingsButton.this.repaint();
			}
		});

		this.setBounds(new Rectangle(10, 10, SettingsController.SETTINGS_BUTTON_SIZE.width,
				SettingsController.SETTINGS_BUTTON_SIZE.height));
		this.setPreferredSize(SettingsController.SETTINGS_BUTTON_SIZE);
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(cache, 0, 0, null);
	}

}
