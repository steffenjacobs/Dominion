package com.tpps.ui.loginscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.Box;
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

/**
 * 
 * @author Nishit Agrawal - nagrawal
 *
 */

public class CreateAccount extends JFrame {

	private static final long serialVersionUID = 1L;
	// private ImageIcon loading;
	private BufferedImage background;
	private JButton createAccount;
	private JTextField email, username;
	private JPasswordField passwordbox, passwordboxRetype;
	private JLabel[] description;
	private JLabel header;
	private JLabel all;
	private JPanel[] panels;
	private Font smallfont, customFont;
	// private BufferedImage walterWhite;
	private Color textAndLabelColor;

	private LoginGUIController guicontroller;

	private JPanel content;
	private JButton cancel;

	private static final Dimension size = new Dimension(500, 350);

	/**
	 * simple constructor (first call) merging all elements
	 * @param guicontroller 
	 * @param location 
	 */
	public CreateAccount(LoginGUIController guicontroller, Point location) {
		this.guicontroller = guicontroller;

		loadImage();
		// resizeImage();
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
		this.createpanel5();
		this.createpanel6();
		this.setLocation(location);
		this.revalidate();
	}

	/**
	 * A simple initialize method to set specific Frame.
	 */

	private void init() {
		content = new JPanel() {
			private static final long serialVersionUID = -2340670434638379812L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, null);
			}
		};
		BorderLayout bl = new BorderLayout();
		content.setLayout(bl);

		this.setContentPane(content);

		bl.addLayoutComponent(Box.createHorizontalStrut(30), BorderLayout.LINE_START);
		bl.addLayoutComponent(Box.createHorizontalStrut(30), BorderLayout.LINE_END);

		all = new JLabel();
		all.setLayout(new GridLayout(6, 1, 0, 30));

		this.setSize(size);
		this.setLocationRelativeTo(null);
		this.setTitle("Create Account");
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		smallfont = new Font("Calibri", Font.BOLD, 19);
		this.setIconImage(ImageLoader.getImage("resources/img/loginScreen/Icon.png"));

		description = new JLabel[4];
		panels = new JPanel[6];
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new JPanel(new FlowLayout());
		}
		// this.setContentPane(new JLabel(loading));
		this.textAndLabelColor = System.getProperty("os.name").startsWith("Windows") ? Color.WHITE : Color.BLACK;
	}

	/**
	 * importing background image from resources
	 */

	private void loadImage() {
		this.background = GraphicsUtil.resize(ImageLoader.getImage("resources/img/loginScreen/LoginBackground.jpg"),
				size.width, size.height);

	}

	/**
	 * first panel created as a header
	 */

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

	/**
	 * second panel created as JTextfield for email
	 */

	private void createpanel2() {
		panels[1].setLayout(new GridLayout(1, 2));

		description[0] = new JLabel("Email: ");
		description[0].setFont(smallfont);
		description[0].setHorizontalAlignment(JLabel.CENTER);
		email = new JTextField() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.4"), 0, 0, null);
				super.paint(g);
			}
		};
		email.setForeground(textAndLabelColor);
		email.setOpaque(false);
		email.setFont(smallfont);

		panels[1].add(description[0]);
		panels[1].add(email);
		panels[1].setOpaque(false);
		// panels[1].setBorder(BorderFactory.createLineBorder(Color.RED, 4));
		all.add(panels[1]);
		panels[0].revalidate();
	}

	/**
	 * third panel created for the USER-ID
	 */

	private void createpanel3() {
		panels[2].setLayout(new GridLayout(1, 2));

		description[1] = new JLabel("Username: ");
		description[1].setFont(smallfont);
		description[1].setHorizontalAlignment(JLabel.CENTER);
		username = new JTextField() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.4"), 0, 0, null);
				super.paint(g);
			}
		};
		username.setForeground(textAndLabelColor);
		username.setOpaque(false);
		username.setFont(smallfont);
		panels[2].add(description[1]);
		panels[2].add(username);
		panels[2].setOpaque(false);
		// panels[1].setBorder(BorderFactory.createLineBorder(Color.RED, 4));
		all.add(panels[2]);
		panels[2].revalidate();
	}

	/**
	 * fourth panel is for the first password
	 */

	private void createpanel4() {
		panels[3].setLayout(new GridLayout(1, 2));

		description[2] = new JLabel("Password: ");
		description[2].setFont(smallfont);
		description[2].setHorizontalAlignment(JLabel.CENTER);
		passwordbox = new JPasswordField() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.4"), 0, 0, null);
				super.paint(g);
			}
		};
		passwordbox.setForeground(textAndLabelColor);
		passwordbox.setOpaque(false);
		passwordbox.setFont(smallfont);
		panels[3].add(description[2]);
		panels[3].add(passwordbox);
		panels[3].setOpaque(false);
		// panels[2].setBorder(BorderFactory.createLineBorder(Color.CYAN, 4));
		all.add(panels[3]);
		panels[3].revalidate();
	}

	/**
	 * password recheck panel
	 */

	private void createpanel5() {
		panels[4].setLayout(new GridLayout(1, 2));

		description[3] = new JLabel("Retype Password: ");
		description[3].setFont(smallfont);
		description[3].setHorizontalAlignment(JLabel.CENTER);
		passwordboxRetype = new JPasswordField() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.4"), 0, 0, null);
				super.paint(g);
			}
		};
		passwordboxRetype.setForeground(textAndLabelColor);
		passwordboxRetype.setOpaque(false);
		passwordboxRetype.setFont(smallfont);
		panels[4].add(description[3]);
		panels[4].add(passwordboxRetype);
		panels[4].setOpaque(false);
		// panels[2].setBorder(BorderFactory.createLineBorder(Color.CYAN, 4));
		all.add(panels[4]);
		panels[4].revalidate();
	}

	// /**
	// * User-Description panel.
	// */
	//
	// private void createpanel6() {
	// panels[5].setLayout(new GridLayout(1, 2));
	//
	// description[4] = new JLabel("Description: ");
	// description[4].setFont(smallfont);
	// description[4].setHorizontalAlignment(JLabel.CENTER);
	// describing = new JTextField();
	//
	// describing.setOpaque(false);
	// describing.setFont(smallfont);
	// panels[5].add(description[4]);
	// panels[5].add(describing);
	// panels[5].setOpaque(false);
	// // panels[1].setBorder(BorderFactory.createLineBorder(Color.RED, 4));
	// all.add(panels[5]);
	// panels[5].revalidate();
	// }

	/**
	 * Create Account button with specific listener.
	 */

	private void createpanel6() {
		panels[5].setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
		// panels[3].setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

		createAccount = new JButton("Create New Account") {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.4"), 0, 0, null);
				super.paint(g);
			}
		};
		cancel = new JButton("Cancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(ImageLoader.getImage("black_0.4"), 0, 0, null);
				super.paint(g);
			}
		};

		createAccount.setOpaque(false);
		createAccount.setForeground(textAndLabelColor);
		createAccount.setBorderPainted(true);
		createAccount.setContentAreaFilled(false);
		createAccount.setFont(customFont.deriveFont(15f));
		createAccount.setPreferredSize(new Dimension(180, 30));

		cancel.setOpaque(false);
		cancel.setForeground(textAndLabelColor);
		cancel.setBorderPainted(true);
		cancel.setContentAreaFilled(false);
		cancel.setFont(customFont.deriveFont(15f));
		cancel.setPreferredSize(new Dimension(180, 30));

		panels[5].add(createAccount);
		panels[5].add(cancel);
		panels[5].setOpaque(false);
		all.add(panels[5]);
		this.getContentPane().add(all);

		createAccount.addMouseListener(new CreateAccountListener(this, guicontroller));
		cancel.addMouseListener(new CreateAccountListener(this, guicontroller));

		panels[5].revalidate();
	}

	/**
	 * getter Email
	 * 
	 * @return email
	 */

	public JTextField getEmail() {
		return email;
	}

	/**
	 * Getter USER-ID
	 * 
	 * @return username
	 */

	public JTextField getUsername() {
		return username;
	}

	/**
	 * Getter for password
	 * 
	 * @return passwordbox
	 */

	public JPasswordField getPasswordbox() {
		return passwordbox;
	}

	/**
	 * Confirmation password getter
	 * 
	 * @return passwordboxRetype
	 */

	public JPasswordField getPasswordboxRetype() {
		return passwordboxRetype;
	}

	/**
	 * Button getter.
	 * 
	 * @return createAccount
	 */

	public JButton getCreateAccount() {
		return createAccount;
	}

	/**
	 * Button getter
	 * 
	 * @return cancel
	 */

	public JButton getCancel() {
		return cancel;
	}
}
