package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

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

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.gameplay.GameWindow;

public class ChatWindowForInGame extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private BufferedImage blackBeauty;
	private JTextArea textbox;
	private Font font;
	private JScrollPane scrollpane;
	private JTextField chatInputLine;
	private JButton sendButton;
	private final int SPACE_FROM_CHATINPUT_TO_BUTTON = 20;
	private static final float BLACK_TRANSPARENCY = 0.3F;

	public ChatWindowForInGame() {
		this.loadImage();
		this.setOpaque(false);
		this.setVisible(true);
		
		this.createMiddlePanel();		
		this.createComponents();
	}
	
	private void createComponents(){
		this.setLayout(new BorderLayout(0, 5));
		this.add(scrollpane, BorderLayout.CENTER);
		this.add(this.createChatInputArea(), BorderLayout.PAGE_END);
	}
	
	private void loadImage(){
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			this.blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, BLACK_TRANSPARENCY);
		} catch (IOException e) {		
			e.printStackTrace();
		}		
	}
	
	private void createMiddlePanel(){
		textbox = new JTextArea();
		textbox.setFocusable(false);
		textbox.setForeground(Color.WHITE);
		textbox.setBorder(BorderFactory.createEmptyBorder());
		textbox.setLineWrap(true);
		textbox.setOpaque(false);
		textbox.setText("Welcome to our chatserver \n");		
		font = new Font("Calibri", Font.PLAIN, 20);
		textbox.setFont(font);
		scrollpane =  new JScrollPane(textbox){
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
	
	private JTextField initChatInputLine(){		
		chatInputLine = new JTextField("Type in your chatmessage"){
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
	
	private JButton initSendButton(){
		sendButton = new JButton("SEND"){
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
		sendButton.addActionListener(new SendButtonListener());
		return sendButton;
	}
	
	private JPanel createChatInputArea(){	
		chatInputLine = this.initChatInputLine();
		sendButton = this.initSendButton();
		
		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.LINE_AXIS ));
		center.setOpaque(false);
		center.add(chatInputLine);
		center.add(Box.createRigidArea(new Dimension(SPACE_FROM_CHATINPUT_TO_BUTTON ,0)));
		center.add(sendButton);
		return center;
	}
	
	private void handleChatmessage(String message){
	//	System.out.println("send message: " + message);
		ChatWindowForInGame.this.chatInputLine.setText("");
		this.appendChat(message + "\n");
	}
	
	public synchronized void appendChat(String chatmessage) {
		this.textbox.append(chatmessage);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.scrollpane.getVerticalScrollBar().setValue(this.scrollpane.getVerticalScrollBar().getMaximum());
	}

	private class SendButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(!ChatWindowForInGame.this.chatInputLine.getText().equals("")){			
				ChatWindowForInGame.this.handleChatmessage(ChatWindowForInGame.this.chatInputLine.getText());				
			}
			ChatWindowForInGame.this.chatInputLine.requestFocus();
		}		
	}
	
	private class ChatButtonInputListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {			
			if(e.getKeyCode() == KeyEvent.VK_ENTER && !ChatWindowForInGame.this.chatInputLine.getText().equals("")){
				ChatWindowForInGame.this.handleChatmessage(ChatWindowForInGame.this.chatInputLine.getText());
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) { }

		@Override
		public void keyTyped(KeyEvent arg0) { }
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
//		int width = (int) (sizeFactorWidth*gameWindow.getWIDTH()/8);
//		int height =  (int) (sizeFactorHeight*gameWindow.getHEIGHT()/8);
//		System.out.println(x-width*1.5+"x");
//		System.out.println(y+"y");
//		this.setBounds(x-(int) (width*1.5), y, width,height);
		
	}
}
