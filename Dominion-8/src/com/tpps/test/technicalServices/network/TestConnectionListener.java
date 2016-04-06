package com.tpps.test.technicalServices.network;

import com.tpps.technicalServices.network.core.events.NetworkListener;

public class TestConnectionListener implements NetworkListener {

	private int countConnect = 0, countDisconnect = 0;

	public int getConnectCount() {
		return countConnect;
	}

	public int getDisconnectCount() {
		return countDisconnect;
	}

	public void resetTest() {
		countConnect = 0;
		countDisconnect = 0;
	}

	@Override
	public void onClientConnect(int port) {
		countConnect++;
	}

	@Override
	public void onClientDisconnect(int port) {
		countDisconnect++;
	}
}
