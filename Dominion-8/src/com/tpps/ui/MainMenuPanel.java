package com.tpps.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.components.MainMenuButton;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class MainMenuPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final float INITIALIZE_ALPHA;
	private BufferedImage originalBackground, actualBackground;
	
	private float[] alpha;
	private MainMenuButton[] buttons;	
	private final MainMenu parent;

	/**
	 * 
	 * @param background: backgroundImage
	 * @param alpha 
	 * @param buttons
	 */
	public MainMenuPanel(MainMenu parent) {
		this.INITIALIZE_ALPHA = 0.6F;
		this.parent = parent;
		loadBackgroundImage();
		initializeAlpha();				
		createButtons(this.parent);
		registrateMouseListener();		
	}

	/**
	 * creates the Button
	 * @param parent
	 */
	private void createButtons(MainMenu parent) {
	
			buttons = new MainMenuButton[4];
			String[] names = new String[]{"Single Player", "Multi Player", "Settins", "Community"};
			try {
				for (int i = 0; i < buttons.length; i++) {
					buttons[i] = new MainMenuButton((parent.getWidth() / 2), parent.getHeight() / 6 * (i + 1),
							names[i]);
				}				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	/**
	 * loads the backgroundImage from filesystem
	 */
	private void loadBackgroundImage() {
		try {
			this.originalBackground = ImageIO.read(ClassLoader
					.getSystemResource("resources/img/mainMenu/Dominion.jpg"));
			
			int newWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
			int newHeight = Toolkit.getDefaultToolkit().getScreenSize().height;			
			this.actualBackground = GraphicsUtil.resize(this.originalBackground, newWidth, newHeight);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * initializes the alpha value of each button.
	 */
	private void initializeAlpha() {
		this.alpha = new float[4];

		for (int i = 0; i < 4; i++) {
			alpha[i] = INITIALIZE_ALPHA;
		}
	}
	
	/**
	 * changes the alpha value of the button if the mouse is on the button
	 * @param e
	 */
	protected void changeAlphaForButton(MouseEvent e) {
		for (int i = 0; i < alpha.length; i++) {

			if (buttons[i].isOn(e.getX(), e.getY())) {
				if (this.alpha[i] != 1.0F) {
					this.alpha[i] = 1F;
					repaint();
				}
			} else {
				if (this.alpha[i] != INITIALIZE_ALPHA) {
					alpha[i] = (float) INITIALIZE_ALPHA;
					repaint();
				}
			}
		}
	}
	
	/**
	 * registrates the MouseListener on the panel
	 */
	private void registrateMouseListener() {
		Mouse m = new Mouse();
		this.addMouseListener(m);
		this.addMouseMotionListener(m);
	}
	
	protected void onResize(double sizeFactorWidth, double sizeFactorHeight){
		this.actualBackground = GraphicsUtil.resize((BufferedImage) this.originalBackground,
				this.parent.getWidth(), this.parent.getHeight());
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].onResize((parent.getWidth() / 2)
					- (buttons[i].getActualImage().getWidth(null) / 2), (parent.getHeight() / 6) * (i + 1), sizeFactorWidth, sizeFactorHeight);
		}
	}

	/**
	 * inner class for listening mouseEvents
	 * @author Lukas
	 *
	 */
	private class Mouse extends MouseAdapter {

		/**
		 * reacts on mouseclicked event
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			if (buttons[0].isOn(e.getX(), e.getY())) {
				MainMenuPanel.this.parent.dispose();
				try {
					new GameWindow().setVisible(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		/**
		 * reacts on mouseMoved event
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			MainMenuPanel.this.changeAlphaForButton(e);
		}
	}

	/**
	 * draws the background and the buttons
	 */
	@Override
	public void paint(Graphics g) {

		g.drawImage(actualBackground, 0, 0, null);
		Graphics2D g2 = (Graphics2D) g;

		for (int i = 0; i < alpha.length; i++) {
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[i]);
			g2.setComposite(ac);
			g2.drawImage(buttons[i].getActualImage(), buttons[i].getX(), buttons[i].getY(), null);
		}
	}
}
