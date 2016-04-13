package com.tpps.technicalServices.logger;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * 
 * @author Nicolas
 *
 */
public class GameLogUI {

	/**
	 * constructor for the LogUI, initializes all required settings
	 */
	public GameLogUI(GameLogTextPane pane) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			JFrame frame = new JFrame();
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			frame.getContentPane().add(pane);
			frame.setSize(900, 400);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} catch (Exception e) {
			GameLog.log(MsgType.EXCEPTION, e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		GameLog.init();
		new GameLogUI(GameLog.getTextPane());
		GameLog.log(MsgType.GAME, "abc");
	}
}