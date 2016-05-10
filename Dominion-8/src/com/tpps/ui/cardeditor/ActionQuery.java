package com.tpps.ui.cardeditor;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.storage.SerializedCard;

public class ActionQuery extends JFrame implements ActionListener  {
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
	private SerializedCard newCard;
	private CardAction cactions;
	private LinkedHashMap<CardAction, String> actions;
	private LinkedList<CardType> types;
	private int cost;
	private String name,aktionswert,aktionswert2,aktionswert3;
	private BufferedImage image;
	
	/**
	 * 
	 * @param radioButtons, die ausgewählten Aktionen der Karteneditor GUI
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
		if (radioButtons.size() > 1 )
		rbs2 = radioButtons.get(1);
		else
		rbs2 = "";
		if (radioButtons.size() > 2)
		rbs3 = radioButtons.get(2);
		else 
	    rbs3 = "";
		
		iniateGUI();
		
	}
	
	public void iniateGUI() {
		c2.setLayout(new GridLayout(3,3,30,10));
		JPanel labels = new JPanel();
    	labels.setLayout(new FlowLayout(10,20,5));
        lb1 = new JLabel(rbs1);
        lb2 = new JLabel(rbs2);
        lb3 = new JLabel(rbs3);
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
		if (rbs2 != "")
		checkboxes.add(cb2);
		if (rbs3 != "")
		checkboxes.add(cb3);
		c2.add(checkboxes);
		JPanel button = new JPanel();
		okbutton = new JButton("Confirm");
		okbutton.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
			    actions = new LinkedHashMap<CardAction, String>();
			    aktionswert = (String) cb1.getSelectedItem();
			    aktionswert2 = (String) cb2.getSelectedItem();
			    aktionswert3 = (String) cb3.getSelectedItem();
				if (rbs1 == "AddAction")	
			    actions.put(CardAction.ADD_ACTION_TO_PLAYER,aktionswert);
				if (rbs1 == "addMoney")
					actions.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN,aktionswert);
				if (rbs1 == "addPurchase")
					actions.put(CardAction.ADD_PURCHASE,aktionswert);
				if (rbs1 == "drawCard")
					actions.put(CardAction.DRAW_CARD,aktionswert);
				if (rbs1 == "drawCardUntil")
					actions.put(CardAction.DRAW_CARD_UNTIL,aktionswert);
				if (rbs1 == "putBack")
					actions.put(CardAction.PUT_BACK,aktionswert);
				if (rbs1 == "gainCard")
					actions.put(CardAction.GAIN_CARD,aktionswert);
				if (rbs1 == "discardCard")
					actions.put(CardAction.DISCARD_CARD,aktionswert);
				if (rbs1 == "trashCard")				
					actions.put(CardAction.TRASH_CARD,aktionswert);
				if (rbs1 == "revealCard")
					actions.put(CardAction.REVEAL_CARD,aktionswert);
				if (rbs1 == "isTreasure")
					actions.put(CardAction.IS_TREASURE,aktionswert);
				if (rbs1 == "isVictory")
					actions.put(CardAction.IS_VICTORY,aktionswert);
				
				
				if (rbs2 != null) {
						if (rbs2 == "AddAction")	
					    actions.put(CardAction.ADD_ACTION_TO_PLAYER,aktionswert2);
						if (rbs2 == "addMoney")
							actions.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN,aktionswert2);
						if (rbs2 == "addPurchase")
							actions.put(CardAction.ADD_PURCHASE,aktionswert2);
						if (rbs2 == "drawCard")
							actions.put(CardAction.DRAW_CARD,aktionswert2);
						if (rbs2 == "drawCardUntil")
							actions.put(CardAction.DRAW_CARD_UNTIL,aktionswert2);
						if (rbs2 == "putBack")
							actions.put(CardAction.PUT_BACK,aktionswert2);
						if (rbs2 == "gainCard")
							actions.put(CardAction.GAIN_CARD,aktionswert2);
						if (rbs2 == "discardCard")
							actions.put(CardAction.DISCARD_CARD,aktionswert2);
						if (rbs2 == "trashCard")				
							actions.put(CardAction.TRASH_CARD,aktionswert2);
						if (rbs2 == "revealCard")
							actions.put(CardAction.REVEAL_CARD,aktionswert2);
						if (rbs2 == "isTreasure")
							actions.put(CardAction.IS_TREASURE,aktionswert2);
						if (rbs2 == "isVictory")
							actions.put(CardAction.IS_VICTORY,aktionswert2);
						}
				if (rbs3 != null) {
					if (rbs3 == "AddAction")	
				    actions.put(CardAction.ADD_ACTION_TO_PLAYER,aktionswert3);
					if (rbs3 == "addMoney")
						actions.put(CardAction.ADD_TEMPORARY_MONEY_FOR_TURN,aktionswert3);
					if (rbs3 == "addPurchase")
						actions.put(CardAction.ADD_PURCHASE,aktionswert3);
					if (rbs3 == "drawCard")
						actions.put(CardAction.DRAW_CARD,aktionswert3);
					if (rbs3 == "drawCardUntil")
						actions.put(CardAction.DRAW_CARD_UNTIL,aktionswert3);
					if (rbs3 == "putBack")
						actions.put(CardAction.PUT_BACK,aktionswert3);
					if (rbs3 == "gainCard")
						actions.put(CardAction.GAIN_CARD,aktionswert3);
					if (rbs3 == "discardCard")
						actions.put(CardAction.DISCARD_CARD,aktionswert3);
					if (rbs3 == "trashCard")				
						actions.put(CardAction.TRASH_CARD,aktionswert3);
					if (rbs3 == "revealCard")
						actions.put(CardAction.REVEAL_CARD,aktionswert3);
					if (rbs3 == "isTreasure")
						actions.put(CardAction.IS_TREASURE,aktionswert3);
					if (rbs3 == "isVictory")
						actions.put(CardAction.IS_VICTORY,aktionswert3);
					}
		
				newCard = new SerializedCard(actions, types, priceint, name, image);
			
			}
		});
		button.add(okbutton);
		c2.add(button);
	}
	
   private void kartenwerteladen() {
	  
   }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		new ActionQuery(args).setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
