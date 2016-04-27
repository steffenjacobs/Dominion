package com.tpps.technicalServices.network.core.events;

import java.util.concurrent.CopyOnWriteArrayList;

import com.tpps.technicalServices.network.core.PortCheckable;

/**
 * This Listener Manager is responsible for managing, adding and removing
 * Network-Listeners. It is also called to fire Disconnect- and Connect-Events,
 * if a client connects to a server or disconnects.
 * 
 * @author Steffen Jacobs
 */
public class NetworkListenerManager {

	private PortCheckable sender;

	/**
	 * @param checkable
	 *            the client or server-instance who this listener-manager is
	 *            handling the events of
	 */
	public NetworkListenerManager(PortCheckable checkable) {
		this.sender = checkable;
	}

	private CopyOnWriteArrayList<NetworkListener> listeners = new CopyOnWriteArrayList<>();

	/**
	 * adds a listener
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void registerListener(NetworkListener listener) {
		listeners.add(listener);
	}

	/**
	 * removes a listener
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void unregisterListener(NetworkListener listener) {
		listeners.remove(listener);
	}

	/**
	 * call all listener's connect-procedure
	 * 
	 * @param port
	 *            the port where someone connected
	 */
	public void fireConnectEvent(int port) {
		for (NetworkListener listen : listeners) {
			listen.onClientConnect(port);
		}
	}

	/**
	 * call all listener's disconnect-procedure
	 * 
	 * @param port
	 *            the port where someone disconnected
	 */
	public void fireDisconnectEvent(int port) {
		if (this.sender.hasPortConnected(port)) {
			for (NetworkListener listen : listeners) {
				listen.onClientDisconnect(port);
			}
		}
	}
}