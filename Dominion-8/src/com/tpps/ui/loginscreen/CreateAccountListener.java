package com.tpps.ui.loginscreen;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

		if (ca.getCancel().equals(e.getSource())) {
			this.guicontroller.backtoLogin();
			return;
		}

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

		if (CharMatcher.WHITESPACE.matchesAnyOf(ca.getUsername().getText())) {
			JOptionPane.showMessageDialog(null, "Username has Whitespaces");
			return;
		}

		if (!(ca.getUsername().getText().matches("^[_a-zA-Z0-9]+$"))) {
			JOptionPane.showMessageDialog(null, "Username doesn't match pattern");
			return;
		}

		if (ca.getUsername().getText().length() < 3 || ca.getUsername().getText().length() > 24) {
			JOptionPane.showMessageDialog(null, "Your Username is too short or too long");
			return;
		}

		// --------------------------------------
		guicontroller.createAccountWithServer(ca.getUsername().getText(),
				String.valueOf(ca.getPasswordbox().getPassword()), ca.getEmail().getText());
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (ca.getCancel().equals(e.getSource())) {
			this.guicontroller.backtoLogin();
			return;
		}

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

		if (CharMatcher.WHITESPACE.matchesAnyOf(ca.getUsername().getText())) {
			JOptionPane.showMessageDialog(null, "Your Username consists of Whitespaces");
			return;
		}

		if (!(ca.getUsername().getText().matches("^[_a-zA-Z0-9]+$"))) {
			JOptionPane.showMessageDialog(null, "Username does't match the pattern");
			return;
		}

		if (ca.getUsername().getText().length() < 3 || ca.getUsername().getText().length() > 24) {
			JOptionPane.showMessageDialog(null, "Your Username is too short or too long");
			return;
		}

		// --------------------------------------
		else if (ca.getCreateAccount().getText().equals("Create New Account")) {
			guicontroller.createAccountWithServer(ca.getUsername().getText(),
					String.valueOf(ca.getPasswordbox().getPassword()), ca.getEmail().getText());
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (ca.getCreateAccount().equals(e.getSource())) {
			ca.getCreateAccount().setOpaque(false);
			ca.getCreateAccount().setForeground(Color.GRAY);
			ca.getCreateAccount().setBorderPainted(true);
			ca.getCreateAccount().setContentAreaFilled(false);
		} else {
			ca.getCancel().setOpaque(false);
			ca.getCancel().setForeground(Color.GRAY);
			ca.getCancel().setBorderPainted(true);
			ca.getCancel().setContentAreaFilled(false);
		}

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (ca.getCreateAccount().equals(e.getSource())) {
			ca.getCreateAccount().setOpaque(false);
			ca.getCreateAccount().setForeground(textAndLabelColor);
			ca.getCreateAccount().setBorderPainted(true);
			ca.getCreateAccount().setContentAreaFilled(false);
		} else {
			ca.getCancel().setOpaque(false);
			ca.getCancel().setForeground(textAndLabelColor);
			ca.getCancel().setBorderPainted(true);
			ca.getCancel().setContentAreaFilled(false);
		}
	}
}
