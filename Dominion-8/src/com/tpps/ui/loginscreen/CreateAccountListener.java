package com.tpps.ui.loginscreen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;

import com.google.common.base.CharMatcher;

/**
 * 
 * @author Nishit Agrawal - nagrawal
 *
 */
public class CreateAccountListener implements MouseListener {
	CreateAccount ca;
	LoginGUIController guicontroller;
	Color textAndLabelColor;

	/**
	 * simple constructor initializing a parameter
	 * 
	 * @param createAccount
	 */

	public CreateAccountListener(CreateAccount createAccount, LoginGUIController guicontroller) {
		this.ca = createAccount;
		this.guicontroller = guicontroller;
		this.textAndLabelColor = System.getProperty("os.name").startsWith("Windows") ? Color.WHITE : Color.BLACK;
	}

	/**
	 * 
	 * listener method, checking validity of password,email and user-name.
	 * Creating new Account as well.
	 * 
	 * @param e
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		
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
		// if (ca.getDescribing().getText().length() > 40) {
		// JOptionPane.showMessageDialog(null, "Your Description is too long");
		// return;
		// }

		if (CharMatcher.WHITESPACE.matchesAnyOf(ca.getUsername().getText())) {
			JOptionPane.showMessageDialog(null, "Username not allowed");
			return;
		}
		
		if(!(ca.getUsername().getText().matches("^[_a-zA-Z0-9]+$"))){
			JOptionPane.showMessageDialog(null, "Username not allowed");
			return;
		}

		if (ca.getUsername().getText().length() < 3 || ca.getUsername().getText().length() > 24) {
			JOptionPane.showMessageDialog(null, "Your Username is too short or too long");
			return;
		}

		// --------------------------------------
		guicontroller.createAccountWithServer(ca.getUsername().getText(),
				String.valueOf(ca.getPasswordbox().getPassword()), ca.getEmail().getText());
				// new
				// LoginClient().handleAccountCreation(ca.getUsername().getText(),
				// String.valueOf(ca.getPasswordbox().getPassword()),ca.getEmail().getText());
				// --------------------------------------

		// new LogInGUI(ca.getUsername().getText(),
		// ca.getPasswordbox().getPassword());
		// ca.dispose();
	}

	@Override
	public void mousePressed(MouseEvent e) {

		
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
		// if (ca.getDescribing().getText().length() > 40) {
		// JOptionPane.showMessageDialog(null, "Your Description is too long");
		// return;
		// }

		if (CharMatcher.WHITESPACE.matchesAnyOf(ca.getUsername().getText())) {
			JOptionPane.showMessageDialog(null, "Username not allowed");
			return;
		}
		
		if(!(ca.getUsername().getText().matches("^[_a-zA-Z0-9]+$"))){
			JOptionPane.showMessageDialog(null, "Username not allowed");
			return;
		}

		if (ca.getUsername().getText().length() < 3 || ca.getUsername().getText().length() > 24) {
			JOptionPane.showMessageDialog(null, "Your Username is too short or too long");
			return;
		}

		// --------------------------------------
		guicontroller.createAccountWithServer(ca.getUsername().getText(),
				String.valueOf(ca.getPasswordbox().getPassword()), ca.getEmail().getText());
				// new
				// LoginClient().handleAccountCreation(ca.getUsername().getText(),
				// String.valueOf(ca.getPasswordbox().getPassword()),ca.getEmail().getText());
				// --------------------------------------

		// new LogInGUI(ca.getUsername().getText(),
		// ca.getPasswordbox().getPassword());
		// ca.dispose();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {	
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		ca.getCreateAccount().setOpaque(false);
		ca.getCreateAccount().setForeground(Color.GRAY);
		ca.getCreateAccount().setBorderPainted(true);
		ca.getCreateAccount().setContentAreaFilled(false);
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		ca.getCreateAccount().setOpaque(false);
		ca.getCreateAccount().setForeground(textAndLabelColor);
		ca.getCreateAccount().setBorderPainted(true);
		ca.getCreateAccount().setContentAreaFilled(false);	
	}
}
