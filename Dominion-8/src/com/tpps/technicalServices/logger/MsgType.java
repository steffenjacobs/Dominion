package com.tpps.technicalServices.logger;

import java.awt.Color;

import com.tpps.technicalServices.util.ColorUtil;

/**
 * @author nicolaswipfler
 */
public enum MsgType {

	INIT("[INIT]", ColorUtil.MEDIUMGRAY, true),
	INFO("[INFO]", ColorUtil.MEDIUMGRAY, true),
	DEBUG("[BUG]", Color.ORANGE, true),
	EXCEPTION("[EXCEPTION]", Color.RED, true),
	ERROR("[ERROR]", Color.RED, true),
	GAME("[GAME]", Color.GREEN, true),
	NETWORK_INFO("[NETWORK-INFO]", Color.BLUE, true),
	NETWORK_ERROR("[NETWORK-ERROR]", Color.MAGENTA, true);

	private String slang;
	private Color color;
	private boolean display;

	private MsgType(String slang, Color awtColor, boolean display) {
		this.slang = slang;
		this.color = awtColor;
		this.display = display;
	}

	/**
	 * @return
	 */
	public String getSlang() {
		return this.slang;
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
}
