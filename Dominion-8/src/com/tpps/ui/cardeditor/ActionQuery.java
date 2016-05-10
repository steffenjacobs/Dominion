package com.tpps.ui.cardeditor;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ActionQuery extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int width,height;
	private Container c2;
	private String rbs1,rbs2,rbs3;
	private JLabel lb1,lb2,lb3;
	private JComboBox cb1,cb2,cb3;
	private JButton okbutton;
	private ArrayList Namelist;
	
	/**
	 * 
	 * @param radioButtons
	 */

	
	public ActionQuery(ArrayList<String> radioButtons){
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setVisible(true);
		this.setSize(width / 6, (height / 5));
		this.setLocationRelativeTo(null);
		this.setTitle("Choose Values");
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		c2 = this.getContentPane();
		System.out.println(radioButtons);
		rbs1 = radioButtons.get(0);
		if (radioButtons.get(1) != null)
		rbs2 = radioButtons.get(1);
		else
		rbs2 = "";
		if (radioButtons.get(2) != null)
		rbs3 = radioButtons.get(2);
		else 
	    rbs3 = "";
		
		iniateGUI();
		
	}
	
	public void iniateGUI() {
		c2.setLayout(new GridLayout(3,1,30,10));
		JPanel labels = new JPanel();
		labels.setLayout(new FlowLayout(10,20,5));
        lb1 = new JLabel(rbs1);
        lb2 = new JLabel(rbs2);
        lb3 = new JLabel(rbs3);
        System.out.println(rbs1);
        labels.add(lb1);
        labels.add(lb2);
        labels.add(lb3);
		c2.add(labels);
		JPanel checkboxes = new JPanel();
		checkboxes.setLayout(new FlowLayout(40,50,5));
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
//		new ActionQuery(args).setVisible(true);
	}

}
