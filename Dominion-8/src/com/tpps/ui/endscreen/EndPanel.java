package com.tpps.ui.endscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.network.gameSession.packets.PacketShowEndScreen;
import com.tpps.technicalServices.util.FontLoader;
import com.tpps.technicalServices.util.GraphicsUtil;

/**
 * 
 * This class provides a JPanel
 * 
 * @author Nishit Agrawal - nagrawal, jhuhn
 *
 */
public class EndPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private BufferedImage originalBackground;
	private JLabel header;
	private JPanel center;
	private JButton returnButton;
	private static final float FONT_SIZE_NAMES = 50f;
	private static final float FONT_SIZE_POINTS = 40f;
	private Font customFont, resultFont;
	private BufferedImage blackBeauty;
	private JPanel temp;
	private ArrayList<JPanel> userPanels;

	
	public EndPanel(PacketShowEndScreen packetShowEndScreen) {
		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		this.fontLoading();
		this.loadingImages();
		
		this.setVisible(true);
		createPanelHeader();

		createPanelButton();
		userPanels = new ArrayList<JPanel>();
		for (int i = 0; i < packetShowEndScreen.getPlayerAmount(); i++) {
			userPanels.add(this.createPlayerJPanel(
					packetShowEndScreen.getNameForPlayer("player" + (i+1)),
					packetShowEndScreen.getPointsForPlayer("player" + (i+1)),
					new JPanel()));
			System.out.println( "PLAYER:_" + packetShowEndScreen.getNameForPlayer("player" + (i+1)) + "POINTS: " + packetShowEndScreen.getPointsForPlayer("player" + (i+1)));
		}
		addUserPanels();
		
		this.add(Box.createHorizontalStrut(50), BorderLayout.LINE_START);
		this.add(Box.createHorizontalStrut(50), BorderLayout.LINE_END);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D h = (Graphics2D) g;
		h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		h.drawImage(originalBackground, 0, 0, this.getWidth(), this.getHeight(), null);
		super.paint(h);
	}
	
	
	public JPanel createPlayerJPanel(String playerOne, int points, JPanel panel) {
		panel = new JPanel(new GridLayout(1, 2)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
			}
		};
		
		panel.setOpaque(false);		

		JLabel name = new JLabel(playerOne);
		name.setHorizontalAlignment(JLabel.CENTER);
		name.setOpaque(false);
		name.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 5, Color.BLACK));
		
		JLabel tempPoints = new JLabel(Integer.toString(points) + " Points");
		tempPoints.setHorizontalAlignment(JLabel.CENTER);
		tempPoints.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Color.BLACK));

		name.setVerticalAlignment(JLabel.CENTER);
		tempPoints.setVerticalAlignment(JLabel.CENTER);

		name.setForeground(Color.WHITE);
		name.setFont(resultFont.deriveFont(FONT_SIZE_NAMES));
		tempPoints.setForeground(Color.WHITE);
		tempPoints.setFont(resultFont.deriveFont(FONT_SIZE_POINTS));

		panel.add(name);
		panel.add(tempPoints);
		return panel;
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

	public void createPanelHeader() {
		header = new JLabel("Results", SwingConstants.CENTER);
		header.setForeground(Color.WHITE);
		header.setFont(customFont.deriveFont(100f));
		this.add(header, BorderLayout.PAGE_START);
	}

	public void addUserPanels() {
		center = new JPanel(new GridLayout(this.userPanels.size() + 1, 1, 0, 30));
		center.setOpaque(false);
		for (int i = 0; i < this.userPanels.size(); i++) {
			center.add(userPanels.get(i));
		}
		center.add(temp);
		this.add(center, BorderLayout.CENTER);
	}

	public void createPanelButton() {
		returnButton = new JButton("Return") {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
			}
		};
		returnButton.setOpaque(false);
		Font returnButtonFont = new Font("Tahoma", Font.PLAIN + Font.BOLD, 20);
		returnButton.setFont(returnButtonFont);
		
		returnButton.setOpaque(false);
		returnButton.setBorderPainted(false);
		returnButton.setContentAreaFilled(false);
		returnButton.setForeground(Color.WHITE);
		returnButton.setVisible(true);
		temp = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		temp.setOpaque(false);
		gbc.ipadx = 60;
		gbc.ipady = 20;
		temp.add(returnButton, gbc);
		// background.add(temp, BorderLayout.SOUTH);
		returnButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DominionController.getInstance().joinMainMenu();
			}
		});

	}

	public void fontLoading() {
		try {
			if (customFont == null) {
				customFont = FontLoader.getInstance().getXenipa();
				resultFont = customFont;
				if (customFont == null) {
					customFont = new FontLoader().importFont();
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
//		jf.setSize(1280, 720);
//		
//		PacketShowEndScreen packetShowEndScreen = new PacketShowEndScreen();
//		packetShowEndScreen.add("player1", "kevin1", 14);
//		packetShowEndScreen.add("player2", "kevin2", 45);
//		packetShowEndScreen.add("player3", "kevin3", 1);
//		packetShowEndScreen.add("player4", "kevin4", 94);
//		
//		jf.add(new EndPanel(packetShowEndScreen));
////		jf.setMinimumSize(new Dimension(1280, 720));
//		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		jf.setVisible(true);
//	}
}
