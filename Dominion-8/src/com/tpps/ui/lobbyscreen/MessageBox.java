package com.tpps.ui.lobbyscreen;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MessageBox extends JTextField implements FocusListener{

	private static final long serialVersionUID = 1L;
	private Font font;
	private static int FONT_SIZE = 20;
	ChatPanel chatpanel;
	
	public MessageBox(ChatPanel chatpanel){
		this.chatpanel = chatpanel;
		font = new Font("TimesRoman", Font.PLAIN, FONT_SIZE);
		this.setFont(font);
		this.setOpaque(false);
		this.setSize(400, 30);
		this.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
		
		this.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) { }

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				chatpanel.getParent().repaint();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				chatpanel.getParent().repaint();
			}
		});
	}
	
	@Override
	public void focusGained(FocusEvent arg0) {
		System.out.println("Focus Gained");
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		System.out.println("Focus Lost");
		
	}

}
