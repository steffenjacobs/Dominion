package com.tpps.ui.settings;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.util.AutoCreatingProperties;
import com.tpps.technicalServices.util.MyAudioPlayer;
import com.tpps.ui.gameplay.GameWindow;

/**
 * This class is the central controller for all changeable game-settings
 * 
 * @author Steffen Jacobs
 */
public final class SettingsController {

	/**
	 * standard settings-window size
	 */
	public static final Dimension SETTINGS_WINDOW_SIZE = new Dimension(610, 300);

	/**
	 * standard settings-button size
	 */
	static final Dimension SETTINGS_BUTTON_SIZE = new Dimension(30, 30);

	/**
	 * full-screen-mode enabled
	 */
	static boolean fullScreen;

	static int volumeMenu, volumeIngame, volumeFX;
	
	private static final Dimension STANDARD_WINDOW_SIZE = new Dimension(1280, 720);
	private static final String SETTINGS_FILE = "settings.cfg";

	private static AutoCreatingProperties conf;

	/** @return the fully working settings-button */
	public static JPanel getSettingsButton() {
		return SettingsButton.getInstance();
	}

	/** never call this. SettingsController is a singleton! */
	private SettingsController() {
		throw new AssertionError();
	}

	/** @return if full-screen-mode */
	public static boolean isFullscreen() {
		return fullScreen;
	}

	/**
	 * sets the menu-volume to the new volume
	 * 
	 * @param newVolume
	 *            the new volume
	 */
	public static void changeVolumeMenu(int newVolume) {
		volumeMenu = newVolume;
		MyAudioPlayer.setMainMenuVolume(newVolume);
		conf.setProperty("VOLUME_MAIN", "" + newVolume);
	}

	/**
	 * sets the game-volume to the new volume
	 * 
	 * @param newVolume
	 *            the new volume
	 */
	public static void changeVolumeIngame(int newVolume) {
		volumeIngame = newVolume;
		MyAudioPlayer.setGameVolume(newVolume);
		conf.setProperty("VOLUME_GAME", "" + newVolume);
	}

	/**
	 * sets the effects-volume to the new volume
	 * 
	 * @param newVolume
	 *            the new volume
	 */
	public static void changeVolumeFX(int newVolume) {
		volumeFX = newVolume;
		MyAudioPlayer.setEffectsVolume(newVolume);
		conf.setProperty("VOLUME_FX", "" + newVolume);
	}	

	/**
	 * enables or disables fullscreen-mode or game-window
	 * 
	 * @param state
	 *            fullscreen-mode or not
	 */
	private static void changeFullScreenGameWindow(boolean state) {
		if (GameWindow.getInstance() != null) {
			Container windowPane = GameWindow.getInstance().getContentPane();
			GameWindow.getInstance().dispose();
			if (state) {
				GameWindow.getInstance().setExtendedState(JFrame.MAXIMIZED_BOTH);
				GameWindow.getInstance().setUndecorated(true);
			} else {
				GameWindow.getInstance().setSize(STANDARD_WINDOW_SIZE);
				GameWindow.getInstance().setUndecorated(false);
			}
			GameWindow.getInstance().setVisible(true);
			GameWindow.getInstance().setContentPane(windowPane);
		}
		conf.setProperty("FULLSCREEN", "" + state);
	}

	/**
	 * enables or disables fullscreen-mode or main-menu
	 * 
	 * @param state
	 *            fullscreen-mode or not
	 */
	private static void changeFullScreenMenu(boolean state) {
		boolean visible = DominionController.getInstance().getMainFrame().isVisible();
		Container con = DominionController.getInstance().getMainFrame().getContentPane();

		DominionController.getInstance().getMainFrame().dispose();
		if (state) {
			DominionController.getInstance().getMainFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
			DominionController.getInstance().getMainFrame().setUndecorated(true);
		} else {
			Point oldCenter = getCenter();

			DominionController.getInstance().getMainFrame().setSize(STANDARD_WINDOW_SIZE);

			DominionController.getInstance().getMainFrame().setLocation(
					(int) (oldCenter.getX() - STANDARD_WINDOW_SIZE.width / 2),
					(int) (oldCenter.getY() - STANDARD_WINDOW_SIZE.height / 2));

			DominionController.getInstance().getMainFrame().setUndecorated(false);

		}
		DominionController.getInstance().getMainFrame().setVisible(visible);
		DominionController.getInstance().getMainFrame().setContentPane(con);

	}

	/** @return the center */
	static Point getCenter() {
		return new Point(
				(int) (DominionController.getInstance().getMainFrame().getLocation().getX()
						+ DominionController.getInstance().getMainFrame().getSize().getWidth() / 2),
				(int) (DominionController.getInstance().getMainFrame().getLocation().getY()
						+ DominionController.getInstance().getMainFrame().getSize().getHeight() / 2));
	}

	/**
	 * changes the applications fullscreen-mode
	 * 
	 * @param state
	 *            true: enable fullscreen-mode, false: go to window-mode
	 */
	public static void changeFullscreen(boolean state) {
		fullScreen = state;
		changeFullScreenMenu(state);
		changeFullScreenGameWindow(state);
	}

	/**
	 * opens the settings panel
	 * 
	 * @param location
	 *            the location to spawn the settings-panel
	 */
	public static void showSettingsWindow(Point location) {
		SwingUtilities.invokeLater(() -> {
			SettingsWindow.getInstance().show(location);
		});
	}

	/** loads everything from file */
	public static void load() {
		conf = new AutoCreatingProperties();
		conf.load(new File(SETTINGS_FILE));

		// set fullscreen-mode
		try {
			boolean fullscreen = Boolean.parseBoolean(conf.getProperty("FULLSCREEN", "0"));
			SettingsController.changeFullscreen(fullscreen);
		} catch (NumberFormatException nfe) {
			SettingsController.changeFullscreen(false);
		}

		// set menu-audio-volume
		try {
			int volumeMain = Integer.parseInt(conf.getProperty("VOLUME_MAIN", "20"));
			SettingsController.changeVolumeMenu(volumeMain);
		} catch (NumberFormatException nfe) {
			SettingsController.changeVolumeMenu(20);
		}

		// set game-audio-volume
		try {
			int volumeGame = Integer.parseInt(conf.getProperty("VOLUME_GAME", "20"));
			SettingsController.changeVolumeIngame(volumeGame);
		} catch (NumberFormatException nfe) {
			SettingsController.changeVolumeIngame(20);
		}

		// set FX-audio-volume
		try {
			int volumeFX = Integer.parseInt(conf.getProperty("VOLUME_FX", "20"));
			SettingsController.changeVolumeFX(volumeFX);
		} catch (NumberFormatException nfe) {
			SettingsController.changeVolumeFX(20);
		}
	}

}
