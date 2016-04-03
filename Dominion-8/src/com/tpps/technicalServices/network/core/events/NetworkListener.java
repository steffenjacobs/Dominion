package com.tpps.technicalServices.network.core.events;

public interface NetworkListener {
	public void onClientConnect(int port);
	
	public void onClientDisconnect(int port);
}