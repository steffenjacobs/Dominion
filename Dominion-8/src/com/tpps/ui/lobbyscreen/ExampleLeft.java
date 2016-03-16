package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
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

import com.tpps.technicalServices.util.GraphicsUtil;

public class ExampleLeft extends JPanel{
	
	JTextArea textbox;
	JScrollPane pane;
	JTextField chatmessage;
	Example parent;
	BufferedImage blackBeauty;
	Font font = new Font("Calibri", Font.PLAIN, 20);
	JButton but;
	
	private static final long serialVersionUID = 1L;
	
	public ExampleLeft(Example parent) {
		this.parent = parent;
		this.setVisible(true);
		this.setOpaque(false);
		BorderLayout layout = new BorderLayout();
		layout.setHgap(50);
	//	layout.setVgap();
		this.setLayout(layout);
		
		this.scroller();
		this.messageTyper();
		
		this.add(pane, BorderLayout.CENTER);
		this.add(this.createPanelForChatInput(),BorderLayout.PAGE_END);
		this.add(Box.createVerticalStrut(30), BorderLayout.PAGE_START);
		this.add(new JLabel(), BorderLayout.LINE_START);
		this.add(new JLabel(), BorderLayout.LINE_END);
		
		this.revalidate();
		parent.revalidate();
		new Thread(() -> {
			for (int i = 0; i < 10000; i++) {
				ExampleLeft.this.appendChat("TestString " + i + "\n");
			}
		}).start();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.revalidate();
		parent.revalidate();
	}
	
	/*works shit */
//	private JPanel createPanelForChatInput(){
//		JPanel panel = new JPanel(new FlowLayout());
//		panel.setOpaque(false);
//		JTextField field = new JTextField("chat");
//		field.setFont(font);
//		field.setOpaque(false);
//		JButton but = new JButton("SEND");
//		field.setPreferredSize(new Dimension(500,25));
//		panel.add(field);
//		panel.add(but);
//		return panel;
//	}
	
	private JPanel createPanelForChatInput(){
		BorderLayout layout = new BorderLayout();
	//	layout.setHgap(50);
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		JTextField field = new JTextField("chat"){

			private static final long serialVersionUID = -3736750923112424111L;
			
			@Override
			public void paint(Graphics g) {
			//	System.out.println("field paint");
			this.setPreferredSize(new Dimension(ExampleLeft.this.pane.getWidth() - ExampleLeft.this.but.getWidth() -3, 25));
			System.out.println(but.getSize());
				super.paint(g);
			}
			
		};
		field.setFont(font);
		field.setOpaque(false);
		but = new JButton("SEND");		
		field.setPreferredSize(new Dimension(465,25));
		panel.add(field, BorderLayout.CENTER);
		panel.add(but, BorderLayout.CENTER);		
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
//		pane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
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
