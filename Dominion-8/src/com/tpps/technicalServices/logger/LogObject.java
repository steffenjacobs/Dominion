package com.tpps.technicalServices.logger;

import java.awt.Color;

/**
 * represents a LogObject with type, line and color
 * 
 * @author Nicolas
 *
 */
public class LogObject {

	private MsgType type;
	private String line;
	private Color color;

	/**
	 * @param type the type
	 * @param line the line
	 * @param color the color
	 */
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
