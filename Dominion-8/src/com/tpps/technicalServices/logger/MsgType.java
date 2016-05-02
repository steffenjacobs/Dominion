package com.tpps.technicalServices.logger;

import java.awt.Color;

import com.tpps.technicalServices.util.ANSIUtil;
import com.tpps.technicalServices.util.ColorUtil;

/**
 * MsgType defines the type of the log Message
 * 
 * 
 * @author nicolaswipfler
 */
public enum MsgType {

	INIT("[INITIALIZED]", ColorUtil.MEDIUMGRAY, true, true, ANSIUtil.ANSI_GREEN),
	INFO("[INFO]", ColorUtil.MEDIUMGRAY, true, true, ANSIUtil.ANSI_CYAN),
	DEBUG("[BUG]", Color.ORANGE, true, true, ANSIUtil.ANSI_RED),
	EXCEPTION("[EXCEPTION]", Color.RED, true, true, ANSIUtil.ANSI_RED),
	ERROR("[ERROR]", Color.RED, true, true, ANSIUtil.ANSI_RED),
	GAME("[GAME]", Color.GREEN, true, true, ANSIUtil.ANSI_GREEN),
	AI("[AI]",Color.ORANGE, true, true, ANSIUtil.ANSI_GREEN),
	NETWORK_INFO("[NETWORK-INFO]", Color.BLUE, true, true, ANSIUtil.ANSI_BLUE),
	NETWORK_ERROR("[NETWORK-ERROR]", Color.MAGENTA, true, true, ANSIUtil.ANSI_MAGENTA),
	STATISTICS("[STATS]",Color.YELLOW,true,true, ANSIUtil.ANSI_YELLOW);

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

	private String ansiColor;
	
	/**
	 * 
	 * @param message
	 * @param awtColor
	 * @param display
	 * @param timestamp
	 */
	private MsgType(String message, Color awtColor, boolean display, boolean timestamp, String ansiColor) {
		this.message = message;
		this.color = awtColor;
		this.display = display;
		this.timestamp = timestamp;
		this.ansiColor = ansiColor;
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
	 * @return the timestamp
	 */
	public boolean isTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(boolean timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the ansiColor
	 */
	public String getAnsiColor() {
		return ansiColor;
	}

	/**
	 * @param ansiColor the ansiColor to set
	 */
	public void setAnsiColor(String ansiColor) {
		this.ansiColor = ansiColor;
	}
}
