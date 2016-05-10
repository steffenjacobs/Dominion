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
import com.tpps.technicalServices.util.MyAudioPlayer;

/**
 * KI + or KI - Button class. This class is used to add or remove KI's
 * 
 * @author jhuhn
 */
public class KIButton extends JButton implements MouseListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private BufferedImage switchimage;
	private BufferedImage brain;
	private BufferedImage hoverimage;
	private boolean aiAdd;
	private PlayerSettingsPanel playerSettingsPanel;

	/**
	 * initializes the KI Button instance
	 * 
	 * @author jhuhn
	 * @param brain
	 *            Image that is shown instead of the standard JButton look
	 * @param aiAdd
	 *            boolean true: add KI, false: remove KI
	 * @param playerSettingsPanel
	 *            playersettingspanel instance to connect functionalities
	 */
	public KIButton(BufferedImage brain, boolean aiAdd, PlayerSettingsPanel playerSettingsPanel) {
		this.aiAdd = aiAdd;
		if (aiAdd) {
			this.setText("+ AI");
		} else {
			this.setText("- AI");
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

	/**
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
    public void paint(Graphics g) {
        Graphics2D h = (Graphics2D) g;
        if (System.getProperty("os.name").startsWith("Windows")) {
            h.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            h.drawImage(switchimage, 0, 0, this.getWidth(), this.getHeight(), null);
        }
        super.paint(h);
    }

	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		this.switchimage = hoverimage;
	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		this.switchimage = brain;
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		MyAudioPlayer.doClick();
		//add ki		
		if (aiAdd) {
			if(this.playerSettingsPanel.getAllplayers() < 4){
				PlayerSettingsPanel.kicount++;
				String aiName = this.playerSettingsPanel.getAiName();
				try {
					DominionController.getInstance().getMatchmaker().sendAIPacket(aiName + " (AI)", DominionController.getInstance().getLobbyID(), false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				this.playerSettingsPanel.handleStartButton();
			}					
		//remove ki	
		} else {
			try {
				if(PlayerSettingsPanel.kicount > 0){
					PlayerSettingsPanel.kicount--;
					DominionController.getInstance().getMatchmaker().sendAIPacket(this.playerSettingsPanel.getAiNames().remove(this.playerSettingsPanel.getAiNames().size() - 1) + " (AI)", DominionController.getInstance().getLobbyID(), true);
				}								
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			this.playerSettingsPanel.handleStartButton();
		}
		System.err.println("kicount: " + PlayerSettingsPanel.kicount);
	}
}
