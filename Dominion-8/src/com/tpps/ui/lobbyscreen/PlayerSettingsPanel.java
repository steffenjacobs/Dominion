package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.statisticsscreen.StatisticsBoard;

public class PlayerSettingsPanel extends JPanel{

	private static final long serialVersionUID = 1L;
 //	private final Font font = new Font("Calibri", Font.PLAIN, 20);
	private final Font head = new Font("Arial Black", Font.BOLD, 20);
	private final Font optionsFont = new Font("Calibri", Font.BOLD, 21);
	
	private BufferedImage[] images = new BufferedImage[4];
	private JLabel[] labelImages;
	boolean imageselected;
	private BufferedImage blackBeauty;
	private BufferedImage selectedImage;
	
	private SearchingField[] connectedPlayers;
	
	private JCheckBox[] options;
	
	private static final int SPACE_PANEL_TO_PANEL = 25;
	private static final int SPACE_PLAYER_TO_PLAYER = 5;
	private static final int SPACE_FIRSTPANEL_TO_SECONDPANEL = 10;
	private static int H_SPACE_EDGE_TO_FIRSTPANEL = 170;
	private static int H_GAP_OPTIONS = 100;
	private static int V_GAP_OPTIONS = 20;
	private static int EMPTYBORDER_LEFT_RIGHT = 100;
	private static int EMPTYBORDER_UP_BOTTOM = 25;
	private static float OPTIONS_TRANSPARENCY = 0.6F;
	private static final int IMG_GRID_GAP = 20;
	private static int HEADER_TO_IMG_MARGIN = 15;
	private static int IMG_TO_BOTTOM = 15;
	private static final int IMG_TO_EDGE = 30;
	
	public PlayerSettingsPanel() {
		this.setOpaque(false);
		this.setLayout(new GridLayout(3,1, 0, SPACE_PANEL_TO_PANEL));

		this.add(this.upperAreaPanel());
		this.add(this.middleAreaPanel());
		this.add(this.bottomAreaPanel());
	}
	
	private JPanel upperAreaPanel(){
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		
		connectedPlayers = new SearchingField[4];
		for (int i = 0; i < connectedPlayers.length; i++) {
			connectedPlayers[i] = new SearchingField();
			connectedPlayers[i].start();
		}
		
		JPanel center = new JPanel(new GridLayout(8,1));
		center.setOpaque(false);
		
		center.add(Box.createVerticalStrut(SPACE_PLAYER_TO_PLAYER));
		center.add(connectedPlayers[0]);
		center.add(Box.createVerticalStrut(SPACE_PLAYER_TO_PLAYER));
		
		center.add(connectedPlayers[1]);
		center.add(Box.createVerticalStrut(SPACE_PLAYER_TO_PLAYER));
		
		center.add(connectedPlayers[2]);
		center.add(Box.createVerticalStrut(SPACE_PLAYER_TO_PLAYER));
		
		center.add(connectedPlayers[3]);
		
		JTextField header = this.createHeader("Connected Players:");		
		
		panel.add(center, BorderLayout.CENTER);
		panel.add(header, BorderLayout.PAGE_START);
		panel.add(Box.createVerticalStrut(SPACE_FIRSTPANEL_TO_SECONDPANEL), BorderLayout.PAGE_END);
		panel.add(Box.createHorizontalStrut(H_SPACE_EDGE_TO_FIRSTPANEL), BorderLayout.LINE_END);
		panel.add(Box.createHorizontalStrut(H_SPACE_EDGE_TO_FIRSTPANEL), BorderLayout.LINE_START);
		return panel;		
	}
	
	private JPanel middleAreaPanel(){
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);		
		panel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
		
/*		JPanel optionPanel = new JPanel(new GridLayout(3,3,H_GAP_OPTIONS,V_GAP_OPTIONS));
		
		optionPanel.setBorder(BorderFactory.createEmptyBorder(EMPTYBORDER_UP_BOTTOM, EMPTYBORDER_LEFT_RIGHT, EMPTYBORDER_UP_BOTTOM, EMPTYBORDER_LEFT_RIGHT));
		optionPanel.setOpaque(false);
		this.options = new JCheckBox[6];
		
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, OPTIONS_TRANSPARENCY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		for (int i = 0; i < 6; i++) {
			this.options[i] = new JCheckBox("Option " + i){				
				private static final long serialVersionUID = 1L;

				@Override
				public void paint(Graphics g) {
					g.drawImage(blackBeauty, 0, 0, null);
					super.paint(g);
				}
			};
			this.options[i].setOpaque(false);
			this.options[i].setFont(optionsFont);
			this.options[i].setForeground(Color.WHITE);
			this.options[i].setHorizontalAlignment(JCheckBox.CENTER);
			
			optionPanel.add(options[i]);
		} */
		
		JTextField header = this.createHeader("Statistics: ");
		
