package com.tpps.ui.loginscreen;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.tpps.technicalServices.util.FontLoader;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.technicalServices.util.ImageLoader;

//TODO: underline Header, set Background, set transparent, map 'RETURN' key to Login

/***
 * @author nagrawal - Nishit Agrawal
 */
public class LogInGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private Container c;
	private JButton execute;

	private JButton cancel;
	private ImageIcon loading;
	private BufferedImage background;
	private JButton createAccount;

	private JTextField userinfo;
	private JPasswordField passwordbox;
	private JLabel[] description;
	private JLabel header;
	private JLabel all;
	private JPanel[] panels;
	private Font smallfont, customFont;
	private LoginGUIController guicontroller;
//	private BufferedImage blackBeauty;
//	private BufferedImage walterWhite;

	private static final int FRAME_X = 500;
	private static final int FRAME_Y = 250;

	/**
	 * constructor first call
	 * 
	 * @param loginGUIController
	 */

	public LogInGUI(LoginGUIController loginGUIController) {
		this.guicontroller = loginGUIController;
		createdComponent();
	}

	public LogInGUI(LoginGUIController loginGUIController, String username, String plaintext) {
		this.guicontroller = loginGUIController;
		createdComponent();
		this.userinfo.setText(username);
		this.passwordbox.setText(plaintext);
	}

	/**
	 * All components merged together
	 */

	private void createdComponent() {

		loadImage();
		resizeImage();
		try {
			if (customFont == null) {
				customFont = FontLoader.getInstance().getXenipa();
				if (customFont == null) {
					customFont = new FontLoader().importFont();
				}
			}
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		this.init();
		this.createpanel1();
		this.createpanel2();
		this.createpanel3();
		this.createpanel4();
		this.revalidate();
	}

	/**
	 * A simple initialize method to set specific Frame.
	 */

	private void init() {
		c = this.getContentPane();
		all = new JLabel(loading);
		all.setLayout(new GridLayout(4, 1, 0, 30));
		this.setSize(FRAME_X, FRAME_Y);
		this.setLocationRelativeTo(null);
		this.setTitle("LogIn");
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		smallfont = new Font("Calibri", Font.BOLD, 19);
		try {
			this.setIconImage((ImageIO.read(ClassLoader.getSystemResource("resources/img/loginScreen/Icon.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		description = new JLabel[2];
		panels = new JPanel[4];
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new JPanel(new FlowLayout());
		}
		// this.setContentPane(new JLabel(loading));
	}

	/**
	 * loading an image from resources
	 */

	private void loadImage() {
		this.background = GraphicsUtil.resize(ImageLoader.getImage("resources/img/loginScreen/LoginBackground.jpg"), FRAME_X, FRAME_Y);

//		try {
//			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
//			blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, 0.4F);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		try {
//			this.walterWhite = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/walterWhite.jpg"));
//			walterWhite = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, 0.4F);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * resizing image according to the window.
	 */
	private void resizeImage() {
		loading = new ImageIcon(background);
		Image newing = background.getScaledInstance(FRAME_X, FRAME_Y, java.awt.Image.SCALE_SMOOTH);
		loading = new ImageIcon(newing);
	}

	/**
	 * first panel creation - Header
	 */
	private void createpanel1() {
		header = new JLabel();
		header.setText("Type in Userinformation");
		header.setFont(customFont.deriveFont(25f));
		header.setForeground(Color.WHITE);

		panels[0].add(header);
		panels[0].setOpaque(false);
		// panels[0].setBorder(BorderFactory.createLineBorder(Color.GREEN, 4));
		all.add(panels[0]);
		// c.add(panels[0]);
	}

	/**
	 * second panel creation - registered UserID
	 */

	private void createpanel2() {
		panels[1].setLayout(new GridLayout(1, 2));

		description[0] = new JLabel("Accountname: ");
		description[0].setFont(smallfont);
		description[0].setHorizontalAlignment(JLabel.CENTER);
		userinfo = new JTextField() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.4"), 0, 0, null);
				super.paint(g);
			}
		};
		if (System.getProperty("os.name").startsWith("Windows")) {
			userinfo.setForeground(Color.WHITE);
		} else {
			userinfo.setForeground(Color.BLACK);
		}
		userinfo.setOpaque(false);
		userinfo.setFont(smallfont);
		panels[1].add(description[0]);
		panels[1].add(userinfo);
		panels[1].setOpaque(false);
		// panels[1].setBorder(BorderFactory.createLineBorder(Color.RED, 4));
		all.add(panels[1]);
		panels[0].revalidate();
	}

	/**
	 * third panel with password box
	 */

	private void createpanel3() {
		panels[2].setLayout(new GridLayout(1, 2));

		description[1] = new JLabel("Password: ");
		description[1].setFont(smallfont);
		description[1].setHorizontalAlignment(JLabel.CENTER);
		passwordbox = new JPasswordField() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.4"), 0, 0, null);
				super.paint(g);
			}
		};
		if (System.getProperty("os.name").startsWith("Windows"))
			passwordbox.setForeground(Color.WHITE);
		else
			passwordbox.setForeground(Color.BLACK);
		passwordbox.setOpaque(false);
		panels[2].add(description[1]);
		panels[2].add(passwordbox);
		panels[2].setOpaque(false);
		// panels[2].setBorder(BorderFactory.createLineBorder(Color.CYAN, 4));
		all.add(panels[2]);
		panels[2].revalidate();
	}

	/**
	 * fourth panel with Buttons and referring listener.
	 */

	private void createpanel4() {
		panels[3].setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
		// panels[3].setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

		execute = new JButton("Login") {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.4"), 0, 0, null);
				super.paint(g);
			}
		};
		execute.setOpaque(false);
		if (System.getProperty("os.name").startsWith("Windows"))
			execute.setForeground(Color.WHITE);
		else
			execute.setForeground(Color.BLACK);
		execute.setBorderPainted(true);
		execute.setContentAreaFilled(false);
		execute.setFont(customFont.deriveFont(15f));

		cancel = new JButton("Cancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.4"), 0, 0, null);
				super.paint(g);
			}
		};
		cancel.setOpaque(false);
		if (System.getProperty("os.name").startsWith("Windows"))
			cancel.setForeground(Color.WHITE);
		else
			cancel.setForeground(Color.BLACK);
		cancel.setBorderPainted(true);
		cancel.setContentAreaFilled(false);
		cancel.setFont(customFont.deriveFont(15f));

		createAccount = new JButton("New Account") {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.4"), 0, 0, null);
				super.paint(g);
			}
		};
		createAccount.setOpaque(false);
		if (System.getProperty("os.name").startsWith("Windows"))
			createAccount.setForeground(Color.WHITE);
		else
			createAccount.setForeground(Color.BLACK);
		createAccount.setBorderPainted(true);
		createAccount.setContentAreaFilled(false);
		createAccount.setFont(customFont.deriveFont(10f));

		cancel.setPreferredSize(new Dimension(120, 30));
		createAccount.setPreferredSize(new Dimension(120, 30));
		execute.setPreferredSize(new Dimension(120, 30));

		panels[3].add(execute);
		panels[3].add(createAccount);
		panels[3].add(cancel);
		panels[3].setOpaque(false);
		all.add(panels[3]);
		c.add(all);
		panels[3].revalidate();

		createAccount.addMouseListener(
				new LoginListener(createAccount, userinfo, passwordbox, guicontroller, cancel, createAccount));
		cancel.addMouseListener(new LoginListener(cancel, userinfo, passwordbox, guicontroller, cancel, createAccount));
		execute.addMouseListener(
				new LoginListener(execute, userinfo, passwordbox, guicontroller, cancel, createAccount));
	}
}
