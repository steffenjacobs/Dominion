package com.tpps.ui.endscreen;

import java.awt.Color;

import javax.swing.JPanel;

public class EndPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	// ++++++++++++++++
	// TODO: bei ok button: um zur�ck zum main menu zu kommen:
	// Dominioncontroller.getInstance().joinMainMenu();
	// ++++++++++++++++
	
	public EndPanel() {
		this.setVisible(true);
		this.setBackground(Color.RED);
	}
}
