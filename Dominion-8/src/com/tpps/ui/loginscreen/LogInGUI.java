package com.tpps.ui.loginscreen;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
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

//TODO: underline Header, set Background, set transparent, map 'RETURN' key to Login

/***
 * @author nagrawal - Nishit Agrawal
 */
public class LogInGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
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

	/**
	 * constructor first call
	 */

	public LogInGUI() {
		createdComponent();
	}

	/**
	 * If new Account created, this constructor will be used by the Class
	 * CreateAccount
	 * 
	 * @param text
	 * @param password
	 */

	public LogInGUI(String text, char[] password) {
		createdComponent();
		this.userinfo.setText(text);
		this.passwordbox.setText(String.valueOf(password));
	}

	/**
	 * All components merged together
	 */

	private void createdComponent() {
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;

		loadImage();
		resizeImage();
		importFont();

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
		this.setSize(width / 4, height / 4);
		this.setLocationRelativeTo(null);
		this.setTitle("Log in !");
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		smallfont = new Font("Calibri", Font.BOLD, 19);

		description = new JLabel[2];
		panels = new JPanel[4];
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new JPanel(new FlowLayout());
		}
		// this.setContentPane(new JLabel(loading));
	}

	/**
	 * importing font from resources
	 */

	private void importFont() {

		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT,
					ClassLoader.getSystemResourceAsStream("resources/font/xenippa1.TTF"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
					ClassLoader.getSystemResourceAsStream("resources/font/xenippa1.TTF")));
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * loading an image from resources
	 */

	private void loadImage() {
		try {
			this.background = ImageIO
					.read(ClassLoader.getSystemResource("resources/img/loginScreen/LoginBackground.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * resizing image according to the window.
	 */

	private void resizeImage() {
		loading = new ImageIcon(background);
		Image newing = background.getScaledInstance(width / 4, height / 4, java.awt.Image.SCALE_SMOOTH);
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
		userinfo = new JTextField();

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
		passwordbox = new JPasswordField();
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

		execute = new JButton("Login");
		execute.setFont(customFont.deriveFont(15f));
		execute.setPreferredSize(new Dimension(120, 30));
		cancel = new JButton("Cancel");
		cancel.setFont(customFont.deriveFont(15f));
		createAccount = new JButton("New Account");
		createAccount.setFont(customFont.deriveFont(10f));
		cancel.setPreferredSize(new Dimension(120, 30));
		createAccount.setPreferredSize(new Dimension(120, 30));
		panels[3].add(execute);
		panels[3].add(createAccount);
		panels[3].add(cancel);
		panels[3].setOpaque(false);
		all.add(panels[3]);
		c.add(all);
		panels[3].revalidate();

		createAccount.addActionListener(new LoginListener(createAccount, this, userinfo, passwordbox));
		cancel.addActionListener(new LoginListener(cancel, this, userinfo, passwordbox));
		execute.addActionListener(new LoginListener(execute, this, userinfo, passwordbox));
	}

	/**
	 * a testing main
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		new LogInGUI();
	}
}
