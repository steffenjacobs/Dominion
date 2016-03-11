package com.tpps.technicalServices.logger;

/**
 * 
 * @author nicolaswipfler
 *
 */
public enum MsgType {

	INIT("[INI]"),
	INFO("[INF]"),
	DEBUG("[BUG]"),
	EXCEPTION("[EXC]"),
	GAME("[GAM]");
	
	private String slang;

	private MsgType(String slang) {
		this.slang = slang;
	}
	
	public String getSlang() {
		return this.slang;
	}
}
