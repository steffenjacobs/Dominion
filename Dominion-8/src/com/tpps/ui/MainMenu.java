package com.tpps.ui;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.tpps.ui.components.MainMenuButton;

public class MainMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private final Container c;
	private BufferedImage background;
	private MainMenuButton[] buttons;
	private int width, height;
	private float[] alpha;
	private Panel panel;
	private final float INITIALIZE_ALPHA;

	public MainMenu() {
		this.INITIALIZE_ALPHA = 0.6F;
		c = this.getContentPane();
		this.width = Toolkit.getDefaultToolkit().getScreenSize().width;
		this.height = Toolkit.getDefaultToolkit().getScreenSize().height;
		initializeAlpha();

		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		loadImage();
		registrateMouseListener();

		try {
			BufferedImage[] images = new BufferedImage[4];
			for (int i = 0; i < images.length; i++) {
				images[i] = ImageIO
						.read(ClassLoader
								.getSystemResource("resources/img/gameObjects/testButton.png"));
			}

			buttons = new MainMenuButton[4];

			buttons[0] = new MainMenuButton((width / 2)
					- (images[0].getWidth(null) / 2), height / 6, images[0],
					"Single Player");
			buttons[1] = new MainMenuButton((width / 2)
					- (images[1].getWidth(null) / 2), (height / 6) * 2,
					images[1], "Multi Player");
			buttons[2] = new MainMenuButton((width / 2)
					- (images[2].getWidth(null) / 2), (height / 6) * 3,
					images[2], "Settings");
			buttons[3] = new MainMenuButton((width / 2)
					- (images[3].getWidth(null) / 2), (height / 6) * 4,
					images[3], "Community");

		} catch (IOException e) {
			e.printStackTrace();
		}
		this.panel = new Panel(this.background, this.alpha, this.buttons);
		c.add(panel);
		this.panel.repaint();
	}

	private void initializeAlpha() {
		this.alpha = new float[4];

		for (int i = 0; i < 4; i++) {
			alpha[i] = INITIALIZE_ALPHA;
		}
	}

	private void loadImage() {
		try {
			this.background = ImageIO.read(ClassLoader
					.getSystemResource("resources/img/mainMenu/Dominion.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void registrateMouseListener() {
		Mouse m = new Mouse();
		c.addMouseListener(m);
		c.addMouseMotionListener(m);
	}

	private class Mouse extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (buttons[0].isIn(e.getX(), e.getY())) {

			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			makeTransparent(e);
		}

		private void makeTransparent(MouseEvent e) {
			for (int i = 0; i < alpha.length; i++) {

				if (buttons[i].isIn(e.getX(), e.getY())) {
					if (MainMenu.this.alpha[i] != 1.0F) {
						MainMenu.this.alpha[i] = (float) 1;
						MainMenu.this.panel.repaint();
					}
				} else {
					if (MainMenu.this.alpha[i] != INITIALIZE_ALPHA) {
						alpha[i] = (float) INITIALIZE_ALPHA;
						MainMenu.this.panel.repaint();
					}
				}
			}
		}

	}

	public static void main(String[] args) {
		MainMenu menu = new MainMenu();
		menu.setVisible(true);
	}

}
