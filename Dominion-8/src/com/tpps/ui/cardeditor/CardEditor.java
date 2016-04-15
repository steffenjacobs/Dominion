package com.tpps.ui.cardeditor;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.tpps.technicalServices.util.GraphicsUtil;

/**
 * 
 * @author Nishit Agrawal -nagrawal, Maximilian Hauk -mhauk
 *
 */

public class CardEditor extends JFrame {
	private static final long serialVersionUID = 1L;
	private BufferedImage blackBeauty;
	private BufferedImage walterWhite;
	private BufferedImage background;
	private JButton uploadImage,increasePrice,decreasePrice,standartPrice,createCard,cancel;
	private ButtonGroup actionSelect = new ButtonGroup();
	private JRadioButton addAction, addMoney, addPurchase, drawCard, drawCardUntil, putBack, gainCard,
	                     discardCard, trashCard, revealCard, isTreasure, isVictory;
	private Container c;
	private JLabel all, enterName, price, cardType, testImage;
	private JTextField nameField;
	private JComboBox selectCardType;
	private ImageIcon loading;
	private int width,gridwidth;
	private int height,gridheight;
	private Font smallfont;
	private GridBagLayout gbl;
	private GridBagConstraints gbc;

	
	
	public CardEditor(){
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		gbl = new GridBagLayout();
		setLayout(gbl);
	    gbc = new GridBagConstraints();
		//createButtons();
	    loadImage();
		resizeImage();
		iniateLayout();
		c = this.getContentPane();
		all = new JLabel(loading);
//		all.setLayout(new GridLayout(4, 1, 0, 30));
//		all.setLayout(new GridBagLayout());
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
	
	private void createPanels(){
		
	}
	
	//TODO set Positions etc
	private void createLabels(){
		enterName	= new JLabel("Geben Sie den Kartennamen ein");
//		enterName.setBounds(x, y, width, height);
		price		= new JLabel("");
//		price.setBounds(x, y, width, height);
		cardType	= new JLabel("Cardtype");
//		cardType.setBounds(x, y, width, height);
		testImage	= new JLabel("");
//		testImage.setBounds(x,y,width,height);
	}
	
//TODO : Dokumentation
//TODO : Layout fixen	
	
	private void createButtons(){
		uploadImage = new JButton("uploadImage");
//		uploadImage.setPreferredSize(new Dimension(10,20));
		gbl.setConstraints(uploadImage,gbc);
		gbl.addLayoutComponent(uploadImage, gbc);
		increasePrice = new JButton("increasePrice");
//		add(increasePrice,gbc);
		decreasePrice = new JButton("decreasePrice");
		standartPrice = new JButton("standardPrice");
		createCard= new JButton("createCard");
		cancel= new JButton("cancel");
	}
	
	private void iniateLayout() {

	   gbc.gridwidth = 6;
	   gbc.gridheight = 6;
	   gbc.gridy = 2; 
	   gbc.gridx = 2;
	   gbc.weightx = 2;
	   gbc.weighty = 2;
	   gbc.fill = GridBagConstraints.BOTH;
	   gbc.ipadx = 2;
	   gbc.ipady = 2;
	   gbc.anchor = GridBagConstraints.ABOVE_BASELINE;
	
	   createButtons();
		
	}

	
	private void createTextfield(){
		nameField = new JTextField("");
	}
	
	private void createCombobox(){
		selectCardType = new JComboBox();
	}
	
	
	public static void main(String[] args) {
		new CardEditor().setVisible(true);
	}
	
	
	
	
}
