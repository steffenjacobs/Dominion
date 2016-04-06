package com.tpps.ui.cardeditor;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
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
	private JButton uploadImage;
	private ButtonGroup actionSelect = new ButtonGroup();
	private JRadioButton addAction, addMoney, addPurchase, drawCard, drawCardUntil, putBack, gainCard,
	                     discardCard, trashCard, revealCard, isTreasure, isVictory;
	private Container c;
	private JLabel all, enterName, price;
	private JComboBox selectCardType;
	private ImageIcon loading;
	private int width;
	private int height;
	private Font smallfont;

	
	
	public CardEditor(){
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		loadImage();
		resizeImage();
		c = this.getContentPane();
		all = new JLabel(loading);
//		all.setLayout(new GridLayout(4, 1, 0, 30));
		all.setLayout(new GridBagLayout());
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
	
	
	public static void main(String[] args) {
		new CardEditor().setVisible(true);
	}
	
	
	
	
}
