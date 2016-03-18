package com.tpps.ui.lobbyscreen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

import com.tpps.technicalServices.util.GraphicsUtil;

public class SearchingField extends JTextField implements Runnable{

	private static final long serialVersionUID = 1L;
	private BufferedImage blackBeauty;
	private final Font font = new Font("Calibri", Font.PLAIN, 20);
	boolean playerFlag;
	
	public SearchingField() {
		playerFlag = false;
		this.setFont(font);
		this.setOpaque(false);
		this.setFocusable(false);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setHorizontalAlignment(JTextField.CENTER);
		this.setForeground(Color.WHITE);
		
		
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, 0.6F);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paint(Graphics g) {							
		g.drawImage(blackBeauty, 0, 0, null);
		super.paint(g);
	}
	
	public void start(){
		new Thread(this).start();
	}

	@Override
	public void run() {
		int points = 1;
		while(!playerFlag){
			String appender = "";
			for (int j = 0; j < points; j++) {
				appender += ".";
			}
			this.setText("Loading " + appender);
			points++;
			if(points == 4){
				points = 1;
			}
			
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
