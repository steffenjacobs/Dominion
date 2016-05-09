package com.tpps.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.ImageLoader;

/**
 * This represents a loading-screen
 * 
 * @author Steffen Jacobs
 */
public class LoadingScreen {
	private JFrame loadingFrame;
	private JLabel lblSubMessage;

	/** removes the loading-screen */
	public void dispose() {
		this.loadingFrame.dispose();
	}

	/**
	 * sets the sub-text
	 * 
	 * @param submessage
	 *            the new sub-message
	 */
	public void setSubText(String submessage) {
		lblSubMessage.setText(submessage);
	}

	/**
	 * constructor for loadingscreen
	 * 
	 * @param message
	 *            main message
	 */
	public LoadingScreen(String message) {
		Dimension loadingSize = new Dimension(400, 180);
		loadingFrame = new JFrame();
		loadingFrame.setSize(loadingSize);

		loadingFrame.setLocation(
				(int) (DominionController.getInstance().getMainFrame().getLocation().getX()
						+ DominionController.getInstance().getMainFrame().getSize().getWidth() / 2
						- loadingFrame.getSize().getWidth() / 2),
				(int) (DominionController.getInstance().getMainFrame().getLocation().getY()
						+ DominionController.getInstance().getMainFrame().getSize().getHeight() / 2
						- loadingFrame.getSize().getHeight() / 2));

		JPanel content = new JPanel() {
			private static final long serialVersionUID = -7511174609023430339L;
			private final BufferedImage background = GraphicsUtil.resize(
					ImageLoader.getImage("resources/img/loginScreen/LoginBackground.jpg"), loadingSize.width,
					loadingSize.height);

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, null);
			}
		};

		content.setLayout(new BorderLayout());

		JPanel pLabels = new JPanel();
		pLabels.setOpaque(false);
		pLabels.setLayout(new GridLayout(2, 1));

		JLabel lblMessage = new JLabel(message);
		lblMessage.setFont(new Font("Calibri", Font.BOLD, 35));
		lblMessage.setForeground(Color.WHITE);

		lblSubMessage = new JLabel("...");
		lblSubMessage.setFont(new Font("Calibri", Font.ITALIC, 25));
		lblSubMessage.setForeground(Color.WHITE);

		pLabels.add(lblMessage);
		pLabels.add(lblSubMessage);
		content.add(pLabels, BorderLayout.CENTER);

		content.add(Box.createVerticalStrut(30), BorderLayout.PAGE_START);
		content.add(Box.createVerticalStrut(30), BorderLayout.PAGE_END);
		content.add(Box.createHorizontalStrut(30), BorderLayout.LINE_START);
		content.add(Box.createHorizontalStrut(30), BorderLayout.LINE_END);

		loadingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		loadingFrame.setContentPane(content);
		loadingFrame.setUndecorated(true);
		loadingFrame.setResizable(false);
		
		SwingUtilities.invokeLater(() -> {
			loadingFrame.setVisible(true);
		});
	}
}
