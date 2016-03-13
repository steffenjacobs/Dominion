package com.tpps.ui.lobbyscreen;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ChatPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	public static int h_gap = 10;
	public static int v_gap = 50;
	private int CHATWIDTH = 1920 / 2;
	private int CHATHEIGHT = 900;
	private BufferedImage blackBeauty;
	private float pictureAlpha;
	private JTextArea chatwindow;
	private LobbyScreenPanel parent;
	
	private MessageBox messageBox;
	private BoxLayout layout;
	
	public ChatPanel(LobbyScreenPanel parent) {
		this.parent = parent;
		this.init();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(Box.createRigidArea(new Dimension(50,0)));
		this.initChatBox();
		this.initChatTypeBox();
		this.repaint();
		
		//this.setComponentOrientation(ComponentOrientation.);
	}
	
	private void initChatBox(){
		this.chatwindow = new JTextArea();
		this.chatwindow.setFocusable(false);
		this.chatwindow.setPreferredSize(new Dimension((int) (CHATWIDTH * 0.9), (int) (CHATHEIGHT* 0.9)));
		this.chatwindow.setSize(new Dimension(CHATWIDTH, CHATHEIGHT));
		this.chatwindow.setOpaque(false);
		this.chatwindow.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
		this.add(chatwindow, BorderLayout.CENTER);
		this.add(Box.createRigidArea(new Dimension(0,50)));
		
		chatwindow.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) { }

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				ChatPanel.this.parent.getParent().repaint();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				ChatPanel.this.parent.getParent().repaint();
			}
		});
	}
	
	private void initChatTypeBox(){
		this.messageBox = new MessageBox(this);
		this.add(messageBox, BorderLayout.PAGE_END);
		//this.messageBox.
	}
	
	private void init(){
		this.setVisible(true);
		this.pictureAlpha = 0.6F;
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {	
		Graphics2D g2 = (Graphics2D) g;

		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pictureAlpha);
		g2.setComposite(ac);
		g2.drawImage(blackBeauty, 0, 0, null);
	//	System.out.println("repainted chatpanel");
	}	
}
