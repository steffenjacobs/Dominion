package com.tpps.ui.endscreen;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.sun.javafx.tk.Toolkit;
import com.sun.xml.internal.ws.api.Component;
import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.Loader;
import com.tpps.ui.GraphicFramework;

import javafx.scene.layout.Border;
import sun.font.CreatedFontTracker;

/**
 * 
 * @author Nishit Agrawal - nagrawal
 *
 */
public class EndPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private BufferedImage originalBackground;
	private JLabel background;
	private JLabel header;
	private JPanel center;
	private JButton returnButton;
	private static final float FONT_SIZE_NAMES = 50f;
	private static final float FONT_SIZE_POINTS = 40f;
	private Font customFont, resultFont;
	private JPanel playerOnePanel, playerTwoPanel, playerThreePanel, playerFourPanel;
	private BufferedImage blackBeauty;
	private JPanel temp;

	public EndPanel() {
		this.setLayout(new BorderLayout());
		this.setVisible(true);
		fontLoading();
		loadingImages();
		
//		TODO comment out those methods if implemented 


		playerOnePanel = new JPanel();
		playerTwoPanel = new JPanel();
		playerThreePanel = new JPanel();
		playerFourPanel= new JPanel();
//		this.playerOne("die Schweinepriester", 20);
//		this.playerTwo("die Steffenverehrer", 19);
//		this.playerThree("die Möchtegernwifos", 22);
//		this.playerFour("Wipaeds..", 12);
		
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}

		this.setVisible(true);
		this.background = new JLabel(new ImageIcon(originalBackground));
		this.background.setLayout(new BorderLayout());
		createPanel1();

		createPanel3();
		createPanel2();

		this.add(background);
	}

	private void loadingImages() {
		try {
			this.originalBackground = ImageIO
					.read(ClassLoader.getSystemResource("resources/img/loginScreen/LoginBackground.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, 0.4F);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void createPanel1() {
		header = new JLabel("Results", SwingConstants.CENTER);
		header.setForeground(Color.WHITE);
		header.setFont(customFont.deriveFont(100f));
		background.add(header, BorderLayout.PAGE_START);
	}

	public void createPanel2() {
		center = new JPanel(new GridLayout(5, 1));
		center.setOpaque(false);
		center.add(playerOnePanel);
		center.add(playerTwoPanel);
		center.add(playerThreePanel);
		center.add(playerFourPanel);
		center.add(temp);
		background.add(center, BorderLayout.CENTER);
	}

	public void createPanel3() {
		returnButton = new JButton("Return"){

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
			}
		};
		returnButton.setOpaque(false);
		returnButton.setContentAreaFilled(false);
		returnButton.setBorderPainted(true);
		returnButton.setForeground(Color.WHITE);
		returnButton.setVisible(true);
		temp = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		temp.setOpaque(false);
		gbc.ipadx=60;
		gbc.ipady = 20;
		temp.add(returnButton,gbc);
//		background.add(temp, BorderLayout.SOUTH);
		returnButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DominionController.getInstance().joinMainMenu();
			}
		});

	}

	public void playerOne(String playerOne, int points) {

		playerOnePanel = new JPanel(new GridLayout(1, 2)){

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
			}
		};
		playerOnePanel.setOpaque(false);

		JLabel name = new JLabel(playerOne);
		JLabel tempPoints = new JLabel(Integer.toString(points) + " Points");

		name.setVerticalAlignment(JLabel.CENTER);
		tempPoints.setVerticalAlignment(JLabel.CENTER);

		name.setForeground(Color.WHITE);
		name.setFont(resultFont.deriveFont(FONT_SIZE_NAMES));
		tempPoints.setForeground(Color.WHITE);
		tempPoints.setFont(resultFont.deriveFont(FONT_SIZE_POINTS));

		playerOnePanel.add(name);
		playerOnePanel.add(tempPoints);
	}

	public void playerTwo(String playerTwo, int points) {

		playerTwoPanel = new JPanel(new GridLayout(1, 2));
		playerTwoPanel.setOpaque(false);

		JLabel name = new JLabel(playerTwo);
		JLabel tempPoints = new JLabel(Integer.toString(points) + " Points");

		name.setForeground(Color.WHITE);
		name.setFont(resultFont.deriveFont(FONT_SIZE_NAMES));
		tempPoints.setForeground(Color.WHITE);
		tempPoints.setFont(resultFont.deriveFont(FONT_SIZE_POINTS));

		playerTwoPanel.add(name);
		playerTwoPanel.add(tempPoints);
	}

	public void playerThree(String playerThree, int points) {

		playerThreePanel = new JPanel(new GridLayout(1, 2)){

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
			}
		};
		playerThreePanel.setOpaque(false);

		JLabel name = new JLabel(playerThree);
		JLabel tempPoints = new JLabel(Integer.toString(points) + " Points");

		name.setForeground(Color.WHITE);
		name.setFont(resultFont.deriveFont(FONT_SIZE_NAMES));
		tempPoints.setForeground(Color.WHITE);
		tempPoints.setFont(resultFont.deriveFont(FONT_SIZE_POINTS));

		playerThreePanel.add(name);
		playerThreePanel.add(tempPoints);
	}

	public void playerFour(String playerFour, int points) {
		playerFourPanel = new JPanel(new GridLayout(1, 2));
		playerFourPanel.setOpaque(false);

		JLabel name = new JLabel(playerFour);
		JLabel tempPoints = new JLabel(Integer.toString(points) + " Points");

		name.setForeground(Color.WHITE);
		tempPoints.setForeground(Color.WHITE);
		name.setFont(resultFont.deriveFont(FONT_SIZE_NAMES));
		tempPoints.setFont(resultFont.deriveFont(FONT_SIZE_POINTS));

		playerFourPanel.add(name);
		playerFourPanel.add(tempPoints);
	}

	public void fontLoading() {
		try {
			if (customFont == null) {
				customFont = Loader.getInstance().getXenipa();
				resultFont = customFont;
				if (customFont == null) {
					customFont = new Loader().importFont();
					resultFont = customFont;

				}
			}
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//		JFrame jf = new JFrame();
//		jf.add(new EndPanel());
//		jf.setMinimumSize(new Dimension(1280, 720));
//		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		jf.setVisible(true);
//	}
}
