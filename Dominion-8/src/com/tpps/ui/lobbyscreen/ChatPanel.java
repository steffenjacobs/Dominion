package com.tpps.ui.lobbyscreen;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ChatPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private int CHATWIDTH, CHATHEIGHT;
	private BufferedImage blackBeauty;
	private float pictureAlpha;
	private JTextArea chatwindow;
	
	public ChatPanel() {
		this.init();
		this.initChatBox();
		this.repaint();
	}
	
	private void initChatBox(){
		this.CHATWIDTH = (int) (this.WIDTH *0.9);
		this.CHATHEIGHT = (int) (this.HEIGHT *0.9);
		this.chatwindow = new JTextArea("Hallihallo");
		this.chatwindow.setPreferredSize(new Dimension(CHATWIDTH, CHATHEIGHT));
		this.chatwindow.setSize(new Dimension(CHATWIDTH, CHATHEIGHT));
		this.add(chatwindow);
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
	}	

}
