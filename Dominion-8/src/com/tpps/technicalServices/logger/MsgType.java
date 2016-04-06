package com.tpps.technicalServices.logger;

import java.awt.Color;

import com.tpps.technicalServices.util.ColorUtil;

/**
 * @author nicolaswipfler
 */
public enum MsgType {

	INIT("[INIT]", ColorUtil.MEDIUMGRAY, true, true),
	INFO("[INFO]", ColorUtil.MEDIUMGRAY, true, true),
	DEBUG("[BUG]", Color.ORANGE, true, true),
	EXCEPTION("[EXCEPTION]", Color.RED, true, true),
	ERROR("[ERROR]", Color.RED, true, true),
	GAME("[GAME]", Color.GREEN, true, false),
	NETWORK_INFO("[NETWORK-INFO]", Color.BLUE, true, true),
	NETWORK_ERROR("[NETWORK-ERROR]", Color.MAGENTA, true, true);

	private String slang;
	private Color color;
	private boolean display;
	private boolean timestamp;

	private MsgType(String slang, Color awtColor, boolean display, boolean timestamp) {
		this.slang = slang;
		this.color = awtColor;
		this.display = display;
		this.timestamp = timestamp;
	}

	public static void setGameMode() {
		for (MsgType m : MsgType.values()) {
			if (m.equals(MsgType.GAME))
				m.setDisplay(true);
			else m.setDisplay(false);
		}
	}
	
	public static void resetGameMode() {
		for (MsgType m : MsgType.values()) {
			m.setDisplay(true);
		}
	}
	/**
	 * @return
	 */
	public String getSlang() {
		return this.slang;
	}
	
	public void setSlang(String slang) {
		this.slang = slang;
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(Color c) {
		this.color = c;
	}
	
	public boolean getDisplay() {
		return this.display;
	}
	
	public void setDisplay(boolean display) {
		this.display = display;
	}
	
	public boolean getTimeStamp(){
		return this.timestamp;
	}
	
	public void setTimeStamp(boolean timestamp) {
		this.timestamp = timestamp;
	}
}
