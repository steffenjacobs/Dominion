package com.tpps.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.util.ImageLoader;
import com.tpps.ui.settings.SettingsController;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private final Container c;
	// private MainMenuPanel panel;
	private JPanel panel;

	/**
	 * Contructor for the mainMenu
	 */
	public MainFrame() {
		this.setIconImage((ImageLoader.getImage("resources/img/loginScreen/Icon.png")));
		this.c = this.getContentPane();
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.changeFullscreenMode(SettingsController.isFullscreen());
		this.setMinimumSize(new Dimension(1280, 720));
		GameLog.log(MsgType.INIT, "MainFrame");
	}

	/**
	 * changes the full-screen-mode
	 * 
	 * @param fullscreen
	 *            whether the window should fill the entire screen
	 */
	public void changeFullscreenMode(boolean fullscreen) {
		if (fullscreen) {
			this.setUndecorated(true);
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			this.setSize(1280, 720);
		}
	}

	public void setPanel(JPanel panel) {
		if (this.panel != null) {
			c.remove(this.panel);
		}
		this.panel = panel;
		c.add(this.panel);
		this.revalidate();
		this.repaint();
	}

	public static void main(String[] args) {
		MainFrame menu = new MainFrame();
		menu.setVisible(true);
	}
}
