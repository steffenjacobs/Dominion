package com.tpps.ui.loginscreen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * 
 * @author Nishit Agrawal - nagrawal
 *
 */

public class LoginListener implements MouseListener {

	private JButton clicked;

	private JTextField userinfo;
	private JPasswordField passwordbox;
	private LoginGUIController guicontroller;

	/**
	 * simple constructor initialize all parameters
	 * 
	 * @param clicked
	 * @param logInGUI
	 * @param userinfo
	 * @param passwordbox
	 */

	public LoginListener(JButton clicked, JTextField userinfo, JPasswordField passwordbox,
			LoginGUIController guicontroller, JButton cancel, JButton createAccount) {
		this.clicked = clicked;
		this.userinfo = userinfo;
		this.passwordbox = passwordbox;
		this.guicontroller = guicontroller;
		userinfo.requestFocus();
		passwordbox.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					guicontroller.createLoginClient(userinfo.getText(), String.valueOf(passwordbox.getPassword()));
				}
			}
		});
	}

	/**
	 * action performed when clicked on a specific button
	 * 
	 * @param e
	 */

	@Override
	public void mouseClicked(MouseEvent e) {
		if (clicked.getText().equals("New Account")) {
			guicontroller.createAccountGUI();
		} else if (clicked.getText().equals("Cancel")) {
			System.exit(0);
		} else if (clicked.getText().equals("Login")) {
			// -------------------------------
			guicontroller.createLoginClient(userinfo.getText(), String.valueOf(passwordbox.getPassword()));
			// -------------------------------
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (clicked.getText().equals("New Account")) {
			guicontroller.createAccountGUI();
		} else if (clicked.getText().equals("Cancel")) {
			System.exit(0);
		} else if (clicked.getText().equals("Login")) {
			// -------------------------------
			guicontroller.createLoginClient(userinfo.getText(), String.valueOf(passwordbox.getPassword()));
			// -------------------------------
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (clicked.getText().equals("New Account")) {
			clicked.setOpaque(false);
			clicked.setForeground(Color.GRAY);
			clicked.setBorderPainted(true);
			clicked.setContentAreaFilled(false);
		} else if (clicked.getText().equals("Cancel")) {
			clicked.setOpaque(false);
			clicked.setForeground(Color.GRAY);
			clicked.setBorderPainted(true);
			clicked.setContentAreaFilled(false);
		} else if (clicked.getText().equals("Login")) {
			clicked.setOpaque(false);
			clicked.setForeground(Color.GRAY);
			clicked.setBorderPainted(true);
			clicked.setContentAreaFilled(false);
		}

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (clicked.getText().equals("New Account")) {
			clicked.setOpaque(false);
			if (System.getProperty("os.name").startsWith("Windows"))
				clicked.setForeground(Color.WHITE);
			else
				clicked.setForeground(Color.BLACK);
			clicked.setBorderPainted(true);
			clicked.setContentAreaFilled(false);
		} else if (clicked.getText().equals("Cancel")) {
			clicked.setOpaque(false);
			if (System.getProperty("os.name").startsWith("Windows"))
				clicked.setForeground(Color.WHITE);
			else
				clicked.setForeground(Color.BLACK);
			clicked.setBorderPainted(true);
			clicked.setContentAreaFilled(false);
		} else if (clicked.getText().equals("Login")) {
			clicked.setOpaque(false);
			if (System.getProperty("os.name").startsWith("Windows"))
				clicked.setForeground(Color.WHITE);
			else
				clicked.setForeground(Color.BLACK);
			clicked.setBorderPainted(true);
			clicked.setContentAreaFilled(false);
		}
	}
}
