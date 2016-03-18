package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.tpps.technicalServices.util.GraphicsUtil;

public class LeftPanel extends JPanel{
	
	JTextArea textbox;
	JScrollPane pane;
	JTextField chatmessage;
	LobbyScreen parent;
	BufferedImage blackBeauty;
	Font font = new Font("Calibri", Font.PLAIN, 20);
	JButton but;
	
	private static final long serialVersionUID = 1L;
	
	public LeftPanel(LobbyScreen parent) {
		this.parent = parent;
		this.setVisible(true);
		this.setOpaque(false);
		BorderLayout layout = new BorderLayout();
	//	layout.setHgap(50);
		this.setLayout(layout);
		
		this.scroller();
		this.messageTyper();
		
		this.add(pane, BorderLayout.CENTER);
		this.add(this.createPanelForChatInput(),BorderLayout.PAGE_END);
		this.add(Box.createVerticalStrut(30), BorderLayout.PAGE_START);
		this.add(Box.createHorizontalStrut(50), BorderLayout.LINE_START);
		this.add(Box.createHorizontalStrut(50), BorderLayout.LINE_END);
		
		this.revalidate();
		parent.revalidate();
		new Thread(() -> {
			for (int i = 0; i < 10000; i++) {
				LeftPanel.this.appendChat("TestString " + i + "\n");
			}
		}).start();		
		this.revalidate();
		parent.revalidate();
	}
	
	//TODO: fix resize bug -.-'
	private JPanel createPanelForChatInput(){
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		JTextField field = new JTextField("chat"){

			private static final long serialVersionUID = -3736750923112424111L;
			
			@Override
			public void paint(Graphics g) {				
			//	this.setPreferredSize(new Dimension(LeftPanel.this.pane.getWidth() - LeftPanel.this.but.getWidth() -3, 25));
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
			}
			
		};
		field.setFont(font);
		field.setForeground(Color.WHITE);
		field.setBorder(BorderFactory.createEmptyBorder());
		field.setOpaque(false);
		but = new JButton("SEND"){
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(blackBeauty, 0, 0, null);
				super.paint(g);
			}
		};
		but.setForeground(Color.WHITE);	
		but.setContentAreaFilled(false);
		but.setBorderPainted(true);
		but.setOpaque(false);
	//	field.setPreferredSize(new Dimension(465,25));
		
		
		panel.add(Box.createVerticalStrut(5), BorderLayout.PAGE_START);
		panel.add(Box.createVerticalStrut(5), BorderLayout.PAGE_END);
		panel.add(Box.createHorizontalStrut(50), BorderLayout.LINE_START);
		panel.add(Box.createHorizontalStrut(50), BorderLayout.LINE_END);
		
		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.LINE_AXIS ));
	//	center.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
		center.setOpaque(false);
		center.add(field);
		center.add(Box.createRigidArea(new Dimension(20,0)));
		center.add(but);
		panel.add(center, BorderLayout.CENTER);		
		return panel;
	}
	
	private void messageTyper(){
		this.chatmessage = new JTextField();
		this.chatmessage.setOpaque(false);
		this.chatmessage.setVisible(true);
	}
	
	private void scroller(){
		
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, 0.6F);
		} catch (IOException e) {
			e.printStackTrace();
		}
		textbox = new JTextArea();
		textbox.setForeground(Color.WHITE);
		textbox.setBorder(BorderFactory.createEmptyBorder());
		textbox.setLineWrap(true);
		textbox.setOpaque(false);
		textbox.setText("TeSt");		
		font = new Font("Calibri", Font.PLAIN, 20);
		textbox.setFont(font);
		pane =  new JScrollPane(textbox){
				private static final long serialVersionUID = 1L;
				
				@Override
				public void paint(Graphics g) {
					g.drawImage(blackBeauty, 0, 0, null);					
					super.paint(g);					
				}
				
			};
		pane.setOpaque(false);		
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setVisible(true);
		pane.getViewport().setOpaque(false);
		pane.getVerticalScrollBar().setOpaque(false);
	}
	
	public synchronized void appendChat(String str) {
		this.textbox.append(str);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.pane.getVerticalScrollBar().setValue(this.pane.getVerticalScrollBar().getMaximum());
	}
}
