package com.tpps.test.application.network;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.tpps.technicalServices.network.game.ClientGamePacketHandler;
import com.tpps.technicalServices.network.game.GameClient;


public class ServerTest {
	
	public void setUp(){
		GameClient g = null;
		try {
			
			g = new GameClient(new InetSocketAddress("localhost", 1339), new ClientGamePacketHandler());
			
//					g.sendMessage(new PacketPlayCard("Copper6", "karl"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ServerTest().setUp();
	}

}
