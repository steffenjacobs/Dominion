package com.tpps.technicalServices.network.statisticServer.Client;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Client;
import com.tpps.technicalServices.network.core.PacketHandler;
import com.tpps.technicalServices.network.core.packet.Packet;

public class MatchMakingClient extends PacketHandler{

	private Client mm_client;
	
	public MatchMakingClient() {
		try {
			this.mm_client = new Client(new InetSocketAddress(Addresses.getLocalHost(), 1345), this, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		
	}
	
	
	public static void main(String[] args) {
		
	}
}
