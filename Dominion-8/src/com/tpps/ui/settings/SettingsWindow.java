package com.tpps.ui.settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.ImageLoader;

/**
 * This class is a visual representation of the values from the
 * SettingsController - Singleton
 * 
 * @author Steffen Jacobs
 */
public final class SettingsWindow {

	private static SettingsWindow INSTANCE;

	private JFrame frame;
	private int locX, locY;

	/** @return the only instance of this window */
	public static SettingsWindow getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SettingsWindow();
		}
		return INSTANCE;
	}

	/** constructor for the settings-window */
	private SettingsWindow() {
		frame = new JFrame();
		frame.setSize(SettingsController.SETTINGS_WINDOW_SIZE.width, SettingsController.SETTINGS_WINDOW_SIZE.height);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setVisible(true);
		frame.setLayout(new FlowLayout());

		JPanel layoutPane = new JPanel() {
			private static final long serialVersionUID = -7511174609023430339L;
			private final BufferedImage background = GraphicsUtil.resize(
					ImageLoader.getImage("resources/img/loginScreen/LoginBackground.jpg"),
					SettingsController.SETTINGS_WINDOW_SIZE.width, SettingsController.SETTINGS_WINDOW_SIZE.height);

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, null);
			}
		};

		frame.setContentPane(layoutPane);
		layoutPane.setLayout(new BorderLayout());
		layoutPane.add(Box.createHorizontalStrut(30), BorderLayout.LINE_START);

		JLabel label = new JLabel();
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(JLabel.RIGHT);
		label.setText("x ");
		label.setOpaque(false);
		label.setFont(new Font("Calibri", Font.PLAIN, 25));
		layoutPane.add(Box.createHorizontalGlue(), BorderLayout.NORTH);
		layoutPane.add(Box.createHorizontalStrut(30), BorderLayout.LINE_END);
		layoutPane.add(Box.createVerticalStrut(30), BorderLayout.PAGE_END);
		layoutPane.add(label, BorderLayout.NORTH);
		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				frame.dispose();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				locX = e.getX();
				locY = e.getY();
			}
		});

		MouseMotionAdapter dragListener = new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setLocation((int) (frame.getLocation().getX() - locX + e.getX()),
						(int) (frame.getLocation().getY() - locY + e.getY()));
			}
		};

		frame.addMouseMotionListener(dragListener);
		label.addMouseMotionListener(dragListener);

		frame.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				locX = e.getX();
				locY = e.getY();
			}
		});
		layoutPane.add(createSettingsPanel(dragListener));
	}

	/**
	 * @param font
	 *            the font for the header + displayed value
	 * @param listener
	 *            a change-listener
	 * @param headerText
	 *            the text for the header
	 * @return a volume-slider with header-text and displayed value
	 */
	private JPanel createVolumeSlider(Font font, ChangeListener listener, String headerText) {
		JPanel res = new JPanel();
		res.setOpaque(false);
		res.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel lblSlider = new JLabel();
		JLabel lblSliderHeader = new JLabel(headerText);
		lblSliderHeader.setOpaque(false);
		lblSliderHeader.setForeground(Color.WHITE);
		lblSliderHeader.setFont(font);

		lblSlider.setOpaque(false);
		lblSlider.setForeground(Color.WHITE);
		lblSlider.setFont(font);

		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 20);
		slider.addChangeListener(listener);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				lblSlider.setText("[" + slider.getValue() + "]");

			}
		});
		lblSlider.setText("[" + slider.getValue() + "]");

		slider.setOpaque(false);
		slider.setForeground(Color.WHITE);
		slider.setFont(font);
		// mainVolumeSlider.setSize(400, 60);
		slider.setPreferredSize(new Dimension(360, 60));

		res.add(lblSliderHeader);
		res.add(slider);
		res.add(lblSlider);

		return res;
	}

	/**
	 * @param dragListener
	 * @param dragClickListener
	 * @param font
	 * @return the fullscreen-checkbox
	 */
	private JCheckBox createFullscreenCheckbox(MouseMotionListener dragListener, MouseListener dragClickListener,
			Font font) {
		JCheckBox chkFullscreen = new JCheckBox();
		chkFullscreen.setOpaque(false);
		chkFullscreen.setFont(font);
		chkFullscreen.setForeground(Color.WHITE);
		chkFullscreen.setText("Fullscreen");
		chkFullscreen.addMouseMotionListener(dragListener);
		chkFullscreen.addMouseListener(dragClickListener);

		chkFullscreen.setSelected(SettingsController.fullScreen);

		chkFullscreen.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				SettingsController.changeFullscreen(chkFullscreen.isSelected());

			}
		});
		return chkFullscreen;
	}

	/**
	 * @return the panel with the information
	 * @param playerName
	 *            the name of the player
	 * @param dragListener
	 *            ...
	 */
	private JPanel createSettingsPanel(MouseMotionListener dragListener) {
		JPanel panel = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1511323112772019677L;

			private final BufferedImage black = GraphicsUtil.resize(ImageLoader.getImage("black_0.4"),
					SettingsController.SETTINGS_WINDOW_SIZE.width, SettingsController.SETTINGS_WINDOW_SIZE.height);

			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(black, 0, 0, null);
				super.paintComponent(g);
			}

		};

		JPanel gridPane = new JPanel(new GridLayout(4, 1));

		Font settingsFont = new Font("Calibri", Font.PLAIN, 20);
		Font bigFont = new Font("Calibri", Font.BOLD, 35);

		JLabel header = new JLabel();
		header.setFont(bigFont);
		header.setText("Settings");
		header.setOpaque(false);
		header.setForeground(Color.WHITE);
		header.setHorizontalAlignment(JLabel.CENTER);
		panel.add(header, BorderLayout.PAGE_START);

		MouseListener dl2 = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				locX = e.getX();
				locY = e.getY();

			}
		};

		gridPane.add(createFullscreenCheckbox(dragListener, dl2, settingsFont), BorderLayout.CENTER);

		ChangeListener mainVolumeSliderChangeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				SettingsController.changeVolumeMenu(((JSlider) e.getSource()).getValue());
			}
		};

		ChangeListener gameVolumeSliderChangeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				SettingsController.changeVolumeIngame(((JSlider) e.getSource()).getValue());
			}
		};

		ChangeListener effectsVolumeSliderChangeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				SettingsController.changeVolumeFX(((JSlider) e.getSource()).getValue());
			}
		};

		gridPane.add(createVolumeSlider(settingsFont, mainVolumeSliderChangeListener, "Main Volume   "),
				BorderLayout.CENTER);
		gridPane.add(createVolumeSlider(settingsFont, gameVolumeSliderChangeListener, "Game Volume "),
				BorderLayout.CENTER);
		gridPane.add(createVolumeSlider(settingsFont, effectsVolumeSliderChangeListener, "Effects Volume	"),
				BorderLayout.CENTER);

		panel.add(gridPane);
		panel.setOpaque(false);
		gridPane.setOpaque(false);

		return panel;
	}

	/**
	 * spawns the window
	 * 
	 * @param location
	 *            location to spawn the window at
	 */
	public void show(Point location) {
		frame.setLocation(location);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
	}
}