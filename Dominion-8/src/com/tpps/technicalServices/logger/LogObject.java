package com.tpps.technicalServices.logger;

import java.awt.Color;

public class LogObject {

	private MsgType type;
	private String line;
	private Color color;

	public LogObject(MsgType type, String line, Color color) {
		this.type = type;
		this.line = line;
		this.color = color;
	}

	/**
	 * @return the type
	 */
	public MsgType getType() {
		return type;
	}

	/**
	 * @return the line
	 */
	public String getLine() {
		return line;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
}
