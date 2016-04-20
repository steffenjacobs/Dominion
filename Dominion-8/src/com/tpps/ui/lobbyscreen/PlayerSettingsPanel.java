package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.tpps.application.game.DominionController;
import com.tpps.application.game.ai.ArtificialIntelligence;
import com.tpps.technicalServices.network.matchmaking.packets.PacketJoinLobby;
import com.tpps.technicalServices.util.GraphicsUtil;
import com.tpps.ui.statisticsscreen.StatisticsBoard;

public class PlayerSettingsPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private final Font head = new Font("Arial Black", Font.BOLD, 20);
	
	private BufferedImage[] originalImages = new BufferedImage[4];
	private BufferedImage[] transparentImages = new BufferedImage[4];
	private BufferedImage selectedImage;
	private JLabel[] labelImages;
		
	private SearchingField[] connectedPlayers;	
	private JCheckBox[] options;
	
	private static final int SPACE_PANEL_TO_PANEL = 25;
	private static final int SPACE_PLAYER_TO_PLAYER = 5;
	private static final int SPACE_FIRSTPANEL_TO_SECONDPANEL = 10;
	private static int H_SPACE_EDGE_TO_FIRSTPANEL = 170;
	private static final int IMG_GRID_GAP = 20;
	private static int HEADER_TO_IMG_MARGIN = 15;
	private static int IMG_TO_BOTTOM = 15;
	private static final int IMG_TO_EDGE = 30;
	
	private JButton plusKI,minusKi;
	
	
//	TODO Statistics einkommentieren
//	private StatisticsBoard statisticsBoardPanel;
	
	private JPanel panel;
	JPanel panelMid;
	
	public PlayerSettingsPanel(StatisticsBoard statisticsBoardPanel) {
		this.initOriginalBackgroundImages();
		this.initTransparentBackgroundImages();
//		this.statisticsBoardPanel = statisticsBoardPanel;
		this.setOpaque(false);
		this.setLayout(new GridLayout(3,1, 0, SPACE_PANEL_TO_PANEL));

		this.add(this.upperAreaPanel());
		this.add(this.middleAreaPanel());
		this.add(this.bottomAreaPanel());
	}
	
	private JPanel upperAreaPanel(){
		this.panel = new JPanel(new BorderLayout());
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
//		panelMid = new JPanel(new BorderLayout());
		panelMid = new JPanel(new FlowLayout());
		JTextField header = this.createHeader("Statistics: ");
		panelMid.setOpaque(false);						
		panelMid.add(header, BorderLayout.PAGE_START);
		plusKI = new JButton("Add KI");
		minusKi = new JButton("Remove KI");
//		panelMid.add(this.statisticsBoardPanel, BorderLayout.CENTER);
		panelMid.add(plusKI,BorderLayout.CENTER);
		panelMid.add(minusKi,BorderLayout.CENTER);
		panelMid.add(Box.createHorizontalStrut(10), BorderLayout.LINE_START);
		panelMid.add(Box.createHorizontalStrut(10), BorderLayout.LINE_END);
		plusKI.addActionListener(new KiListener());
		minusKi.addActionListener(new KiListener());
		return panelMid;
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
		
		this.initStandardBackground();
		
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
	
	public void initOriginalBackgroundImages(){
		try {
			this.originalImages[0] = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/spring.jpg"));
			this.originalImages[1] = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/summer.jpg"));
			this.originalImages[2] = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/fall.jpg"));
			this.originalImages[3] = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/winter.jpg"));
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	public void initTransparentBackgroundImages(){			
			this.transparentImages[0] = (BufferedImage) GraphicsUtil.setAlpha(originalImages[0], 0.5F);			
			this.transparentImages[1] = (BufferedImage) GraphicsUtil.setAlpha(originalImages[1], 0.5F);			
			this.transparentImages[2] = (BufferedImage) GraphicsUtil.setAlpha(originalImages[2], 0.5F);
			this.transparentImages[3] = (BufferedImage) GraphicsUtil.setAlpha(originalImages[3], 0.5F);
	}
	
	
	
	public void changeSelectedPicture(int index){		
		for (int i = 0; i < originalImages.length; i++) {
			if(i == index){
				labelImages[i].setIcon(new ImageIcon(originalImages[i]));
				labelImages[i].setBorder(BorderFactory.createLineBorder(Color.GREEN ,3));
				this.selectedImage = originalImages[i];
			}else{
				labelImages[i].setIcon(new ImageIcon(transparentImages[i]));
				labelImages[i].setBorder(BorderFactory.createLineBorder(Color.RED ,3));
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		System.out.println("ZERO: " + this.selectedImage);
		return this.selectedImage;
	}
	
	public boolean insertPlayer(String player){
		for (int i = 0; i < connectedPlayers.length; i++) {
			if(!connectedPlayers[i].isPlayerFlag()){
				this.connectedPlayers[i].setPlayer(player);
				System.out.println("GUI: inserted Player: " + connectedPlayers[i].getText());
				return true;
			}
		}
		return false;
	}
	
	public boolean removePlayer(String player){
		for (int i = 0; i < connectedPlayers.length; i++) {
			if(connectedPlayers[i].getText().equals(player)){
				System.out.println("GUI: removed Player: " + connectedPlayers[i].getText());
				connectedPlayers[i].resetSearchingField();
				return true;
			}
		}
		return false;
	}
	
	public void clearAllPlayers(){
		for (int i = 0; i < connectedPlayers.length; i++) {
			if(!connectedPlayers[i].getText().startsWith("Loading")){
				System.out.println("GUI: removed Player(all): " + connectedPlayers[i].getText());
				connectedPlayers[i].resetSearchingField();
			}
		}
	}
	
	public void initStandardBackground(){
		this.changeSelectedPicture(0);
	}
	
	public JCheckBox[] getOptions() {
		return options;
	}
	
	private class KiListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.out.println("click");
			
//			??
//			ArtificialIntelligence ai1 = new ArtificialIntelligence();
			DominionController.getInstance().sendAIPacket("todo test ai" + System.identityHashCode(this));
		}
	}
	
//	public void setStatisticsBoardPanel(StatisticsBoard statisticsBoardPanel) {
//		this.statisticsBoardPanel = statisticsBoardPanel;
//	}
}
