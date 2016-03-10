package com.tpps.application.network.core.events;

public interface NetworkListener {
	public void onClientConnect(int port);
	
	public void onClientDisconnect(int port);
}