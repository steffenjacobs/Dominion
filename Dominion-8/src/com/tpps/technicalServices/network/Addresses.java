package com.tpps.technicalServices.network;

import java.io.File;

import com.tpps.technicalServices.util.AutoCreatingProperties;

/**
 * contains all relevant IP-addresses & interfaces
 * 
 * @author Steffen Jacobs
 */
public final class Addresses {

	private static AutoCreatingProperties conf;

	private static final String CONFIG_FILE = "addresses.cfg";

	private static AutoCreatingProperties sqlconf;

	private static final String SQL_CONFIG_FILE = "sqladdresses.cfg";

	private static String remoteAddress;

	static {
		conf = new AutoCreatingProperties();
		conf.load(new File(CONFIG_FILE));
		sqlconf = new AutoCreatingProperties();
		sqlconf.load(new File(SQL_CONFIG_FILE));
		remoteAddress = conf.getProperty("REMOTE_HOST", "78.31.66.224");
	}

	/** @return 127.0.0.1 */
	public static final String getLocalHost() {
		return conf.getProperty("LOCAL_HOST", "127.0.0.1");
	}

	/** @return 0.0.0.0 */
	public static final String getAllInterfaces() {
		return conf.getProperty("LOCAL_INTERFACE", "0.0.0.0");

	}

	/** @return the remote-host address */
	public static final String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * sets the remote-host address to a specific IP (used for local-server)
	 * 
	 * @param newAddress
	 *            the new IP-Address-String
	 */
	public static final void setRemoteHost(String newAddress) {
		remoteAddress = newAddress;
	}

	public static final void setSQLInitializiation(String[] data) {
		sqlconf.setProperty("host", data[0]);
		sqlconf.setProperty("port", data[1]);
		sqlconf.setProperty("username", data[2]);
		sqlconf.setProperty("password", data[3]);
		sqlconf.setProperty("database", data[4]);
	}

	public static final String[] getSQLInitialization() {
		String[] data = new String[5];
		data[0] = sqlconf.getProperty("host", "localhost");
		data[1] = sqlconf.getProperty("port", "3306");
		data[2] = sqlconf.getProperty("username", "root");
		data[3] = sqlconf.getProperty("password", "root");
		data[4] = sqlconf.getProperty("database", "accountmanager");
		return data;
	}
}