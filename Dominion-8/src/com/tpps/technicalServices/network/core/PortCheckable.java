package com.tpps.technicalServices.network.core;

/**
 * a simple interface to unite the Server's and Client's ability to check for a
 * connected port
 * 
 * @author Steffen Jacobs
 */
public interface PortCheckable {

	/**
	 * @return whether this has an open connection on a specific port
	 * @param port
	 *            the port to check for a connection
	 */
	public boolean hasPortConnected(int port);
}
