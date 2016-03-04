package com.tpps.ui.loginscreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * 
 * @author Nishit Agrawal - nagrawal
 *
 */

public class LoginListener implements ActionListener {

	JButton clicked;
	LogInGUI lg;
	JTextField userinfo;
	JPasswordField passwordbox;
	LoginGUIController guicontroller;
	
	/**
	 * simple constructor initialize all parameters
	 * @param clicked
	 * @param logInGUI
	 * @param userinfo
	 * @param passwordbox
	 */
	
	public LoginListener(JButton clicked, LogInGUI logInGUI, JTextField userinfo, JPasswordField passwordbox, LoginGUIController guicontroller) {
		this.clicked = clicked;
		lg = logInGUI;
		this.userinfo = userinfo;
		this.passwordbox = passwordbox;
		this.guicontroller = guicontroller;
	}
	
	/**
	 * action performed when clicked on a specific button
	 * @param e
	 */

	public void actionPerformed(ActionEvent e) {
		if (clicked.getText().equals("New Account")) {
			guicontroller.createAccountGUI();
		}
		else if(clicked.getText().equals("Cancel")){
			System.exit(0);
		}
		
		// for Johannes and his further works..
		else if(clicked.getText().equals("Login")){
			//-------------------------------
		//	new LoginClient().handlelogin(userinfo.getText(), String.valueOf(passwordbox.getPassword()));
			guicontroller.createLoginClient(userinfo.getText(), String.valueOf(passwordbox.getPassword()));
			
			//-------------------------------
		}
	}

}
