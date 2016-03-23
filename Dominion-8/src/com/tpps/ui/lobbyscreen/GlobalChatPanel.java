package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.statisticsscreen.StatisticsScreen;

public class GlobalChatPanel extends JPanel{
	
	private JTextArea textbox;
	private JScrollPane scrollpane;
	private LobbyScreen parentLobby;
	private StatisticsScreen parentStat;
	private JTextField chatInputLine;
	private BufferedImage blackBeauty;
	private Font font;
	private JButton sendButton;
	
	private static final int VERTICAL_STRUT = 30;
	private static final int HORIZONTAL_STRUT = 50;
	private static final int SPACE_FROM_CHATBOX_TO_CHATINPUT = 5;
	private static final int SPACE_FROM_CHATINPUT_TO_BUTTON = 20;
	private static final float BLACK_TRANSPARENCY = 0.6F;
	
	private static final long serialVersionUID = 1L;
	
	public GlobalChatPanel(LobbyScreen parent) {
		this.parentLobby = parent;
		this.createComponents();
	}
	
	public GlobalChatPanel(StatisticsScreen parent){
		this.parentStat = parent;
		this.createComponents();
	}
	
	private void createComponents(){
		this.setVisible(true);
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		
		this.createScrollingChatArea();
		
		this.add(scrollpane, BorderLayout.CENTER);
		this.add(this.createPanelForChatInput(),BorderLayout.PAGE_END);
		this.add(Box.createVerticalStrut(VERTICAL_STRUT), BorderLayout.PAGE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_END);		
		
	//	this.testChatInput();

	//	this.revalidate();
	//	parentLobby.revalidate();
	}
	
	public void testChatInput(){
		new Thread(() -> {
			for (int i = 0; i < 10000; i++) {
				GlobalChatPanel.this.appendChat("TestString " + i + "\n");
			}
		}).start();	
	}
	
	private JPanel createPanelForChatInput(){
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
	
	private JPanel createChatInputArea(){	
		chatInputLine = this.initChatInputLine();
		sendButton = this.initSendButton();
		
		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.LINE_AXIS ));
		center.setOpaque(false);
		center.add(chatInputLine);
		center.add(Box.createRigidArea(new Dimension(SPACE_FROM_CHATINPUT_TO_BUTTON,0)));
		center.add(sendButton);
		return center;
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
		sendButton.addActionListener(new ChatMessageListener());
		return sendButton;
	}
	
	private JTextField initChatInputLine(){
		chatInputLine = new JTextField("chat"){
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
	
	private void createScrollingChatArea(){
		
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, BLACK_TRANSPARENCY);
		} catch (IOException e) {
			e.printStackTrace();
		}
		textbox = new JTextArea();
		textbox.setFocusable(false);
		textbox.setForeground(Color.WHITE);
		textbox.setBorder(BorderFactory.createEmptyBorder());
		textbox.setLineWrap(true);
		textbox.setOpaque(false);
		textbox.setText("TeSt");		
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
	
	/**
	 * methods uses thread.sleeep() because the scrollpane is not as a fast as Usain Bolt.
	 * @param chatmessage
	 */
	public synchronized void appendChat(String chatmessage) {
		this.textbox.append(chatmessage);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.scrollpane.getVerticalScrollBar().setValue(this.scrollpane.getVerticalScrollBar().getMaximum());
	}
	
	public LobbyScreen getParentX(){
		return this.parentLobby;
	}
	
	private class ChatMessageListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			GlobalChatPanel.this.handleChatmessage(GlobalChatPanel.this.chatInputLine.getText());
		}
		
	}
	
	private void handleChatmessage(String message){
	//	System.out.println("send message: " + message);
		GlobalChatPanel.this.chatInputLine.setText("");
		this.appendChat(message + "\n");
	}

	
	private class ChatButtonInputListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {			
			if(e.getKeyCode() == KeyEvent.VK_ENTER && !GlobalChatPanel.this.chatInputLine.getText().equals("")){
				GlobalChatPanel.this.handleChatmessage(GlobalChatPanel.this.chatInputLine.getText());
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) { }

		@Override
		public void keyTyped(KeyEvent arg0) { }
	}
}
