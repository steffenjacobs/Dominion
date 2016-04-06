package com.tpps.test.technicalServices.network;

import com.tpps.technicalServices.network.core.events.NetworkListener;

/**
 * custom NetworkListener for the testing of the network-framework This listener
 * counts the connect-/disconnect-events and makes them available through
 * getters
 * 
 * @author Steffen Jacobs
 */
public class TestConnectionListener implements NetworkListener {

	private int countConnect = 0, countDisconnect = 0;

	/** @return the caught connect-events since the last reset */
	public int getConnectCount() {
		return countConnect;
	}

	/** @return the caught disconnect-events since the last reset */
	public int getDisconnectCount() {
		return countDisconnect;
	}

	/** resets the counter for the connect-/disconnect-events */
	public void resetTest() {
		countConnect = 0;
		countDisconnect = 0;
	}

	/** trivial */
	@Override
	public void onClientConnect(int port) {
		countConnect++;
	}

	/** trivial */
	@Override
	public void onClientDisconnect(int port) {
		countDisconnect++;
	}
}
