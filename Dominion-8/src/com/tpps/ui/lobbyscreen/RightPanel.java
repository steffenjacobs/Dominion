package com.tpps.ui.lobbyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.tpps.technicalServices.util.GraphicsUtil;

public class RightPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	JTextField[] names = new JTextField[4];
	LobbyScreen parent;
	Font font = new Font("Calibri", Font.PLAIN, 20);
	Font head = new Font("Calibri", Font.BOLD, 23);
	
	private BufferedImage[] images = new BufferedImage[4];
	JLabel[] labelImages;
	boolean imageselected;
	BufferedImage blackBeauty;
	
	public RightPanel(LobbyScreen parent) {
		this.parent = parent;
		this.setOpaque(false);
		this.setLayout(new GridLayout(3,1, 0, 0));
	//	this.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		this.add(this.firstPanel());
		this.add(this.middlePanel());
		this.add(this.lastPanel());
//		parent.revalidate();
//		parent.repaint();
	}
	
	private JPanel firstPanel(){
		JPanel panel = new JPanel(new BorderLayout());
	//	panel.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
		panel.setOpaque(false);
		
		try {
			this.blackBeauty = ImageIO.read(ClassLoader.getSystemResource("resources/img/lobbyScreen/blackbeauty.png"));
			blackBeauty = (BufferedImage) GraphicsUtil.setAlpha(blackBeauty, 0.6F);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < names.length; i++) {
			names[i] = new JTextField("Searching..."){

				private static final long serialVersionUID = 1L;
				
				@Override
				public void paint(Graphics g) {									
					g.drawImage(blackBeauty, 0, 0, null);
					super.paint(g);
				}
			};			
			
//			Runnable thread1 = () -> {
//				int points = 1;
//				while(true){
//					String appender = "";
//					for (int j = 0; j < points; j++) {
//						appender += ".";
//					}
//					ExampleRight.this.names[counter].setText("Loading " + appender);
//					points++;
//					if(points == 4){
//						points = 1;
//					}
//				}
//
//			};
//			
//			thread1.run();

			
//			Thread t = new Thread(){
//				
//				@Override
//				public void run(){
//					int points = 1;
//					while(true){
//						String appender = "";
//						for (int j = 0; j < points; j++) {
//							appender += ".";
//						}
//						names[i].setText("Loading " + appender);
//						points++;
//						if(points == 4){
//							points = 1;
//						}
//					}
//				}
//			};
			
			names[i].setFont(font);
			names[i].setOpaque(false);
			names[i].setFocusable(false);
			names[i].setBorder(BorderFactory.createEmptyBorder());
			names[i].setHorizontalAlignment(JTextField.CENTER);
			names[i].setForeground(Color.WHITE);
		}
		
		JPanel center = new JPanel(new GridLayout(7,1));
		center.setOpaque(false);
	//	center.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
		center.add(names[0]);
		center.add(Box.createVerticalStrut(5));
		
		center.add(names[1]);
		center.add(Box.createVerticalStrut(5));
		
		center.add(names[2]);
		center.add(Box.createVerticalStrut(5));
		
		center.add(names[3]);
//		center.add(Box.createVerticalStrut(5));
		
		
		//-------header-----
		JTextField header = new JTextField("Connected Players");
		Map attributes = head.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		header.setFont(head.deriveFont(attributes));
		
		header.setOpaque(false);
		header.setFocusable(false);
		header.setBorder(BorderFactory.createEmptyBorder());
		header.setHorizontalAlignment(JTextField.LEFT);
		header.setForeground(Color.WHITE);
		//------------
		
		
		panel.add(center, BorderLayout.CENTER);
		panel.add(header, BorderLayout.PAGE_START);
		panel.add(Box.createVerticalStrut(30), BorderLayout.PAGE_END);
		panel.add(Box.createHorizontalStrut(170), BorderLayout.LINE_END);
		panel.add(Box.createHorizontalStrut(170), BorderLayout.LINE_START);
		return panel;
		
	}
	
	private JPanel middlePanel(){
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		return panel;
	}

	private JPanel lastPanel(){
		JPanel overhead = new JPanel(new BorderLayout());
		overhead.setOpaque(false);
		
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(2,2, 20, 20));
		
		labelImages = new JLabel[4];
		for (int i = 0; i < labelImages.length; i++) {
			labelImages[i] = new JLabel();
			labelImages[i].addMouseListener(new ImageListener(labelImages[i]));
			labelImages[i].setHorizontalAlignment(JLabel.CENTER);
			labelImages[i].setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		}
		
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
		
		labelImages[0].setIcon((new ImageIcon(images[0])));
		labelImages[1].setIcon((new ImageIcon(images[1])));
		labelImages[2].setIcon((new ImageIcon(images[2])));
		labelImages[3].setIcon((new ImageIcon(images[3])));
		
		panel.add(labelImages[0]);
		panel.add(labelImages[1]);
		panel.add(labelImages[2]);
		panel.add(labelImages[3]);
		
		overhead.add(panel, BorderLayout.CENTER);
		overhead.add(Box.createVerticalStrut(20), BorderLayout.PAGE_START);
		overhead.add(Box.createVerticalStrut(20), BorderLayout.PAGE_END);
		overhead.add(Box.createHorizontalStrut(20), BorderLayout.LINE_START);
		overhead.add(Box.createHorizontalStrut(20), BorderLayout.LINE_END);
		
		return overhead;
	}
	
	private class ImageListener implements MouseListener{

		JLabel labelImages;
		
		public ImageListener(JLabel labelImages){
			this.labelImages = labelImages;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			//if(e.getSource().equals(labelImages)){
				System.out.println(labelImages.toString());
				BufferedImage img = RightPanel.this.iconToBufferedImage(labelImages.getIcon());
				img = (BufferedImage) GraphicsUtil.setAlpha(img, 1F);
				labelImages.setIcon(new ImageIcon(img));
			//	labelImages.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));				
			//}
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
	
	public BufferedImage iconToBufferedImage(Icon icon){
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0,0);
		g.dispose();
		return bi;
	}

}
