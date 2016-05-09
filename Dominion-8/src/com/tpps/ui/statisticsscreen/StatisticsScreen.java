package com.tpps.ui.statisticsscreen;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.tpps.technicalServices.util.ImageLoader;
import com.tpps.ui.lobbyscreen.GlobalChatPanel;

/**
 * This class is mainly for testing purposes (Statistics panel)
 * 
 * @author jhuhn
 */
public class StatisticsScreen extends JFrame {

	private static final long serialVersionUID = 1L;
	private int width = 1280;
	private int height = 720;
	private Container c;

	private StatisticsBoard statBoard;

	/**
	 * init testclass
	 */
	public StatisticsScreen() {

		this.setVisible(true);
		this.c = new JLabel(new ImageIcon(ImageLoader.getImage("resources/img/loginScreen/LoginBackground.jpg")));
		this.setContentPane(c);
		c.setLayout(new GridLayout(1, 2));

		this.setMinimumSize(new Dimension(1280, 720));
		this.setSize(width, height);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		statBoard = new StatisticsBoard();
		c.add(new GlobalChatPanel());
		c.add(statBoard);

		this.revalidate();
		this.repaint();
	}

	/**
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		new StatisticsScreen();
	}

}