		panel.add(header, BorderLayout.PAGE_START);
		panel.add(new StatisticsBoard(), BorderLayout.CENTER);
		panel.add(Box.createHorizontalStrut(10), BorderLayout.LINE_START);
		panel.add(Box.createHorizontalStrut(10), BorderLayout.LINE_END);
		return panel;
	}

	private JPanel bottomAreaPanel(){
		JPanel overhead = new JPanel(new BorderLayout());
		overhead.setOpaque(false);
		
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(2,2, IMG_GRID_GAP, IMG_GRID_GAP));
		
		labelImages = new JLabel[4];
		for (int i = 0; i < labelImages.length; i++) {
			labelImages[i] = new JLabel();
			labelImages[i].addMouseListener(new ImageListener());
			labelImages[i].setHorizontalAlignment(JLabel.CENTER);
		}
		this.changeSelectedPicture(0);
		
		panel.add(labelImages[0]);
		panel.add(labelImages[1]);
		panel.add(labelImages[2]);
		panel.add(labelImages[3]);
		
		JTextField header = this.createHeader("Choose Background:");
		header.setBorder(BorderFactory.createEmptyBorder(0, 0, HEADER_TO_IMG_MARGIN, 0));
		
		overhead.add(panel, BorderLayout.CENTER);
		overhead.add(header, BorderLayout.PAGE_START);
		overhead.add(Box.createVerticalStrut(IMG_TO_BOTTOM), BorderLayout.PAGE_END);
		overhead.add(Box.createHorizontalStrut(IMG_TO_EDGE), BorderLayout.LINE_START);
		overhead.add(Box.createHorizontalStrut(IMG_TO_EDGE), BorderLayout.LINE_END);		
		return overhead;
	}
	
	private class ImageListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {	
			if(e.getSource() == labelImages[0]){
				PlayerSettingsPanel.this.changeSelectedPicture(0);
			}else if(e.getSource() == labelImages[1]){
				PlayerSettingsPanel.this.changeSelectedPicture(1);
			}else if(e.getSource() == labelImages[2]){
				PlayerSettingsPanel.this.changeSelectedPicture(2);
			}else if(e.getSource() == labelImages[3]){
				PlayerSettingsPanel.this.changeSelectedPicture(3);
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) { }

		@Override
		public void mouseExited(MouseEvent arg0) { }

		@Override
		public void mousePressed(MouseEvent arg0) { }

		@Override
		public void mouseReleased(MouseEvent arg0) { }		
	}
	
	public void changeSelectedPicture(int index){
		try {
			this.images[0] = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/spring.jpg"));
			this.images[0] = (BufferedImage) GraphicsUtil.setAlpha(images[0], 0.5F);
			this.images[1] = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/summer.jpg"));
			this.images[1] = (BufferedImage) GraphicsUtil.setAlpha(images[1], 0.5F);
			this.images[2] = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/fall.jpg"));
			this.images[2] = (BufferedImage) GraphicsUtil.setAlpha(images[2], 0.5F);
			this.images[3] = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/winter.jpg"));
			this.images[3] = (BufferedImage) GraphicsUtil.setAlpha(images[3], 0.5F);
		} catch (IOException e) {		
			e.printStackTrace();
		}
		
		for (int i = 0; i < images.length; i++) {
			if(i == index){
				this.setAlphaOnPicture(index);
			}else{
				labelImages[i].setIcon(new ImageIcon(images[i]));
				labelImages[i].setBorder(BorderFactory.createLineBorder(Color.RED ,3));
			}
		}
	}
	
	public void setAlphaOnPicture(int index){
		String picture = "";
		switch(index){
			case 0: picture = "spring"; break;
			case 1: picture = "summer"; break;
			case 2: picture = "fall"; break;
			case 3: picture = "winter"; break;
			default:return;
		}
		try {
			this.images[index] = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/" + picture + ".jpg"));
			selectedImage = this.images[index];
			labelImages[index].setIcon(new ImageIcon(this.images[index]));
			labelImages[index].setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	
	public JTextField createHeader(String text){
		JTextField header = new JTextField(text);
		Map attributes = head.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		header.setFont(head.deriveFont(attributes));
		
		header.setOpaque(false);
		header.setFocusable(false);
		header.setBorder(BorderFactory.createEmptyBorder());
		header.setHorizontalAlignment(JTextField.LEFT);
		header.setForeground(Color.WHITE);
		return header;
	}
	
	public BufferedImage getSelectedPicture(){
		return this.selectedImage;
	}
	
	public boolean insertPlayer(String player){
		for (int i = 0; i < connectedPlayers.length; i++) {
			if(!connectedPlayers[i].isPlayerFlag()){
				this.connectedPlayers[i].setPlayer(player);
				return true;
			}
		}
		return false;
	}
	
	public JCheckBox[] getOptions() {
		return options;
	}
}
