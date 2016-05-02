package com.tpps.ui.endscreen;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.sun.javafx.tk.Toolkit;
import com.sun.xml.internal.ws.api.Component;
import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.util.Loader;
import com.tpps.ui.GraphicFramework;

import javafx.scene.layout.Border;
import sun.font.CreatedFontTracker;

/**
 * 
 * @author Nishit Agrawal - nagrawal
 *
 */
public class EndPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private BufferedImage originalBackground;
	private JLabel background;
	private JLabel header;
	private JPanel center;
	private JButton returnButton;
	private Font customFont;
	
	public EndPanel() {
		this.setLayout(new BorderLayout());
		this.setVisible(true);
		try {
			this.originalBackground = ImageIO.read(ClassLoader.getSystemResource("resources/img/loginScreen/LoginBackground.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
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
		

		this.setVisible(true);
		this.background = new JLabel(new ImageIcon(originalBackground));
		this.background.setLayout(new BorderLayout());
		fontLoading();
		createPanel1();
		createPanel2();
		createPanel3();
		
		this.add(background);
	}
	
	
	public void createPanel1(){
		header = new JLabel("Results",SwingConstants.CENTER);
		header.setForeground(Color.WHITE);
		header.setFont(customFont.deriveFont(100f));
		background.add(header,BorderLayout.PAGE_START);
	}
	
	public void createPanel2(){
		center = new JPanel(new GridBagLayout());
		center.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		background.add(center,BorderLayout.CENTER);
	}
	
	public void createPanel3(){
		returnButton = new JButton("Return");
		returnButton.setVisible(true);
		JPanel temp = new JPanel(new FlowLayout());
		temp.setOpaque(false);
		temp.add(returnButton);
		background.add(temp,BorderLayout.SOUTH);
		returnButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DominionController.getInstance().joinMainMenu();
			}
		});
		
	}
	
	public void playerOne(String playerOne){
		
	}
	
	public void playerTwo(String playerTwo){
		
	}
	public void playerThree(String playerThree){
		
	}
	public void playerFour(String playerFour){
		
	}
	
	
	public void fontLoading(){
		try {
			if (customFont == null) {
				customFont = Loader.getInstance().getXenipa();
				if (customFont == null){
					customFont = new Loader().importFont();
				}
			}
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.add(new EndPanel());
		jf.setMinimumSize(new Dimension(1920,1000));
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
}
