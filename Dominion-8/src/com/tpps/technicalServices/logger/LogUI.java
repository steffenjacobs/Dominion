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
public class LogUI {

	private LogTextPane dis;
	private JFrame frame;

	/**
	 * @return the dis
	 */
	public LogTextPane getDis() {
		return dis;
	}

	/**
	 * @param dis the dis to set
	 */
	public void setDis(LogTextPane dis) {
		this.dis = dis;
	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * @param frame the frame to set
	 */
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * constructor for the LogUI, initializes all required settings
	 */
	public LogUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			this.dis = new LogTextPane();
			this.frame = new JFrame();
			this.frame.setResizable(false);
			this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.frame.setLayout(new BorderLayout());
			this.frame.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			this.frame.getContentPane().add(this.dis);
			this.frame.setSize(900, 400);
			this.frame.setLocationRelativeTo(null);
			this.frame.setVisible(true);
		} catch (Exception e) {
			Log.log(MsgType.EXCEPTION, e.getMessage());
		}
	}
}