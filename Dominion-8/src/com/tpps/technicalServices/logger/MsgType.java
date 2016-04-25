package com.tpps.technicalServices.logger;

import java.awt.Color;

import com.tpps.technicalServices.util.ColorUtil;

/**
 * MsgType defines the type of the log Message
 * 
 * 
 * @author nicolaswipfler
 */
public enum MsgType {

	INIT("[INIT]", ColorUtil.MEDIUMGRAY, true, true),
	INFO("[INFO]", ColorUtil.MEDIUMGRAY, true, true),
	DEBUG("[BUG]", Color.ORANGE, true, true),
	EXCEPTION("[EXCEPTION]", Color.RED, true, true),
	ERROR("[ERROR]", Color.RED, true, true),
	GAME("[GAME]", Color.GREEN, true, true),
	NETWORK_INFO("[NETWORK-INFO]", Color.BLUE, true, true),
	NETWORK_ERROR("[NETWORK-ERROR]", Color.MAGENTA, true, true);

	/**
	 * message is the String which shows the type of the log in GameLog (in front of every message)
	 */
	private String message;
	/**
	 * color of the log message
	 */
	private Color color;
	/**
	 * display is a boolean value which determines if messages of this type will be displayed in the GameLog
	 */
	private boolean display;
	/**
	 * timestamp is a boolean value which determines if there will be a timestamp in front of the log message
	 */
	private boolean timestamp;

	/**
	 * 
	 * @param message
	 * @param awtColor
	 * @param display
	 * @param timestamp
	 */
	private MsgType(String message, Color awtColor, boolean display, boolean timestamp) {
		this.message = message;
		this.color = awtColor;
		this.display = display;
		this.timestamp = timestamp;
	}

	/**
	 * 
	 */
	public static void setGameMode() {
		for (MsgType m : MsgType.values()) {
			if (m.equals(MsgType.GAME))
				m.setDisplay(true);
			else m.setDisplay(false);
		}
	}

	/**
	 * 
	 */
	public static void resetGameMode() {
		for (MsgType m : MsgType.values()) {
			m.setDisplay(true);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * 
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 
	 * @return
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * 
	 * @param c
	 */
	public void setColor(Color c) {
		this.color = c;
	}

	/**
	 * 
	 * @return
	 */
	public boolean getDisplay() {
		return this.display;
	}

	/**
	 * 
	 * @param display
	 */
	public void setDisplay(boolean display) {
		this.display = display;
	}

	/**
	 * 
	 * @return
	 */
	public boolean getTimeStamp(){
		return this.timestamp;
	}

	/**
	 * 
	 * @param timestamp
	 */
	public void setTimeStamp(boolean timestamp) {
		this.timestamp = timestamp;
	}
}
