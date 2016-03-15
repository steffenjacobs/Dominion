package com.tpps.technicalServices.logger;

import java.awt.Color;


/**
 * @author nicolaswipfler
 */
public enum MsgType {

	INIT("[INIT]", new Color(215, 215, 215)),
	INFO("[INFO]", new Color(215, 215, 215)),
	DEBUG("[BUG]", Color.ORANGE),
	EXCEPTION("[EXCEPTION]", Color.RED),
	GAME("[GAME]", new Color(255,250,250)),
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
