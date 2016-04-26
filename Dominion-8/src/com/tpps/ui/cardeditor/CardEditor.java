package com.tpps.ui.cardeditor;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.util.GraphicsUtil;

/**
 * 
 * @author Nishit Agrawal -nagrawal, Maximilian Hauk -mhauk
 *
 */

public class CardEditor extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private BufferedImage blackBeauty;
	private BufferedImage walterWhite;
	private BufferedImage background;
	private JButton uploadImage, increasePrice, decreasePrice, standartPrice, createCard, cancel;
	private ButtonGroup actionSelect = new ButtonGroup();
	private JRadioButton addAction, addMoney, addPurchase, drawCard, drawCardUntil, putBack, gainCard, discardCard,
			trashCard, revealCard, isTreasure, isVictory;
	private Container c;
	private JLabel all, enterName, price, cardType, testImage;
	private JTextField nameField;
	private JComboBox selectCardType;
	private ImageIcon loading;
	private int width, gridwidth;
	private int height, gridheight;
	private Font smallfont;
	private GridBagLayout gbl;
	private GridBagConstraints gbc;
	private JPanel obenLinks;

	public CardEditor() {
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		gbc = new GridBagConstraints();
		// createButtons();
		// loadImage();
		// resizeImage();
		c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		;
		all = new JLabel(loading);
		// all.setLayout(new GridLayout(4, 1, 0, 30));
		// all.setLayout(new GridBagLayout());
		iniateLayout();
		this.setSize(width / 5, height / 2);
		this.setLocationRelativeTo(null);
		this.setTitle("Card Editor !");
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		smallfont = new Font("Calibri", Font.BOLD, 19);
		try {
			this.setIconImage((ImageIO.read(ClassLoader.getSystemResource("resources/img/loginScreen/Icon.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		c.add(all);
	}

	private void resizeImage() {
		this.loading = new ImageIcon(this.background);
		Image newing = this.background.getScaledInstance(width / 5, height / 2, java.awt.Image.SCALE_SMOOTH);
		this.loading = new ImageIcon(newing);
		System.out.println(background);
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
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, 0.4F);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.walterWhite = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/walterWhite.jpg"));
			walterWhite = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, 0.4F);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void createPanels() {

	}

	// TODO set Positions etc
	private void createLabels() {
		enterName = new JLabel("Geben Sie den Kartennamen ein");
		// enterName.setBounds(x, y, width, height);
		price = new JLabel("");
		// price.setBounds(x, y, width, height);
		cardType = new JLabel("Cardtype");
		// cardType.setBounds(x, y, width, height);
		testImage = new JLabel("");
		// testImage.setBounds(x,y,width,height);
	}

	// TODO : Dokumentation
	// TODO : Layout fixen

	private void createButtons() {

		// uploadImage.setPreferredSize(new Dimension(10,20));
		// gbl.setConstraints(uploadImage,gbc);

		increasePrice = new JButton("increasePrice");
		// add(increasePrice,gbc);
		decreasePrice = new JButton("decreasePrice");
		standartPrice = new JButton("standardPrice");
		createCard = new JButton("createCard");
		cancel = new JButton("cancel");
	}

	private void iniateLayout() {

		JPanel obenLinks = new JPanel();
		obenLinks.setBackground(Color.green); // temp
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.85;
		gbc.weighty = 0.65;
		enterName = new JLabel("Geben Sie den Kartennamen ein");
		obenLinks.add(enterName);
		c.add(obenLinks, gbc);

		JPanel pnlBuy = new JPanel();
		gbc.gridx = 2;
		pnlBuy.setBackground(Color.blue); // temp
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.15;
		gbc.weighty = 0.46;
		nameField = new JTextField("");
		pnlBuy.add(nameField);
		c.add(pnlBuy, gbc);

		JPanel untenLinks = new JPanel();
		untenLinks.setBackground(Color.red); // temp
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.61;
		gbc.weighty = 0.35;
		createCard = new JButton("Create Card");
		untenLinks.add(createCard);
		c.add(untenLinks, gbc);

		JPanel untenRechts = new JPanel();
		untenRechts.setBackground(Color.gray); // temp
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.38;
		gbc.weighty = 0.31;
		cancel = new JButton("Cancel");
		cancel.addActionListener((new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				DominionController.getInstance().abortSearching();
				DominionController.getInstance().joinMainMenu();
				DominionController.getInstance().clearAllPlayersFromGUI();
			}
		}));
		untenRechts.add(cancel);
		c.add(untenRechts, gbc);

		// createButtons();

	}

	private void createTextfield() {
		nameField = new JTextField("");
	}

	private void createCombobox() {
		selectCardType = new JComboBox();
	}

	public static void main(String[] args) {
		new CardEditor().setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
