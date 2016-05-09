package com.tpps.technicalServices.util;

import java.io.IOException;
import java.net.InetAddress;

/**
 * provides simple network-functions
 * 
 * @author Steffen Jacobs
 */
public class NetUtil {

	private static final int CONNECTION_TIMEOUT = 8000;

	/**
	 * @param address
	 *            Inet-Address as a string to check if reachable
	 * @return true if server is online
	 */
	public static boolean isNetworkReachable(String address) {
		try {
			return InetAddress.getByName(address).isReachable(CONNECTION_TIMEOUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
