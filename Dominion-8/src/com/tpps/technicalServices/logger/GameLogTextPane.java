package com.tpps.technicalServices.logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
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
	private JScrollPane scrollPane;
	private int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	private int maxHeight =	Toolkit.getDefaultToolkit().getScreenSize().height;	

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
		MsgType.setGameMode();
		this.textPane = new JTextPane();
		this.textPane.setEditable(false);
//		this.textPane.setFont(new Font("Courier New", Font.PLAIN, 14));
		this.textPane.setFont(new Font("Calibri", Font.PLAIN, 12));
		this.textPane.setBackground(GameLog.getBackgroundColor());
		this.setLayout(new BorderLayout());
		this.scrollPane = new JScrollPane(this.textPane);
		this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
//		this.scrollPane.setFocusable(false);
		this.add(scrollPane);
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

		double width = (sizeFactorWidth * maxWidth) / 6;
		double height = (sizeFactorHeight * maxHeight) / 6;
		this.setBounds(x - (int) ((maxWidth/1.097) * sizeFactorWidth), y - (int) 
				((maxHeight*0.999) * sizeFactorHeight), (int) (width), (int) (height));
		repaint();
		revalidate();
	}
}