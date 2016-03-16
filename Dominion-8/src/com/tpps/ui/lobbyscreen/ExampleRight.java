package com.tpps.ui.lobbyscreen;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class ExampleRight extends JPanel{

	private static final long serialVersionUID = 1L;
	JTextField[] names = new JTextField[4];
	Example parent;
	
	public ExampleRight(Example parent) {
		this.parent = parent;
		this.setOpaque(false);
//		this.setLayout(new GridLayout(3,1, 0, 0));
//		this.add(this.firstPanel());
//		this.add(this.firstPanel());
//		this.add(this.firstPanel());
//		parent.revalidate();
//		parent.repaint();
	}
	
	private JPanel firstPanel(){
		JPanel panel = new JPanel(new GridLayout(4,1));
		
		for (int i = 0; i < names.length; i++) {
			names[i] = new JTextField("Loading :D");
		}
		
		panel.add(names[0]);
		panel.add(names[1]);
		panel.add(names[2]);	
		panel.add(names[3]);
		return panel;
		
	}
}
