package com.tpps.ui.loginscreen;

import javax.swing.JOptionPane;

import com.tpps.application.network.login.client.LoginClient;

public class LoginGUIController{
	
	LogInGUI logingui;
	CreateAccount createaccount;
	LoginClient loginclient;
	
	public LoginGUIController(){
		logingui = new LogInGUI(this);
	}
	
	public void createLoginClient(String nickname, String plaintext){
		loginclient = new LoginClient(this);
		loginclient.handlelogin(nickname, plaintext);
	}
	
	public void createAccountGUI(){
		this.logingui.dispose();
		createaccount = new CreateAccount(this);
	}
	
	public void createAccountWithServer(String username, String plaintext, String email){
		loginclient.handleAccountCreation(username, plaintext, email);
	}
	
	public void getStateOfAccountCreation(int state, String nickname, String plaintext){
		if(state == 1) {
			JOptionPane.showMessageDialog(null, "Account created succesfully", "Create Account", JOptionPane.INFORMATION_MESSAGE);
			this.createaccount.dispose();
			this.logingui = new LogInGUI(this, nickname, plaintext);
		} else if(state == 2) {
			JOptionPane.showMessageDialog(null, "Nickname already in use", "Create Account", JOptionPane.ERROR_MESSAGE);
		} else if(state == 3){
			JOptionPane.showMessageDialog(null, "EMAIL already in use", "Create Account", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void getStateOfLoginRequest(boolean state){
		if (state) { // Anmeldung erfolgreich, pw richtig
			JOptionPane.showMessageDialog(null, "You logged in successfully", "Login", JOptionPane.INFORMATION_MESSAGE);
		} else {// Anmeldung fehlgeschlagen, PW falsch
			JOptionPane.showMessageDialog(null, "Wrong Password or nickname", "Login", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void main(String[] args) {
		new LoginGUIController();
	}
}
