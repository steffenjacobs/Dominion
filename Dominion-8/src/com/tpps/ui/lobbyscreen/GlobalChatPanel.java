package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.logger.GameLog;
import com.tpps.technicalServices.logger.MsgType;
import com.tpps.technicalServices.network.chat.client.BadWordFilter;
import com.tpps.technicalServices.util.ImageLoader;

/**
 * This method is responsible to create and handle the global chat in the gui
 * 
 * @author jhuhn
 *
 */
public class GlobalChatPanel extends JPanel {

	private JTextPane textbox;
	private JScrollPane scrollpane;
	private JTextField chatInputLine;
	private BufferedImage blackBeauty;
	private Font font;
	private JButton sendButton;
	private Color textAndLabelColor;

	private BackButton backButton;
	
	private static final int HORIZONTAL_STRUT = 50;
	private static final int SPACE_FROM_CHATBOX_TO_CHATINPUT = 5;
	private static final int SPACE_FROM_CHATINPUT_TO_BUTTON = 20;

	private static final long serialVersionUID = 1L;
	private static final Color ownColor = new Color(0, 255, 0);
	/**
	 * just a simpledateformat instnce to add a timestamp, when a chatmessage
	 * receives
	 */
	public static final SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]: ");
	private static final Color whiteColor = new Color(255, 255, 255);

	/**
	 * initializes the object
	 * 
	 * @author jhuhn
	 */
	public GlobalChatPanel() {
		this.createComponents();
		GameLog.log(MsgType.INIT, "GlobalChatPanel");
	}

	/**
	 * this method creates all UI components and put them into a BoderLayout
	 * 
	 * @author jhuhn
	 */
	private void createComponents() {
		this.backButton = new BackButton();
		this.setVisible(true);
		this.setOpaque(false);
		this.setLayout(new BorderLayout());

		this.createScrollingChatArea();

		this.add(scrollpane, BorderLayout.CENTER);
		this.add(this.createPanelForChatInput(), BorderLayout.PAGE_END);
		this.add(this.createArrowButtonPanel(), BorderLayout.PAGE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_END);

		// this.testChatInput();
		// this.revalidate();
	}

	/**
	 * upper area of the globalchatpanel
	 * 
	 * @author jhuhn
	 * @return a JPanel with button (painted as arrow)
	 */
	private JPanel createArrowButtonPanel() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		panel.add(this.backButton);
		return panel;
	}

	/**
	 * This method is for testing purposes only. It create 10000 teststrings and
	 * put them into the global chat
	 * 
	 * @author jhuhn
	 */
	public void testChatInput() {
		new Thread(() -> {
			for (int i = 0; i < 10000; i++) {
				GlobalChatPanel.this.appendChatGlobal("TestString " + i + "\n");
			}
		}).start();
	}

	/**
	 * This method creates the overall chatinput area. It centers the textfield
	 * bar and creates gaps from the chatinputbar to the frame
	 * 
	 * @author jhuhn
	 * @return a JPanel with a chatbar and a send button
	 */
	private JPanel createPanelForChatInput() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);

		panel.add(Box.createVerticalStrut(SPACE_FROM_CHATBOX_TO_CHATINPUT), BorderLayout.PAGE_START);
		panel.add(Box.createVerticalStrut(SPACE_FROM_CHATBOX_TO_CHATINPUT), BorderLayout.PAGE_END);
		panel.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_START);
		panel.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_END);

		JPanel center = this.createChatInputArea();
		panel.add(center, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * this method puts the chatbar and the send button into a panel without
	 * margin or gaps
	 * 
	 * @author jhuhn
	 * @return a panel with a chatbar and a sendbutton
	 */
	private JPanel createChatInputArea() {
		chatInputLine = this.initChatInputLine();
		sendButton = this.initSendButton();

		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.LINE_AXIS));
		center.setOpaque(false);
		center.add(chatInputLine);
		center.add(Box.createRigidArea(new Dimension(SPACE_FROM_CHATINPUT_TO_BUTTON, 0)));
		center.add(sendButton);
		return center;
	}

	/**
	 * This method initializes and creates the send button object. The button
	 * delivers a semitransparent look
	 * 
	 * @author jhuhn
	 * @return a JButton with a white text 'SEND' a semitransparent black
	 *         background
	 */
	private JButton initSendButton() {
		this.textAndLabelColor = System.getProperty("os.name").startsWith("Windows") ? Color.WHITE : Color.BLACK;
		sendButton = new JButton("SEND") {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				h.drawImage(blackBeauty, 0, 0, this.getWidth(), this.getHeight(), null);
				super.paint(h);
			}
		};
		sendButton.setForeground(this.textAndLabelColor);
		sendButton.setContentAreaFilled(false);
		sendButton.setBorderPainted(true);
		sendButton.setOpaque(false);
		sendButton.addMouseListener(new SendButtonListener());
		return sendButton;
	}

	/**
	 * This method initializes and create the chatinputbar
	 * 
	 * @author jhuhn
	 * @return a JTextField with semitransparent look and white characters, used
	 *         to type in chatmessages
	 */
	private JTextField initChatInputLine() {
		chatInputLine = new JTextField("Type in /help for commands") {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				h.drawImage(blackBeauty, 0, 0, this.getWidth(), this.getHeight(), null);
				super.paint(h);
			}

		};
		chatInputLine.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(chatInputLine.getText().startsWith("Type in /help for commands")){
					chatInputLine.setText("");
				}
			}
		});
		chatInputLine.setFont(font);
		chatInputLine.setCaretColor(Color.WHITE);
		chatInputLine.setForeground(Color.WHITE);
		chatInputLine.setBorder(BorderFactory.createEmptyBorder());
		chatInputLine.setOpaque(false);
		chatInputLine.addKeyListener(new ChatButtonInputListener());
		return chatInputLine;
	}

	/**
	 * This method creates and initizalizes the globalchatarea. The
	 * globalchatarea is represented in textbox which is embedded in a
	 * scrollpane. The globalchatarea is in a semitransparent look with white
	 * characters
	 * 
	 * @author jhuhn
	 */
	private void createScrollingChatArea() {

		this.blackBeauty = ImageLoader.getImage("black_0.6");
		textbox = new JTextPane();
		textbox.setFocusable(false);
		textbox.setForeground(Color.WHITE);
		textbox.setBackground(new Color(100, 100, 100, 100));
		textbox.setBorder(BorderFactory.createEmptyBorder());
		// textbox.setLineWrap(true);
		textbox.setOpaque(false);
		textbox.setText("Welcome to the Chat!\nType /help to see all available Commands.\n");
		font = new Font("Calibri", Font.PLAIN, 20);
		textbox.setFont(font);
		scrollpane = new JScrollPane(new JPanel(new BorderLayout()).add(textbox)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				h.drawImage(blackBeauty, 0, 0, this.getWidth(), this.getHeight(), null);
				super.paint(h);
			}

		};
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setOpaque(false);
		scrollpane.setBorder(BorderFactory.createEmptyBorder());
		scrollpane.setFocusable(false);
		scrollpane.setVisible(true);
		scrollpane.getViewport().setOpaque(false);
		scrollpane.getVerticalScrollBar().setOpaque(false);
	}

	/**
	 * This method appends a chatmessage to the globalchat on the UI and sends
	 * it to the server. The carret will be set to the maximum (last
	 * chatmessage)
	 * 
	 * @param chatmessage
	 *            a String representation of the chatmessage to send
	 * @author jhuhn
	 */
	public synchronized void appendChatGlobal(String chatmessage) {
		if (!chatmessage.startsWith("/")) 
			chatmessage = BadWordFilter.parseForbiddenWords(chatmessage);
		GlobalChatPanel.this.chatInputLine.setText("");
		this.createChatInputPart(sdf.format(new Date()), whiteColor);
		this.createChatInputPart(DominionController.getInstance().getUsername() + ": ", ownColor);
		this.createChatInputPart(chatmessage + "\n", whiteColor);
		DominionController.getInstance().sendChatMessage(chatmessage.trim());

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.scrollpane.getVerticalScrollBar().setValue(this.scrollpane.getVerticalScrollBar().getMaximum());
		// this.scrollpane.getViewport().setViewPosition(new
		// Point(this.scrollpane.getWidth(), this.scrollpane.getHeight()));
	}

	/**
	 * This method formats a String with a specific color to a documentobject
	 * 
	 * @author jhuhn
	 * @param chatmessage
	 *            the string of text that should be shown in the chatarea
	 * @param color
	 *            Chatmessage gets that visible color
	 */
	public synchronized void createChatInputPart(String chatmessage, Color color) {
		Style style = textbox.addStyle("Style", null);
		StyleConstants.setForeground(style, color);
		StyledDocument doc = textbox.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), chatmessage, style);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * calls the appendChatLocal with default value true
	 * 
	 * for JavaDocumentation of this method see {appendChatLocal(String, String, String, Color, boolean}
	 @param message
	 *            a String representation of the chatmessage
	 * @param user
	 *            a String representation of the user
	 * @param timestamp
	 *            a String representation of time
	 * @param color
	 *            the username should be shown as this given color
	 */
	public synchronized void appendChatLocal(String message, String user, String timestamp, Color color) {
		this.appendChatLocal(message, user, timestamp, color, true);
	}
	
	/**
	 * This method appends a chatmessage to the globalchat on the UI. The carret
	 * will be set to the maximum (last chatmessage)
	 * 
	 * @author jhuhn
	 * @param message
	 *            a String representation of the chatmessage
	 * @param user
	 *            a String representation of the user
	 * @param timestamp
	 *            a String representation of time
	 * @param color
	 *            the username should be shown as this given color
	 * @param point 
	 *            whether the message should get a point appended after the username or not
	 */
	public synchronized void appendChatLocal(String message, String user, String timestamp, Color color, boolean point) {
		this.createChatInputPart(timestamp, whiteColor);
		this.createChatInputPart(user + (point ? ": " : ""), color);
		this.createChatInputPart(message + "\n", whiteColor);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.scrollpane.getVerticalScrollBar().setValue(this.scrollpane.getVerticalScrollBar().getMaximum());
	}

	/**
	 * This inner class is responsible to handle the send button
	 * 
	 * @author jhuhn - Johannes Huhn
	 *
	 */
	private class SendButtonListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
			if (!GlobalChatPanel.this.chatInputLine.getText().trim().equals("")
				&& GlobalChatPanel.this.checkChatString(chatInputLine.getText())) {
					GlobalChatPanel.this.appendChatGlobal(GlobalChatPanel.this.chatInputLine.getText());
			}
			GlobalChatPanel.this.chatInputLine.requestFocus();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}
	
	/**
	 * This method checks if a given String contains of Whitespaces
	 * 
	 * @author jhuhn
	 * @param text
	 *            String representation of the text to check
	 * @return true, if the given String is allowed to send
	 */
	private boolean checkChatString(String text){
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(text);
		boolean whitespaces = matcher.find();
		
		if(text.length() > 30 && !whitespaces){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * This inner class is responsible to send messages via the enter key
	 * 
	 * @author jhuhn - Johannes Huhn
	 *
	 */
	private class ChatButtonInputListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER
				&& !GlobalChatPanel.this.chatInputLine.getText().trim().equals("")
				&& GlobalChatPanel.this.checkChatString(chatInputLine.getText())) {
					GlobalChatPanel.this.appendChatGlobal(GlobalChatPanel.this.chatInputLine.getText());
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}
	}
	
	/**
	 * @author jhuhn
	 * @return the backButton instance
	 */
	public BackButton getBackButton() {
		return backButton;
	}
}
