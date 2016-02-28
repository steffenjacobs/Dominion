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
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

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

public class CreateAccount extends JFrame {

	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private Container c;
	private ImageIcon loading;
	private BufferedImage background;
	private JButton createAccount;
	private JTextField email,describing,username;
	private JPasswordField passwordbox,passwordboxRetype;
	private JLabel[] description;
	private JLabel header;
	private JLabel all;
	private JPanel[] panels;
	private Font smallfont, customFont;

	public CreateAccount() {
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
		this.createpanel5();
		this.createpanel6();
		this.createpanel7();
		this.revalidate();
	}

	private void init() {
		c = this.getContentPane();
		all = new JLabel(loading);
		all.setLayout(new GridLayout(7, 1, 0, 30));
		this.setSize(width / 4, height / 2);
		this.setLocation((int)(width*0.65), height/4);
		this.setTitle("Create Account");
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		smallfont = new Font("Calibri", Font.BOLD, 19);

		description = new JLabel[5];
		panels = new JPanel[7];
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new JPanel(new FlowLayout());
		}
		// this.setContentPane(new JLabel(loading));
	}

	// not working why? Path checking!!
	private void importFont() {
		try {
			customFont = Font
					.createFont(Font.TRUETYPE_FONT,
							new File(URLDecoder.decode(ClassLoader.getSystemResource("resources/font/xenippa1.TTF").getFile(),"UTF-8")));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			// ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new
			// File("src/resources/font/xenippa1.TTF")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
					new File(URLDecoder.decode(ClassLoader.getSystemResource("resources/font/xenippa1.TTF").getFile(),"UTF-8"))));
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private void loadImage() {
		try {
			this.background = ImageIO
					.read(ClassLoader.getSystemResource("resources/img/loginScreen/LoginBackground.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void resizeImage() {
		loading = new ImageIcon(background);
		Image newing = background.getScaledInstance(width / 4, height / 2, java.awt.Image.SCALE_SMOOTH);
		loading = new ImageIcon(newing);
	}

	private void createpanel1() {
		header = new JLabel();
		header.setText("Create Account");
		header.setFont(customFont.deriveFont(25f));
		header.setForeground(Color.WHITE);

		panels[0].add(header);
		panels[0].setOpaque(false);
		// panels[0].setBorder(BorderFactory.createLineBorder(Color.GREEN, 4));
		all.add(panels[0]);
		// c.add(panels[0]);
	}

	private void createpanel2() {
		panels[1].setLayout(new GridLayout(1, 2));

		description[0] = new JLabel("Email: ");
		description[0].setFont(smallfont);
		description[0].setHorizontalAlignment(JLabel.CENTER);
		email = new JTextField();

		email.setOpaque(false);
		email.setFont(smallfont);
		panels[1].add(description[0]);
		panels[1].add(email);
		panels[1].setOpaque(false);
		// panels[1].setBorder(BorderFactory.createLineBorder(Color.RED, 4));
		all.add(panels[1]);
		panels[0].revalidate();
	}
	private void createpanel3() {
		panels[2].setLayout(new GridLayout(1, 2));

		description[1] = new JLabel("Username: ");
		description[1].setFont(smallfont);
		description[1].setHorizontalAlignment(JLabel.CENTER);
		username = new JTextField();

		username.setOpaque(false);
		username.setFont(smallfont);
		panels[2].add(description[1]);
		panels[2].add(username);
		panels[2].setOpaque(false);
		// panels[1].setBorder(BorderFactory.createLineBorder(Color.RED, 4));
		all.add(panels[2]);
		panels[2].revalidate();
	}

	private void createpanel4() {
		panels[3].setLayout(new GridLayout(1, 2));

		description[2] = new JLabel("Password: ");
		description[2].setFont(smallfont);
		description[2].setHorizontalAlignment(JLabel.CENTER);
		passwordbox = new JPasswordField();
		passwordbox.setOpaque(false);
		panels[3].add(description[2]);
		panels[3].add(passwordbox);
		panels[3].setOpaque(false);
		// panels[2].setBorder(BorderFactory.createLineBorder(Color.CYAN, 4));
		all.add(panels[3]);
		panels[3].revalidate();
	}
	
	private void createpanel5() {
		panels[4].setLayout(new GridLayout(1, 2));

		description[3] = new JLabel("Retype Password: ");
		description[3].setFont(smallfont);
		description[3].setHorizontalAlignment(JLabel.CENTER);
		passwordboxRetype = new JPasswordField();
		passwordboxRetype.setOpaque(false);
		panels[4].add(description[3]);
		panels[4].add(passwordboxRetype);
		panels[4].setOpaque(false);
		// panels[2].setBorder(BorderFactory.createLineBorder(Color.CYAN, 4));
		all.add(panels[4]);
		panels[4].revalidate();
	}
	
	private void createpanel6() {
		panels[5].setLayout(new GridLayout(1, 2));

		description[4] = new JLabel("Description: ");
		description[4].setFont(smallfont);
		description[4].setHorizontalAlignment(JLabel.CENTER);
		describing = new JTextField();

		describing.setOpaque(false);
		describing.setFont(smallfont);
		panels[5].add(description[4]);
		panels[5].add(describing);
		panels[5].setOpaque(false);
		// panels[1].setBorder(BorderFactory.createLineBorder(Color.RED, 4));
		all.add(panels[5]);
		panels[5].revalidate();
	}


	private void createpanel7() {
		panels[6].setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
		// panels[3].setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));


		createAccount = new JButton("Create New Account");
		createAccount.setFont(customFont.deriveFont(15f));
		createAccount.setPreferredSize(new Dimension(180, 30));
		panels[6].add(createAccount);
		panels[6].setOpaque(false);
		all.add(panels[6]);
		c.add(all);
		
		createAccount.addActionListener(new CreateAccountListener(this));
		
		panels[6].revalidate();
	}
	
	public JTextField getEmail() {
		return email;
	}

	public JTextField getDescribing() {
		return describing;
	}

	public JTextField getUsername() {
		return username;
	}

	public JPasswordField getPasswordbox() {
		return passwordbox;
	}
	
	public JPasswordField getPasswordboxRetype() {
		return passwordboxRetype;
	}

	public JButton getCreateAccount() {
		return createAccount;
	}
}
