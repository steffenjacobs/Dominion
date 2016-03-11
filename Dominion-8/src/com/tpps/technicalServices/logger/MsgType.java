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

	/**
	 * 
	 * @param slang
	 */
	private MsgType(String slang) {
		this.slang = slang;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSlang() {
		return this.slang;
	}
}
