package com.tpps.technicalServices.network.login.packets;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class PacketGetAllStatistics extends Packet{

	private static final long serialVersionUID = 6242390014329039234L;
	private String[][] allStatistics;
	
	public PacketGetAllStatistics(String[][] allStatistics) {
		super(PacketType.GET_ALL_STATISTICS);
		this.allStatistics = allStatistics;
	}
	
	public PacketGetAllStatistics() {
		super(PacketType.GET_ALL_STATISTICS);
		this.allStatistics = null;
	}
	
	public String[][] getAllStatistics() {
		return allStatistics;
	}
	
	public void setAllStatistics(String[][] allStatistics) {
		this.allStatistics = allStatistics;
	}

	@Override
	public String toString() {
		return "all Statistics are in this packet";
	}

}
