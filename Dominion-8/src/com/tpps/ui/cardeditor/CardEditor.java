package com.tpps.ui.cardeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.util.FontLoader;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.lobbyscreen.BackButton;

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
	private ButtonGroup actionSelect;
	private JRadioButton addAction, addMoney, addPurchase, drawCard, drawCardUntil, putBack, gainCard, discardCard,
			trashCard, revealCard, isTreasure, isVictory;
	private Container c;
	private JLabel all, enterName, price, cardType, testImage;
	private JTextField nameField;
	private JComboBox selectCardType;
	private ImageIcon loading;
	private int width, gridwidth;
	private int height, gridheight;
	private Font smallfont,radioFont,priceFont,customFont;
	private GridBagLayout gbl;
	private GridBagConstraints gbc, gbc2;
	private JPanel obenLinks, uImage;
	private int priceint = 2;
	private JFileChooser fc;
	private final String newline = "\n";
	private String basePath;
	private BufferedImage targetImg,buttonIcon;
	private File targetFile;
	private final int baseSize = 128;
	private BufferedImage back;
	private BackButton backButton;
    private boolean showPic;
    private Dimension d;



	public CardEditor() {
		this.setVisible(true);
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		gbc = new GridBagConstraints();
		try {
			if (customFont == null) {
				customFont = FontLoader.getInstance().getXenipa();
				customFont.isBold();
				if (customFont == null) {
					customFont = new FontLoader().importFont();
				}
			}
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// loadImage();
		// resizeImage();

		all = new JLabel(loading);
		// all.setLayout(new GridLayout(4, 1, 0, 30));
		// all.setLayout(new GridBagLayout());

		this.setSize(width / 5, height / 2);
		this.setLocationRelativeTo(null);
		this.setTitle("Card Editor !");
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		smallfont = new Font("Calibri", Font.BOLD, 19);
		radioFont = new Font("Arial", Font.BOLD, 12);
		priceFont = new Font("Arial", Font.BOLD, 30);
		try {
			this.setIconImage((ImageIO.read(ClassLoader.getSystemResource("resources/img/loginScreen/Icon.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadButtonIcon();
		initComponents();
		c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		iniateLayout();
		// BackgroundPanel bg = new BackgroundPanel();
		// bg.setSize(new Dimension(width, height));
		// c.add(bg);
		// c.add(all);
	}

	private void fullscreenmode() {
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// this.setUndecorated(true);
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

	private void createLabels() {
		enterName = new JLabel("Geben Sie den Kartennamen ein");
		price = new JLabel("");
		cardType = new JLabel("Cardtype");
		testImage = new JLabel("");
	}

	// TODO : ButtonIcons
	// TODO : Upload Image verschiebt Radiobuttons
	// TODO : Radiobutton ActionListener - als Checkbox machen?
	// TODO : Documentation und Formatierung
	// TODO : Textfield, Namesabfrage ändern
	// TODO : Upload in die Cloud

	private void initComponents() {
		/**
		 * loads the backgroundimage
		 */
		try {
			setContentPane(new JPanel() {

				private Image img;

				{
					img = ImageIO.read(ClassLoader.getSystemResource("resources/img/loginScreen/LoginBackground.jpg"));

					MediaTracker mt = new MediaTracker(this);
					mt.addImage(img, 1);
					try {
						mt.waitForAll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
				 */
				@Override
				protected void paintComponent(Graphics g) {
					g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * creates the GUI
	 */

	private void iniateLayout() {

		/**
		 * creates the top panel
		 */

		JPanel obenLinks = new JPanel();
		obenLinks.setOpaque(false);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.ipadx = 60;
		obenLinks.setLayout(new GridLayout(2, 2, 30, 10));
		enterName = new JLabel("Enter Cardname");
		enterName.setFont(smallfont);
		enterName.setForeground(Color.WHITE);
		obenLinks.add(enterName);
		nameField = new JTextField(1);
		obenLinks.add(nameField);
		cardType = new JLabel("Choose Cardtype");
		cardType.setFont(smallfont);
		cardType.setForeground(Color.WHITE);
		obenLinks.add(cardType);
		String comboBoxListe[] = { "Action", "Treasure", "Victory", "Point" };
		selectCardType = new JComboBox(comboBoxListe);
		obenLinks.add(selectCardType);
		c.add(obenLinks, gbc);

		/**
		 * creates the Panel for uploading the Image
		 */

		JPanel uImage = new JPanel();
		gbc.gridx = 1;
		uImage.setOpaque(false);
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.27;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.SOUTH;
		uImage.setLayout(new BorderLayout());
		uploadImage = new JButton("Upload Image") {

		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			g.drawImage(buttonIcon, 0, 0, null);
			super.paint(g);
		}
	};
	uploadImage.setOpaque(false);
	uploadImage.setContentAreaFilled(false);
	uploadImage.setBackground(null);
	uploadImage.setBorder(null);
	uploadImage.setBorderPainted(false);
	uploadImage.setFont(customFont.deriveFont(15f));
	uploadImage.setPreferredSize(d);
		testImage = new JLabel("");
		uploadImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadImageActionPerformed(e);
			}
		});
		uImage.add(testImage, BorderLayout.CENTER);
		uImage.add(uploadImage, BorderLayout.PAGE_END);
		c.add(uImage, gbc);

		/**
		 * creates the Panel for setting the Price
		 */

		JPanel mitte = new JPanel();

		mitte.setOpaque(false);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0.99;
		gbc.weighty = 0.05;
		gbc.fill = GridBagConstraints.BOTH;
		mitte.setLayout(new GridBagLayout());
		gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		gbc2.weightx = 0.99;
		gbc2.gridwidth = 1;
		gbc2.ipady = 60;
		gbc2.anchor = GridBagConstraints.NORTH;
		price = new JLabel(Integer.toString(priceint));
		;
		price.setFont(priceFont);
		price.setForeground(Color.WHITE);
		mitte.add(price, gbc2);
		// gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		// gbc.fill = GridBagConstraints.HORIZONTAL;
		increasePrice = new JButton("Increase Price") {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(buttonIcon, 0, 0, null);
				super.paint(g);
			}
		};
		increasePrice.setOpaque(false);
		increasePrice.setContentAreaFilled(false);
		increasePrice.setBackground(null);
		increasePrice.setBorder(null);
		increasePrice.setBorderPainted(false);
		increasePrice.setFont(customFont.deriveFont(15f));
		increasePrice.setPreferredSize(d);
		standartPrice = new JButton("Standart Price") {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(buttonIcon, 0, 0, null);
				super.paint(g);
			}
		};
		standartPrice.setOpaque(false);
		standartPrice.setContentAreaFilled(false);
		standartPrice.setBackground(null);
		standartPrice.setBorder(null);
		standartPrice.setBorderPainted(false);
		standartPrice.setFont(customFont.deriveFont(15f));
		standartPrice.setPreferredSize(d);
		
		decreasePrice = new JButton("Decreace Price") {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(buttonIcon, 0, 0, null);
				super.paint(g);
			}
		};
		decreasePrice.setOpaque(false);
		decreasePrice.setContentAreaFilled(false);
		decreasePrice.setBackground(null);
		decreasePrice.setBorder(null);
		decreasePrice.setBorderPainted(false);
		decreasePrice.setFont(customFont.deriveFont(15f));
		decreasePrice.setPreferredSize(d);		
		

		/**
		 * change the price preview when clicking on button
		 */

		increasePrice.addActionListener((new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				priceint = priceint + 1;
				price.setText(Integer.toString(priceint));
				System.out.println("Cost : " + Integer.toString(priceint));
			}
		}));
		standartPrice.addActionListener((new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				priceint = 2;
				price.setText(Integer.toString(priceint));
				System.out.println("Cost : " + Integer.toString(priceint));
			}
		}));
		decreasePrice.addActionListener((new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (priceint > 0) {
					priceint = priceint - 1;
					price.setText(Integer.toString(priceint));
					System.out.println("Cost : " + Integer.toString(priceint));
				} else {
					System.out.println("The cost can't be lower than 0");
				}
			}
		}));
		mitte.add(price, gbc2);
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		gbc2.anchor = GridBagConstraints.BELOW_BASELINE;
		gbc2.ipady = 0;
		mitte.add(increasePrice,gbc2);
		mitte.add(increasePrice, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 1;
		mitte.add(standartPrice, gbc2);
		gbc2.gridx = 2;
		gbc2.gridy = 1;
		mitte.add(decreasePrice, gbc2);
		c.add(mitte, gbc);

		/**
		 * creates the Panel for setting the Cardaction
		 */

		JPanel radio = new JPanel(); // Panel für die RadioButtons
		radio.setOpaque(false);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0.99;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;

		/**
		 * creates the radiobuttons
		 */

		actionSelect = new ButtonGroup();
		addAction = new JRadioButton("Add Action");
		addAction.setFont(radioFont);
		addAction.setForeground(Color.WHITE);
		addMoney = new JRadioButton("Add Money");
		addMoney.setFont(radioFont);
		addMoney.setForeground(Color.WHITE);
		addPurchase = new JRadioButton("Add Purchase");
		addPurchase.setFont(radioFont);
		addPurchase.setForeground(Color.WHITE);
		drawCard = new JRadioButton("Draw Card");
		drawCard.setFont(radioFont);
		drawCard.setForeground(Color.WHITE);
		drawCardUntil = new JRadioButton("Draw Card Until");
		drawCardUntil.setFont(radioFont);
		drawCardUntil.setForeground(Color.WHITE);
		putBack = new JRadioButton("Put Back");
		putBack.setFont(radioFont);
		putBack.setForeground(Color.WHITE);
		gainCard = new JRadioButton("Gain Card");
		gainCard.setFont(radioFont);
		gainCard.setForeground(Color.WHITE);
		discardCard = new JRadioButton("Discard Card");
		discardCard.setFont(radioFont);
		discardCard.setForeground(Color.WHITE);
		trashCard = new JRadioButton("Trash Card");
		trashCard.setFont(radioFont);
		trashCard.setForeground(Color.WHITE);
		revealCard = new JRadioButton("Reveal Card");
		revealCard.setFont(radioFont);
		revealCard.setForeground(Color.WHITE);
		isTreasure = new JRadioButton("Is Treasure");
		isTreasure.setFont(radioFont);
		isTreasure.setForeground(Color.WHITE);
		isVictory = new JRadioButton("Is Victory");
		isVictory.setFont(radioFont);
		isVictory.setForeground(Color.WHITE);
		actionSelect.add(addAction);
		actionSelect.add(addMoney);
		actionSelect.add(addPurchase);
		actionSelect.add(drawCard);
		actionSelect.add(drawCardUntil);
		actionSelect.add(putBack);
		actionSelect.add(gainCard);
		actionSelect.add(discardCard);
		actionSelect.add(trashCard);
		actionSelect.add(revealCard);
		actionSelect.add(isTreasure);
		actionSelect.add(isVictory);
		addAction.setOpaque(false);
		addMoney.setOpaque(false);
		addPurchase.setOpaque(false);
		drawCard.setOpaque(false);
		drawCardUntil.setOpaque(false);
		putBack.setOpaque(false);
		gainCard.setOpaque(false);
		discardCard.setOpaque(false);
		trashCard.setOpaque(false);
		revealCard.setOpaque(false);
		isTreasure.setOpaque(false);
		isVictory.setOpaque(false);
		radio.add(addAction);
		radio.add(addMoney);
		radio.add(addPurchase);
		radio.add(drawCard);
		radio.add(drawCardUntil);
		radio.add(putBack);
		radio.add(gainCard);
		radio.add(discardCard);
		radio.add(trashCard);
		radio.add(revealCard);
		radio.add(isTreasure);
		radio.add(isVictory);
		c.add(radio, gbc);

		/**
		 * creates the Panel for creating the card
		 */

		JPanel untenLinks = new JPanel();
		untenLinks.setOpaque(false);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.5;
		gbc.weighty = 0.35;
		createCard = new JButton("Create Card") {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(buttonIcon, 0, 0, null);
				super.paint(g);
			}
		};
		createCard.setOpaque(false);
//		createCard.setBorderPainted(true);
		createCard.setContentAreaFilled(false);
        createCard.setBackground(null);
        createCard.setBorder(null);
        createCard.setBorderPainted(false);
		createCard.setFont(customFont.deriveFont(15f));
		createCard.setPreferredSize(d);
		untenLinks.add(createCard);
		c.add(untenLinks, gbc);

		/**
		 * creates the Panel for getting back into the main menu
		 */

		JPanel untenRechts = new JPanel();
		untenRechts.setOpaque(false);
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.5;
		gbc.weighty = 0.31;
		cancel = new JButton("Cancel") {


		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			g.drawImage(buttonIcon, 0, 0, null);
			super.paint(g);
		}
	};
	cancel.setOpaque(false);
//	createCard.setBorderPainted(true);
	cancel.setContentAreaFilled(false);
    cancel.setBackground(null);
    cancel.setBorder(null);
    cancel.setBorderPainted(false);
	cancel.setFont(customFont.deriveFont(15f));
	cancel.setPreferredSize(d);

		/*
		 * ImageIcon back; //TODO Vorlage erstellen für Buttons try {
		 * 
		 * back = new ImageIcon (ImageIO.read(ClassLoader.getSystemResource(
		 * "resources/img/cardEditor/Vorlage.jpg"))); Dimension d = new
		 * Dimension(back.getIconWidth(), back.getIconHeight());
		 * cancel.setOpaque(false); cancel.setContentAreaFilled(false);
		 * cancel.setBorderPainted(false); cancel.setPreferredSize(d);
		 * cancel.setIcon(back);
		 * 
		 * } catch (IOException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 */

		cancel.addActionListener((new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				DominionController.getInstance().joinMainMenu();
				CardEditor.this.dispose();
			}
		}));
		untenRechts.add(cancel);
		c.add(untenRechts, gbc);

		}
	
	private void loadButtonIcon() {
		try {
			this.buttonIcon = ImageIO.read(ClassLoader.getSystemResource("resources/img/cardEditor/cEbutton2.png"));
			d = new Dimension(buttonIcon.getWidth(), buttonIcon.getHeight());
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	


	/**
	 * resizing the uploaded image
	 */

	public BufferedImage rescale(BufferedImage originalImage) {
		BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
		g.dispose();
		return resizedImage;
	}

	/**
	 * adds the uploaded image to the layout
	 */

	public void setTarget(File reference) {
		try {
			targetFile = reference;
			targetImg = rescale(ImageIO.read(reference));
		} catch (IOException ex) {
			Logger.getLogger(CardEditor.class.getName()).log(Level.SEVERE, null, ex);
		}
		testImage.setIcon(new ImageIcon(targetImg));
		// System.out.println("wird abgerufen");
		setVisible(true);
	}

	/**
	 * uploads any jpeg image that the user chooses from his computer
	 */

	private void uploadImageActionPerformed(java.awt.event.ActionEvent evt) {
		// System.out.println("wird abgerufen");
		JFileChooser fc = new JFileChooser(basePath);
		fc.setFileFilter(new JPEGImageFileFilter());
		int res = fc.showOpenDialog(null);
		// We have an image!
		try { 
			if (res == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				setTarget(file);
				// System.out.println("wird abgerufen");
			} // Oops!
			else {
				JOptionPane.showMessageDialog(null, "You must select one image to be the reference.", "Aborting...",
						JOptionPane.WARNING_MESSAGE);

			}
		} catch (Exception iOException) {

		}

	}

	public static void main(String[] args) {
		new CardEditor().setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
