package com.tpps.technicalServices.util;

import java.io.IOException;
import java.net.InetAddress;

public class NetUtil {

	private static final int CONNECTION_TIMEOUT = 8000;

	public static boolean isNetworkReachable(String address) {
		try {
			return InetAddress.getByName(address).isReachable(CONNECTION_TIMEOUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
