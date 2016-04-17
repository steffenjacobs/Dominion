package com.tpps.technicalServices.network.core.events;

/**
 * This interface is used to inform all listeners, when a client connects or
 * disconnects. It can also be used by the client and will be called, when the
 * client connects/disconnects to the server
 * 
 * @author Steffen Jacobs
 */
public interface NetworkListener {

	/**
	 * is called when a connection was established.
	 * 
	 * @param port
	 *            the connection is established through
	 */
	public void onClientConnect(int port);

	/**
	 * is called when a connection was lost or closed.
	 * 
	 * @param port
	 *            the connection was established through
	 */
	public void onClientDisconnect(int port);
}