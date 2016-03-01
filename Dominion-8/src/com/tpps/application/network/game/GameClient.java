package com.tpps.application.network.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.application.network.packet.PacketType;

public class GameClient extends Client{

	public GameClient(SocketAddress _address, PacketHandler _handler) throws IOException {
		super(_address, _handler);
		
	}
	
	public static void main(String[] args) {
		try {
			GameClient g = new GameClient(new InetSocketAddress("localhost", 1339), new ClientGamePacketHandler());
			
			while (!g.isConnected()) {				
				
				System.out.println("wait on connection");
			}
			g.sendMessage(PacketType.getBytes(new PacketPlayCard(2, "karl")));
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
