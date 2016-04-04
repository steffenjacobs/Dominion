package com.tpps.technicalServices.network.statisticServer.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.chat.client.ChatClient;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.statisticServer.Packet.PacketGetAllStatistics;

public class StatisticClient extends PacketHandler{

	private Client s_login;
	
	public StatisticClient() {
		try {
			this.s_login = new Client(new InetSocketAddress(Addresses.getLocalHost(), 1345), this, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		System.out.println("Server received a packet");
		switch(packet.getType()){
		case GET_ALL_STATISTICS:
			this.hanleAllStatistics((PacketGetAllStatistics) packet);
			break;
		default: System.out.println("sth went wrong with received packet");break;
		}		
	}
	
	private void hanleAllStatistics(PacketGetAllStatistics packet){
		String[][] allStatistics = packet.getAllStatistics();
		System.out.println("received all statistics");
	}
	
	public void sendPacketForAllStatistics(){
		try {
			s_login.sendMessage(new PacketGetAllStatistics());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new StatisticClient().sendPacketForAllStatistics();
	}
}
