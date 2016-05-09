package com.tpps.ui.statisticsscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.tpps.technicalServices.network.login.SQLHandling.Ranking;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.ImageLoader;
import com.tpps.technicalServices.util.MathUtil;

/**
 * this represents a small window that calculates the stats and displays them.
 * 
 * @author Steffen Jacobs
 *
 */
public class StatsCard {

	private static final Dimension STATSCARD_SIZE = new Dimension(540, 350);
	private final StatisticsBoard parent;
	private int locX, locY;

	/**
	 * constructor
	 * 
	 * @param e
	 *            the mouse-event where had been clicked to find the correct
	 *            user
	 * @param sboard
	 *            the instance of the StatisticsBoard the table is on
	 */
	public StatsCard(MouseEvent e, StatisticsBoard sboard) {
		this.parent = sboard;
		JFrame frame = new JFrame();
		frame.setSize(STATSCARD_SIZE.width, STATSCARD_SIZE.height);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setVisible(true);
		frame.setLayout(new FlowLayout());
		frame.setLocation(
				(int) (parent.getLocation().getX() + parent.getSize().getWidth() / 2 - frame.getSize().getWidth() / 2),
				(int) (parent.getLocation().getY() + parent.getSize().getHeight() / 2
						- frame.getSize().getHeight() / 2));

		JPanel layoutPane = new JPanel() {
			private static final long serialVersionUID = -7511174609023430339L;
			private final BufferedImage background = GraphicsUtil.resize(
					ImageLoader.getImage("resources/img/loginScreen/LoginBackground.jpg"), STATSCARD_SIZE.width,
					STATSCARD_SIZE.height);

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
		layoutPane
				.add(createStatisticsCard(
						(String) parent.getTable()
								.getValueAt(parent.getTable()
										.convertRowIndexToModel(parent.getTable().rowAtPoint(e.getPoint())), 0),
						dragListener));
	}

	/**
	 * @return the panel with the information
	 * @param playerName
	 *            the name of the player
	 * @param dragListener
	 *            ...
	 */
	private JPanel createStatisticsCard(String playerName, MouseMotionAdapter dragListener) {
		JPanel panel = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1511323112772019677L;

			private final BufferedImage black = GraphicsUtil.resize(ImageLoader.getImage("black_0.4"),
					STATSCARD_SIZE.width, STATSCARD_SIZE.height);

			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(black, 0, 0, null);
				super.paintComponent(g);
			}

		};

		String[][] stats = getStatsOf(playerName);

		if (stats == null) {
			panel.add(new JLabel("Player not found: " + playerName), BorderLayout.CENTER);
			return panel;
		}

		JPanel gridPane = new JPanel(new BorderLayout());

		Font font = new Font("Calibri", Font.PLAIN, 19);
		Font bigFont = new Font("Calibri", Font.BOLD, 25);

		JLabel header = new JLabel();
		header.setFont(bigFont);
		header.setText("Statistics for " + stats[0][0] + ": ");
		header.setOpaque(false);
		header.setForeground(Color.WHITE);
		header.setHorizontalAlignment(JLabel.CENTER);
		gridPane.add(header, BorderLayout.PAGE_START);

		JTextPane textPane = new JTextPane();
		textPane.setFocusable(false);
		textPane.setOpaque(false);
		textPane.setFont(font);
		textPane.addMouseMotionListener(dragListener);
		textPane.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				locX = e.getX();
				locY = e.getY();

			}
		});
		textPane.setCursor(header.getCursor());

		Style styleWhite = textPane.addStyle("Style", null);
		StyleConstants.setForeground(styleWhite, Color.WHITE);
		StyleConstants.setLineSpacing(styleWhite, 0.6f);

		Style styleGray = textPane.addStyle("Style", null);
		StyleConstants.setForeground(styleGray, new Color(180, 180, 190));

		StyledDocument doc = textPane.getStyledDocument();

		try {
			doc.insertString(doc.getLength(), " Wins:			" + stats[1][0], styleWhite);
			doc.insertString(doc.getLength(), "		" + formatTopPercentage(stats[1][1]), styleGray);
			doc.insertString(doc.getLength(), " Losses:			" + stats[2][0], styleWhite);
			doc.insertString(doc.getLength(), "		" + formatTopPercentage(stats[2][1]), styleGray);
			doc.insertString(doc.getLength(), " Win-Loss-Ration:		" + stats[3][0], styleWhite);
			doc.insertString(doc.getLength(), "		" + formatTopPercentage(stats[3][1]), styleGray);
			doc.insertString(doc.getLength(), " Total Matches:		" + stats[4][0], styleWhite);
			doc.insertString(doc.getLength(), "		" + formatTopPercentage(stats[4][1]), styleGray);

			String rankString = Ranking.getRankByScore(stats[0][0], Integer.parseInt(stats[5][0]));
			doc.insertString(doc.getLength(), " Rank:			" + rankString, styleWhite);
			doc.insertString(doc.getLength(),
					"	" + (rankString.length() < 9 ? "	" : "") + formatTopPercentage(stats[5][1]), styleGray);

			String timeString = MathUtil.getTimeString(Long.parseLong(stats[6][0]));
			doc.insertString(doc.getLength(), " Playtime:		" + timeString, styleWhite);
			doc.insertString(doc.getLength(),
					"	" + (timeString.length() < 9 ? "	" : "") + formatTopPercentage(stats[6][1]), styleGray);
			doc.setParagraphAttributes(0, doc.getLength(), styleWhite, false);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		gridPane.add(textPane, BorderLayout.CENTER);

		panel.add(gridPane);
		panel.setOpaque(false);
		gridPane.setOpaque(false);

		return panel;
	}

	/**
	 * @return a String-Array of statistics paired with top-percentage for a
	 *         specific player
	 * @param playerName
	 *            the name of the player to get the stats for
	 */
	private String[][] getStatsOf(String playerName) {
		String[][] statistics = this.parent.getStatistics();
		if (statistics.length < 1) {
			return null;
		}
		String[][] result = new String[7][2];
		int oldSort = this.parent.getSortColumn();
		for (int i = 0; i < statistics[0].length; i++) {
			parent.sortTableByColumn(i);
			for (int j = 0; j < statistics.length; j++) {
				if (statistics[j][0].equals(playerName)) {
					result[i][0] = statistics[j][i];
					result[i][1] = String.valueOf(j * 100 / statistics.length);
					break;
				}
			}
		}
		parent.sortTableByColumn(oldSort);
		return result;
	}

	/**
	 * @return a formatted top-percentage or "best"
	 * @param value
	 *            the value to format
	 */
	private static String formatTopPercentage(String value) {
		if (value.equals("0"))
			return "(Best)\n";
		return "(Top " + value + "%)\n";
	}
}