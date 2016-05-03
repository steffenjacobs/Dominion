package com.tpps.ui.cardeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.util.logging.Level;
import java.util.logging.Logger;

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
	private GridBagConstraints gbc,gbc2;
	private JPanel obenLinks,pnlBuy;
	private int priceint = 2;
	private JFileChooser fc;
	private final String newline = "\n";
	private String basePath;
	private BufferedImage targetImg;
	private File targetFile;
	private final int baseSize = 128;

	public CardEditor() {
		this.setVisible(true);
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		gbc = new GridBagConstraints();
		// createButtons();
		// loadImage();
		// resizeImage();
		c = this.getContentPane();
		c.setLayout(new GridBagLayout());
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


	private void createLabels() {
		enterName = new JLabel("Geben Sie den Kartennamen ein");
		price = new JLabel("");
		cardType = new JLabel("Cardtype");
		testImage = new JLabel("");
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
	

	  
	
	//TODO : Ungefähres Layout vollenden
	//TODO : Layout an relative Positionen anpassen
	//TODO : Layout ans Design anpassen
	//TODO : Komponenten ans Design anpassen
	//TODO : Listener der Komponenten
	
	/**
	 * creates the GUI
	 */
	
	private void iniateLayout() {
	
		JPanel obenLinks = new JPanel();      //Panel für Karteninitation
		obenLinks.setBackground(Color.green); 
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.ipadx = 60;
		obenLinks.setLayout(new GridLayout(2,2,30,10));
		enterName = new JLabel("Enter Cardname");
		obenLinks.add(enterName);
		nameField = new JTextField(1);
		obenLinks.add(nameField);
		cardType = new JLabel("Choose Cardtype");
		obenLinks.add(cardType);
		selectCardType = new JComboBox();
		obenLinks.add(selectCardType);
		
		c.add(obenLinks, gbc);

		JPanel pnlBuy = new JPanel();        //Panel für das Bildhochladen
		gbc.gridx = 1;                       //Anpassen fürs Bildhochladen
		pnlBuy.setBackground(Color.blue); 
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.anchor = GridBagConstraints.SOUTH;
		pnlBuy.setLayout(new BorderLayout());
		testImage = new JLabel("");
        uploadImage = new JButton("Upload Image");
        uploadImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               uploadImageActionPerformed(e);
            }
        });			
        pnlBuy.add(testImage,BorderLayout.PAGE_START);
        pnlBuy.add(uploadImage,BorderLayout.PAGE_END);
		c.add(pnlBuy, gbc);
		
		JPanel mitte = new JPanel();           //Panel für den Preis
		mitte.setBackground(Color.ORANGE);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0.99;
		gbc.weighty = 0.1;
		gbc.fill = GridBagConstraints.BOTH;
		mitte.setLayout(new GridBagLayout());
		gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		gbc2.weightx = 0.99;
		gbc2.gridwidth = 1;
		gbc2.ipady = 60;
		gbc2.anchor = GridBagConstraints.NORTH;
		price = new JLabel(Integer.toString(priceint));;
		mitte.add(price,gbc2);
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		gbc2.anchor = GridBagConstraints.BELOW_BASELINE;
		gbc2.ipady = 0;
//		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
		increasePrice = new JButton("Increase Price");
		standartPrice = new JButton("Standart Price");
		decreasePrice = new JButton("Decreace Price");
		mitte.add(increasePrice,gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 1;
		mitte.add(standartPrice,gbc2);
		gbc2.gridx = 2;
		gbc2.gridy = 1;
		mitte.add(decreasePrice, gbc2);
		c.add(mitte,gbc);
		
		
		JPanel radio = new JPanel();            //Panel für die RadioButtons
		radio.setBackground(Color.YELLOW);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0.99;
		gbc.weighty = 0.5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		actionSelect = new ButtonGroup();		
		addAction = new JRadioButton("Add Action");
		addMoney = new JRadioButton("Add Money");
		addPurchase = new JRadioButton("Add Purchase");
		drawCard= new JRadioButton("Draw Card");
		drawCardUntil= new JRadioButton("Draw Card Until");
		putBack = new JRadioButton("Put Back");
		gainCard = new JRadioButton("Gain Card");
		discardCard= new JRadioButton("Discard Card");
		trashCard= new JRadioButton("Trash Card");
		revealCard = new JRadioButton("Reveal Card");
		isTreasure = new JRadioButton("Is Treasure");
		isVictory= new JRadioButton("Is Victory");
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
		c.add(radio,gbc);
		

		JPanel untenLinks = new JPanel();		//Karte erstellen
		untenLinks.setBackground(Color.red); 
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.5;
		gbc.weighty = 0.35;
		createCard = new JButton("Create Card");
		untenLinks.add(createCard);
		c.add(untenLinks, gbc);

		JPanel untenRechts = new JPanel();		//Zurück ins Menü
		untenRechts.setBackground(Color.gray);
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.5;
		gbc.weighty = 0.31;
		cancel = new JButton("Cancel");
		cancel.addActionListener((new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				DominionController.getInstance().joinMainMenu();
				CardEditor.this.dispose();
			}
		}));
		untenRechts.add(cancel);
		c.add(untenRechts, gbc);

		// createButtons();

	}

	/**
	 * resizing the uploaded image
	 */
	
    public BufferedImage rescale(BufferedImage originalImage)
    {
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }
    
	/**
	 * adds the uploaded image to the layout
	 */
	
    public void setTarget(File reference)
    {
        try {
            targetFile = reference;
            targetImg = rescale(ImageIO.read(reference));
        } catch (IOException ex) {
            Logger.getLogger(CardEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        pnlBuy.setLayout(new BorderLayout());
        pnlBuy.add(new JLabel(new ImageIcon(targetImg)),BorderLayout.PAGE_START); //TestImage verwenden statt neues
        setVisible(true);
    }
    
	/**
	 * uploads any jpeg image that the user chooses from his computer
	 */
    
    
    private void uploadImageActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fc = new JFileChooser(basePath);
        fc.setFileFilter(new JPEGImageFileFilter());
        int res = fc.showOpenDialog(null);
        // We have an image!
        try {
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                setTarget(file);
            } // Oops!
            else {
                JOptionPane.showMessageDialog(null,
                        "You must select one image to be the reference.", "Aborting...",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception iOException) {
        }

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
