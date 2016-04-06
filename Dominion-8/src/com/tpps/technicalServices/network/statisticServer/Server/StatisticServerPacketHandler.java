package com.tpps.technicalServices.network.statisticServer.Server;

import java.io.IOException;

import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.login.SQLHandling.SQLStatisticsHandler;
import com.tpps.technicalServices.network.statisticServer.Packet.PacketGetAllStatistics;

public class StatisticServerPacketHandler extends PacketHandler{

	private StatisticServer server;
	
	@Override
	public void handleReceivedPacket(int port, final Packet packet) {
		System.out.println("Server received a packet");
		switch(packet.getType()){
		case GET_ALL_STATISTICS:
			PacketGetAllStatistics pac = (PacketGetAllStatistics) packet;
			pac.setAllStatistics(SQLStatisticsHandler.getAllStatistics());
			try {
				server.sendMessage(port, pac);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			System.out.println("Server sent a packet with all statistics");
		break;
		default: System.out.println("sth. went wrong with received packet");break;		
		}
	}
	
	
	
	
	
	public void setServer(StatisticServer server) {
		this.server = server;
	}
}
