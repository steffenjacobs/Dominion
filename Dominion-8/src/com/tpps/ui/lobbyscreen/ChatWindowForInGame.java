package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
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
import javax.swing.JFrame;
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
import com.tpps.ui.gameplay.GameWindow;

/**
 * @author jhuhn
 *
 */
public class ChatWindowForInGame extends JPanel {

	private static final long serialVersionUID = 1L;
	private BufferedImage blackBeauty;
	private JTextPane textbox;
	private Font font;
	private JScrollPane scrollpane;
	private JTextField chatInputLine;
	private JButton sendButton;
	private int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	private int maxHeight =	Toolkit.getDefaultToolkit().getScreenSize().height;	
	private final int SPACE_FROM_CHATINPUT_TO_BUTTON = 20;
	
	private Color textAndLabelColor;
	private static final Color ownColor = new Color(0,255,0);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]: ");
	private static final Color whiteColor = new Color(255,255,255);
	
	public ChatWindowForInGame() {
		this.loadImage();
		this.setOpaque(false);
		this.setVisible(true);

		this.createMiddlePanel();
		this.createComponents();
		GameLog.log(MsgType.INIT, "ChatWindowForInGame");
	}

	private void createComponents() {
		this.setLayout(new BorderLayout(0, 5));
		this.add(scrollpane, BorderLayout.CENTER);
		this.add(this.createChatInputArea(), BorderLayout.PAGE_END);
	}

	private void loadImage() {
		this.blackBeauty = ImageLoader.getImage("black_0.6");
	}

	private void createMiddlePanel() {
		textbox = new JTextPane();
		textbox.setFocusable(false);
		textbox.setForeground(Color.WHITE);
		textbox.setBorder(BorderFactory.createEmptyBorder());
	//	textbox.setLineWrap(true);
		textbox.setOpaque(false);
		textbox.setText(" Welcome to the Chat!\n Type /help to see all available Commands.\n Press [1] for Chat and [2] for Log.\n Press [SPACE] to hide or open.\n\n");
		font = new Font("Calibri", Font.PLAIN, 12);
		textbox.setFont(font);
		scrollpane = new JScrollPane(textbox) {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
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

	private JTextField initChatInputLine() {
		chatInputLine = new JTextField("Type your message here!") {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
			}

		};
		chatInputLine.setFont(font);
		chatInputLine.setCaretColor(Color.WHITE);
		chatInputLine.setForeground(Color.WHITE);
		chatInputLine.setBorder(BorderFactory.createEmptyBorder());
		chatInputLine.setOpaque(false);
		chatInputLine.addKeyListener(new ChatButtonInputListener());
		return chatInputLine;
	}

	private JButton initSendButton() {
		sendButton = new JButton("SEND") {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
			}
		};
		sendButton.setForeground(this.textAndLabelColor);
		sendButton.setContentAreaFilled(false);
		sendButton.setBorderPainted(true);
		sendButton.setOpaque(false);
		sendButton.addMouseListener(new SendButtonListener());
		return sendButton;
	}

	private JPanel createChatInputArea() {
		chatInputLine = this.initChatInputLine();
        chatInputLine.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                chatInputLine.setText("");
            }
        });
		
        this.textAndLabelColor = System.getProperty("os.name").startsWith("Windows") ? Color.WHITE : Color.BLACK;
		sendButton = this.initSendButton();

		JPanel center = new JPanel();
//		center.addFocusListener(new FordFocus());
		center.setLayout(new BoxLayout(center, BoxLayout.LINE_AXIS));
		center.setOpaque(false);
		center.add(chatInputLine);
		center.add(Box.createRigidArea(new Dimension(SPACE_FROM_CHATINPUT_TO_BUTTON, 0)));
		center.add(sendButton);
		return center;
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
		String msg = chatmessage;
		if (!msg.startsWith("/")) 
			msg = BadWordFilter.parseForbiddenWords(chatmessage);
		ChatWindowForInGame.this.chatInputLine.setText("");
		this.createChatInputPart(" " + sdf.format(new Date()), whiteColor);
		this.createChatInputPart(DominionController.getInstance().getUsername() + ": ", ownColor);
		this.createChatInputPart(msg + "\n", whiteColor);
		DominionController.getInstance().sendChatMessage(msg.trim());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.scrollpane.getVerticalScrollBar().setValue(this.scrollpane.getVerticalScrollBar().getMaximum());
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
	public synchronized void createChatInputPart(String chatmessage, Color color){
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
	 * This method appends a chatmessage to the globalchat on the UI. The carret
	 * will be set to the maximum (last chatmessage)
	 * 
	 * @author jhuhn
	 * @param message
	 *            a String representation of the chatmessage
	 * @param user
	 *            a String representation of the user
	 * @param timeStamp
	 *            a String representation of time
	 * @param color
	 *            the username should be shown as this given color
	 */
	public synchronized void appendChatLocal(String message, String user, String timeStamp, Color color){
		this.createChatInputPart(timeStamp, whiteColor);
		this.createChatInputPart(user + ": ", color);
		this.createChatInputPart(message + "\n", whiteColor);		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		this.scrollpane.getVerticalScrollBar().setValue(this.scrollpane.getVerticalScrollBar().getMaximum());
	}
	

	private class SendButtonListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) { }

		@Override
		public void mouseEntered(MouseEvent e) { }

		@Override
		public void mouseExited(MouseEvent e) { }

		@Override
		public void mousePressed(MouseEvent e) {
			if (!ChatWindowForInGame.this.chatInputLine.getText().trim().equals("") 
				&& ChatWindowForInGame.this.checkChatString(chatInputLine.getText())) {
					ChatWindowForInGame.this.appendChatGlobal(ChatWindowForInGame.this.chatInputLine.getText());
			}
			ChatWindowForInGame.this.chatInputLine.requestFocus();
		}

		@Override
		public void mouseReleased(MouseEvent e) { }
	}

	private class ChatButtonInputListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER 
				&& !ChatWindowForInGame.this.chatInputLine.getText().trim().equals("") 
				&& ChatWindowForInGame.this.checkChatString(chatInputLine.getText())) {
					ChatWindowForInGame.this.appendChatGlobal(ChatWindowForInGame.this.chatInputLine.getText());
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}
	}
	
	public JTextField getChatInputLine() {
		return chatInputLine;
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
		
		if(text.length() > 22 && !whitespaces){
			return false;
		}else{
			return true;
		}
	}

	public static void main(String[] args) {
		ChatWindowForInGame chat = new ChatWindowForInGame();

		JFrame frame = new JFrame();
		frame.getContentPane().add(chat);
		frame.setVisible(true);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * 
	 * method designed for the relative layout in the GameGui
	 * 
	 *  @author nagrawal
	 * 
	 * @param x
	 * @param y
	 * @param sizeFactorWidth
	 * @param sizeFactorHeight
	 * @param gameWindow
	 */

	public void onResize(int x, int y, double sizeFactorWidth, double sizeFactorHeight, GameWindow gameWindow) {
		double width = (sizeFactorWidth * maxWidth) / 4;
		double height = (sizeFactorHeight * maxHeight) / 4;
		//3.84
		this.setBounds(x - (int) ((maxWidth/3.5) * sizeFactorWidth), y - (int) ((maxHeight*(1-0.65)) * sizeFactorHeight), (int) (width),
				(int) height);
		repaint();
		revalidate();
	}
}
