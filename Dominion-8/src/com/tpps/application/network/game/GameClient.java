package com.tpps.application.network.game;

import java.io.IOException;
import java.net.SocketAddress;

import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.gameSession.packets.PacketRegistratePlayerByServer;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class GameClient extends Client{
	private int id;
	private static int classId = 0;

	public GameClient(SocketAddress _address, PacketHandler _handler) throws IOException {
		super(_address, _handler, false);	
		registrateByServer();
	}

	private void registrateByServer() throws IOException {
		this.id = GameClient.classId++;		
		this.sendMessage(new PacketRegistratePlayerByServer(this.id));
	}


}
