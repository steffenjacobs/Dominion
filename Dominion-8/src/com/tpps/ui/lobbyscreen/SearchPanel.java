package com.tpps.ui.lobbyscreen;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class SearchPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private BufferedImage blackBeauty;
	private float pictureAlpha;
	
	public SearchPanel() {
		this.setLayout(null);
		this.init();
		this.repaint();
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

	public void paintComponent(Graphics g) {
//		Graphics2D g2 = (Graphics2D) g;
//
//		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pictureAlpha);
//		g2.setComposite(ac);
//		g2.drawImage(blackBeauty, 0, 0, null);
//		System.out.println("#repaint");
	}
}
