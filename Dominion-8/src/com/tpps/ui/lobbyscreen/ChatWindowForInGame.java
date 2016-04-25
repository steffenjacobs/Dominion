package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.gameplay.GameWindow;

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
	private static final float BLACK_TRANSPARENCY = 0.6F;
	
	private static final Color ownColor = new Color(0,255,0);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]: ");
	private static final Color whiteColor = new Color(255,255,255);

	
	public ChatWindowForInGame() {
		this.loadImage();
		this.setOpaque(false);
		this.setVisible(true);

		this.createMiddlePanel();
		this.createComponents();
	}

	private void createComponents() {
		this.setLayout(new BorderLayout(0, 5));
		this.add(scrollpane, BorderLayout.CENTER);
		this.add(this.createChatInputArea(), BorderLayout.PAGE_END);
	}

	private void loadImage() {
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			this.blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, BLACK_TRANSPARENCY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createMiddlePanel() {
		textbox = new JTextPane();
		textbox.setFocusable(false);
		textbox.setForeground(Color.WHITE);
		textbox.setBorder(BorderFactory.createEmptyBorder());
	//	textbox.setLineWrap(true);
		textbox.setOpaque(false);
		textbox.setText(" Welcome to our Chat!\n Type /help to see all available Commands.\n");
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
		sendButton.setForeground(Color.WHITE);
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
		ChatWindowForInGame.this.chatInputLine.setText("");
		this.createChatInputPart(" " + sdf.format(new Date()), whiteColor);
		this.createChatInputPart(DominionController.getInstance().getUsername() + ": ", ownColor);
		this.createChatInputPart(chatmessage + "\n", whiteColor);
		DominionController.getInstance().sendChatMessage(chatmessage.trim());
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
			if (!ChatWindowForInGame.this.chatInputLine.getText().equals("")) {
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
			if (e.getKeyCode() == KeyEvent.VK_ENTER && !ChatWindowForInGame.this.chatInputLine.getText().equals("")) {
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

	public static void main(String[] args) {
		ChatWindowForInGame chat = new ChatWindowForInGame();

		JFrame frame = new JFrame();
		frame.getContentPane().add(chat);
		frame.setVisible(true);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

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
