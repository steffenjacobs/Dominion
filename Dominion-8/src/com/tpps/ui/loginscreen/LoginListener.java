package com.tpps.ui.loginscreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class LoginListener implements ActionListener {

	JButton clicked;
	LogInGUI lg;

	public LoginListener(JButton clicked, LogInGUI logInGUI) {
		this.clicked = clicked;
		lg = logInGUI;
	}

	public void actionPerformed(ActionEvent e) {
		if (clicked.getText().equals("New Account")) {
			lg.dispose();
			new CreateAccount();
		}
		else if(clicked.getText().equals("Cancel")){
			System.exit(0);
		}
		
		// for Johannes and his further works..
		else if(clicked.getText().equals("Login")){
			System.exit(0);
		}
	}

}
