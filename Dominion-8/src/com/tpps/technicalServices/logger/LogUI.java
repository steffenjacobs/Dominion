package com.tpps.technicalServices.logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * 
 * @author Nicolas
 *
 */
public class LogUI {

	private Display dis;
	private JFrame frame;

	/**
	 * constructor for the LogUI, initializes all required settings
	 */
	public LogUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			this.dis = new Display();
			this.frame = new JFrame();
			this.frame.setResizable(false);
			this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.frame.setLayout(new BorderLayout());
			this.frame.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			this.frame.getContentPane().add(this.dis);
			this.frame.setSize(900, 400);
			this.frame.setLocationRelativeTo(null);
			this.frame.setVisible(true);
		} catch (Exception e) {
			GameLog.log(MsgType.EXCEPTION, e.getMessage());
		}
	}

	/**
	 * 
	 * @param line the line update
	 * @param textColor the color of the line
	 * @param timestamp determines whether a timestamp is written in front of the text line
	 */
	public void updateLogger(final String line, Color textColor, boolean timestamp) {
		if (timestamp) {
			this.dis.updateTextArea(line.split("]")[0] + "]", GameLog.getTimestampColor());
			this.dis.updateTextArea(line.split("]")[1] + "]", textColor);
			this.dis.updateTextArea(line.split("]")[2] + "\n", GameLog.getMsgColor());
		} else {
			this.dis.updateTextArea(line, textColor);
		}
	}

	/**
	 * 
	 * @author Nicolas
	 *
	 */
	@SuppressWarnings("serial")
	public class Display extends JPanel {

		private JTextPane textPane;

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
		public Display() {
			this.textPane = new JTextPane();
			this.textPane.setEditable(false);
			this.textPane.setFont(new Font("Courier New", Font.PLAIN, 13));
			this.textPane.setBackground(GameLog.getBackgroundColor());
			this.setLayout(new BorderLayout());
			this.add(new JScrollPane(this.textPane));
		}

		/**
		 * 
		 * @param text the text to update on the JTextPane
		 * @param fontColor the fontColor of the text
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
						// TODO
					}
				}
			});
		}
	}
}