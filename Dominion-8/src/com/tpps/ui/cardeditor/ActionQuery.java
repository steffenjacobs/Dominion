package com.tpps.ui.cardeditor;

import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ActionQuery extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int width,height;
	private Container c2;
	private String rbs1,rbs2,rbs3;
	private JComboBox cb1,cb2,cb3;
	private JButton okbutton;
	
	
	

	public ActionQuery(String [] Radiostrings){
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setVisible(true);
		this.setSize(width / 8, (height / 5));
		this.setLocationRelativeTo(null);
		this.setTitle("Choose Values");
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		c2 = this.getContentPane();
		iniateGUI();
	}
	
	public void iniateGUI() {
		c2.setLayout(new GridLayout(3,1,30,10));
		JPanel labels = new JPanel();
//		rbs1 = CardEditor.
		c2.add(labels);
		JPanel checkboxes = new JPanel();
		checkboxes.setLayout(new FlowLayout(5,30,5));
		String actionBoxListe[] = {"1", "2", "3", "4"}; 
		cb1 = new JComboBox(actionBoxListe);
		cb2 = new JComboBox(actionBoxListe);
		cb3 = new JComboBox(actionBoxListe);
		checkboxes.add(cb1);
		checkboxes.add(cb2);
		checkboxes.add(cb3);
		c2.add(checkboxes);
		JPanel button = new JPanel();
		okbutton = new JButton("Confirm");
		button.add(okbutton);
		c2.add(button);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ActionQuery(args).setVisible(true);
	}

}
