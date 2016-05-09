package com.tpps.ui.lobbyscreen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import com.tpps.technicalServices.util.ImageLoader;

/**
 * An instance of this class handles one joined (or not) player in the lobby
 * only on the UI
 * 
 * @author jhuhn
 *
 */
public class SearchingField extends JTextField implements Runnable{

	private static final long serialVersionUID = 1L;
	private BufferedImage blackBeauty;
	private final Font font = new Font("Calibri", Font.PLAIN, 20);
	private boolean playerFlag;
	
	/**
	 * initializes the object, load image
	 * 
	 * @author jhuhn
	 */
	public SearchingField() {
		playerFlag = false;
		this.setFont(font);
		this.setOpaque(false);
		this.setFocusable(false);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setHorizontalAlignment(JTextField.CENTER);
		this.setForeground(Color.WHITE);
		
		
		this.blackBeauty = ImageLoader.getImage("black_0.6");
	}
	
	/**
	 * @param junitTest
	 *            just to overload the constructor for a junit test
	 * @author jhuhn
	 */
	public SearchingField(boolean junitTest){
		playerFlag = false;
	}
	
	/**
	 * overrides the paint method for semitransparend causes
	 * 
	 * @author jhuhn
	 */
	@Override
	public void paint(Graphics g) {							
		g.drawImage(blackBeauty, 0, 0, null);
		super.paint(g);
	}
	
	/**
	 * starts the thread, searching procedure on UI starts
	 * 
	 * @author jhuhn
	 */
	public void start(){
		new Thread(this).start();
	}

	/**
	 * executes the searching procedure on the GUI. possible states: ->
	 * Searching . -> Searching .. -> Searching ...
	 * 
	 * @author jhuhn
	 */
	@Override
	public void run() {
		int points = 1;
		while(!playerFlag){
			String appender = "";
			for (int j = 0; j < points; j++) {
				appender += ".";
			}
			this.setText("Searching " + appender);
			points++;
			if(points == 4){
				points = 1;
			}
			
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @author jhuhn
	 * @return a boolean which shows if a player already joined true, a player
	 *         already joined the lobby false, no player joined for this
	 *         searchingfield
	 */
	public boolean isPlayerFlag() {
		return playerFlag;
	}

	/**
	 * ends the searching procedure on the UI and sets the playername who joined
	 * 
	 * @author jhuhn
	 * @param playername
	 *            a String representation of the playername who joined the lobby
	 */
	public synchronized void setPlayer(String playername) {
		this.playerFlag = true;
		this.setText(playername);
	}
	
	/**
	 * resets the searchingfield instance
	 * 
	 * @author jhuhn
	 */
	public synchronized void resetSearchingField(){
		this.playerFlag = false;
		this.start();
	}
}
