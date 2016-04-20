package com.tpps.technicalServices.logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.tpps.ui.gameplay.GameWindow;

/**
 * 
 * @author Nicolas
 * 
 */
@SuppressWarnings("serial")
public class GameLogTextPane extends JPanel {

	private JTextPane textPane;
	private int topGap;

	/**
	 * 
	 * @return the textPane
	 */
	public JTextPane getTextPane() {
		return this.textPane;
	}

	/**
	 * constructor for Display JPanel
	 */
	public GameLogTextPane() {
		this.topGap = Toolkit.getDefaultToolkit().getScreenSize().height / 6;
		MsgType.setGameMode();
		this.textPane = new JTextPane();
		this.textPane.setEditable(false);
		this.textPane.setFont(new Font("Courier New", Font.PLAIN, 14));
		this.textPane.setBackground(GameLog.getBackgroundColor());
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(this.textPane));
		repaint();
		revalidate();
	}

	/**
	 * 
	 * @param text
	 *            the text to update on the JTextPane
	 * @param fontColor
	 *            the fontColor of the text
	 */
	public void updateTextArea(final String text, Color fontColor) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Style style = textPane.addStyle("Style", null);
					StyleConstants.setBackground(style, GameLog.getBackgroundColor());
					StyleConstants.setForeground(style, fontColor);
					StyledDocument doc = textPane.getStyledDocument();
					doc.insertString(doc.getLength(), text, style);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 
	 * @param line
	 *            the line update
	 * @param textColor
	 *            the color of the line
	 * @param timestamp
	 *            determines whether there is a timestamp written in front of
	 *            the text line so the line is only parsed if its necessary
	 */
	public void updateLogger(final String line, Color textColor, boolean timestamp) {
		if (timestamp) {
			this.updateTextArea(line.split("]")[0] + "]", GameLog.getTimestampColor());
			this.updateTextArea(line.split("]")[1] + "]", textColor);
			this.updateTextArea(line.split("]")[2] + "\n", GameLog.getMsgColor());
		} else {
			this.updateTextArea(line + "\n", GameLog.getMsgColor());
		}
	}

	public void onResize(int x, int y, double sizeFactorWidth, double sizeFactorHeight, GameWindow gameWindow) {

		double width = (sizeFactorWidth * Toolkit.getDefaultToolkit().getScreenSize().width) / 6;
		double height = (sizeFactorHeight * Toolkit.getDefaultToolkit().getScreenSize().height) / 6;
		this.setBounds(x - (int) (1750 * sizeFactorWidth), y - (int) (1100 * sizeFactorHeight), (int) (width),
				(int) (height));
		repaint();
		revalidate();
	}
}