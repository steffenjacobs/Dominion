package com.tpps.technicalServices.network;

import java.io.File;

import com.tpps.technicalServices.util.AutoCreatingProperties;

public final class Addresses {

	private static AutoCreatingProperties conf;

	private static final String CONFIG_FILE = "addresses.cfg";

	static {

		conf = new AutoCreatingProperties();
		conf.load(new File(CONFIG_FILE));
	}

	public static final String getLocalHost() {
		return conf.getProperty("LOCAL_HOST", "127.0.0.1");
	}

	public static final String getAllInterfaces() {
		return conf.getProperty("LOCAL_INTERFACE", "0.0.0.0");

	}

	public static final String getRemoteAddress() {
		return conf.getProperty("REMOTE_HOST", "78.31.66.224");
	}

	public static final void setRemoteHost(String newAddress) {
		conf.setProperty("REMOTE_HOST", newAddress);
	}
}