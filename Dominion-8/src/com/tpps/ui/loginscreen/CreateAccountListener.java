package com.tpps.ui.loginscreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import com.tpps.application.network.login.client.LoginClient;


/**
 * 
 * @author Nishit Agrawal - nagrawal
 *
 */
public class CreateAccountListener implements ActionListener {
	CreateAccount ca;
	
	/**
	 * simple constructor initializing a parameter
	 * @param createAccount
	 */
			
	public CreateAccountListener(CreateAccount createAccount) {
		this.ca = createAccount;
	}
	
	/**
	 * 
	 * listener method, checking validity of password,email and user-name. Creating new Account as well. 
	 * @param e
	 */
	
	public void actionPerformed(ActionEvent e) {

		if (!(String.valueOf(ca.getPasswordbox().getPassword())
				.equals(String.valueOf(ca.getPasswordboxRetype().getPassword())))) {
			JOptionPane.showMessageDialog(null, "Password does not match. Please recheck");
			return;
		}
		if (ca.getPasswordbox().getPassword().length == 0) {
			JOptionPane.showMessageDialog(null, "Password is empty. Please recheck");
			return;
		}

		if (!(ca.getEmail().getText()
				.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"))
				|| ca.getEmail().getText().length() > 256) {
			JOptionPane.showMessageDialog(null, "Email invalid. Please recheck");
			return;
		}
		if (ca.getDescribing().getText().length() > 40) {
			JOptionPane.showMessageDialog(null, "Your Description is too long");
			return;
		}

		if (ca.getUsername().getText().length() < 3 || ca.getUsername().getText().length() > 24) {
			JOptionPane.showMessageDialog(null, "Your Username is too short or too long");
			return;
		}

		// --------------------------------------

		new LoginClient().handleAccountCreation(ca.getUsername().getText(), String.valueOf(ca.getPasswordbox().getPassword()),
				ca.getEmail().getText());
		// --------------------------------------

		new LogInGUI(ca.getUsername().getText(), ca.getPasswordbox().getPassword());
		ca.dispose();
	}

}
