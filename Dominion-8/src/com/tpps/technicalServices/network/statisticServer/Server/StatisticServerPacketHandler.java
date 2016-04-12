package com.tpps.technicalServices.network.statisticServer.Server;

import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;

public class StatisticServerPacketHandler extends PacketHandler{

	private StatisticServer server;
	
	@Override
	public void handleReceivedPacket(int port, final Packet packet) {
		System.out.println("Server received a packet");
		switch(packet.getType()){
				
		default: System.out.println("sth. went wrong with received packet");break;		
		}
	}
	
	
	
	
	
	public void setServer(StatisticServer server) {
		this.server = server;
	}
}
