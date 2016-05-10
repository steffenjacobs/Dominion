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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
	private static JTextField nameField;
	private static JComboBox selectCardType;
	private ImageIcon loading;
	private int width, gridwidth;
	private int height, gridheight, schleifenZaehler = 0;
	private Font smallfont,radioFont,priceFont,customFont;
	private GridBagLayout gbl;
	private GridBagConstraints gbc, gbc2;
	private JPanel obenLinks, uImage;
	private static int priceint = 2;
	private JFileChooser fc;
	private final String newline = "\n";
	private String basePath;
	private static String name;
	private static BufferedImage targetImg;
	private static String type;
	private BufferedImage buttonIcon;
	private File targetFile;
	private final int baseSize = 128;
	private BufferedImage back;
	private BackButton backButton;
    private boolean rbp1,rbp2,rbp3,rbp4,rbp5,rbp6,rbp7,rbp8,rbp9,rbp10,rbp11,rbp12;
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
		
		all = new JLabel(loading);
		this.setSize(width / 5, (int) (height / 1.8));
		this.setLocationRelativeTo(null);
		this.setTitle("Card Editor !");
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Verschiedene Schriftarten für Labels,RadioButtons und den Price
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

	}
   
	//nicht benutzt
	private void fullscreenmode() {
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// this.setUndecorated(true);
	}


	// TODO : Documentation und Formatierung
	// TODO : Upload in die Cloud

	private void initComponents() {
		/**
		 * loading the background, using the paint method
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
		nameField = new JTextField(1);      //Hier kann der User den Namen der Karte eingeben
		obenLinks.add(nameField);
		cardType = new JLabel("Choose Cardtype");
		cardType.setFont(smallfont);
		cardType.setForeground(Color.WHITE);
		obenLinks.add(cardType);
		String comboBoxListe[] = { "Action", "Treasure", "Victory", "Reaction", "Attack", "Prize", "Curse","Ruins", "Shelter", 	"Knight", 
								   "Traveller","Reserve", };
		selectCardType = new JComboBox(comboBoxListe);  //Hier wird der CardType ausgewählt
		selectCardType.setMaximumRowCount(5);
		//Falls Victory oder Treasure ausgewählt ist werden die Radiobuttons disabled und Victory/Treasure ist ausgewählt
		selectCardType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
			    int localZaehler;
			    localZaehler = selectCardType.getSelectedIndex();
			    if (localZaehler == 0) {
			    	setRadiobuttons();  //Radiobuttons wieder aktiveren
			    }
				if (localZaehler == 1) {
					
					//Alles deaktiviert und Treasure ausgewählt
					addAction.setEnabled(false);
					addMoney.setEnabled(false);
					addPurchase.setEnabled(false);
					drawCard.setEnabled(false);
					drawCardUntil.setEnabled(false);
					putBack.setEnabled(false);
					gainCard.setEnabled(false);
					discardCard.setEnabled(false);
					trashCard.setEnabled(false);
					revealCard.setEnabled(false);
					isTreasure.setEnabled(false);
					isVictory.setEnabled(false);
					addAction.setSelected(false);
					addMoney.setSelected(false);
					addPurchase.setSelected(false);
					drawCard.setSelected(false);
					drawCardUntil.setSelected(false);
					putBack.setSelected(false);
					gainCard.setSelected(false);
					discardCard.setSelected(false);
					trashCard.setSelected(false);
					revealCard.setSelected(false);
					isTreasure.setSelected(true);
					isVictory.setSelected(false);
					
					//Prüfer setzen, sonst ist zwar der Button ausgewählt aber der Prüfer stimmt nicht überein
					rbp1 = false;
					rbp2 = false;
					rbp3 = false;
					rbp4 = false;
					rbp5 = false;
					rbp6 = false;
					rbp7 = false;
					rbp8 = false;
					rbp9 = false;
					rbp10 = false;
					rbp11 = true;
					rbp12 = false;
					radioButtonSelect(rbp11);


				}
				if (localZaehler == 2) {
					
					//Alles deaktiviert und Victory ausgewählt
					addAction.setEnabled(false);
					addMoney.setEnabled(false);
					addPurchase.setEnabled(false);
					drawCard.setEnabled(false);
					drawCardUntil.setEnabled(false);
					putBack.setEnabled(false);
					gainCard.setEnabled(false);
					discardCard.setEnabled(false);
					trashCard.setEnabled(false);
					revealCard.setEnabled(false);
					isTreasure.setEnabled(false);
					isVictory.setEnabled(false);
					addAction.setSelected(false);
					addMoney.setSelected(false);
					addPurchase.setSelected(false);
					drawCard.setSelected(false);
					drawCardUntil.setSelected(false);
					putBack.setSelected(false);
					gainCard.setSelected(false);
					discardCard.setSelected(false);
					trashCard.setSelected(false);
					revealCard.setSelected(false);
					isTreasure.setSelected(false);
					isVictory.setSelected(true);
					
					//Prüfer setzen, sonst ist zwar der Button ausgewählt aber der Prüfer stimmt nicht überein
					rbp1 = false;
					rbp2 = false;
					rbp3 = false;
					rbp4 = false;
					rbp5 = false;
					rbp6 = false;
					rbp7 = false;
					rbp8 = false;
					rbp9 = false;
					rbp10 = false;
					rbp11 = false;
					rbp12 = true;
					radioButtonSelect(rbp12);	
				}
				if (localZaehler >= 3) {
			 setRadiobuttons();  //RadioButtons enablen
				}
				}
				
			}
		});
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
		gbc.weighty = 0.5;
		gbc.anchor = GridBagConstraints.SOUTH;
		uImage.setLayout(new BorderLayout());
		
		//ButtonIcon wird gemalt
		uploadImage = new JButton("Upload Image") {              //Button um ein Bild hochzuladen

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
		testImage.setSize(baseSize, baseSize);
		
		//läd ein Placeholder Image, sonst gibt es Probleme mit der Panelgröße
		try {
			testImage.setIcon(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("resources/img/cardEditor/placeHolder.png"))));
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		uploadImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadImageActionPerformed(e);              //Läd ein ausgewähltes Bild hoch
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
		price = new JLabel(Integer.toString(priceint));      //Das Label mit dem der Preis angezeigt wird
		;
		price.setFont(priceFont);
		price.setForeground(Color.WHITE);
		mitte.add(price, gbc2);
		
		//ButtonIcon malen
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
		
		//ButtonIcon malen
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
		
		//ButtonIcon malen
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
				priceint = priceint + 1;                           //Erhöht den Preis
				price.setText(Integer.toString(priceint));
				System.out.println("Cost : " + Integer.toString(priceint));
			}
		}));
		standartPrice.addActionListener((new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				priceint = 2;                                     //Setzt den Preis auf default(2)
				price.setText(Integer.toString(priceint));
				System.out.println("Cost : " + Integer.toString(priceint));
			}
		}));
		decreasePrice.addActionListener((new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (priceint > 0) {
					priceint = priceint - 1;                      //Preis wird runtergesetzt
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

		JPanel radio = new JPanel(); 
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
		 * creates the radiobuttons, sets their color and the font
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
		
		//Verschönerung der GUI
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
		
		/**
		 * die RadioButtonPrüfer werden gesetzt und prüft ob der Button selektiert oder nicht ist
		 */
		
		
		addAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp1 = addAction.isSelected();                //Prüfer wird gesetzt
				radioButtonSelect(rbp1);                      //Methode zur Prüfung
			}
				});
		addMoney.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp2 = addMoney.isSelected();
				radioButtonSelect(rbp2);
			}
				});
		addPurchase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp3 = addPurchase.isSelected();
				radioButtonSelect(rbp3);
			}
				});
		drawCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp4 = drawCard.isSelected();
				radioButtonSelect(rbp4);
			}
				});
		drawCardUntil.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp5 = drawCardUntil.isSelected();
				radioButtonSelect(rbp5);
			}
				});
		putBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp6 = putBack.isSelected();
				radioButtonSelect(rbp6);
			}
				});
		gainCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp7 = gainCard.isSelected();
				radioButtonSelect(rbp7);
			}
				});
		discardCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp8 = discardCard.isSelected();
				radioButtonSelect(rbp8);
			}
				});
		trashCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp9 = trashCard.isSelected();
				radioButtonSelect(rbp9);
			}
				});
		revealCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp10 = revealCard.isSelected();
				radioButtonSelect(rbp10);
			}
				});
		isTreasure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp11 = isTreasure.isSelected();
				radioButtonSelect(rbp11);
			}
				});
		isVictory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbp12 = isVictory.isSelected();
				radioButtonSelect(rbp12);
			}
				});
		
		
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
		
		//Buttonicon wird gezeichnet
		createCard = new JButton("Create Card") {

			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.drawImage(buttonIcon, 0, 0, null);
				super.paint(g);
			}
		};
		createCard.setOpaque(false);
		createCard.setContentAreaFilled(false);
        createCard.setBackground(null);
        createCard.setBorder(null);
        createCard.setBorderPainted(false);
		createCard.setFont(customFont.deriveFont(15f));
		createCard.setPreferredSize(d);
		createCard.addActionListener(new ActionListener() {      
		
			/**
			 * falls die RadioButtons selektiert sind werden sie zur RadioButton Arraylist hinzugefügt, diese wird dann an ActionQuery übergeben für die LinkedHashmap
			 */
			
			public void actionPerformed(ActionEvent e) {
				if (schleifenZaehler < 4 && schleifenZaehler > 0) {
				ArrayList<String> radioButtons = new ArrayList<String>();
				if (rbp1 == true)
				radioButtons.add(addAction.getText());				
				if (rbp2 == true)
				radioButtons.add(addMoney.getText());
				if (rbp3 == true)
				radioButtons.add(addPurchase.getText());
				if (rbp4 == true)
				radioButtons.add(drawCard.getText());
				if (rbp5 == true)
				radioButtons.add(drawCardUntil.getText());	
				if (rbp6 == true)
				radioButtons.add(putBack.getText());	
				if (rbp7 == true)
				radioButtons.add(gainCard.getText());
				if (rbp8 == true)
				radioButtons.add(discardCard.getText());
				if (rbp9 == true)
				radioButtons.add(trashCard.getText());	
                if (rbp10 == true)	
                radioButtons.add(revealCard.getText());
				if (rbp11 == true)
				radioButtons.add(isTreasure.getText());
				if (rbp12 == true)
				radioButtons.add(isVictory.getText());
				
				new ActionQuery(radioButtons);
				}
				else {
					JOptionPane.showMessageDialog(null, "You have to choose between 1 and 3 Actions", "Error!",
							JOptionPane.WARNING_MESSAGE);
				}
				
			}
		});
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
		
		//ButtonIcon wird geladen
		cancel = new JButton("Cancel") {


		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			g.drawImage(buttonIcon, 0, 0, null);
			super.paint(g);
		}
	};
	cancel.setOpaque(false);
	cancel.setContentAreaFilled(false);
    cancel.setBackground(null);
    cancel.setBorder(null);
    cancel.setBorderPainted(false);
	cancel.setFont(customFont.deriveFont(15f));
	cancel.setPreferredSize(d);
	cancel.addActionListener((new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				DominionController.getInstance().joinMainMenu();
				CardEditor.this.dispose();
			}
		}));
		untenRechts.add(cancel);
		c.add(untenRechts, gbc);

		}
	
	/**
	 * this method enables all the radiobuttons and unselects them
	 */

	private void setRadiobuttons() {                     
		addAction.setEnabled(true);
		addMoney.setEnabled(true);
		addPurchase.setEnabled(true);
		drawCard.setEnabled(true);
		drawCardUntil.setEnabled(true);
		putBack.setEnabled(true);
		gainCard.setEnabled(true);
		discardCard.setEnabled(true);
		trashCard.setEnabled(true);
		revealCard.setEnabled(true);
		isTreasure.setEnabled(true);
		isVictory.setEnabled(true);
		addAction.setSelected(false);
		addMoney.setSelected(false);
		addPurchase.setSelected(false);
		drawCard.setSelected(false);
		drawCardUntil.setSelected(false);
		putBack.setSelected(false);
		gainCard.setSelected(false);
		discardCard.setSelected(false);
		trashCard.setSelected(false);
		revealCard.setSelected(false);
		isTreasure.setSelected(false);
		isVictory.setSelected(false);
	}
	
	/**
	 * This methods counts the selected radiobuttons, informs the user how many he has selected
	 */
	
	private void radioButtonSelect(boolean pruefer) {
		if (pruefer == true) {
		schleifenZaehler = schleifenZaehler + 1;
		System.out.println("You have selected " + Integer.toString(schleifenZaehler) + " of three possible actions");
		pruefer = false;
		
		} else if (pruefer == false) {
				schleifenZaehler = schleifenZaehler - 1;
				System.out.println("You have selected " + Integer.toString(schleifenZaehler) + " of three possible actions");
				pruefer = true;
		}
			}
	
	
	/**
	 * this method loads the buttonicon and sets its dimension
	 */
	
	private void loadButtonIcon() {
		try {
			this.buttonIcon = ImageIO.read(ClassLoader.getSystemResource("resources/img/cardEditor/cEbutton2.png"));
			d = new Dimension(buttonIcon.getWidth(), buttonIcon.getHeight());
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	
	/**
	 * 
	 * @param String
	 *                gets the name of the card which is written bei the User in the JTextField
	 */
	
	public static String getCardname() {
		name = nameField.getText();
		return name;
	}
	
	/**
	 * 
	 * @param int
	 *             gets the price of the card which the user sets with the "increase/decrease" Buttons
	 */
	
	public static int getPrize() {
		return priceint;
	}
	
	/**
	 * 
	 * @param BufferedImage
	 *             gets the image of the card which the user has uploaded with the "uploadImage" Button and is previewed in the GUI
	 */
	
   public static BufferedImage getImage() {
	   return targetImg;
   }
   
	/**
	 * 
	 * @param String
	 *             gets the type of the card which the user selects in the "selectCardTyoe" JComboBox
	 */
   public static String getCardtype() {
	   type = (String) selectCardType.getSelectedItem();
	   return type;
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
