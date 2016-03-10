package com.tpps.application.network.core.events;

import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkListenerManager {

	private CopyOnWriteArrayList<NetworkListener> listeners = new CopyOnWriteArrayList<>();

	public void registerListener(NetworkListener listener) {
		listeners.add(listener);
	}

	public void unregisterListener(NetworkListener listener) {
		listeners.remove(listener);
	}
	
	public void fireConnectEvent(int port){
		for(NetworkListener listen : listeners){
			listen.onClientConnect(port);
		}
	}
	
	public void fireDisconnectEvent(int port){
		for(NetworkListener listen : listeners){
			listen.onClientDisconnect(port);
		}
	}
}