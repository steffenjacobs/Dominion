package com.tpps.ui.loginscreen;

import java.util.UUID;

import javax.swing.JOptionPane;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.network.login.client.LoginClient;
import com.tpps.ui.MainFrame;

/**
 * Tis class is used as an interface between LoginGUI and 
 * @author jhuhn - Johannes Huhn
 */
public class LoginGUIController{
	
	private LogInGUI logingui;
	private CreateAccount createaccount;
	private LoginClient loginclient;
	
	/**
	 * Initializes this object
	 * @author jhuhn - Johannes Huhn
	 */
	public LoginGUIController(){
		logingui = new LogInGUI(this);
		loginclient = new LoginClient(this);
	}
	
	/**
	 * This method sends a packet to the LoginServer to validate the accountinformation.
	 * Initialize the the LoginClient
	 * @author jhuhn - Johannes Huhn
	 * @param nickname a String representation of the accountname
	 * @param plaintext a String representation as a plaintext of the password
	 */
	public void createLoginClient(String nickname, String plaintext){
		loginclient.handlelogin(nickname, plaintext);
	}
	
	/**
	 * This method closes the Loginwindow and opens the window for accountcreation
	 * @author jhuhn - Johannes Huhn
	 */
	public void createAccountGUI(){
		this.logingui.dispose();
		createaccount = new CreateAccount(this);
	}
	
	public void createAccountWithServer(String username, String plaintext, String email){		
		loginclient.handleAccountCreation(username, plaintext, email);
	}
	
	/**
	 * This method opens a small messagebox (JOptionPane) which contains the state of your Accountcreation
	 * @author jhuhn - Johannes Huhn
	 * @param state an Integer which represents the status quo of the accountcreation
	 * 			1 account creates successfully
	 * 			2 the desired nickname is already in use
	 * 			3 the desired email adress is already in use
	 * @param nickname a String representation of the desires user account
	 * @param plaintext a String representation as plaintext of the password
	 */
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
	
	/**
	 * This method opens a small messagebox(JOptionPane)  which contains the state of your Login request.
	 * @author jhuhn - Johannes Huhn
	 * @param state true if your login request ellaborated correctly, false else
	 */
	public void getStateOfLoginRequest(int state){
		if (state ==1) { // logged in successfully
		//	JOptionPane.showMessageDialog(null, "You logged in successfully", "Login", JOptionPane.INFORMATION_MESSAGE);
			this.logingui.dispose();
			DominionController.getInstance().setUsername(this.loginclient.getUsername());
			DominionController.getInstance().endLogin();			
		} else if(state == 2) {
			//already logged in
			JOptionPane.showMessageDialog(null, "Already logged in", "Login", JOptionPane.ERROR_MESSAGE);
		}
		else{//login request failed 
			JOptionPane.showMessageDialog(null, "Wrong Password or nickname", "Login", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void main(String[] args) {
		new LoginGUIController();
	}
	
	public LoginClient getLoginclient() {
		return loginclient;
	}
	
	public UUID getUUID(){
		return this.loginclient.getSessionid();
	}
}
