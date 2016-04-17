package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.util.GraphicsUtil;

/**
 * This method is responsible to create and handle the global chat in the gui
 * @author jhuhn
 *
 */
public class GlobalChatPanel extends JPanel{
	
	private JTextArea textbox;
	private JScrollPane scrollpane;
	private JTextField chatInputLine;
	private BufferedImage blackBeauty;
	private Font font;
	private JButton sendButton;
	
	private static final int HORIZONTAL_STRUT = 50;
	private static final int SPACE_FROM_CHATBOX_TO_CHATINPUT = 5;
	private static final int SPACE_FROM_CHATINPUT_TO_BUTTON = 20;
	private static final float BLACK_TRANSPARENCY = 0.6F;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * initializes the object
	 */
	public GlobalChatPanel() {
		this.createComponents();
	}

	
	/**
	 * this method creates all UI components and put them into a BoderLayout
	 */
	private void createComponents(){
		this.setVisible(true);
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		
		this.createScrollingChatArea();
		
		this.add(scrollpane, BorderLayout.CENTER);
		this.add(this.createPanelForChatInput(),BorderLayout.PAGE_END);		
		this.add(this.createArrowButtonPanel(), BorderLayout.PAGE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_START);
		this.add(Box.createHorizontalStrut(HORIZONTAL_STRUT), BorderLayout.LINE_END);		
		
	//	this.testChatInput();

	//	this.revalidate();
	//	parentLobby.revalidate();
	}
	
	private JPanel createArrowButtonPanel(){
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
 
		panel.add(new Button());
		return panel;			  
	}
	
	
	/**
	 * This method is for testing purposes only. It create 10000 teststrings and put them into the global chat
	 */
	public void testChatInput(){
		new Thread(() -> {
			for (int i = 0; i < 10000; i++) {
				GlobalChatPanel.this.appendChatGlobal("TestString " + i + "\n");
			}
		}).start();	
	}
	
	/**
	 * This method creates the overall chatinput area. It centers the textfield bar 
	 * and creates gaps from the chatinputbar to the frame 
	 * @return a JPanel with a chatbar and a send button
	 */
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
	
	/**
	 * this method puts the chatbar and the send button into a panel without margin or gaps
	 * @return a panel with a chatbar and a sendbutton
	 */
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
	
	/**
	 * This method initializes and creates the send button object.
	 * The button delivers a semitransparent look
	 * @return a JButton with a white text 'SEND' a semitransparent black background
	 */
	private JButton initSendButton(){
		sendButton = new JButton("SEND"){
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {				
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				
				h.drawImage(blackBeauty, 0, 0,this.getWidth(), this.getHeight(), null);
				super.paint(h);
			}
		};
		sendButton.setForeground(Color.WHITE);	
		sendButton.setContentAreaFilled(false);
		sendButton.setBorderPainted(true);
		sendButton.setOpaque(false);
		sendButton.addActionListener(new SendButtonListener());
		return sendButton;
	}
	
	/**
	 * This method initializes and create the chatinputbar
	 * @return a JTextField with semitransparent look and white characters, used to type in chatmessages
	 */
	private JTextField initChatInputLine(){
		chatInputLine = new JTextField("Type in your chatmessage"){
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {				
				Graphics2D h = (Graphics2D) g;
				h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				
				h.drawImage(blackBeauty, 0, 0,this.getWidth(), this.getHeight(), null);
				super.paint(h);
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
	
	/**
	 * This method creates and initizalizes the globalchatarea. The globalchatarea is represented in textbox which is embedded in
	 * a scrollpane. The globalchatarea is in a semitransparent look with white characters
	 */
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
	
	/**
	 * This method appends a chatmessage to the globalchat on the UI and sends it to the server.
	 * The carret will be set to the maximum (last chatmessage)
	 * @param chatmessage a String representation of the chatmessage to send
	 */
	public synchronized void appendChatGlobal(String chatmessage) {
		this.textbox.append(chatmessage);
		DominionController.getInstance().sendChatMessage(chatmessage);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.scrollpane.getVerticalScrollBar().setValue(this.scrollpane.getVerticalScrollBar().getMaximum());
	}
	
	
	/**
	 * This method appends a chatmessage to the globalchat on the UI.
	 * The carret will be set to the maximum (last chatmessage)
	 * @param chatmessage a String representation of the chatmessage
	 */
	public synchronized void appendChatLocal(String chatmessage){
		this.textbox.append(chatmessage);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.scrollpane.getVerticalScrollBar().setValue(this.scrollpane.getVerticalScrollBar().getMaximum());
	}
	
	
	/**
	 * This inner class is responsible to handle the send button
	 * @author jhuhn - Johannes Huhn
	 *
	 */
	private class SendButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(!GlobalChatPanel.this.chatInputLine.getText().equals("")){
				GlobalChatPanel.this.handleChatmessage(GlobalChatPanel.this.chatInputLine.getText());				
			}
			GlobalChatPanel.this.chatInputLine.requestFocus();
		}
		
	}
	
	private void handleChatmessage(String message){
	//	System.out.println("send message: " + message);
		GlobalChatPanel.this.chatInputLine.setText("");
		this.appendChatGlobal(message + "\n");
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
