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
	 * 
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
			// TODO
		}
	}

	/**
	 * 
	 * @param line
	 * @param isLog
	 */
	public void updateLogger(final String line, boolean isLog) {
		if (isLog) {
			this.dis.updateTextArea(line.split("]")[0] + "]", GameLog.getTimestampColor());
			this.dis.updateTextArea(line.split("]")[1] + "]", GameLog.getMsgtypeColor());
			this.dis.updateTextArea(line.split("]")[2] + "\n", GameLog.getMsgColor());
		} else {
			this.dis.updateTextArea(line, GameLog.getTimestampColor());
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
		 * @return
		 */
		public JTextPane getTextPane() {
			return this.textPane;
		}

		/**
		 * 
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
		 * @param text
		 * @param font
		 */
		public void updateTextArea(final String text, Color font) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						Style style = textPane.addStyle("Style", null);
						StyleConstants.setBackground(style, GameLog.getBackgroundColor());
						StyleConstants.setForeground(style, font);
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