package com.tpps.technicalServices.logger;

import java.awt.Color;

import com.tpps.technicalServices.util.ColorUtil;

/**
 * @author nicolaswipfler
 */
public enum MsgType {

	INIT("[INIT]", ColorUtil.MEDIUMGRAY),
	INFO("[INFO]", ColorUtil.MEDIUMGRAY),
	DEBUG("[BUG]", Color.ORANGE),
	EXCEPTION("[EXCEPTION]", Color.RED),
	GAME("[GAME]", Color.GREEN),
	NETWORK_INFO("[NETWORK-INFO]", Color.BLUE),
	NETWORK_ERROR("[NETWORK-ERROR]", Color.MAGENTA);

	private String slang;
	private Color color;

	private MsgType(String slang, Color awtColor) {
		this.slang = slang;
		this.color = awtColor;
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
}
