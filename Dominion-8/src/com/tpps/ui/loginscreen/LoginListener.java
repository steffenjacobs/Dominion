package com.tpps.ui.loginscreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.tpps.application.network.login.client.LoginClient;

public class LoginListener implements ActionListener {

	JButton clicked;
	LogInGUI lg;
	JTextField userinfo;
	JPasswordField passwordbox;
	
	public LoginListener(JButton clicked, LogInGUI logInGUI, JTextField userinfo, JPasswordField passwordbox) {
		this.clicked = clicked;
		lg = logInGUI;
		this.userinfo = userinfo;
		this.passwordbox = passwordbox;
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
			//-------------------------------
			new LoginClient().handlelogin(userinfo.getText(), String.valueOf(passwordbox.getPassword()));
			
			
			//-------------------------------
		}
	}

}
