package com.tpps.ui.lobbyscreen;
	
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.util.GraphicsUtil;
	
	public class KIButton extends JButton implements MouseListener, ActionListener{
	
		private static final long serialVersionUID = 1L;
		private BufferedImage switchimage;
		private BufferedImage brain;
		private BufferedImage hoverimage;
		private boolean kiAdd;
		private PlayerSettingsPanel playerSettingsPanel;
		
		public KIButton(BufferedImage brain,  boolean kiAdd, PlayerSettingsPanel playerSettingsPanel) {
			this.kiAdd = kiAdd;
			if(kiAdd){
				this.setText("+ KI");
			}else{
				this.setText("- KI");
			}
			this.setForeground(Color.BLACK);
			this.playerSettingsPanel = playerSettingsPanel;
			this.setFont(new Font("Arial", Font.BOLD, 22));
			this.setOpaque(false);
			this.setBorderPainted(true);
			this.setContentAreaFilled(false);
			this.brain = brain;
			this.switchimage = brain;
			this.hoverimage = brain;
			this.hoverimage = (BufferedImage) GraphicsUtil.setAlpha(hoverimage, 0.4F);
			this.setVisible(true);
			this.addMouseListener(this);
			this.addActionListener(this);
		}
		
		public void setBrain(BufferedImage brain) {
			this.brain = brain;
		}
		
		@Override
		public void paint(Graphics g) {
			Graphics2D h = (Graphics2D) g;
			h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			h.drawImage(switchimage, 0, 0, this.getWidth(), this.getHeight(), null);
			super.paint(h);
		}
	
		@Override
		public void mouseClicked(MouseEvent e) {	}
	
		@Override
		public void mouseEntered(MouseEvent e) {
			this.switchimage = hoverimage;
		}
	
		@Override
		public void mouseExited(MouseEvent e) {
			this.switchimage = brain;
		}
	
		@Override
		public void mousePressed(MouseEvent e) { }
	
		@Override
		public void mouseReleased(MouseEvent e) { }

		@Override
		public void actionPerformed(ActionEvent e) {
			if(kiAdd){
				this.playerSettingsPanel.getAiNames().add("" + System.identityHashCode(e));
				try {
					DominionController.getInstance().getMatchmaker().sendAIPacket("AI_" + System.identityHashCode(e),
							DominionController.getInstance().getLobbyID(), false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				this.playerSettingsPanel.handleStartButton();
			}else{
				try {
					DominionController.getInstance().getMatchmaker().sendAIPacket(
							"AI_" + this.playerSettingsPanel.getAiNames().remove(this.playerSettingsPanel.getAiNames().size() - 1), DominionController.getInstance().getLobbyID(),
							true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				this.playerSettingsPanel.handleStartButton();
			}
		}
	}
