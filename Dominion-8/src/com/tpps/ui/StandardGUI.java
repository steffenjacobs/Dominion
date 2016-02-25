package com.tpps.ui;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class StandardGUI extends JFrame{

	Container c;
	JButton button;
	
	public StandardGUI(){
		c =  this.getContentPane();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(500, 300);
		this.setVisible(true);
		
		this.revalidate();
		this.repaint();
	}
	
	public static void main(String[] args) {
		new StandardGUI();
	}	
}
