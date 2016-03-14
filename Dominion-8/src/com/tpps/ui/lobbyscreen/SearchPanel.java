package com.tpps.ui.lobbyscreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SearchPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private JTextField[] names;
	LobbyScreenPanel parent;
	
	public SearchPanel(LobbyScreenPanel parent) {
		this.parent = parent;
		this.setLayout(new GridLayout(4,1));
		this.init();
		this.repaint();
	}
	
	private void initTextFields(){
		names = new JTextField[4];
		JPanel loadings = new JPanel();
		loadings.setOpaque(false);
		loadings.setLayout(new BoxLayout(loadings, BoxLayout.PAGE_AXIS));
		for (int i = 0; i < names.length; i++) {
			names[i] = new JTextField("Loading");
			names[i].setOpaque(false);
			names[i].setForeground(Color.WHITE);
			names[i].setPreferredSize(new Dimension(100, 50));
			loadings.add(names[i]);
		}
		this.add(loadings);
		this.add(new JLabel());
		this.add(new JLabel());
		this.add(new JLabel());
	}
	
	private void init(){
		this.setVisible(true);
		this.initTextFields();
		
	}

	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, 1920, 1080);
		this.parent.paintComponent(g);
//		Graphics2D g2 = (Graphics2D) g;
//
//		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pictureAlpha);
//		g2.setComposite(ac);
//		g2.drawImage(blackBeauty, 0, 0, null);
//		System.out.println("#repaint");
	}
}
