package com.tpps.technicalServices.network.login.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This class represents a packet which will be send through the network.
 * This packet include all statistics in a twodimensional array
 * @author jhuhn
 */
public class PacketGetAllStatistics extends Packet{

	private static final long serialVersionUID = 6242390014329039234L;
	private String[][] allStatistics;
	
	/**
	 * Initializes the object
	 * @param allStatistics a twodimensional array of all statistics from the database
	 */
	public PacketGetAllStatistics(String[][] allStatistics) {
		super(PacketType.GET_ALL_STATISTICS);
		this.allStatistics = allStatistics;
	}
	
	/**
	 * Initializes the object
	 */
	public PacketGetAllStatistics() {
		super(PacketType.GET_ALL_STATISTICS);
		this.allStatistics = null;
	}
	
	/**
	 * 
	 * @return gets all statistics from the object (in a twodimensional array)
	 */
	public String[][] getAllStatistics() {
		return allStatistics;
	}
	
	/**
	 * 
	 * @param allStatistics sets the twodimensional array of statistics to this object
	 */
	public void setAllStatistics(String[][] allStatistics) {
		this.allStatistics = allStatistics;
	}

	/**
	 * overrides the toString method, mainly for debug purposes
	 * @return a readable representation of the object
	 */
	@Override
	public String toString() {
		return "all Statistics are in this packet";
	}
}
