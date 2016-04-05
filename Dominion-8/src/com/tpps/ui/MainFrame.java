package com.tpps.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private final Container c;
//	private  MainMenuPanel panel;
	private JPanel panel;

	/**
	 * Contructor for the mainMenu
	 */
	public MainFrame() {
		this.c = this.getContentPane();
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//this.fullscreenmode();
		
		this.setMinimumSize(new Dimension(1280, 720));		
	}
	
	private void fullscreenmode(){
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		this.setUndecorated(true);
	}
	
	public void setPanel(JPanel panel){
		if(this.panel != null){		
			c.remove(this.panel);
		}
			this.panel = panel;
			c.add(this.panel);
			this.revalidate();
			this.repaint();	
	}

	public static void main(String[] args) {
		MainFrame menu = new MainFrame();
		menu.setVisible(true);
	}
}
