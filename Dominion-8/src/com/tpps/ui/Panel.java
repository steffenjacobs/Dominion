package com.tpps.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import com.tpps.ui.components.MainMenuButton;

public class Panel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private BufferedImage background;
	private float[] alpha;
	private MainMenuButton[] buttons;	
	
	public Panel(BufferedImage background, float[] alpha, MainMenuButton[] buttons) {
		 
		this.background = background;
		this.buttons = buttons;
		this.alpha = alpha;
		
	}
	@Override
	public void paint(Graphics g) {
		
	
			g.drawImage(background, 0, 0, null);
		

		
		Graphics2D g2 = (Graphics2D) g;
		
		for (int i = 0; i < alpha.length; i++) {
			
		
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[i]); 
		g2.setComposite(ac);
		
		
		g2.drawImage(buttons[i].getSourceImage(), buttons[i].getX(), buttons[i].getY(), null);
	}

}
}
