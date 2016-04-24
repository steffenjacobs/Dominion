package com.tpps.technicalServices.logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.gameplay.GameWindow;

/**
 * This TextPane displays all log messages on a transparent panel and with
 * colored text.
 * 
 * @caution By default, only MsgType.GAME messages will be displayed, because of
 *          the setGameMode() call in init();.
 * 
 * @author Nicolas Wipfler
 * 
 */
public class GameLogTextPane extends JPanel {

	/**
	 * default
	 */
	private static final long serialVersionUID = 1L;

	private JTextPane textPane;
	private JScrollPane scrollPane;

	private BufferedImage blackBeauty;
	private static final float BLACK_TRANSPARENCY = 0.6F;

	private int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	private int maxHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

	/**
	 * 
	 * @return the textPane
	 */
	public JTextPane getTextPane() {
		return this.textPane;
	}

	/**
	 * constructor for GameLogTextPane() JPanel
	 */
	public GameLogTextPane() {
		MsgType.setGameMode();
		this.setLayout(new BorderLayout());

		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			this.blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(this.blackBeauty, GameLogTextPane.BLACK_TRANSPARENCY);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.textPane = new JTextPane();
		this.textPane.setEditable(false);
		this.textPane.setFocusable(false);
		this.textPane.setOpaque(false);
		this.textPane.setFont(new Font("Calibri", Font.PLAIN, 12));
		this.textPane.setForeground(Color.WHITE);
		this.textPane.setBackground(new Color(100, 100, 100, 100));
		this.textPane.setBorder(BorderFactory.createEmptyBorder());
		// this.textPane.setBackground(GameLog.getBackgroundColor());
		// this.textPane.setLineWrap(true);

		this.scrollPane = new JScrollPane(textPane) {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				h.drawImage(blackBeauty, 0, 0, this.getWidth(), this.getHeight(), null);
				super.paint(h);
			}

		};
		this.scrollPane.setOpaque(false);
		this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
		this.scrollPane.setFocusable(false);
		this.scrollPane.setVisible(true);
		this.scrollPane.getViewport().setOpaque(false);
		this.scrollPane.getVerticalScrollBar().setOpaque(false);
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

	/**
	 * 
	 * @param x
	 * @param y
	 * @param sizeFactorWidth
	 * @param sizeFactorHeight
	 * @param gameWindow
	 * @author Nishit
	 */
	public void onResize(int x, int y, double sizeFactorWidth, double sizeFactorHeight, GameWindow gameWindow) {
		double width = (sizeFactorWidth * maxWidth) / 6;
		double height = (sizeFactorHeight * maxHeight) / 6;
		this.setBounds(x - (int) ((maxWidth / 1.097) * sizeFactorWidth), y - (int) ((maxHeight * 0.999) * sizeFactorHeight), (int) (width), (int) (height));
		repaint();
		revalidate();
	}
}