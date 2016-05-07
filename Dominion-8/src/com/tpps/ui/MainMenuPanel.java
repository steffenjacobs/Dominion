package com.tpps.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.MyAudioPlayer;
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
	private final MainFrame parent;
	private final int gapFactor, topGap;

	/**
	 * Constructor for the MainMenuPanell
	 * @param the MainMenu which holds this panel
	 */
	public MainMenuPanel(MainFrame parent) {
		this.gapFactor = 7;
		this.parent = parent;
		this.topGap = Toolkit.getDefaultToolkit().getScreenSize().height / 6;
		this.INITIALIZE_ALPHA = 0.6F;
		MyAudioPlayer.init();
		loadBackgroundImage();
		initializeAlpha();				
		createButtons(this.parent);
		registrateMouseListener();
//		MyAudioPlayer.handleMainMusic(true);
		this.addComponentListener(new MyComponentAdapter());
		repaint();
		if(DominionController.isOffline()){
			try {
				this.buttons[1].setEnabled(false);
				this.buttons[2].setEnabled(false);
				this.buttons[3].setEnabled(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		GameLog.log(MsgType.INIT, "MainMenuPanel");
	}

	/**
	 * creates the Button on the Jpanel
	 * @param parent
	 */
	private void createButtons(MainFrame parent) {
	
			buttons = new MainMenuButton[4];
			String[] names = new String[]{"Single Player", "Multi Player", "Card Editor", "Community"};
			try {
				for (int i = 0; i < buttons.length; i++) {
					buttons[i] = new MainMenuButton((parent.getWidth() / 2), (parent.getHeight() / gapFactor) * (i + 1) + topGap,
							names[i]);
				}				
				
			} catch (IOException e) {
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

			if (buttons[i].isOn(e.getX(), e.getY()) && buttons[i].isEnabled()) {
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
		MainMenuMouseListener m = new MainMenuMouseListener();
		this.addMouseListener(m);
		this.addMouseMotionListener(m);
	}
	
	protected void onResize(double sizeFactorWidth, double sizeFactorHeight){
		this.actualBackground = GraphicsUtil.resize((BufferedImage) this.originalBackground,
				this.parent.getWidth(), this.parent.getHeight());
		for (int i = 0; i < buttons.length; i++) {			
			buttons[i].onResize((parent.getWidth() / 2)
					, (parent.getHeight() / gapFactor) * (i + 1) + topGap,
					sizeFactorWidth, sizeFactorHeight);
		}				
	}

	/**
	 * inner class for listening mouseEvents
	 * @author Lukas
	 *
	 */
	private class MainMenuMouseListener extends MouseAdapter {

		/**
		 * reacts on mouseclicked event
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			MyAudioPlayer.doClick();
			MyAudioPlayer.handleMainMusic(false);
			if (buttons[0].isOn(e.getX(), e.getY()) && buttons[0].isEnabled()) {
				DominionController.getInstance().playSingleplayer();
				
			}
			if (buttons[1].isOn(e.getX(), e.getY()) && buttons[1].isEnabled()) {								
				DominionController.getInstance().joinLobbyGui(false);
				DominionController.getInstance().sendPacketToGetStatistics();
				DominionController.getInstance().findMatch();
			}
			if (buttons[2].isOn(e.getX(), e.getY())&& buttons[2].isEnabled()) {
				DominionController.getInstance().openCardeditor();
			}
			if (buttons[3].isOn(e.getX(), e.getY()) && buttons[3].isEnabled()) {				
				DominionController.getInstance().openStatisticsGui();
				DominionController.getInstance().sendPacketToGetStatistics();
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
	 * inner class which reacts on resize events of the gui
	 * @author Lukas Adler
	 *
	 */
	private class MyComponentAdapter extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			super.componentResized(e);

			int width = MainMenuPanel.this.parent.getContentPane().getWidth();
			int height = MainMenuPanel.this.parent.getContentPane().getHeight();

			int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
			int maxHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

			onResize(width / Double.parseDouble(Integer.toString(maxWidth)),
					height / Double.parseDouble(Integer.toString(maxHeight)));
			repaint();
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
